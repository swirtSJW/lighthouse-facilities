package gov.va.api.lighthouse.facilities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(produces = "application/json")
public class FacilitiesHealthController {
  private final AtomicBoolean hasCachedRecently = new AtomicBoolean(false);

  FacilityRepository repository;

  FacilitiesHealthController(@Autowired FacilityRepository repository) {
    this.repository = repository;
  }

  /**
   * Builds an acutator health object that already contains the fields common to all health checks.
   */
  private Health buildHealth(String name, Boolean isUp, Map<String, Object> details) {
    return Health.status(new Status(isUp ? "UP" : "DOWN", name))
        .withDetail("name", name)
        .withDetails(details)
        .withDetail("time", Instant.now())
        .build();
  }

  /** Clears the health cache every 5 minutes. */
  @Scheduled(cron = "0 */5 * * * *")
  @CacheEvict(value = "health")
  public void clearCacheScheduler() {
    if (hasCachedRecently.getAndSet(false)) {
      log.info("Clearing downstream health cache.");
    }
  }

  /**
   * The entry point for the FacilitiesHealthController.
   *
   * @return A list of healthy/unhealthy downstream services (with matching statusCode).
   */
  @GetMapping("/collection/status")
  @Cacheable("health")
  public ResponseEntity<Health> collectionStatus() {
    hasCachedRecently.set(true);
    Health collectionStatus = facilitiesCollectionStatus();
    log.info(collectionStatus.toString());
    HttpStatus healthStatus =
        collectionStatus.getStatus().equals(Status.UP)
            ? HttpStatus.OK
            : HttpStatus.SERVICE_UNAVAILABLE;
    return ResponseEntity.status(healthStatus).body(collectionStatus);
  }

  private Health facilitiesCollectionStatus() {
    Instant lastSuccessfulUpdate = repository.findLastUpdated();
    Instant oldestValidDate = Instant.now().minus(24, ChronoUnit.HOURS);
    Boolean isOutOfDate =
        lastSuccessfulUpdate == null || oldestValidDate.isAfter(lastSuccessfulUpdate);
    return buildHealth(
        "Facilities Collection", !isOutOfDate, Map.of("lastUpdated", lastSuccessfulUpdate));
  }
}
