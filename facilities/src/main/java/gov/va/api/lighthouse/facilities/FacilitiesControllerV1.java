package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static gov.va.api.lighthouse.facilities.ControllersV1.page;
import static gov.va.api.lighthouse.facilities.ControllersV1.validateFacilityType;
import static gov.va.api.lighthouse.facilities.ControllersV1.validateServices;
import static gov.va.api.lighthouse.facilities.FacilityUtils.distance;
import static gov.va.api.lighthouse.facilities.FacilityUtils.entityIds;
import static gov.va.api.lighthouse.facilities.FacilityUtils.haversine;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesIdsResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacilityReadResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/v1")
public class FacilitiesControllerV1 {
  private static final ObjectMapper MAPPER = FacilitiesJacksonConfigV1.createMapper();

  private static final FacilityOverlayV1 FACILITY_OVERLAY =
      FacilityOverlayV1.builder().mapper(MAPPER).build();

  private final FacilityRepository facilityRepository;

  private final String linkerUrl;

  @Builder
  FacilitiesControllerV1(
      @Autowired FacilityRepository facilityRepository,
      @Value("${facilities.url}") String baseUrl,
      @Value("${facilities.base-path}") String basePath) {
    this.facilityRepository = facilityRepository;
    String url = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    String path = basePath.replaceAll("/$", "");
    path = path.isEmpty() ? path : path + "/";
    linkerUrl = url + path + "v1/";
  }

  @SneakyThrows
  private static Facility facility(HasFacilityPayload entity) {
    return FACILITY_OVERLAY.apply(entity);
  }

  private static GeoFacility geoFacility(Facility facility) {
    return GeoFacilityTransformerV1.builder().facility(facility).build().toGeoFacility();
  }

