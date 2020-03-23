package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static java.util.stream.Collectors.toList;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ServiceType;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = {"/v0"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilitiesController {
  @Autowired FacilityRepository facilityRepository;

  @Autowired DriveTimeBandRepository driveTimeBandRepository;

  @SneakyThrows
  private static Facility readHardcoded(String id) {
    if ("vha_666".equalsIgnoreCase(id)) {
      try (InputStream in = new ClassPathResource("facility-hardcoded.json").getInputStream()) {
        return JacksonConfig.createMapper().readValue(in, Facility.class);
      }
    }
    throw new ExceptionsV0.NotFound(id);
  }

  /** Get all facilities. */
  @GetMapping(value = {"/facilities/all"})
  public List<String> all() {
    /* TODO return an actual list of objects. */
    return StreamSupport.stream(facilityRepository.findAll().spliterator(), false)
        .map(FacilityEntity::facility)
        .collect(toList());
  }

  /** Temporary method. */
  @GetMapping(value = {"/garbage/{sn}"})
  public void makeSomeGarbage(@PathVariable(name = "sn") String stationNumber) {
    // TODO REMOVE ME
    Random r = new SecureRandom();
    HealthService[] healthyBois = HealthService.values();
    Set<ServiceType> services = new HashSet<>();
    services.add(healthyBois[r.nextInt(healthyBois.length)]);
    services.add(healthyBois[r.nextInt(healthyBois.length)]);
    FacilityEntity hotGarbage =
        FacilityEntity.typeSafeBuilder()
            .id(FacilityEntity.Pk.of(FacilityEntity.Type.vha, stationNumber))
            .state(r.nextBoolean() ? "FL" : "South")
            .zip("3290" + r.nextInt(10))
            .longitude(r.nextDouble())
            .latitude(r.nextDouble())
            .servicesTypes(services)
            .facility("{\"stationNumber\":\"" + stationNumber + "\",\"gargbage\":\"hot\"}")
            .build();
    facilityRepository.save(hotGarbage);
    var moreGarbage =
        DriveTimeBandEntity.builder()
            .id(DriveTimeBandEntity.Pk.of(stationNumber, 0, 10))
            .minLongitude(r.nextDouble())
            .minLatitude(r.nextDouble())
            .maxLongitude(r.nextDouble())
            .maxLatitude(r.nextDouble())
            .band("{\"stationNumber\":\"" + stationNumber + "\",\"gargbage\":\"hot\"}")
            .build();
    driveTimeBandRepository.save(moreGarbage);
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
  @SneakyThrows
  @GetMapping(value = "facilities/{id}", produces = "application/vnd.geo+json")
  public GeoFacilityReadResponse readGeoJson(@PathVariable("id") String id) {
    log.info("Read geo+json facility {}", sanitize(id));
    GeoFacility geo =
        GeoFacilityTransformer.builder().facility(readHardcoded(id)).build().toGeoFacility();
    return GeoFacilityReadResponse.builder()
        .type(geo.type())
        .geometry(geo.geometry())
        .properties(geo.properties())
        .build();
  }

  /** Read facility. */
  @SneakyThrows
  @GetMapping(value = "facilities/{id}", produces = "application/json")
  public FacilityReadResponse readJson(@PathVariable("id") String id) {
    log.info("Read facility {}", sanitize(id));
    return FacilityReadResponse.builder().facility(readHardcoded(id)).build();
  }
}
