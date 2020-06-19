package gov.va.api.lighthouse.facilities.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import io.restassured.http.Method;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith(RequiresFacilitiesExtension.class)
public class CmsOverlayIT {
  private static void assertUpdate(
      OperatingStatusCode code, String message, ActiveStatus expectedActiveStatus) {
    var id = SystemDefinitions.systemDefinition().facilitiesIds().facility();
    log.info("Updating facility {} operating status to be {}", id, code);
    OperatingStatus op =
        OperatingStatus.builder().code(code).additionalInfo(message + " " + code).build();
    TestClients.facilities()
        .post(
            TestClients.facilities().service().urlWithApiPath()
                + "v0/facilities/"
                + id
                + "/cms-overlay",
            CmsOverlay.builder().operatingStatus(op).build())
        .expect(200);
    var facility =
        ExpectedResponse.of(
                TestClients.facilities()
                    .service()
                    .requestSpecification()
                    .accept("application/json")
                    .header(SystemDefinitions.systemDefinition().apikeyAsHeader())
                    .request(
                        Method.GET,
                        TestClients.facilities().service().urlWithApiPath()
                            + "v0/facilities/"
                            + id))
            .expect(200)
            .expectValid(FacilityReadResponse.class);
    assertThat(facility.facility().attributes().operatingStatus()).isEqualTo(op);
    assertThat(facility.facility().attributes().activeStatus()).isEqualTo(expectedActiveStatus);
  }

  @BeforeAll
  static void assumeEnvironment() {
    // CMS overlay tests alter data, but do not infinitely create more
    // These can run in lower environments, but not SLA'd environments
    String m = "Skipping CmsOverlayIT in " + Environment.get();
    assumeThat(Environment.get()).overridingErrorMessage(m).isNotEqualTo(Environment.LAB);
    assumeThat(Environment.get()).overridingErrorMessage(m).isNotEqualTo(Environment.PROD);
  }

  @Test
  void badCmsFacility() {
    var id = "vba_NOPE";
    log.info("Updating invalid facility {} with cmsOverlay", id);
    OperatingStatus ops =
        OperatingStatus.builder().code(OperatingStatusCode.NOTICE).additionalInfo("Shrug").build();
    TestClients.facilities()
        .post(
            TestClients.facilities().service().urlWithApiPath()
                + "v0/facilities/"
                + id
                + "/cms-overlay",
            CmsOverlay.builder().operatingStatus(ops).build())
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
  void validation() {
    var id = SystemDefinitions.systemDefinition().facilitiesIds().facility();
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
    TestClients.facilities()
        .post(
            TestClients.facilities().service().urlWithApiPath()
                + "v0/facilities/"
                + id
                + "/cms-overlay",
            CmsOverlay.builder().operatingStatus(op).build())
        .expect(400);
  }
}
