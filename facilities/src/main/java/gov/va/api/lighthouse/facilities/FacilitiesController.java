package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfig.quietlyMap;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.google.common.base.Splitter;
import gov.va.api.lighthouse.facilities.FacilityEntity.Pk;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse.Type;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  private final FacilityRepository facilityRepository;

  @SuppressWarnings("unused")
  private final DriveTimeBandRepository driveTimeBandRepository;

  private final String baseUrl;

  FacilitiesController(
      @Autowired FacilityRepository facilityRepository,
      @Autowired DriveTimeBandRepository driveTimeBandRepository,
      @Value("${facilities.url}") String baseUrl) {
    this.facilityRepository = facilityRepository;
    this.driveTimeBandRepository = driveTimeBandRepository;
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
  }

  @SneakyThrows
  private static Facility facility(FacilityEntity entity) {
    return FacilitiesJacksonConfig.createMapper().readValue(entity.facility(), Facility.class);
  }

  private static GeoFacility geoFacility(Facility facility) {
    return GeoFacilityTransformer.builder().facility(facility).build().toGeoFacility();
  }

  private static List<FacilityEntity> page(List<FacilityEntity> entities, int page, int perPage) {
    checkArgument(page >= 1);
    checkArgument(perPage >= 0);
    if (perPage == 0) {
      return emptyList();
    }
    int fromIndex = (page - 1) * perPage;
    if (entities.size() < fromIndex) {
      return emptyList();
    }
    return entities.subList(fromIndex, Math.min(fromIndex + perPage, entities.size()));
  }

  /** Get all facilities. */
  @GetMapping(value = {"/facilities/all"})
  public GeoFacilitiesResponse all() {
    var mapper = FacilitiesJacksonConfig.createMapper();
    return GeoFacilitiesResponse.builder()
        .type(Type.FeatureCollection)
        .features(
            StreamSupport.stream(facilityRepository.findAll().spliterator(), false)
                .map(e -> quietlyMap(mapper, e.facility(), Facility.class))
                .map(f -> geoFacility(f))
                .collect(toList()))
        .build();
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
            .collect(Collectors.toMap(e -> e.id(), Function.identity()));
    return pks.stream().map(pk -> entities.get(pk)).filter(Objects::nonNull).collect(toList());
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

  /** Get facilities by IDs. */
  @GetMapping(value = "/facilities", produces = "application/json", params = "ids")
  public FacilitiesResponse jsonFacilitiesByIds(
      @RequestParam(value = "ids") String ids,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<FacilityEntity> entities = entitiesByIds(ids);
    PageLinker linker =
        PageLinker.builder()
            .url(baseUrl + "v0/facilities")
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
        .meta(FacilitiesResponse.Metadata.builder().pagination(linker.pagination()).build())
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

  private Facility read(String id) {
    Pk pk = null;
    try {
      pk = FacilityEntity.Pk.fromIdString(id);
    } catch (IllegalArgumentException ex) {
      throw new ExceptionsV0.NotFound(id, ex);
    }
    Optional<FacilityEntity> opt = facilityRepository.findById(pk);
    if (opt.isEmpty()) {
      throw new ExceptionsV0.NotFound(id);
    }
    return facility(opt.get());
  }

  /** Read geo facility. */
  @GetMapping(value = "/facilities/{id}", produces = "application/vnd.geo+json")
  public GeoFacilityReadResponse readGeoJson(@PathVariable("id") String id) {
    return GeoFacilityReadResponse.of(geoFacility(read(id)));
  }

  /** Read facility. */
  @GetMapping(value = "/facilities/{id}", produces = "application/json")
  public FacilityReadResponse readJson(@PathVariable("id") String id) {
    return FacilityReadResponse.builder().facility(read(id)).build();
  }
}
