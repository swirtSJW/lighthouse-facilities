package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.ControllersV1.*;
import static gov.va.api.lighthouse.facilities.FacilityUtils.distance;
import static gov.va.api.lighthouse.facilities.FacilityUtils.haversine;
import static java.util.stream.Collectors.toList;

import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesIdsResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
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
  private static final FacilityOverlayV1 FACILITY_OVERLAY = FacilityOverlayV1.builder().build();

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

  /** Get all facilities as CSV. */
  @SneakyThrows
  @GetMapping(value = "/facilities", produces = "text/csv")
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

  private FacilityEntity entityById(String id) {
    FacilityEntity.Pk pk = null;
    try {
      pk = FacilityEntity.Pk.fromIdString(id);
    } catch (IllegalArgumentException ex) {
      throw new ExceptionsUtils.NotFound(id, ex);
    }
    Optional<FacilityEntity> opt = facilityRepository.findById(pk);
    if (opt.isEmpty()) {
      throw new ExceptionsUtils.NotFound(id);
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

  private List<FacilityEntity> filterByBoundingBox(
      List<BigDecimal> bbox, List<FacilityEntity> allEntities) {
    double centerLng = (bbox.get(0).doubleValue() + bbox.get(2).doubleValue()) / 2;
    double centerLat = (bbox.get(1).doubleValue() + bbox.get(3).doubleValue()) / 2;
    return allEntities.stream()
        .sorted(
            (left, right) ->
                Double.compare(
                    distance(left, centerLng, centerLat), distance(right, centerLng, centerLat)))
        .collect(toList());
  }

  @SneakyThrows
  private List<DistanceEntity> filterByLatLong(
          BigDecimal latitude, BigDecimal longitude, BigDecimal rad, List<FacilityEntity> entities) {
    double lng = longitude.doubleValue();
    double lat = latitude.doubleValue();
    Optional<BigDecimal> radius = Optional.ofNullable(rad);
    return entities.stream()
        .map(
            e ->
                DistanceEntity.builder()
                    .entity(e)
                    .distance(BigDecimal.valueOf(haversine(e, lng, lat)))
                    .build())
        .filter(
            radius.isPresent()
                ? de -> radius.get().compareTo(de.distance().abs()) >= 0
                : de -> true)
        .sorted((left, right) -> left.distance().compareTo(right.distance()))
        .collect(toList());
  }

  @SneakyThrows
  @GetMapping(
      value = "/facilities",
      produces = {"application/json"})
  FacilitiesResponse jsonFacilities(
      @RequestParam(value = "bbox[]", required = false) List<BigDecimal> bbox,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "zip", required = false) String zip,
      @RequestParam(value = "type", required = false) String rawType,
      @RequestParam(value = "lat", required = false) BigDecimal latitude,
      @RequestParam(value = "long", required = false) BigDecimal longitude,
      @RequestParam(value = "radius", required = false) BigDecimal radius,
      @RequestParam(value = "ids", required = false) String ids,
      @RequestParam(value = "services[]", required = false) List<String> rawServices,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "visn", required = false) String visn,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    Specification<FacilityEntity> spec = validateBoundingBox(bbox, null);
    spec = validateState(state, spec);
    spec = validateZip(zip, spec);
    spec = validateFacilityType(rawType, spec);
    spec = validateIds(ids, spec);
    spec = validateServices(rawServices, spec);
    spec = validateMobile(mobile, spec);
    spec = validateVisn(visn, spec);
    validateLatLong(latitude, longitude, radius);
    List<FacilityEntity> entities = spec == null ?
            StreamSupport.stream(facilityRepository.findAll().spliterator(), false).collect(Collectors.toList()) :
            facilityRepository.findAll(spec);
    List<FacilitiesResponse.Distance> distances = null;
    if (bbox == null && latitude != null && longitude != null) {
      List<DistanceEntity> filteredEntities = filterByLatLong(latitude, longitude, radius, entities);
      entities = filteredEntities.stream().map(DistanceEntity::entity).collect(toList());
      List<DistanceEntity> entitiesPage =
          page(filteredEntities, page, perPage);
      distances =
          entitiesPage.stream()
              .map(
                  e ->
                      FacilitiesResponse.Distance.builder()
                          .id(e.facility().id())
                          .distance(e.distance().setScale(2, RoundingMode.HALF_EVEN))
                          .build())
              .collect(toList());
    }else if (bbox != null && latitude == null && longitude == null) {
      entities = filterByBoundingBox(bbox, entities);
    }else{
      entities.sort(Comparator.comparing(e -> e.id().toIdString()));
    }
    PageLinkerV1 linker =
        PageLinkerV1.builder()
            .url(linkerUrl + "facilities")
            .params(
                Parameters.builder()
                    .addAll("bbox[]", bbox)
                    .addIgnoreNull("state", state)
                    .addIgnoreNull("zip", zip)
                    .addIgnoreNull("type", rawType)
                    .addIgnoreNull("lat", latitude)
                    .addIgnoreNull("long", longitude)
                    .addIgnoreNull("radius", radius)
                    .addIgnoreNull("ids", ids)
                    .addAll("services[]", rawServices)
                    .addIgnoreNull("mobile", mobile)
                    .addIgnoreNull("visn", visn)
                    .addIgnoreNull("page", page)
                    .addIgnoreNull("per_page", perPage)
                    .build())
            .totalEntries(entities.size())
            .build();

    return FacilitiesResponse.builder()
            .data(page(entities, page, perPage).stream().map(e -> facility(e)).collect(toList()))
            .links(linker.links())
            .meta(
                FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(linker.pagination())
                        .distances(distances)
                        .build())
            .build();
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
