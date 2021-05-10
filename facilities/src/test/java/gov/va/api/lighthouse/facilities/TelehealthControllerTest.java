package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.telehealth.TelehealthBody;
import gov.va.api.lighthouse.facilities.api.telehealth.TelehealthResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class TelehealthControllerTest {
  FacilityRepository repository = mock(FacilityRepository.class);

  private TelehealthController controller() {
    return TelehealthController.builder().facilityRepository(repository).build();
  }

  @Test
  void telehealthById() {
    // todo remove stub and save to new telehealth repo (if not facility repo)
    repository.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().telehealthById("vha_757"))
        .isEqualTo(TelehealthResponse.builder().stub("vha_757stub").build());
  }

  @Test
  void updateTelehealth() {
    // todo stubbed until new entity exists or facility entity is expanded
    FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString("vha_757");
    FacilityEntity entity = FacilityEntity.builder().id(pk).build();
    when(repository.findById(pk)).thenReturn(Optional.of(entity));

    assertThat(
            controller().updateTelehealth("vha_757", TelehealthBody.builder().stub("stub").build()))
        .isEqualTo(ResponseEntity.ok().build());
  }

  @Test
  void updateTelehealthMissingFacility() {
    assertThat(
            controller()
                .updateTelehealth("vha_not_here", TelehealthBody.builder().stub("stub").build()))
        .isEqualTo(ResponseEntity.accepted().build());
  }
}
