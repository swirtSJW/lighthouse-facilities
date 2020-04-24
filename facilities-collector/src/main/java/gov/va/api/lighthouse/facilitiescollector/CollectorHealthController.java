package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.lighthouse.facilitiescollector.Transformers.withTrailingSlash;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/collector", produces = "application/json")
public class CollectorHealthController {
  private final RestTemplate restTemplate;

  private final String arcGisBaseUrl;

  private final String atcBaseUrl;

  private final String atpBaseUrl;

  private final String stateCemeteriesBaseUrl;

  private final String vaArcGisBaseUrl;

  private final AtomicBoolean hasCachedRecently = new AtomicBoolean(false);

  CollectorHealthController(
      @Autowired RestTemplate restTemplate,
      @Value("${arc-gis.url}") String arcGisBaseUrl,
      @Value("${access-to-care.url}") String atcBaseUrl,
      @Value("${access-to-pwt.url}") String atpBaseUrl,
      @Value("${state-cemeteries.url}") String stateCemeteriesBaseUrl,
      @Value("${va-arc-gis.url}") String vaArcGisBaseUrl) {
    this.restTemplate = restTemplate;
    this.arcGisBaseUrl = withTrailingSlash(arcGisBaseUrl);
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    this.atpBaseUrl = withTrailingSlash(atpBaseUrl);
    this.stateCemeteriesBaseUrl = withTrailingSlash(stateCemeteriesBaseUrl);
    this.vaArcGisBaseUrl = withTrailingSlash(vaArcGisBaseUrl);
  }

  /** Clears the cache every 5 minutes. */
  @Scheduled(cron = "0 */5 * * * *")
  @CacheEvict(value = {"health"})
  public void clearCacheScheduler() {
    if (hasCachedRecently.getAndSet(false)) {
      /* Help reduce log spam by only reporting cleared _after_ we've actually cached it. */
      log.info("Clearing downstream service health cache");
    }
  }

  /**
   * Gets health status of backend systems.
   *
   * <p>To ensure we are not introducing too much load on the downstream systems, we will cache the
   * results, limiting the interactions to once every 5 minutes. Spring Cacheable is used to record
   * the result, then periodically we invalidate the cache. See clearCacheScheduler() and
   * clearCache()
   */
  @GetMapping(value = "/health")
  @Cacheable("health")
  public ResponseEntity<Health> collectorHealth() {
    hasCachedRecently.set(true);
    var now = Instant.now();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    List<Health> downstreamServices =
        List.of(
                Pair.of(
                    "Access to Care",
                    UriComponentsBuilder.fromHttpUrl(atcBaseUrl + "atcapis/v1.1/patientwaittimes")
                        .toUriString()),
                Pair.of(
                    "Access to PWT",
                    UriComponentsBuilder.fromHttpUrl(atpBaseUrl + "Shep/getRawData")
                        .queryParam("location", "FL")
                        .build()
                        .toUriString()),
                Pair.of(
                    "Public ArcGIS",
                    UriComponentsBuilder.fromHttpUrl(
                            arcGisBaseUrl + "aqgBd3l68G8hEFFE/ArcGIS/rest/info/healthCheck")
                        .queryParam("f", "json")
                        .toUriString()),
                Pair.of(
                    "State Cemeteries",
                    UriComponentsBuilder.fromHttpUrl(stateCemeteriesBaseUrl + "cems/cems.xml")
                        .toUriString()),
                Pair.of(
                    "VA ArcGIS",
                    UriComponentsBuilder.fromHttpUrl(
                            vaArcGisBaseUrl + "server/rest/info/healthCheck")
                        .queryParam("f", "json")
                        .toUriString()))
            .parallelStream()
            .map(p -> testDownstreamHealth(p.getFirst(), p.getSecond()))
            .collect(Collectors.toList());

    Health health =
        Health.status(
                new Status(
                    (downstreamServices.stream().anyMatch(d -> d.getStatus().equals(Status.DOWN))
                        ? "DOWN"
                        : "UP"),
                    "Downstream services"))
            .withDetail("name", "All downstream services")
            .withDetail("downstreamServices", downstreamServices)
            .withDetail("time", now)
            .build();
    log.info(health.toString());
    if (!health.getStatus().equals(Status.UP)) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
    }
    return ResponseEntity.ok(health);
  }

  private Health testDownstreamHealth(String name, String url) {
    HttpStatus statusCode;
    try {
      statusCode =
          restTemplate
              .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
              .getStatusCode();
    } catch (ResourceAccessException e) {
      statusCode = HttpStatus.SERVICE_UNAVAILABLE;
    }
    return Health.status(new Status(statusCode.is2xxSuccessful() ? "UP" : "DOWN", name))
        .withDetail("name", name)
        .withDetail("statusCode", statusCode.value())
        .withDetail("status", statusCode)
        .withDetail("time", Instant.now())
        .build();
  }
}
