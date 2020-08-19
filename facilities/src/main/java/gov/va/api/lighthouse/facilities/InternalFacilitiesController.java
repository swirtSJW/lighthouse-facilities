package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.collector.FacilitiesCollector;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

@Slf4j
@Builder
@Validated
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/internal/management", produces = "application/json")
public class InternalFacilitiesController {
  private static final ObjectMapper MAPPER = FacilitiesJacksonConfig.createMapper();

  private final FacilitiesCollector collector;

  private final FacilityRepository facilityRepository;

  private final FacilityGraveyardRepository graveyardRepository;

  /** Populate the given record with facility data _EXCEPT_ of the PK. */
  @SneakyThrows
  static FacilityEntity populate(FacilityEntity record, Facility facility) {
    checkArgument(record.id() != null);
    record.latitude(facility.attributes().latitude().doubleValue());
    record.longitude(facility.attributes().longitude().doubleValue());
    record.state(stateOf(facility));
    record.zip(zipOf(facility));
    record.servicesFromServiceTypes(serviceTypesOf(facility));
    record.facility(MAPPER.writeValueAsString(facility));
    return record;
  }

  /**
   * Determine the total collection of service types by combining health, benefits, and other
   * services types. This is guaranteed to return a non-null, but potentially empty collection.
   */
  static Set<Facility.ServiceType> serviceTypesOf(Facility facility) {
    var services = facility.attributes().services();
    if (services == null) {
      return Set.of();
    }
    var allServices = new HashSet<Facility.ServiceType>();
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

  @DeleteMapping(value = "/facilities/{id}/cms-overlay")
  ResponseEntity<Void> deleteCmsOverlayById(@PathVariable("id") String id) {
    Optional<FacilityEntity> entity = entityById(id);
    if (entity.isEmpty()) {
      log.info("Facility {} does not exist, ignoring request.", sanitize(id));
      return ResponseEntity.accepted().build();
    }
    log.info("Removing cmsOverlay from facility {}", sanitize(id));
    facilityRepository.save(entity.get().cmsOverlay(null));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(value = "/facilities/{id}")
  ResponseEntity<String> deleteFacilityById(@PathVariable("id") String id) {
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

  void deleteFromGraveyard(ReloadResponse response, FacilityGraveyardEntity entity) {
    try {
      graveyardRepository.delete(entity);
    } catch (Exception e) {
      log.error(
          "Failed to delete facility {} from graveyard: {}",
          entity.id().toIdString(),
          e.getMessage());
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  entity.id().toIdString(),
                  "Failed to delete facility from graveyard: " + e.getMessage()));
      throw e;
    }
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

  @GetMapping("/graveyard")
  GraveyardResponse graveyardAll() {
    return GraveyardResponse.builder()
        .facilities(
            Streams.stream(graveyardRepository.findAll())
                .map(
                    z ->
                        GraveyardResponse.Item.builder()
                            .facility(
                                FacilitiesJacksonConfig.quietlyMap(
                                    MAPPER, z.facility(), Facility.class))
                            .cmsOverlay(
                                z.cmsOverlay() == null
                                    ? null
                                    : FacilitiesJacksonConfig.quietlyMap(
                                        MAPPER, z.cmsOverlay(), CmsOverlay.class))
                            .missing(
                                z.missingTimestamp() == null
                                    ? null
                                    : Instant.ofEpochMilli(z.missingTimestamp()))
                            .lastUpdated(z.lastUpdated())
                            .build())
                .collect(toList()))
        .build();
  }

  private Set<FacilityEntity.Pk> missingIds(List<Facility> collectedFacilities) {
    Set<FacilityEntity.Pk> newIds =
        collectedFacilities.stream()
            .map(f -> FacilityEntity.Pk.optionalFromIdString(f.id()).orElse(null))
            .filter(Objects::nonNull)
            .collect(toCollection(LinkedHashSet::new));
    Set<FacilityEntity.Pk> oldIds = new LinkedHashSet<>(facilityRepository.findAllIds());
    return ImmutableSet.copyOf(Sets.difference(oldIds, newIds));
  }

  private void moveToGraveyard(ReloadResponse response, FacilityEntity entity) {
    FacilityEntity.Pk id = FacilityEntity.Pk.of(entity.id().type(), entity.id().stationNumber());
    try {
      Instant now = response.timing().completeCollection();
      response.facilitiesRemoved().add(id.toIdString());
      log.warn("Moving facility {} to graveyard.", id.toIdString());
      graveyardRepository.save(
          FacilityGraveyardEntity.builder()
              .id(id)
              .facility(entity.facility())
              .cmsOverlay(entity.cmsOverlay())
              .missingTimestamp(entity.missingTimestamp())
              .lastUpdated(now)
              .build());
      facilityRepository.delete(entity);
    } catch (Exception e) {
      log.error("Failed to move facility {} to graveyard: {}", id.toIdString(), e.getMessage());
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  id.toIdString(), "Failed to move facility to graveyard: " + e.getMessage()));
      throw e;
    }
  }

