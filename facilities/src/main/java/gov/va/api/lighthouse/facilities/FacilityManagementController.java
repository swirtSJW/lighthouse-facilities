package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Sets;
import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.facilities.ReloadResponse.Problem;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ServiceType;
import gov.va.api.lighthouse.facilities.collectorapi.CollectorApi;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(
    value = {"/internal/management"},
    produces = {"application/json"})
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Builder
@Slf4j
public class FacilityManagementController {
  private final CollectorApi collector;

  private final FacilityRepository facilityRepository;

  /** Populate the given record with facility data _EXCEPT_ of the PK. */
  @SneakyThrows
  static FacilityEntity populate(FacilityEntity record, Instant time, Facility facility) {
    record.latitude(facility.attributes().latitude().doubleValue());
    record.longitude(facility.attributes().longitude().doubleValue());
    record.state(stateOf(facility));
    record.zip(zipOf(facility));
    record.servicesFromServiceTypes(serviceTypesOf(facility));
    record.facility(FacilitiesJacksonConfig.createMapper().writeValueAsString(facility));
    record.missingTimestamp(null);
    record.lastUpdated(time);
    return record;
  }

  /**
   * Determine the total collection of service types by combining health, benefits, and other
   * services types. This is guaranteed to return a non-null, but potentially empty collection.
   */
  static Set<ServiceType> serviceTypesOf(Facility facility) {
    var services = facility.attributes().services();
    if (services == null) {
      return Set.of();
    }
    var allServices = new HashSet<ServiceType>();
    if (services.health() != null) {
      allServices.addAll(services.health());
    }
    if (services.benefits() != null) {
      allServices.addAll(services.benefits());
    }
    if (services.other() != null) {
      allServices.addAll(services.other());
    }
    return allServices;
  }

  /** Determine the state if available in a physical address, otherwise return null. */
  static String stateOf(Facility facility) {
    if (facility.attributes().address() != null
        && facility.attributes().address().physical() != null
        && isNotBlank(facility.attributes().address().physical().state())) {
      return facility.attributes().address().physical().state();
    }
    return null;
  }

  /** Determine the 5 digit zip if available in a physical address, otherwise return null. */
  static String zipOf(Facility facility) {
    if (facility.attributes().address() != null
        && facility.attributes().address().physical() != null
        && isNotBlank(facility.attributes().address().physical().zip())
        && facility.attributes().address().physical().zip().length() >= 5) {
      /* We only store the destination portion of the zip code, we do not store the route. */
      return facility.attributes().address().physical().zip().substring(0, 5);
    }
    return null;
  }

  @SneakyThrows
  private void createNewEntity(
      ReloadResponse response, Instant time, FacilityEntity.Pk pk, Facility facility) {
    updateAndSave(response, time, FacilityEntity.builder().id(pk).build(), facility);
  }

  /** Delete the cmsOverlay column from a facility entity by id. */
  @DeleteMapping(value = "/facilities/{id}/cms-overlay")
  public ResponseEntity<Void> deleteCmsOverlayById(@PathVariable("id") String id) {
    Optional<FacilityEntity> entity = entityById(id);
    if (entity.isEmpty()) {
      log.info("Facility {} does not exist, ignoring request.", sanitize(id));
      return ResponseEntity.accepted().build();
    }
    log.info("Removing cmsOverlay from facility {}", sanitize(id));
    facilityRepository.save(entity.get().cmsOverlay(null));
    return ResponseEntity.ok().build();
  }

  /** Delete a facility by id unless it has a cmsOverlay. */
  @DeleteMapping(value = "/facilities/{id}")
  public ResponseEntity<String> deleteFacilityById(@PathVariable("id") String id) {
    Optional<FacilityEntity> entity = entityById(id);
    if (entity.isEmpty()) {
      log.info("Facility {} does not exist, ignoring request.", sanitize(id));
      return ResponseEntity.accepted().build();
    }

    if (entity.get().cmsOverlay() != null) {
      log.info("Failed to delete facility {}. cmsOverlay is not null.", sanitize(id));
      return ResponseEntity.status(409)
          .body("{\"message\":\"CMS Overlay must be deleted first.\"}");
    }

    log.info("Deleting facility {}", sanitize(id));
    facilityRepository.delete(entity.get());
    return ResponseEntity.ok().build();
  }

