package gov.va.api.lighthouse.facilities.collectorapi;

import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** The rest client implementation of the collector API. */
@Component
@Loggable
public class CollectorRestClient implements CollectorApi {

  private final RestTemplate restTemplate;

  /** The base URL without the trailing slash. */
  private final String facilitiesCollectorUrl;

  public CollectorRestClient(
      @Autowired RestTemplate restTemplate,
      @Value("${facilities-collector.url:unset}") String facilitiesCollectorUrl) {
    this.restTemplate = restTemplate;
    this.facilitiesCollectorUrl = facilitiesCollectorUrl.replaceAll("/$", "");
  }

  @Override
  public CollectorFacilitiesResponse collectFacilities() {
    var headers = new HttpHeaders();
    headers.add("Accept", "application/json");
    return restTemplate
        .exchange(
            collectorUrl("/collect/facilities"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            CollectorFacilitiesResponse.class)
        .getBody();
  }

  private String collectorUrl(String path) {
    return facilitiesCollectorUrl + path;
  }
}
