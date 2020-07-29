package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import java.time.Instant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith(RequiresFacilitiesExtension.class)
public class CmsOverlayIT {
  private static final ObjectMapper MAPPER = JacksonConfig.createMapper();

  @SneakyThrows
  private static void assertUpdate(
      OperatingStatusCode code, String message, ActiveStatus expectedActiveStatus) {
    var id = systemDefinition().ids().facility();
    log.info("Updating facility {} operating status to be {}", id, code);
    OperatingStatus op =
        OperatingStatus.builder().code(code).additionalInfo(message + " " + code).build();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(CmsOverlay.builder().operatingStatus(op).build()))
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(200);
    var facility =
        ExpectedResponse.of(
                requestSpecification()
                    .request(Method.GET, svc.urlWithApiPath() + "v0/facilities/" + id))
            .expect(200)
            .expectValid(FacilityReadResponse.class);
    assertThat(facility.facility().attributes().operatingStatus()).isEqualTo(op);
    assertThat(facility.facility().attributes().activeStatus()).isEqualTo(expectedActiveStatus);
  }

  @BeforeAll
  static void assumeEnvironment() {
    // CMS overlay tests alter data, but do not infinitely create more
    // These can run in lower environments, but not SLA'd environments
    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  }

  private static RequestSpecification requestSpecification() {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    return RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
  }

  @Test
  @SneakyThrows
  void badCmsFacility() {
    var id = "vba_NOPE";
    log.info("Updating invalid facility {} with cmsOverlay", id);
    OperatingStatus ops =
        OperatingStatus.builder().code(OperatingStatusCode.NOTICE).additionalInfo("Shrug").build();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(CmsOverlay.builder().operatingStatus(ops).build()))
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(202);
  }

  @Test
  void canApplyOverlay() {
    var message = getClass().getSimpleName() + " " + Instant.now();
    assertUpdate(OperatingStatusCode.CLOSED, message, ActiveStatus.T);
    assertUpdate(OperatingStatusCode.LIMITED, message, ActiveStatus.A);
    assertUpdate(OperatingStatusCode.NOTICE, message, ActiveStatus.A);
    assertUpdate(OperatingStatusCode.NORMAL, message, ActiveStatus.A);
  }

  @Test
  @SneakyThrows
  void validation() {
    var id = SystemDefinitions.systemDefinition().ids().facility();
    StringBuilder longMessage = new StringBuilder();
    for (int i = 1; i <= 301; i++) {
      longMessage.append(i % 10);
    }
    log.info("Updating facility {} with invalid operating status", id);
    OperatingStatus op =
        OperatingStatus.builder()
            .code(OperatingStatusCode.CLOSED)
            .additionalInfo(longMessage.toString())
            .build();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(CmsOverlay.builder().operatingStatus(op).build()))
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(400);
  }
}
