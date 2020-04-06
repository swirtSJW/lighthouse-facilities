package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityOverlayTest {

  private static final ObjectMapper mapper = FacilitiesJacksonConfig.createMapper();

  private void assertStatus(
      ActiveStatus expectedActiveStatus,
      OperatingStatus expectedOperatingStatus,
      FacilityEntity entity) {
    Facility facility = FacilityOverlay.builder().mapper(mapper).build().apply(entity);
    assertThat(facility.attributes().activeStatus()).isEqualTo(expectedActiveStatus);
    assertThat(facility.attributes().operatingStatus()).isEqualTo(expectedOperatingStatus);
  }

  @SneakyThrows
  private FacilityEntity entity(Facility facility) {
    return FacilityEntity.builder().facility(mapper.writeValueAsString(facility)).build();
  }

  private Facility fromActiveStatus(ActiveStatus status) {
    return Facility.builder()
        .attributes(FacilityAttributes.builder().activeStatus(status).build())
        .build();
  }

  @Test
  void operatingStatusIsPopulatedByActiveStatusWhenNotAvailable() {
    assertStatus(
        ActiveStatus.A,
        OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build(),
        entity(fromActiveStatus(ActiveStatus.A)));
    assertStatus(
        ActiveStatus.T,
        OperatingStatus.builder().code(OperatingStatusCode.CLOSED).build(),
        entity(fromActiveStatus(ActiveStatus.T)));
    assertStatus(
        null,
        OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build(),
        entity(fromActiveStatus(null)));
  }
}
