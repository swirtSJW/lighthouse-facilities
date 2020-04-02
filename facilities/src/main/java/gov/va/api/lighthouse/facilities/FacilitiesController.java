package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Builder
@Validated
@RestController
@RequestMapping(value = {"/v0"})
public class FacilitiesController {
  private static final Map<String, FacilityEntity.Type> ENTITY_TYPE_LOOKUP =
      caseInsensitiveMap(
          ImmutableMap.of(
              "benefits",
              FacilityEntity.Type.vba,
              "cemetery",
              FacilityEntity.Type.nca,
              "health",
              FacilityEntity.Type.vha,
              "vet_center",
              FacilityEntity.Type.vc));

  private static final Map<String, Facility.ServiceType> SERVICE_LOOKUP =
      caseInsensitiveMap(
          Streams.stream(
                  Iterables.concat(
                      List.<Facility.ServiceType>of(Facility.HealthService.values()),
                      List.<Facility.ServiceType>of(Facility.BenefitsService.values()),
                      List.<Facility.ServiceType>of(Facility.OtherService.values())))
              .collect(toMap(v -> v.toString(), Function.identity())));

  private final FacilityRepository facilityRepository;

  @SuppressWarnings("unused")
  private final DriveTimeBandRepository driveTimeBandRepository;

  private final String baseUrl;

  private final String basePath;

  FacilitiesController(
      @Autowired FacilityRepository facilityRepository,
      @Autowired DriveTimeBandRepository driveTimeBandRepository,
      @Value("${facilities.url}") String baseUrl,
      @Value("${facilities.base-path}") String basePath) {
    this.facilityRepository = facilityRepository;
    this.driveTimeBandRepository = driveTimeBandRepository;
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    basePath = basePath.replace("/", "");
    this.basePath = basePath.isEmpty() ? basePath : basePath + "/";
  }

  private static <T> Map<String, T> caseInsensitiveMap(@NonNull Map<String, T> source) {
    Map<String, T> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.putAll(source);
    return Collections.unmodifiableMap(map);
  }

  /** Unitless distance approximation based on geometric distance formula. For sorting only. */
  private static double distance(@NonNull FacilityEntity entity, double lng, double lat) {
    double lngDiff = entity.longitude() - lng;
    double latDiff = entity.latitude() - lat;
    return Math.sqrt(lngDiff * lngDiff + latDiff * latDiff);
  }

  @SneakyThrows
  private static Facility facility(FacilityEntity entity) {
    return FacilitiesJacksonConfig.createMapper().readValue(entity.facility(), Facility.class);
  }

  private static GeoFacility geoFacility(Facility facility) {
    return GeoFacilityTransformer.builder().facility(facility).build().toGeoFacility();
  }

  /** Distance in miles using Haversine algorithm. */
  private static double haversine(@NonNull FacilityEntity entity, double lng, double lat) {
    double lon1 = Math.toRadians(entity.longitude());
    double lat1 = Math.toRadians(entity.latitude());
    double lon2 = Math.toRadians(lng);
    double lat2 = Math.toRadians(lat);
    double lonDiff = lon2 - lon1;
    double latDiff = lat2 - lat1;
    double x = Math.sin(latDiff / 2);
    double y = Math.sin(lonDiff / 2);
    double coeff = Math.cos(lat1) * Math.cos(lat2);
    // 3958.8 is Earth radius in miles
    return 3958.8 * 2 * Math.asin(Math.sqrt(x * x + coeff * y * y));
  }

  private static <T> List<T> page(List<T> objects, int page, int perPage) {
    checkArgument(page >= 1);
    checkArgument(perPage >= 0);
    if (perPage == 0) {
      return emptyList();
    }
    int fromIndex = (page - 1) * perPage;
    if (objects.size() < fromIndex) {
      return emptyList();
    }
    return objects.subList(fromIndex, Math.min(fromIndex + perPage, objects.size()));
  }

  private static FacilityEntity.Type validateFacilityType(String type) {
    FacilityEntity.Type mapped = ENTITY_TYPE_LOOKUP.get(trimToEmpty(type));
    if (mapped == null && isNotBlank(type)) {
      throw new ExceptionsV0.InvalidParameter("type", type);
    }
    return mapped;
  }

  private static Set<Facility.ServiceType> validateServices(Collection<String> services) {
    if (isEmpty(services)) {
      return emptySet();
    }
    List<Facility.ServiceType> results = new ArrayList<>(services.size());
    for (String service : services) {
      Facility.ServiceType mapped = SERVICE_LOOKUP.get(trimToEmpty(service));
      if (mapped == null) {
        throw new ExceptionsV0.InvalidParameter("services", service);
      }
      results.add(mapped);
    }
    return ImmutableSet.copyOf(results);
  }

