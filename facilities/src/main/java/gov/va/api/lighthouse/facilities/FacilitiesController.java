package gov.va.api.lighthouse.facilities;

import static java.util.stream.Collectors.toList;

import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ServiceType;
import gov.va.api.lighthouse.facilities.api.v0.NearbyFacility;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = {"/v0"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilitiesController {

  @Autowired FacilityRepository facilityRepository;
  @Autowired DriveTimeBandRepository driveTimeBandRepository;

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

  @GetMapping(params = {"lat", "lng"})
  @ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
  public NearbyFacility nearby(
      @RequestParam(name = "lat", required = true) double latitude,
      @RequestParam(name = "lng", required = true) double longitude) {
    return null;
  }
}
