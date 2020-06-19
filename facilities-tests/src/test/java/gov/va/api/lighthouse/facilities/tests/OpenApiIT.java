package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.sentinel.ExpectedResponse;
import io.restassured.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class OpenApiIT {
  private static ExpectedResponse requestOpenApi(String openApiPath, Integer expectedStatus) {
    log.info(
        "Expect {} is status code ({})",
        TestClients.facilities().service().apiPath() + openApiPath,
        expectedStatus);
    return ExpectedResponse.of(
            TestClients.facilities()
                .service()
                .requestSpecification()
                .request(
                    Method.GET, TestClients.facilities().service().urlWithApiPath() + openApiPath))
        .expect(expectedStatus);
  }

  @Test
  void openApiJson() {
    requestOpenApi("v0/facilities/openapi.json", 200);
    requestOpenApi("docs/v0/api", 200);
  }
}
