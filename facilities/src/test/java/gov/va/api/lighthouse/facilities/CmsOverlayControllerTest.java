package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import org.junit.jupiter.api.Test;

public class CmsOverlayControllerTest {

  CmsOverlayController controller() {
    return CmsOverlayController.builder().build();
  }

  @Test
  void updateIsAcceptedForKnownStation() {
    controller()
        .saveOverlay(
            "vha_123",
            CmsOverlay.builder()
                .operatingStatus(
                    OperatingStatus.builder()
                        .code(OperatingStatusCode.NOTICE)
                        .additionalInfo("i need attention")
                        .build())
                .build());
    // TODO verify repo is updated
  }

  @Test
  void updateIsRejectedForKnownStation() {
    controller()
        .saveOverlay(
            "vha_666",
            CmsOverlay.builder()
                .operatingStatus(
                    OperatingStatus.builder()
                        .code(OperatingStatusCode.NOTICE)
                        .additionalInfo("i need attention")
                        .build())
                .build());
    // TODO verify NotFound is thrown.
  }
}
