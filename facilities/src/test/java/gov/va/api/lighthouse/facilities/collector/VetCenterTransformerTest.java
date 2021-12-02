package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.Type.va_facilities;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.DatamartFacility;
import org.junit.jupiter.api.Test;

public class VetCenterTransformerTest {
  @Test
  void empty() {
    assertThat(
            VetCenterTransformer.builder()
                .vast(VastEntity.builder().build())
                .websites(emptyMap())
                .build()
                .toDatamartFacility())
        .isNull();

    assertThat(
            VetCenterTransformer.builder()
                .vast(VastEntity.builder().stationNumber("x").build())
                .websites(emptyMap())
                .build()
                .toDatamartFacility())
        .isEqualTo(DatamartFacility.builder().id("vc_x").type(va_facilities).build());
  }
}
