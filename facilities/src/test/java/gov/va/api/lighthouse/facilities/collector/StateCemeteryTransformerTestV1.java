package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class StateCemeteryTransformerTestV1 {
  @Test
  void asAddress_parseLine1() {
    assertThat(StateCemeteryTransformerV1.asAddress("AL", "Spanish Fort, AL, 36577-1234", "", ""))
        .isEqualTo(
            Facility.Address.builder().zip("36577-1234").city("Spanish Fort").state("AL").build());

    assertThat(StateCemeteryTransformerV1.asAddress("AL", "blah", "", ""))
        .isEqualTo(Facility.Address.builder().address1("blah").build());

    assertThat(StateCemeteryTransformerV1.asAddress("AL", "", "", "")).isNull();
  }

  @Test
  void asAddress_parseLine2() {
    assertThat(
            StateCemeteryTransformerV1.asAddress(
                "AL", "34904 State Highway 225", "Spanish Fort, AL, 365771234", ""))
        .isEqualTo(
            Facility.Address.builder()
                .zip("365771234")
                .city("Spanish Fort")
                .state("AL")
                .address1("34904 State Highway 225")
                .build());

    assertThat(StateCemeteryTransformerV1.asAddress("AL", "34904 State Highway 225", "blah", ""))
        .isEqualTo(
            Facility.Address.builder().state("AL").address1("34904 State Highway 225").build());

    assertThat(StateCemeteryTransformerV1.asAddress("AL", "", "blah", "")).isNull();
  }

  @Test
  void asAddress_parseLine3() {
    assertThat(
            StateCemeteryTransformerV1.asAddress(
                "AL", "34904 State Highway 225", "blah", "Spanish Fort, AL 36577"))
        .isEqualTo(
            Facility.Address.builder()
                .zip("36577")
                .city("Spanish Fort")
                .state("AL")
                .address1("34904 State Highway 225")
                .address2("blah")
                .build());
    assertThat(
            StateCemeteryTransformerV1.asAddress("AL", "34904 State Highway 225", "blah", "blah"))
        .isEqualTo(
            Facility.Address.builder()
                .state("AL")
                .address1("34904 State Highway 225")
                .address2("blah")
                .build());
    assertThat(StateCemeteryTransformerV1.asAddress("AL", "blah", "blah", "blah"))
        .isEqualTo(
            Facility.Address.builder().state("AL").address1("blah").address2("blah").build());
    assertThat(StateCemeteryTransformerV1.asAddress("AL", "", "", "blah")).isNull();
  }

  @Test
  void empty() {
    assertThat(
            StateCemeteryTransformerV1.builder()
                .xml(StateCemeteries.StateCemetery.builder().build())
                .websites(Collections.emptyMap())
                .build()
                .toFacility())
        .isNull();
    assertThat(
            StateCemeteryTransformerV1.builder()
                .xml(StateCemeteries.StateCemetery.builder().id("aBc123").build())
                .websites(Collections.emptyMap())
                .build()
                .toFacility())
        .isEqualTo(Facility.builder().id("nca_saBc123").type(Facility.Type.va_facilities).build());
  }

  @Test
  void website() {
    assertThat(
            StateCemeteryTransformerV1.builder()
                .xml(
                    StateCemeteries.StateCemetery.builder()
                        .id("aBc123")
                        .url("orig-website")
                        .build())
                .websites(ImmutableMap.of("nca_saBc123", "csv-website"))
                .build()
                .website())
        .isEqualTo("orig-website");

    assertThat(
            StateCemeteryTransformerV1.builder()
                .xml(StateCemeteries.StateCemetery.builder().id("abc123").build())
                .websites(ImmutableMap.of("nca_sabc123", "csv-website"))
                .build()
                .website())
        .isEqualTo("csv-website");
  }
}
