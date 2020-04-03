package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.FacilityEntity.Pk;
import gov.va.api.lighthouse.facilities.ReloadResponse.Problem;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ServiceType;
import gov.va.api.lighthouse.facilities.collectorapi.CollectorApi;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(
    value = {"/internal/management/reload"},
    produces = {"application/json"})
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Builder
@Slf4j
public class FacilityManagementController {
  private final CollectorApi collector;

  private final FacilityRepository facilityRepository;

  private final FacilityIdRepository facilityIdRepository;

  /** Populate the given record with facility data _EXCEPT_ of the PK. */
  @SneakyThrows
  static FacilityEntity populate(FacilityEntity record, Facility facility) {
    record.latitude(facility.attributes().latitude().doubleValue());
    record.longitude(facility.attributes().longitude().doubleValue());
    record.state(stateOf(facility));
    record.zip(zipOf(facility));
    record.servicesFromServiceTypes(serviceTypesOf(facility));
    record.facility(FacilitiesJacksonConfig.createMapper().writeValueAsString(facility));
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
  private void createNewEntity(ReloadResponse response, Pk pk, Facility facility) {
    updateAndSave(response, FacilityEntity.builder().id(pk).build(), facility);
  }

  private void deleteStaleFacilities(
      ReloadResponse response, CollectorFacilitiesResponse collectedFacilities) {
    Set<Pk> newIds =
        collectedFacilities.facilities().stream()
            .map(f -> FacilityEntity.Pk.optionalFromIdString(f.id()).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    Set<FacilityEntity.Pk> oldIds =
        Streams.stream(facilityIdRepository.findAll())
            .map(e -> e.id())
            .collect(Collectors.toCollection(LinkedHashSet::new));

    Set<FacilityEntity.Pk> staleIds = Sets.difference(oldIds, newIds);

    for (FacilityEntity.Pk id : staleIds) {
      response.facilitiesDeleted().add(id.toIdString());
      log.debug("Deleting facility {}", id.toIdString());
      try {
        facilityRepository.deleteById(id);
      } catch (Exception e) {
        log.error("Failed to delete facility record {}: {}", id.toIdString(), e.getMessage());
        response
            .problems()
            .add(Problem.of(id.toIdString(), "Failed to delete record: " + e.getMessage()));
        throw e;
      }
    }
  }

  /** Attempt to reload all facilities. */
  @GetMapping
  public ResponseEntity<ReloadResponse> reload() {
    var response = ReloadResponse.start();
    var collectedFacilities = collector.collectFacilities();
    response.timing().markCompleteCollection();
    log.info("Facilities collected: {}", collectedFacilities.facilities().size());
    try {
      collectedFacilities.facilities().parallelStream().forEach(f -> updateFacility(response, f));
      deleteStaleFacilities(response, collectedFacilities);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    } finally {
      response.timing().markComplete();
    }
    return ResponseEntity.ok(response);
  }

  @SneakyThrows
  private void updateAndSave(ReloadResponse response, FacilityEntity record, Facility facility) {
    populate(record, facility);
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

  private void updateFacility(ReloadResponse response, Facility facility) {
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
      log.debug("Creating new facility {}", facility.id());
      createNewEntity(response, pk, facility);
    } else {
      response.facilitiesUpdated().add(facility.id());
      log.debug("Updating old facility {}", facility.id());
      updateAndSave(response, existing.get(), facility);
    }
  }
}
