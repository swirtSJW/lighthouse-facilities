package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityOverlayTest {

  private static final ObjectMapper mapper = FacilitiesJacksonConfig.createMapper();

  @Test
  void activeStatusIsPopulatedByOperatingStatusWhenAvailable() {
    assertStatus(
        ActiveStatus.A,
        op(OperatingStatusCode.NORMAL, "neato"),
        entity(fromActiveStatus(ActiveStatus.T), overlay(op(OperatingStatusCode.NORMAL, "neato"))));
    assertStatus(
        ActiveStatus.A,
        op(OperatingStatusCode.NOTICE, "neato"),
        entity(fromActiveStatus(ActiveStatus.T), overlay(op(OperatingStatusCode.NOTICE, "neato"))));
    assertStatus(
        ActiveStatus.A,
        op(OperatingStatusCode.LIMITED, "neato"),
        entity(
            fromActiveStatus(ActiveStatus.T), overlay(op(OperatingStatusCode.LIMITED, "neato"))));
    assertStatus(
        ActiveStatus.T,
        op(OperatingStatusCode.CLOSED, "neato"),
        entity(fromActiveStatus(ActiveStatus.A), overlay(op(OperatingStatusCode.CLOSED, "neato"))));
  }

  private void assertStatus(
      ActiveStatus expectedActiveStatus,
      OperatingStatus expectedOperatingStatus,
      FacilityEntity entity) {
    Facility facility = FacilityOverlay.builder().mapper(mapper).build().apply(entity);
    assertThat(facility.attributes().activeStatus()).isEqualTo(expectedActiveStatus);
    assertThat(facility.attributes().operatingStatus()).isEqualTo(expectedOperatingStatus);
  }

  @SneakyThrows
  private FacilityEntity entity(Facility facility, CmsOverlay overlay) {
    return FacilityEntity.builder()
        .facility(mapper.writeValueAsString(facility))
        .cmsOverlay(overlay == null ? null : mapper.writeValueAsString(overlay))
        .build();
  }

  private Facility fromActiveStatus(ActiveStatus status) {
    return Facility.builder()
        .attributes(FacilityAttributes.builder().activeStatus(status).build())
        .build();
  }

  private OperatingStatus op(OperatingStatusCode code, String info) {
    return OperatingStatus.builder().code(code).additionalInfo(info).build();
  }

  @Test
  void operatingStatusIsPopulatedByActiveStatusWhenNotAvailable() {
    assertStatus(
        ActiveStatus.A,
        OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build(),
        entity(fromActiveStatus(ActiveStatus.A), null));
    assertStatus(
        ActiveStatus.T,
        OperatingStatus.builder().code(OperatingStatusCode.CLOSED).build(),
        entity(fromActiveStatus(ActiveStatus.T), null));
    assertStatus(
        null,
        OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build(),
        entity(fromActiveStatus(null), null));
  }

  private CmsOverlay overlay(OperatingStatus neato) {
    return CmsOverlay.builder().operatingStatus(neato).build();
  }
}
