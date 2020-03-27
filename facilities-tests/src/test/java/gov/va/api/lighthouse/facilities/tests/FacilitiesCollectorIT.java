package gov.va.api.lighthouse.facilities.tests;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import io.restassured.http.Header;
import io.restassured.http.Method;
import java.util.Map;
import org.junit.Test;

public class FacilitiesCollectorIT {

  private Header clientKey() {
    return new Header(
        "client-key", System.getProperty("facilities-collector.client-key", "not-supplied"));
  }

  @Test
  public void collectFacilities() {
    makeRequest("collect/facilities").expect(200).expectValid(CollectorFacilitiesResponse.class);
  }

  private ExpectedResponse makeRequest(String path) {
    return ExpectedResponse.of(
        TestClients.collector()
            .service()
            .requestSpecification()
            .accept("application/json")
            .header(clientKey())
            .request(Method.GET, TestClients.facilities().service().urlWithApiPath() + path));
  }

  @Test
  public void mentalHealth() {
    var items = makeRequest("mental-health-contact").expect(200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(500);
  }

  @Test
  public void stopCodes() {
    var items = makeRequest("stop-code").expect(200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(1000);
  }
}
