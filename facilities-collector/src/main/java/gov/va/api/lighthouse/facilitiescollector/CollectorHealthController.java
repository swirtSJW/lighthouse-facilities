package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.lighthouse.facilitiescollector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.isBlank;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.withTrailingSlash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final InsecureRestTemplateProvider insecureRestTemplateProvider;

  private final RestTemplate restTemplate;

  private final String arcGisBaseUrl;

  private final String atcBaseUrl;

  private final String atcCovidBaseUrl;

  private final String atpBaseUrl;

  private final String stateCemeteriesBaseUrl;

  private final String vaArcGisBaseUrl;

  private final AtomicBoolean hasCachedRecently = new AtomicBoolean(false);

  CollectorHealthController(
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Autowired RestTemplate restTemplate,
      @Value("${arc-gis.url}") String arcGisBaseUrl,
      @Value("${access-to-care.url}") String atcBaseUrl,
      @Value("${access-to-care.covid.url}") String atcCovidBaseUrl,
      @Value("${access-to-pwt.url}") String atpBaseUrl,
      @Value("${state-cemeteries.url}") String stateCemeteriesBaseUrl,
      @Value("${va-arc-gis.url}") String vaArcGisBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.restTemplate = restTemplate;
    this.arcGisBaseUrl = withTrailingSlash(arcGisBaseUrl);
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    this.atcCovidBaseUrl = withTrailingSlash(atcCovidBaseUrl);
    this.atpBaseUrl = withTrailingSlash(atpBaseUrl);
    this.stateCemeteriesBaseUrl = withTrailingSlash(stateCemeteriesBaseUrl);
    this.vaArcGisBaseUrl = withTrailingSlash(vaArcGisBaseUrl);
  }

  private Supplier<Health> basicHealthCheck(HealthCheck healthCheck) {
    return () -> testDownstreamHealth(healthCheck);
  }

  private Health buildHealthFromStatusCode(String name, HttpStatus statusCode) {
    return Health.status(new Status(statusCode.is2xxSuccessful() ? "UP" : "DOWN", name))
        .withDetail("name", name)
        .withDetail("statusCode", statusCode.value())
        .withDetail("status", statusCode)
        .withDetail("time", Instant.now())
        .build();
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
    RestTemplate insecureTemplate = insecureRestTemplateProvider.restTemplate();
    List<Health> downstreamServices =
        List.of(
                basicHealthCheck(
                    HealthCheck.builder()
                        .restTemplate(insecureTemplate)
                        .name("Access to Care")
                        .url(
                            UriComponentsBuilder.fromHttpUrl(
                                    atcBaseUrl + "atcapis/v1.1/patientwaittimes")
                                .toUriString())
                        .build()),
                basicHealthCheck(
                    HealthCheck.builder()
                        .restTemplate(insecureTemplate)
                        .name("Access to PWT")
                        .url(
                            UriComponentsBuilder.fromHttpUrl(atpBaseUrl + "Shep/getRawData")
                                .queryParam("location", "FL")
                                .build()
                                .toUriString())
                        .build()),
                basicHealthCheck(
                    HealthCheck.builder()
                        .restTemplate(restTemplate)
                        .name("Public ArcGIS")
                        .url(
                            UriComponentsBuilder.fromHttpUrl(
                                    arcGisBaseUrl + "aqgBd3l68G8hEFFE/ArcGIS/rest/info/healthCheck")
                                .queryParam("f", "json")
                                .toUriString())
                        .build()),
                basicHealthCheck(
                    HealthCheck.builder()
                        .restTemplate(insecureTemplate)
                        .name("State Cemeteries")
                        .url(
                            UriComponentsBuilder.fromHttpUrl(
                                    stateCemeteriesBaseUrl + "cems/cems.xml")
                                .toUriString())
                        .build()),
                () -> testVaArcGisHealth(insecureTemplate),
                () -> testAtcCovid19Health(insecureTemplate))
            .parallelStream()
            .map(Supplier::get)
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

  private ResponseEntity<String> requestHealth(RestTemplate rt, String url) {
    return rt.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
  }

  /**
   * For the covid-19 response, the code tests not only its ability to reach out and get a 200
   * response from the endpoint, but also that it is able to deserialize the response to a
   * AccessToCareCovid19Entry. This will allow us to respond faster if the response changes.
   */
  private Health testAtcCovid19Health(RestTemplate insecureRestTemplate) {
    String url =
        UriComponentsBuilder.fromHttpUrl(atcCovidBaseUrl + "vacovid19summary.json").toUriString();
    HttpStatus statusCode;
    try {
      ResponseEntity<String> response = requestHealth(insecureRestTemplate, url);
      statusCode = response.getStatusCode();
      if (statusCode.value() == 200) {
        // Do some custom validation here
        List<AccessToCareCovid19Entry> covid =
            JacksonConfig.createMapper()
                .readValue(
                    response.getBody(), new TypeReference<List<AccessToCareCovid19Entry>>() {})
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (AccessToCareCovid19Entry entry : covid) {
          if (isBlank(entry.stationId()) || allBlank(entry.confirmedCases(), entry.deaths())) {
            /*
             * Not _technically_ the correct use,
             * but it only gets printed as a string past this point
             */
            statusCode = HttpStatus.EXPECTATION_FAILED;
          }
        }
      }
    } catch (ResourceAccessException | JsonProcessingException e) {
      log.info("Exception occurred. GET {} message: {}", url, e.getMessage());
      statusCode = HttpStatus.SERVICE_UNAVAILABLE;
    }
    return Health.status(new Status("UP", "Access to Care: COVID-19"))
        .withDetail("name", "Access to Care: COVID-19")
        .withDetail("statusCode", statusCode.value())
        .withDetail("status", statusCode)
        .withDetail("time", Instant.now())
        .build();
  }

  private Health testDownstreamHealth(HealthCheck healthCheck) {
    HttpStatus statusCode;
    try {
      statusCode = requestHealth(healthCheck.restTemplate(), healthCheck.url()).getStatusCode();
    } catch (ResourceAccessException e) {
      log.info("Exception occurred. GET {} message: {}", healthCheck.url(), e.getMessage());
      statusCode = HttpStatus.SERVICE_UNAVAILABLE;
    }
    return buildHealthFromStatusCode(healthCheck.name(), statusCode);
  }

  private Health testVaArcGisHealth(RestTemplate insecureRestTemplate) {
    String url =
        UriComponentsBuilder.fromHttpUrl(
                vaArcGisBaseUrl
                    + "server/rest/services/VA/FacilitySitePoint_VHA/FeatureServer/0/query")
            .queryParam("f", "json")
            .queryParam("inSR", "4326")
            .queryParam("outSR", "4326")
            .queryParam("orderByFields", "Sta_No")
            .queryParam("outFields", "*")
            .queryParam("resultOffset", "0")
            .queryParam("returnCountOnly", "false")
            .queryParam("returnDistinctValues", "false")
            .queryParam("returnGeometry", "true")
            .queryParam("where", "s_abbr!='VTCR' AND s_abbr!='MVCTR'")
            .queryParam("resultRecordCount", "1")
            .build()
            .toUriString();
    HttpStatus statusCode;
    try {
      ResponseEntity<String> response = requestHealth(insecureRestTemplate, url);
      statusCode = response.getStatusCode();
      if (JacksonConfig.createMapper()
          .readValue(response.getBody(), ArcGisHealths.class)
          .features()
          .isEmpty()) {
        statusCode = HttpStatus.EXPECTATION_FAILED;
      }
    } catch (Exception e) {
      log.info("Exception occurred. GET {} message: {}", url, e.getMessage());
      statusCode = HttpStatus.SERVICE_UNAVAILABLE;
    }
    return buildHealthFromStatusCode("VA ArcGIS", statusCode);
  }

  @lombok.Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class HealthCheck {
    RestTemplate restTemplate;

    String name;

    String url;
  }
}
