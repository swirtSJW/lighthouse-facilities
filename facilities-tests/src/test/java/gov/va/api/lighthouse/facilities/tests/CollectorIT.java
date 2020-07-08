package gov.va.api.lighthouse.facilities.tests;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import io.restassured.http.Header;
import io.restassured.http.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CollectorIT {
  private static Header clientKey() {
    return new Header(
        "client-key", System.getProperty("facilities-collector.client-key", "not-supplied"));
  }

  private static ExpectedResponse makeRequest(String path, Integer expectedStatus) {
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
  void collectFacilities() {
    makeRequest("collect/facilities", 200).expectValid(CollectorFacilitiesResponse.class);
  }

  @Test
  void collectHealth() {
    makeRequest("collector/health", 200);
  }

  @Test
  void mentalHealth() {
    var items = makeRequest("mental-health-contact", 200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(500);
  }

  @Test
  void stopCodes() {
    var items = makeRequest("stop-code", 200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(1000);
  }

  @Test
  void vast() {
    var items = makeRequest("vast", 200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(1000);
  }
}