  private ResponseEntity<ReloadResponse> process(
      ReloadResponse response, List<Facility> collectedFacilities) {
    response.timing().markCompleteCollection();
    log.info("Facilities collected: {}", collectedFacilities.size());
    try {
      collectedFacilities.parallelStream().forEach(f -> updateFacility(response, f));
      for (FacilityEntity.Pk missingId : missingIds(collectedFacilities)) {
        processMissingFacility(response, missingId);
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    } finally {
      response.timing().markComplete();
    }
    return ResponseEntity.ok(response);
  }

  private void processMissingFacility(ReloadResponse response, FacilityEntity.Pk id) {
    Optional<FacilityEntity> optEntity = facilityRepository.findById(id);
    checkState(optEntity.isPresent());
    FacilityEntity entity = optEntity.get();
    Instant now = response.timing().completeCollection();
    if (entity.missingTimestamp() == null) {
      entity.missingTimestamp(now.toEpochMilli());
    }
    if (now.toEpochMilli() - entity.missingTimestamp() <= TimeUnit.HOURS.toMillis(24)) {
      saveAsMissing(response, entity);
      return;
    }
    moveToGraveyard(response, entity);
  }

  @GetMapping(value = "/reload")
  ResponseEntity<ReloadResponse> reload() {
    var response = ReloadResponse.start();
    var collectedFacilities = collector.collectFacilities();
    response.totalFacilities(collectedFacilities.size());
    return process(response, collectedFacilities);
  }

  private void saveAsMissing(ReloadResponse response, FacilityEntity entity) {
    FacilityEntity.Pk id = entity.id();
    try {
      response.facilitiesMissing().add(id.toIdString());
      log.warn("Marking facility {} as missing.", id.toIdString());
      facilityRepository.save(entity);
      return;
    } catch (Exception e) {
      log.error("Failed to mark facility {} as missing: {}", id.toIdString(), e.getMessage());
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  id.toIdString(), "Failed to mark facility as missing: " + e.getMessage()));
      throw e;
    }
  }

  @SneakyThrows
  void updateAndSave(ReloadResponse response, FacilityEntity record, Facility facility) {
    populate(record, facility);
    record.missingTimestamp(null);
    record.lastUpdated(response.timing().completeCollection());

    /*
     * Determine if there is something wrong with the record, but it is still usable.
     */
    if (isBlank(record.zip())) {
      response.problems().add(ReloadResponse.Problem.of(facility.id(), "Missing zip"));
    }
    if (isBlank(record.state())) {
      response.problems().add(ReloadResponse.Problem.of(facility.id(), "Missing state"));
    }

    try {
      facilityRepository.save(record);
    } catch (Exception e) {
      log.error("Failed to save facility record {}: {}", record.id(), e.getMessage());
      log.error("{}", record);
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(facility.id(), "Failed to save record: " + e.getMessage()));
      throw e;
    }
  }

  private void updateFacility(ReloadResponse response, Facility facility) {
    FacilityEntity.Pk pk;
    try {
      pk = FacilityEntity.Pk.fromIdString(facility.id());
    } catch (IllegalArgumentException e) {
      log.error("Cannot process facility {}, ID not understood", facility.id(), e);
      response.problems().add(ReloadResponse.Problem.of(facility.id(), "Cannot parse ID"));
      return;
    }

    var existing = facilityRepository.findById(pk);
    if (existing.isPresent()) {
      response.facilitiesUpdated().add(facility.id());
      log.warn("Updating facility {}", facility.id());
      updateAndSave(response, existing.get(), facility);
      return;
    }

    var zombie = graveyardRepository.findById(pk);
    if (zombie.isPresent()) {
      response.facilitiesRevived().add(facility.id());
      log.warn("Reviving facility {}", facility.id());
      FacilityGraveyardEntity zombieEntity = zombie.get();
      // only thing to retain from graveyard is CMS overlay
      // all other fields will be populated in updateAndSave()
      FacilityEntity facilityEntity =
          FacilityEntity.builder().id(pk).cmsOverlay(zombieEntity.cmsOverlay()).build();
      updateAndSave(response, facilityEntity, facility);
      deleteFromGraveyard(response, zombieEntity);
      return;
    }
    response.facilitiesCreated().add(facility.id());
    log.warn("Creating new facility {}", facility.id());
    updateAndSave(response, FacilityEntity.builder().id(pk).build(), facility);
  }

  @PostMapping(value = "/reload")
  @Loggable(arguments = false)
  ResponseEntity<ReloadResponse> upload(@RequestBody List<Facility> collectedFacilities) {
    var response = ReloadResponse.start();
    return process(response, collectedFacilities);
  }
}
