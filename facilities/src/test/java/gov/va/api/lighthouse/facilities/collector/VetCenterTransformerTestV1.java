package gov.va.api.lighthouse.facilities.collector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.Facility;
import org.junit.jupiter.api.Test;

public class VetCenterTransformerTestV1 {
  @Test
  void empty() {
    assertThat(
            VetCenterTransformerV1.builder()
                .vast(VastEntity.builder().build())
                .websites(emptyMap())
                .build()
                .toFacility())
        .isNull();

    assertThat(
            VetCenterTransformerV1.builder()
                .vast(VastEntity.builder().stationNumber("x").build())
                .websites(emptyMap())
                .build()
                .toFacility())
        .isEqualTo(Facility.builder().id("vc_x").type(Facility.Type.va_facilities).build());
  }
}