  /** Get all facilities. */
  @GetMapping(
      value = "/facilities/all",
      produces = {"application/json", "application/vnd.geo+json"})
  public GeoFacilitiesResponse all() {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            Streams.stream(facilityRepository.findAll())
                .map(e -> geoFacility(facility(e)))
                .collect(toList()))
        .build();
  }

  /** Get all facilities as CSV. */
  @SneakyThrows
  @GetMapping(value = "/facilities/all", produces = "text/csv")
  public String allCsv() {
    List<List<String>> rows =
        Streams.stream(facilityRepository.findAll())
            .parallel()
            .map(e -> CsvTransformer.builder().facility(facility(e)).build().toRow())
            .collect(toList());
    StringBuilder sb = new StringBuilder();
    try (CSVPrinter printer =
        CSVFormat.DEFAULT
            .withHeader(CsvTransformer.HEADERS.stream().toArray(String[]::new))
            .print(sb)) {
      for (List<String> row : rows) {
        printer.printRecord(row);
      }
      return sb.toString();
    }
  }

  private List<FacilityEntity> entitiesByBoundingBox(
      List<BigDecimal> bbox, String rawType, List<String> rawServices) {
    if (bbox.size() != 4) {
      throw new ExceptionsV0.InvalidParameter("bbox", bbox);
    }
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<Facility.ServiceType> services = validateServices(rawServices);
    // lng lat lng lat
    List<FacilityEntity> allEntities =
        facilityRepository.findAll(
            FacilityRepository.BBoxSpecification.builder()
                .minLongitude(bbox.get(0).min(bbox.get(2)))
                .maxLongitude(bbox.get(0).max(bbox.get(2)))
                .minLatitude(bbox.get(1).min(bbox.get(3)))
                .maxLatitude(bbox.get(1).max(bbox.get(3)))
                .facilityType(facilityType)
                .services(services)
                .build());
    double centerLng = (bbox.get(0).doubleValue() + bbox.get(2).doubleValue()) / 2;
    double centerLat = (bbox.get(1).doubleValue() + bbox.get(3).doubleValue()) / 2;
    return allEntities.stream()
        .sorted(
            (left, right) ->
                Double.compare(
                    distance(left, centerLng, centerLat), distance(right, centerLng, centerLat)))
        .collect(toList());
  }

  private List<FacilityEntity> entitiesByIds(String ids) {
    List<FacilityEntity.Pk> pks =
        Splitter.on(",")
            .splitToStream(ids)
            .map(id -> trimToNull(id))
            .filter(Objects::nonNull)
            .distinct()
            .map(id -> FacilityEntity.Pk.optionalFromIdString(id).orElse(null))
            .filter(Objects::nonNull)
            .collect(toList());
    Map<FacilityEntity.Pk, FacilityEntity> entities =
        facilityRepository.findByIdIn(pks).stream()
            .collect(toMap(e -> e.id(), Function.identity()));
    return pks.stream().map(pk -> entities.get(pk)).filter(Objects::nonNull).collect(toList());
  }

  @SneakyThrows
  private List<DistanceEntity> entitiesByLatLong(
      BigDecimal longitude, BigDecimal latitude, String rawType, List<String> rawServices) {
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<Facility.ServiceType> services = validateServices(rawServices);
    List<FacilityEntity> entities =
        facilityRepository.findAll(
            FacilityRepository.TypeServicesOnlySpecification.builder()
                .facilityType(facilityType)
                .services(services)
                .build());
    double lng = longitude.doubleValue();
    double lat = latitude.doubleValue();
    return entities.stream()
        .map(
            e ->
                DistanceEntity.builder()
                    .entity(e)
                    .distance(new BigDecimal(haversine(e, lng, lat)))
                    .build())
        .sorted((left, right) -> left.distance().compareTo(right.distance()))
        .collect(toList());
  }

  private Page<FacilityEntity> entitiesPageByState(
      String rawState, String rawType, List<String> rawServices, int page, int perPage) {
    checkArgument(page >= 1);
    checkArgument(perPage >= 1);
    String state = rawState.trim().toUpperCase(Locale.US);
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<Facility.ServiceType> services = validateServices(rawServices);
    return facilityRepository.findAll(
        FacilityRepository.StateSpecification.builder()
            .state(state)
            .facilityType(facilityType)
            .services(services)
            .build(),
        PageRequest.of(page - 1, perPage, FacilityEntity.naturalOrder()));
  }

  private Page<FacilityEntity> entitiesPageByZip(
      String rawZip, String rawType, List<String> rawServices, int page, int perPage) {
    checkArgument(page >= 1);
    checkArgument(perPage >= 1);
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<Facility.ServiceType> services = validateServices(rawServices);
    String zip = rawZip.substring(0, Math.min(rawZip.length(), 5));
    return facilityRepository.findAll(
        FacilityRepository.ZipSpecification.builder()
            .zip(zip)
            .facilityType(facilityType)
            .services(services)
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

  /** Get facilities by bounding box. */
  @GetMapping(value = "/facilities", produces = "application/vnd.geo+json", params = "bbox[]")
  public GeoFacilitiesResponse geoFacilitiesByBoundingBox(
      @RequestParam(value = "bbox[]") List<BigDecimal> bbox,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            page(entitiesByBoundingBox(bbox, type, services), page, perPage).stream()
                .map(e -> geoFacility(facility(e)))
                .collect(toList()))
        .build();
  }

  /** Get facilities by IDs. */
  @GetMapping(value = "/facilities", produces = "application/vnd.geo+json", params = "ids")
  public GeoFacilitiesResponse geoFacilitiesByIds(
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
      produces = "application/vnd.geo+json",
      params = {"lat", "long"})
  public GeoFacilitiesResponse geoFacilitiesByLatLong(
      @RequestParam(value = "lat") BigDecimal latitude,
      @RequestParam(value = "long") BigDecimal longitude,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            page(entitiesByLatLong(longitude, latitude, type, services), page, perPage).stream()
                .map(e -> geoFacility(e.facility()))
                .collect(toList()))
        .build();
  }

  /** Get facilities by state. */
  @GetMapping(value = "/facilities", produces = "application/vnd.geo+json", params = "state")
  public GeoFacilitiesResponse geoFacilitiesByState(
      @RequestParam(value = "state") String state,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            perPage == 0
                ? emptyList()
                : entitiesPageByState(state, type, services, page, perPage).stream()
                    .map(e -> geoFacility(facility(e)))
                    .collect(toList()))
        .build();
  }

  /** Get facilities by zip. */
  @GetMapping(value = "/facilities", produces = "application/vnd.geo+json", params = "zip")
  public GeoFacilitiesResponse geoFacilitiesByZip(
      @RequestParam(value = "zip") String zip,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            perPage == 0
                ? emptyList()
                : entitiesPageByZip(zip, type, services, page, perPage).stream()
                    .map(e -> geoFacility(facility(e)))
                    .collect(toList()))
        .build();
  }

  /** Get facilities by bounding box. */
  @GetMapping(value = "/facilities", produces = "application/json", params = "bbox[]")
  public FacilitiesResponse jsonFacilitiesByBoundingBox(
      @RequestParam(value = "bbox[]") List<BigDecimal> bbox,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<FacilityEntity> entities = entitiesByBoundingBox(bbox, type, services);
    PageLinker linker =
        PageLinker.builder()
            .url(baseUrl + basePath + "v0/facilities")
            .params(
                Parameters.builder()
                    .addAll("bbox[]", bbox)
                    .addIgnoreNull("type", type)
                    .addAll("services[]", services)
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
  @GetMapping(value = "/facilities", produces = "application/json", params = "ids")
  public FacilitiesResponse jsonFacilitiesByIds(
      @RequestParam(value = "ids") String ids,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<FacilityEntity> entities = entitiesByIds(ids);
    PageLinker linker =
        PageLinker.builder()
            .url(baseUrl + basePath + "v0/facilities")
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
      params = {"lat", "long"})
  public FacilitiesResponse jsonFacilitiesByLatLong(
      @RequestParam(value = "lat") BigDecimal latitude,
      @RequestParam(value = "long") BigDecimal longitude,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<DistanceEntity> entities = entitiesByLatLong(longitude, latitude, type, services);
    PageLinker linker =
        PageLinker.builder()
            .url(baseUrl + basePath + "v0/facilities")
            .params(
                Parameters.builder()
                    .add("lat", latitude)
                    .add("long", longitude)
                    .addIgnoreNull("type", type)
                    .addAll("services[]", services)
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
  @GetMapping(value = "/facilities", produces = "application/json", params = "state")
  public FacilitiesResponse jsonFacilitiesByState(
      @RequestParam(value = "state") String state,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    Page<FacilityEntity> entitiesPage =
        entitiesPageByState(state, type, services, page, Math.max(perPage, 1));
    PageLinker linker =
        PageLinker.builder()
            .url(baseUrl + basePath + "v0/facilities")
            .params(
                Parameters.builder()
                    .add("state", state)
                    .addIgnoreNull("type", type)
                    .addAll("services[]", services)
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

  /** Get facilities by zip. */
  @GetMapping(value = "/facilities", produces = "application/json", params = "zip")
  public FacilitiesResponse jsonFacilitiesByZip(
      @RequestParam(value = "zip") String zip,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    Page<FacilityEntity> entitiesPage =
        entitiesPageByZip(zip, type, services, page, Math.max(perPage, 1));
    PageLinker linker =
        PageLinker.builder()
            .url(baseUrl + basePath + "v0/facilities")
            .params(
                Parameters.builder()
                    .add("zip", zip)
                    .addIgnoreNull("type", type)
                    .addAll("services[]", services)
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

  @SuppressWarnings("unused")
  @GetMapping(params = {"lat", "lng"})
  @ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
  public NearbyResponse nearby(
      @RequestParam(name = "lat", required = true) double latitude,
      @RequestParam(name = "lng", required = true) double longitude) {
    return null;
  }

  /** Read geo facility. */
  @GetMapping(value = "/facilities/{id}", produces = "application/vnd.geo+json")
  public GeoFacilityReadResponse readGeoJson(@PathVariable("id") String id) {
    return GeoFacilityReadResponse.of(geoFacility(facility(entityById(id))));
  }

  /** Read facility. */
  @GetMapping(value = "/facilities/{id}", produces = "application/json")
  public FacilityReadResponse readJson(@PathVariable("id") String id) {
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
        facility = FacilitiesController.facility(entity);
      }
      return facility;
    }
  }
}