  /** Get all facilities. */
  @SneakyThrows
  @GetMapping(
      value = "/facilities/all",
      produces = {"application/json", "application/geo+json", "application/vnd.geo+json"})
  String all() {
    StringBuilder sb = new StringBuilder();
    sb.append("{\"type\":\"FeatureCollection\",\"features\":[");
    List<HasFacilityPayload> all = facilityRepository.findAllProjectedBy();
    if (!all.isEmpty()) {
      all.parallelStream()
          .map(
              e ->
                  FacilitiesJacksonConfigV1.quietlyWriteValueAsString(
                      MAPPER, geoFacility(facility(e))))
          .forEachOrdered(g -> sb.append(g).append(","));
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]}");
    return sb.toString();
  }

  /** Get all facilities as CSV. */
  @SneakyThrows
  @GetMapping(value = "/facilities/all", produces = "text/csv")
  String allCsv() {
    List<List<String>> rows =
        facilityRepository.findAllProjectedBy().stream()
            .parallel()
            .map(e -> CsvTransformerV1.builder().facility(facility(e)).build().toRow())
            .collect(toList());
    StringBuilder sb = new StringBuilder();
    try (CSVPrinter printer =
        CSVFormat.DEFAULT
            .withHeader(CsvTransformerV1.HEADERS.stream().toArray(String[]::new))
            .print(sb)) {
      for (List<String> row : rows) {
        printer.printRecord(row);
      }
      return sb.toString();
    }
  }

  private List<FacilityEntity> entitiesByBoundingBox(
      List<BigDecimal> bbox, String rawType, List<String> rawServices, Boolean rawMobile) {
    if (bbox.size() != 4) {
      throw new ExceptionsV0.InvalidParameter("bbox", bbox);
    }
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<ServiceType> services = validateServices(rawServices);

    // lng lat lng lat
    List<FacilityEntity> allEntities =
        facilityRepository.findAll(
            FacilityRepository.BoundingBoxSpecification.builder()
                .minLongitude(bbox.get(0).min(bbox.get(2)))
                .maxLongitude(bbox.get(0).max(bbox.get(2)))
                .minLatitude(bbox.get(1).min(bbox.get(3)))
                .maxLatitude(bbox.get(1).max(bbox.get(3)))
                .facilityType(facilityType)
                .services(services)
                .mobile(rawMobile)
                .build());
    double centerLng = (bbox.get(0).doubleValue() + bbox.get(2).doubleValue()) / 2;
    double centerLat = (bbox.get(1).doubleValue() + bbox.get(3).doubleValue()) / 2;
    return allEntities.stream()
        .sorted(Comparator.comparingDouble(left -> distance(left, centerLng, centerLat)))
        .collect(toList());
  }

  private List<FacilityEntity> entitiesByIds(String ids) {
    List<FacilityEntity.Pk> pks = entityIds(ids);
    Map<FacilityEntity.Pk, FacilityEntity> entities =
        facilityRepository.findByIdIn(pks).stream()
            .collect(toMap(e -> e.id(), Function.identity()));
    return pks.stream().map(pk -> entities.get(pk)).filter(Objects::nonNull).collect(toList());
  }

  @SneakyThrows
  private List<DistanceEntity> entitiesByLatLong(
      BigDecimal longitude,
      BigDecimal latitude,
      String ids,
      String rawType,
      List<String> rawServices,
      Boolean rawMobile) {
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<ServiceType> services = validateServices(rawServices);
    List<FacilityEntity> entities =
        facilityRepository.findAll(
            FacilityRepository.TypeServicesIdsSpecification.builder()
                .ids(entityIds(ids))
                .facilityType(facilityType)
                .services(services)
                .mobile(rawMobile)
                .build());
    double lng = longitude.doubleValue();
    double lat = latitude.doubleValue();
    return entities.stream()
        .map(
            e ->
                DistanceEntity.builder()
                    .entity(e)
                    .distance(BigDecimal.valueOf(haversine(e, lng, lat)))
                    .build())
        .sorted((left, right) -> left.distance().compareTo(right.distance()))
        .collect(toList());
  }

  private Page<FacilityEntity> entitiesPageByState(
      String rawState,
      String rawType,
      List<String> rawServices,
      Boolean rawMobile,
      int page,
      int perPage) {
    checkArgument(page >= 1);
    checkArgument(perPage >= 1);
    String state = rawState.trim().toUpperCase(Locale.US);
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<ServiceType> services = validateServices(rawServices);
    return facilityRepository.findAll(
        FacilityRepository.StateSpecification.builder()
            .state(state)
            .facilityType(facilityType)
            .services(services)
            .mobile(rawMobile)
            .build(),
        PageRequest.of(page - 1, perPage, FacilityEntity.naturalOrder()));
  }

  private Page<FacilityEntity> entitiesPageByZip(
      String rawZip,
      String rawType,
      List<String> rawServices,
      Boolean rawMobile,
      int page,
      int perPage) {
    checkArgument(page >= 1);
    checkArgument(perPage >= 1);
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<ServiceType> services = validateServices(rawServices);
    String zip = rawZip.substring(0, Math.min(rawZip.length(), 5));
    return facilityRepository.findAll(
        FacilityRepository.ZipSpecification.builder()
            .zip(zip)
            .facilityType(facilityType)
            .services(services)
            .mobile(rawMobile)
            .build(),
        PageRequest.of(page - 1, perPage, FacilityEntity.naturalOrder()));
  }

  private FacilityEntity entityById(String id) {
    FacilityEntity.Pk pk = null;
    try {
      pk = FacilityEntity.Pk.fromIdString(id);
    } catch (IllegalArgumentException ex) {
      throw new ExceptionsV0.NotFound(id, ex);
    }
    Optional<FacilityEntity> opt = facilityRepository.findById(pk);
    if (opt.isEmpty()) {
      throw new ExceptionsV0.NotFound(id);
    }
    return opt.get();
  }

  /** Get all facility IDs as a list by Type. */
  @GetMapping(
      value = "/ids",
      produces = {"application/json"})
  FacilitiesIdsResponse facilityIdsByType(
      @RequestParam(value = "type", required = false) String type) {
    FacilityEntity.Type facilityType = validateFacilityType(type);
    return FacilitiesIdsResponse.builder()
        .data(
            facilityRepository.findAllIds().stream()
                .filter(id -> facilityType == null || id.type() == facilityType)
                .map(id -> id.toIdString())
                .collect(toList()))
        .build();
  }

  /** Get facilities by bounding box. */
  @GetMapping(
      value = "/facilities",
      produces = {"application/geo+json", "application/vnd.geo+json"},
      params = {"bbox[]", "!lat", "!long", "!state", "!visn", "!zip"})
  GeoFacilitiesResponse geoFacilitiesByBoundingBox(
      @RequestParam(value = "bbox[]") List<BigDecimal> bbox,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            page(entitiesByBoundingBox(bbox, type, services, mobile), page, perPage).stream()
                .map(e -> geoFacility(facility(e)))
                .collect(toList()))
        .build();
  }

  /** Get facilities by IDs. */
  @GetMapping(
      value = "/facilities",
      produces = {"application/geo+json", "application/vnd.geo+json"},
      params = {"!bbox[]", "ids", "!lat", "!long", "!state", "!visn", "!zip"})
  GeoFacilitiesResponse geoFacilitiesByIds(
      @RequestParam(value = "ids") String ids,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            page(entitiesByIds(ids), page, perPage).stream()
                .map(e -> geoFacility(facility(e)))
                .collect(toList()))
        .build();
  }

  /** Get facilities by coordinates. */
  @GetMapping(
      value = "/facilities",
      produces = {"application/geo+json", "application/vnd.geo+json"},
      params = {"!bbox[]", "lat", "long", "!state", "!visn", "!zip"})
  GeoFacilitiesResponse geoFacilitiesByLatLong(
      @RequestParam(value = "lat") BigDecimal latitude,
      @RequestParam(value = "long") BigDecimal longitude,
      @RequestParam(value = "ids", required = false) String ids,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            page(entitiesByLatLong(longitude, latitude, ids, type, services, mobile), page, perPage)
                .stream()
                .map(e -> geoFacility(e.facility()))
                .collect(toList()))
        .build();
  }

  /** Get facilities by state. */
  @GetMapping(
      value = "/facilities",
      produces = {"application/geo+json", "application/vnd.geo+json"},
      params = {"!bbox[]", "!lat", "!long", "state", "!visn", "!zip"})
  GeoFacilitiesResponse geoFacilitiesByState(
      @RequestParam(value = "state") String state,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            perPage == 0
                ? emptyList()
                : entitiesPageByState(state, type, services, mobile, page, perPage).stream()
                    .map(e -> geoFacility(facility(e)))
                    .collect(toList()))
        .build();
  }

  /** Get facilities by VISN. */
  @GetMapping(
      value = "/facilities",
      produces = {"application/geo+json", "application/vnd.geo+json"},
      params = {"!bbox[]", "!lat", "!long", "!state", "!type", "visn", "!zip"})
  GeoFacilitiesResponse geoFacilitiesByVisn(
      @RequestParam(value = "visn") String visn,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            page(facilityRepository.findByVisn(visn), page, perPage).stream()
                .map(e -> geoFacility(facility(e)))
                .collect(toList()))
        .build();
  }

  /** Get facilities by zip. */
  @GetMapping(
      value = "/facilities",
      produces = {"application/geo+json", "application/vnd.geo+json"},
      params = {"!bbox[]", "!lat", "!long", "!state", "!visn", "zip"})
  GeoFacilitiesResponse geoFacilitiesByZip(
      @RequestParam(value = "zip") String zip,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            perPage == 0
                ? emptyList()
                : entitiesPageByZip(zip, type, services, mobile, page, perPage).stream()
                    .map(e -> geoFacility(facility(e)))
                    .collect(toList()))
        .build();
  }

  /** Get facilities by bounding box. */
  @GetMapping(
      value = "/facilities",
      produces = "application/json",
      params = {"bbox[]", "!lat", "!long", "!state", "!visn", "!zip"})
  FacilitiesResponse jsonFacilitiesByBoundingBox(
      @RequestParam(value = "bbox[]") List<BigDecimal> bbox,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<FacilityEntity> entities = entitiesByBoundingBox(bbox, type, services, mobile);
    PageLinker linker =
        PageLinker.builder()
            .url(linkerUrl + "facilities")
            .params(
                Parameters.builder()
                    .addAll("bbox[]", bbox)
                    .addIgnoreNull("type", type)
                    .addAll("services[]", services)
                    .addIgnoreNull("mobile", mobile)
                    .add("page", page)
                    .add("per_page", perPage)
                    .build())
            .totalEntries(entities.size())
            .build();
    return FacilitiesResponse.builder()
        .data(page(entities, page, perPage).stream().map(e -> facility(e)).collect(toList()))
        .links(linker.links())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder().pagination(linker.pagination()).build())
        .build();
  }

  /** Get facilities by IDs. */
  @GetMapping(
      value = "/facilities",
      produces = "application/json",
      params = {"!bbox[]", "ids", "!lat", "!long", "!state", "!visn", "!zip"})
  FacilitiesResponse jsonFacilitiesByIds(
      @RequestParam(value = "ids") String ids,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<FacilityEntity> entities = entitiesByIds(ids);
    PageLinker linker =
        PageLinker.builder()
            .url(linkerUrl + "facilities")
            .params(
                Parameters.builder()
                    .add("ids", ids)
                    .add("page", page)
                    .add("per_page", perPage)
                    .build())
            .totalEntries(entities.size())
            .build();
    return FacilitiesResponse.builder()
        .data(page(entities, page, perPage).stream().map(e -> facility(e)).collect(toList()))
        .links(linker.links())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder().pagination(linker.pagination()).build())
        .build();
  }

  /** Get facilities by coordinates. */
  @GetMapping(
      value = "/facilities",
      produces = "application/json",
      params = {"!bbox[]", "lat", "long", "!state", "!visn", "!zip"})
  FacilitiesResponse jsonFacilitiesByLatLong(
      @RequestParam(value = "lat") BigDecimal latitude,
      @RequestParam(value = "long") BigDecimal longitude,
      @RequestParam(value = "ids", required = false) String ids,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<DistanceEntity> entities =
        entitiesByLatLong(longitude, latitude, ids, type, services, mobile);
    PageLinker linker =
        PageLinker.builder()
            .url(linkerUrl + "facilities")
            .params(
                Parameters.builder()
                    .add("lat", latitude)
                    .add("long", longitude)
                    .addIgnoreNull("type", type)
                    .addAll("services[]", services)
                    .addIgnoreNull("mobile", mobile)
                    .add("page", page)
                    .add("per_page", perPage)
                    .build())
            .totalEntries(entities.size())
            .build();
    List<DistanceEntity> entitiesPage = page(entities, page, perPage);
    List<FacilitiesResponse.Distance> distances =
        entitiesPage.stream()
            .map(
                e ->
                    FacilitiesResponse.Distance.builder()
                        .id(e.facility().id())
                        .distance(e.distance().setScale(2, RoundingMode.HALF_EVEN))
                        .build())
            .collect(toList());
    return FacilitiesResponse.builder()
        .data(entitiesPage.stream().map(e -> e.facility()).collect(toList()))
        .links(linker.links())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder()
                .pagination(linker.pagination())
                .distances(distances)
                .build())
        .build();
  }

  /** Get facilities by state. */
  @GetMapping(
      value = "/facilities",
      produces = "application/json",
      params = {"!bbox[]", "!lat", "!long", "state", "!visn", "!zip"})
  FacilitiesResponse jsonFacilitiesByState(
      @RequestParam(value = "state") String state,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    Page<FacilityEntity> entitiesPage =
        entitiesPageByState(state, type, services, mobile, page, Math.max(perPage, 1));
    PageLinker linker =
        PageLinker.builder()
            .url(linkerUrl + "facilities")
            .params(
                Parameters.builder()
                    .add("state", state)
                    .addIgnoreNull("type", type)
                    .addAll("services[]", services)
                    .addIgnoreNull("mobile", mobile)
                    .add("page", page)
                    .add("per_page", perPage)
                    .build())
            .totalEntries((int) entitiesPage.getTotalElements())
            .build();
    return FacilitiesResponse.builder()
        .data(
            perPage == 0
                ? emptyList()
                : entitiesPage.stream().map(e -> facility(e)).collect(toList()))
        .links(linker.links())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder().pagination(linker.pagination()).build())
        .build();
  }

  /** Get facilities by VISN. */
  @GetMapping(
      value = "/facilities",
      produces = "application/json",
      params = {"!bbox[]", "!lat", "!long", "!state", "!type", "visn", "!zip"})
  FacilitiesResponse jsonFacilitiesByVisn(
      @RequestParam(value = "visn") String visn,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<FacilityEntity> entities = facilityRepository.findByVisn(visn);
    PageLinker linker =
        PageLinker.builder()
            .url(linkerUrl + "facilities")
            .params(
                Parameters.builder()
                    .add("visn", visn)
                    .add("page", page)
                    .add("per_page", perPage)
                    .build())
            .totalEntries(entities.size())
            .build();
    return FacilitiesResponse.builder()
        .data(page(entities, page, perPage).stream().map(e -> facility(e)).collect(toList()))
        .links(linker.links())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder().pagination(linker.pagination()).build())
        .build();
  }

  /** Get facilities by zip. */
  @GetMapping(
      value = "/facilities",
      produces = "application/json",
      params = {"!bbox[]", "!lat", "!long", "!state", "!visn", "zip"})
  FacilitiesResponse jsonFacilitiesByZip(
      @RequestParam(value = "zip") String zip,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    Page<FacilityEntity> entitiesPage =
        entitiesPageByZip(zip, type, services, mobile, page, Math.max(perPage, 1));
    PageLinker linker =
        PageLinker.builder()
            .url(linkerUrl + "facilities")
            .params(
                Parameters.builder()
                    .add("zip", zip)
                    .addIgnoreNull("type", type)
                    .addAll("services[]", services)
                    .addIgnoreNull("mobile", mobile)
                    .add("page", page)
                    .add("per_page", perPage)
                    .build())
            .totalEntries((int) entitiesPage.getTotalElements())
            .build();
    return FacilitiesResponse.builder()
        .data(
            perPage == 0
                ? emptyList()
                : entitiesPage.stream().map(e -> facility(e)).collect(toList()))
        .links(linker.links())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder().pagination(linker.pagination()).build())
        .build();
  }

  /** Read geo facility. */
  @GetMapping(
      value = "/facilities/{id}",
      produces = {"application/geo+json", "application/vnd.geo+json"})
  GeoFacilityReadResponse readGeoJson(@PathVariable("id") String id) {
    return GeoFacilityReadResponse.of(geoFacility(facility(entityById(id))));
  }

  /** Read facility. */
  @GetMapping(value = "/facilities/{id}", produces = "application/json")
  FacilityReadResponse readJson(@PathVariable("id") String id) {
    return FacilityReadResponse.builder().facility(facility(entityById(id))).build();
  }

  @Data
  @Builder
  private static final class DistanceEntity {
    final FacilityEntity entity;

    final BigDecimal distance;

    Facility facility;

    Facility facility() {
      if (facility == null) {
        facility = FacilitiesControllerV1.facility(entity);
      }
      return facility;
    }
  }
}
