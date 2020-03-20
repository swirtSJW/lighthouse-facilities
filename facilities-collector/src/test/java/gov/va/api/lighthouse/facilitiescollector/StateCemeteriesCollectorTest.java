package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;

public class StateCemeteriesCollectorTest {
  @Test
  public void collect() {
    assertThat(
            StateCemeteriesCollector.builder()
                .baseUrl("file:src/test/resources/")
                .websites(ImmutableMap.of("nca_s1001", "DONTUSE"))
                .build()
                .stateCemeteries())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("nca_s1001")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.Attributes.builder()
                            .name("Alabama State Veterans Memorial Cemetery At Spanish Fort")
                            .facilityType(Facility.FacilityType.va_cemetery)
                            .classification("State Cemetery")
                            .website("http://www.va.state.al.us/spanishfort.aspx")
                            .latitude(new BigDecimal("30.7346233"))
                            .longitude(new BigDecimal("-87.8985442"))
                            .address(
                                Facility.Addresses.builder()
                                    .physical(
                                        Facility.Address.builder()
                                            .zip("36577")
                                            .city("Spanish Fort")
                                            .state("AL")
                                            .address1("34904 State Highway 225")
                                            .build())
                                    .build())
                            .phone(
                                Facility.Phone.builder()
                                    .fax("251-626-9204")
                                    .main("251-625-1338")
                                    .build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("Sunrise - Sunset")
                                    .tuesday("Sunrise - Sunset")
                                    .wednesday("Sunrise - Sunset")
                                    .thursday("Sunrise - Sunset")
                                    .friday("Sunrise - Sunset")
                                    .saturday("Sunrise - Sunset")
                                    .sunday("Sunrise - Sunset")
                                    .build())
                            .build())
                    .build()));
  }
}
