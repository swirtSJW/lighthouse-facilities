package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class VetCentersCollectorTest {
  @Test
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void collect() {
    RestTemplate restTemplate = mock(RestTemplate.class);

    VastEntity vastEntity =
        VastEntity.builder()
            .latitude(new BigDecimal("48.20065600000004"))
            .longitude(new BigDecimal("-101.29615999999999"))
            .stationNumber("0404V")
            .stationName("Minot Vet Center")
            .address2("3300 South Broadway")
            .city("Minot")
            .state("ND")
            .staPhone("701-852-0177 x")
            .zip("58701")
            .monday("730AM-430PM")
            .tuesday("730AM-430PM")
            .wednesday("730AM-800PM")
            .thursday("730AM-430PM")
            .friday("730AM-430PM")
            .saturday("-")
            .sunday("-")
            .mobile(false)
            .vetCenter(true)
            .build();

    assertThat(
            VetCentersCollector.builder()
                .vastEntities(List.of(vastEntity))
                .websites(ImmutableMap.of())
                .build()
                .collect())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("vc_0404V")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Minot Vet Center")
                            .facilityType(Facility.FacilityType.vet_center)
                            .latitude(new BigDecimal("48.20065600000004"))
                            .longitude(new BigDecimal("-101.29615999999999"))
                            .address(
                                Facility.Addresses.builder()
                                    .physical(
                                        Facility.Address.builder()
                                            .zip("58701")
                                            .city("Minot")
                                            .state("ND")
                                            .address1("3300 South Broadway")
                                            .build())
                                    .build())
                            .phone(Facility.Phone.builder().main("701-852-0177").build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("730AM-430PM")
                                    .tuesday("730AM-430PM")
                                    .wednesday("730AM-800PM")
                                    .thursday("730AM-430PM")
                                    .friday("730AM-430PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .build())
                    .build()));
  }
}
