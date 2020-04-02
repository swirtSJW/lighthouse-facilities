package gov.va.api.lighthouse.facilities.tests;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import gov.va.api.lighthouse.facilities.tests.categories.Collector;
import io.restassured.http.Header;
import io.restassured.http.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Slf4j
public class FacilitiesCollectorIT {

  private Header clientKey() {
    return new Header(
        "client-key", System.getProperty("facilities-collector.client-key", "not-supplied"));
  }

  @Test
  @Category({Collector.class})
  public void collectFacilities() {
    makeRequest("collect/facilities", 200).expectValid(CollectorFacilitiesResponse.class);
  }

  private ExpectedResponse makeRequest(String path, Integer expectedStatus) {
    log.info(
        "Expect {} is status code ({})",
        TestClients.collector().service().apiPath() + path,
        expectedStatus);
    return ExpectedResponse.of(
            TestClients.collector()
                .service()
                .requestSpecification()
                .accept("application/json")
                .header(clientKey())
                .request(Method.GET, TestClients.collector().service().urlWithApiPath() + path))
        .expect(expectedStatus);
  }

  @Test
  @Category({Collector.class})
  public void mentalHealth() {
    var items = makeRequest("mental-health-contact", 200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(500);
  }

  @Test
  @Category({Collector.class})
  public void stopCodes() {
    var items = makeRequest("stop-code", 200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(1000);
  }
}
