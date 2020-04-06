package gov.va.api.lighthouse.facilities;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.ExceptionsV0.NotFound;
import gov.va.api.lighthouse.facilities.FacilityEntity.Pk;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import java.util.Optional;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CmsOverlayControllerTest {

  @Mock FacilityRepository repository;

  CmsOverlayController controller() {
    return CmsOverlayController.builder().repository(repository).build();
  }

  private CmsOverlay overlay() {
    return CmsOverlay.builder()
        .operatingStatus(
            OperatingStatus.builder()
                .code(OperatingStatusCode.NOTICE)
                .additionalInfo("i need attention")
                .build())
        .build();
  }

  @Test
  @SneakyThrows
  void updateIsAcceptedForKnownStation() {
    Pk pk = Pk.fromIdString("vha_123");
    FacilityEntity entity = FacilityEntity.builder().id(pk).build();
    when(repository.findById(pk)).thenReturn(Optional.of(entity));
    CmsOverlay overlay = overlay();
    controller().saveOverlay("vha_123", overlay);
    entity.cmsOverlay(FacilitiesJacksonConfig.createMapper().writeValueAsString(overlay));
    verify(repository).save(entity);
  }

  @Test
  void updateIsRejectedForUnknownStation() {
    Pk pk = Pk.fromIdString("vha_666");
    when(repository.findById(pk)).thenReturn(Optional.empty());
    Assertions.assertThatExceptionOfType(NotFound.class)
        .isThrownBy(() -> controller().saveOverlay("vha_666", overlay()));
  }
}
