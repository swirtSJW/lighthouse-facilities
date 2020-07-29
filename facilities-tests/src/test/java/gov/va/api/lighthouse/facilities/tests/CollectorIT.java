package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CollectorIT {
  private static ExpectedResponse makeRequest(String path, Integer expectedStatus) {
    SystemDefinitions.Service svc = systemDefinition().collector();
    log.info("Expect {} is status code ({})", svc.apiPath() + path, expectedStatus);
    return ExpectedResponse.of(
            RestAssured.given()
                .baseUri(svc.url())
                .port(svc.port())
                .relaxedHTTPSValidation()
                .request(Method.GET, svc.urlWithApiPath() + path))
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