  private Optional<FacilityEntity> entityById(String id) {
    FacilityEntity.Pk pk = null;
    try {
      pk = FacilityEntity.Pk.fromIdString(id);
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
    return facilityRepository.findById(pk);
  }

  private ResponseEntity<ReloadResponse> process(
      ReloadResponse response, CollectorFacilitiesResponse collectedFacilities) {
    response.timing().markCompleteCollection();
    log.info("Facilities collected: {}", collectedFacilities.facilities().size());
    try {
      Instant now = Instant.now();
      collectedFacilities
          .facilities()
          .parallelStream()
          .forEach(f -> updateFacility(response, now, f));
      processMissingFacilities(response, now, collectedFacilities);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    } finally {
      response.timing().markComplete();
    }
    return ResponseEntity.ok(response);
  }

  private void processMissingFacilities(
      ReloadResponse response, Instant time, CollectorFacilitiesResponse collectedFacilities) {
    Set<FacilityEntity.Pk> newIds =
        collectedFacilities.facilities().stream()
            .map(f -> FacilityEntity.Pk.optionalFromIdString(f.id()).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    Set<FacilityEntity.Pk> oldIds = new LinkedHashSet<>(facilityRepository.findAllIds());
    Set<FacilityEntity.Pk> missingIds = Sets.difference(oldIds, newIds);
    for (FacilityEntity.Pk missing : missingIds) {
      log.debug("Marking facility {} as missing.", missing.toIdString());
      try {
        FacilityEntity entity = facilityRepository.findById(missing).get();
        if (entity.missingTimestamp() == null) {
          entity.missingTimestamp(time.toEpochMilli());
        }
        facilityRepository.save(entity);
        response
            .facilitiesMissing()
            .put(missing.toIdString(), Instant.ofEpochMilli(entity.missingTimestamp()));
      } catch (Exception e) {
        log.error(
            "Failed to mark facility record {} as missing: {}",
            missing.toIdString(),
            e.getMessage());
        response
            .problems()
            .add(
                Problem.of(
                    missing.toIdString(), "Failed to mark record as missing: " + e.getMessage()));
        throw e;
      }
    }
  }

  /** Attempt to reload all facilities. */
  @GetMapping(value = "/reload")
  public ResponseEntity<ReloadResponse> reload() {
    var collectedFacilities = collector.collectFacilities();
    var response = ReloadResponse.start().totalFacilities(collectedFacilities.facilities().size());
    return process(response, collectedFacilities);
  }

  @SneakyThrows
  private void updateAndSave(
      ReloadResponse response, Instant time, FacilityEntity record, Facility facility) {
    populate(record, time, facility);
    /*
     * Determine if there is something wrong with the record, but it is still usable.
     */
    if (isBlank(record.zip())) {
      response.problems().add(Problem.of(facility.id(), "Missing zip"));
    }
    if (isBlank(record.state())) {
      response.problems().add(Problem.of(facility.id(), "Missing state"));
    }
    try {
      facilityRepository.save(record);
    } catch (Exception e) {
      log.error("Failed to save facility record {}: {}", record.id(), e.getMessage());
      log.error("{}", record);
      response
          .problems()
          .add(Problem.of(facility.id(), "Failed to save record: " + e.getMessage()));
      throw e;
    }
  }

  private void updateFacility(ReloadResponse response, Instant time, Facility facility) {
    FacilityEntity.Pk pk;
    try {
      pk = FacilityEntity.Pk.fromIdString(facility.id());
    } catch (IllegalArgumentException e) {
      log.error("Cannot process facility {}, ID not understood", facility.id(), e);
      response.problems().add(Problem.of(facility.id(), "Cannot parse ID"));
      return;
    }
    var existing = facilityRepository.findById(pk);
    if (existing.isEmpty()) {
      response.facilitiesCreated().add(facility.id());
      log.warn("Creating new facility {}", facility.id());
      createNewEntity(response, time, pk, facility);
    } else {
      response.facilitiesUpdated().add(facility.id());
      log.warn("Updating old facility {}", facility.id());
      updateAndSave(response, time, existing.get(), facility);
    }
  }

  /** Force feed a collector response. */
  @PostMapping(value = "/reload")
  @Loggable(arguments = false)
  public ResponseEntity<ReloadResponse> upload(
      @RequestBody CollectorFacilitiesResponse collectedFacilities) {
    var response = ReloadResponse.start();
    return process(response, collectedFacilities);
  }
}
