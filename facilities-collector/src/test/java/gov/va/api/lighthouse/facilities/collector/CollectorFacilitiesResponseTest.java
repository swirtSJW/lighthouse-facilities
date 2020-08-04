package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class CollectorFacilitiesResponseTest {
  @SneakyThrows
  public void assertReadable(String json) {
    CollectorFacilitiesResponse dm =
        createMapper()
            .readValue(getClass().getResourceAsStream(json), CollectorFacilitiesResponse.class);
    assertThat(dm).isEqualTo(sample());
  }

  public CollectorFacilitiesResponse sample() {
    return CollectorFacilitiesResponse.builder()
        .facilities(
            List.of(
                Facility.builder()
                    .id("vc_0101V")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Boston Vet Center")
                            .facilityType(Facility.FacilityType.vet_center)
                            .latitude(BigDecimal.valueOf(42.3445959000001))
                            .longitude(BigDecimal.valueOf(-71.0361051099999))
                            .address(
                                Facility.Addresses.builder()
                                    .mailing(Facility.Address.builder().build())
                                    .physical(
                                        Facility.Address.builder()
                                            .zip("02210")
                                            .city("Boston")
                                            .state("MA")
                                            .address1("7 Drydock Avenue")
                                            .address2("Suite 2070")
                                            .build())
                                    .build())
                            .phone(Facility.Phone.builder().main("857-203-6461").build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("830AM-700PM")
                                    .tuesday("830AM-700PM")
                                    .wednesday("830AM-600PM")
                                    .thursday("830AM-600PM")
                                    .friday("830AM-430PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .services(Facility.Services.builder().build())
                            .satisfaction(Facility.Satisfaction.builder().build())
                            .waitTimes(Facility.WaitTimes.builder().build())
                            .build())
                    .build()))
        .build();
  }

  @Test
  @SneakyThrows
  public void unmarshallFacilitiesCollectorResponse() {
    assertReadable("/facilities-collect-response.json");
  }
}
