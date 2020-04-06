package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.lighthouse.facilities.Controllers.page;
import static gov.va.api.lighthouse.facilities.Controllers.validateFacilityType;
import static gov.va.api.lighthouse.facilities.Controllers.validateServices;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Validated
@RestController
@RequestMapping(value = "/v0/nearby")
public class NearbyController {
  private static final Set<Integer> DRIVE_TIME_VALUES = Set.of(10, 20, 30, 40, 50, 60, 70, 80, 90);

  private final FacilityRepository facilityRepository;

  private final DriveTimeBandRepository driveTimeBandRepository;

  private final RestTemplate restTemplate;

  private final String bingKey;

  private final String bingUrl;

  private final String linkerUrl;

  @Builder
  NearbyController(
      @Autowired FacilityRepository facilityRepository,
      @Autowired DriveTimeBandRepository driveTimeBandRepository,
      @Autowired RestTemplate restTemplate,
      @Value("${bing.key}") String bingKey,
      @Value("${bing.url}") String bingUrl,
      @Value("${facilities.url}") String baseUrl,
      @Value("${facilities.base-path}") String basePath) {
    this.facilityRepository = facilityRepository;
    this.driveTimeBandRepository = driveTimeBandRepository;
    this.restTemplate = restTemplate;
    this.bingKey = bingKey;
    this.bingUrl = bingUrl.endsWith("/") ? bingUrl : bingUrl + "/";

    String url = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    String path = basePath.replaceAll("/$", "");
    path = path.isEmpty() ? path : path + "/";
    this.linkerUrl = url + path + "v0/";
  }

  private static Path2D toPath2D(List<List<Double>> coordinates) {
    checkArgument(!coordinates.isEmpty());
    Path2D shape = null;
    for (List<Double> c : coordinates) {
      if (shape == null) {
        shape = new Path2D.Double(Path2D.WIND_NON_ZERO);
        shape.moveTo(c.get(0), c.get(1));
      } else {
        shape.lineTo(c.get(0), c.get(1));
      }
    }
    shape.closePath();
    return shape;
  }

  private static Integer validateDriveTime(Integer val) {
    if (val != null && !DRIVE_TIME_VALUES.contains(val)) {
      throw new ExceptionsV0.InvalidParameter("drive_time", val);
    }
    return val;
  }

  private Optional<DriveTimeBandEntity> firstIntersection(
      @NonNull Point2D point, List<DriveTimeBandEntity> entities) {
    for (DriveTimeBandEntity entity : entities) {
      PssgDriveTimeBand asBand = entity.asPssgDriveTimeBand();
      List<List<List<Double>>> rings = asBand.geometry().rings();
      checkState(!rings.isEmpty());
      List<List<Double>> exteriorRing = rings.get(0);
      Path2D path2D = toPath2D(exteriorRing);
      for (int i = 1; i < rings.size(); i++) {
        List<List<Double>> interiorRing = rings.get(i);
        path2D.append(toPath2D(interiorRing), false);
      }
      if (path2D.contains(point)) {
        return Optional.of(entity);
      }
    }
    return Optional.empty();
  }

  @SneakyThrows
  private Coordinates geocodeAddress(
      @NonNull String street, @NonNull String city, @NonNull String state, @NonNull String zip) {
    String address = street + " " + city + " " + state + " " + zip;
    String bingUriString =
        UriComponentsBuilder.fromHttpUrl(bingUrl + "REST/v1/Locations")
            .queryParam("q", address)
            .queryParam("key", bingKey)
            .build()
            .toUriString();

    String body;
    try {
      body =
          restTemplate
              .exchange(
                  bingUriString, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
              .getBody();
    } catch (Exception ex) {
      throw new ExceptionsV0.BingException(ex);
    }

    BingResponse response = JacksonConfig.createMapper().readValue(body, BingResponse.class);
    Optional<List<BigDecimal>> coordinates =
        response.resourceSets().stream()
            .flatMap(rs -> rs.resources().stream())
            .map(r -> r.point())
            .filter(Objects::nonNull)
            .map(p -> p.coordinates())
            .filter(c -> c.size() >= 2)
            .findFirst();

    if (coordinates.isEmpty()) {
      throw new ExceptionsV0.BingException(
          String.format(
              "Failed to geocode street_address '%s', city '%s', state '%s', zip '%s'",
              street, city, state, zip));
    }
    return Coordinates.builder()
        .latitude(coordinates.get().get(0))
        .longitude(coordinates.get().get(1))
        .build();
  }

  private Map<String, DriveTimeBandEntity> intersections(
      @NonNull BigDecimal longitude,
      @NonNull BigDecimal latitude,
      List<DriveTimeBandEntity> entities) {
    ListMultimap<String, DriveTimeBandEntity> bandsForStation = ArrayListMultimap.create();
    for (DriveTimeBandEntity e : entities) {
      bandsForStation.put(e.id().stationNumber(), e);
    }

    Point2D point = new Point2D.Double(longitude.doubleValue(), latitude.doubleValue());
    return bandsForStation
        .asMap()
        .entrySet()
        .parallelStream()
        .map(
            entry -> {
              List<DriveTimeBandEntity> sortedEntities =
                  entry.getValue().stream()
                      .sorted(
                          (left, right) ->
                              Integer.compare(left.id().fromMinutes(), right.id().fromMinutes()))
                      .collect(toList());
              return firstIntersection(point, sortedEntities).orElse(null);
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(b -> b.id().stationNumber(), Function.identity()));
  }

  /** Nearby facilities by address. */
  @GetMapping(
      produces = "application/json",
      params = {"street_address", "city", "state", "zip"})
  public NearbyResponse nearbyAddress(
      @RequestParam(value = "street_address") String street,
      @RequestParam(value = "city") String city,
      @RequestParam(value = "state") String state,
      @RequestParam(value = "zip") String zip,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "drive_time", required = false) Integer maxDriveTime,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "20") @Min(0) int perPage) {
    Coordinates coor = geocodeAddress(street, city, state, zip);
    List<NearbyId> ids = nearbyIds(coor.longitude(), coor.latitude(), type, services, maxDriveTime);
    MultiValueMap<String, String> parameters =
        Parameters.builder()
            .add("street_address", street)
            .add("city", city)
            .add("state", state)
            .add("zip", zip)
            .addIgnoreNull("type", type)
            .addAll("services[]", services)
            .addIgnoreNull("drive_time", maxDriveTime)
            .add("page", page)
            .add("per_page", perPage)
            .build();
    PageLinker linker =
        PageLinker.builder()
            .url(linkerUrl + "nearby")
            .params(parameters)
            .totalEntries(ids.size())
            .build();
    List<NearbyId> idsPage = page(ids, page, perPage);
    return NearbyResponse.builder()
        .data(idsPage.stream().map(e -> nearbyFacility(e)).collect(toList()))
        .links(linker.links().toBuilder().related(nearbyRelatedLink(ids)).build())
        .meta(NearbyResponse.NearbyMetadata.builder().pagination(linker.pagination()).build())
        .build();
  }

