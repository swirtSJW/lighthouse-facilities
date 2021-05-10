package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.telehealth.TelehealthBody;
import gov.va.api.lighthouse.facilities.api.telehealth.TelehealthResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TelehealthIT {
  private static final ObjectMapper MAPPER = JacksonConfig.createMapper();

  @BeforeAll
  static void assumeEnvironment() {
    // Tests alter data, but do not infinitely create more
    // These can run in lower environments, but not SLA'd environments
    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  }

  private static RequestSpecification requestSpecification() {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    return RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
  }

  @Test
  @SneakyThrows
  void assertUpdate() {
    // todo actual telehealth update instead of stub
    final SystemDefinitions.Service svc = systemDefinition().facilities();
    final String id = systemDefinition().ids().facility();
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(TelehealthBody.builder().stub("stub").build()))
                .request(Method.POST, svc.urlWithApiPath() + "v0/telehealth/" + id + "/update"))
        .expect(200);
  }

  @Test
  void telehealthById() {
    final String id = systemDefinition().ids().facility();
    final String request = "v0/telehealth?id=" + id;
    facilitiesRequest("application/json", request, 200).expectValid(TelehealthResponse.class);
  }
}
