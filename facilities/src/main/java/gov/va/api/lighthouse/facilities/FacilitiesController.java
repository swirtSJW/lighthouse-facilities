package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfig.quietlyMap;
import static java.util.stream.Collectors.toList;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse.Type;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import java.io.InputStream;
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

  /**
   * Get all facilities.
   *
   * @return
   */
  @GetMapping(value = {"/facilities/all"})
  public GeoFacilitiesResponse all() {
    var mapper = FacilitiesJacksonConfig.createMapper();

    return GeoFacilitiesResponse.builder()
        .type(Type.FeatureCollection)
        .features(
            StreamSupport.stream(facilityRepository.findAll().spliterator(), false)
                .map(e -> quietlyMap(mapper, e.facility(), Facility.class))
                .map(f -> GeoFacilityTransformer.builder().facility(f).build().toGeoFacility())
                .collect(toList()))
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
