package gov.va.api.lighthouse.facilities.tests;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.tests.categories.Cms;
import io.restassured.http.Method;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Slf4j
public class CmsOverlayIT {

  @Rule public RequiresFacilities precondition = new RequiresFacilities();

  public void assertUpdate(
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

  @Test
  @Category(Cms.class)
  public void canApplyOverlay() {
    var message = getClass().getSimpleName() + " " + Instant.now();
    assertUpdate(OperatingStatusCode.CLOSED, message, ActiveStatus.T);
    assertUpdate(OperatingStatusCode.LIMITED, message, ActiveStatus.A);
    assertUpdate(OperatingStatusCode.NOTICE, message, ActiveStatus.A);
    assertUpdate(OperatingStatusCode.NORMAL, message, ActiveStatus.A);
  }
}