  private NearbyResponse.Nearby nearbyFacility(@NonNull NearbyId entity) {
    return NearbyResponse.Nearby.builder()
        .id(entity.facilityId())
        .type(NearbyResponse.Type.NearbyFacility)
        .attributes(
            NearbyResponse.NearbyAttributes.builder()
                .minTime(entity.bandId().fromMinutes())
                .maxTime(entity.bandId().toMinutes())
                .build())
        .relationships(
            NearbyResponse.Relationships.builder()
                .vaFacility(
                    NearbyResponse.VaFacility.builder()
                        .links(
                            NearbyResponse.Links.builder()
                                .related(linkerUrl + "facilities/" + entity.facilityId())
                                .build())
                        .build())
                .build())
        .build();
  }

  @SneakyThrows
  private List<NearbyId> nearbyIds(
      @NonNull BigDecimal longitude,
      @NonNull BigDecimal latitude,
      String rawType,
      List<String> rawServices,
      Integer rawMaxDriveTime) {
    FacilityEntity.Type facilityType = validateFacilityType(rawType);
    Set<Facility.ServiceType> services = validateServices(rawServices);
    Integer maxDriveTime = validateDriveTime(rawMaxDriveTime);
    List<DriveTimeBandEntity> maybeBands =
        driveTimeBandRepository.findAll(
            DriveTimeBandRepository.MinMaxSpecification.builder()
                .longitude(longitude)
                .latitude(latitude)
                .maxDriveTime(maxDriveTime)
                .build());
    Map<String, DriveTimeBandEntity> bandsByStation =
        intersections(longitude, latitude, maybeBands);
    List<FacilityEntity> facilityEntities =
        facilityRepository.findAll(
            FacilityRepository.StationNumbersSpecification.builder()
                .stationNumbers(bandsByStation.keySet())
                .facilityType(facilityType)
                .services(services)
                .build());
    return facilityEntities.stream()
        .map(
            e ->
                NearbyId.builder()
                    .bandId(bandsByStation.get(e.id().stationNumber()).id())
                    .facilityId(e.id().toIdString())
                    .build())
        .sorted(
            (left, right) -> Integer.compare(left.bandId().toMinutes(), right.bandId().toMinutes()))
        .collect(toList());
  }

  /** Nearby facilities by coordinates. */
  @GetMapping(
      produces = "application/json",
      params = {"lat", "lng"})
  public NearbyResponse nearbyLatLong(
      @RequestParam(value = "lat") BigDecimal latitude,
      @RequestParam(value = "lng") BigDecimal longitude,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "drive_time", required = false) Integer maxDriveTime,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "20") @Min(0) int perPage) {
    List<NearbyId> ids = nearbyIds(longitude, latitude, type, services, maxDriveTime);
    MultiValueMap<String, String> parameters =
        Parameters.builder()
            .add("lat", latitude)
            .add("lng", longitude)
            .addIgnoreNull("type", type)
            .addAll("services[]", services)
            .addIgnoreNull("drive_time", maxDriveTime)
            .add("page", page)
            .add("per_page", perPage)
            .build();
    PageLinker linker =
        PageLinker.builder()
            .url(linkerUrl + "nearby")
            .params(parameters)
            .totalEntries(ids.size())
            .build();
    List<NearbyId> idsPage = page(ids, page, perPage);
    return NearbyResponse.builder()
        .data(idsPage.stream().map(e -> nearbyFacility(e)).collect(toList()))
        .links(linker.links().toBuilder().related(nearbyRelatedLink(idsPage)).build())
        .meta(NearbyResponse.NearbyMetadata.builder().pagination(linker.pagination()).build())
        .build();
  }

  private String nearbyRelatedLink(List<NearbyId> ids) {
    return isEmpty(ids)
        ? null
        : linkerUrl
            + "facilities?ids="
            + ids.stream().map(e -> e.facilityId()).collect(Collectors.joining(","));
  }

  @Builder
  @lombok.Value
  private static final class Coordinates {
    BigDecimal latitude;

    BigDecimal longitude;
  }

  @Builder
  @lombok.Value
  private static final class NearbyId {
    DriveTimeBandEntity.Pk bandId;

    String facilityId;
  }
}
