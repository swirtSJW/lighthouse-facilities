package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.vet_center;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Type.va_facilities;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.ActiveStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VetCentersCollectorTest {
  @Test
  void collect() {
    VastEntity vastEntity =
        VastEntity.builder()
            .vetCenter(true)
            .latitude(new BigDecimal("48.20065600000004"))
            .longitude(new BigDecimal("-101.29615999999999"))
            .stationNumber("0404V")
            .stationName("Minot Vet Center")
            .abbreviation("VTCR")
            .cocClassificationId("X")
            .address1("Minot Vet Center")
            .address2("3300 South Broadway")
            .city("Minot")
            .state("ND")
            .staFax("771-025-8107")
            .staPhone("701-852-0177 x")
            .zip("58701")
            .zip4("9630")
            .monday("730AM-430PM")
            .tuesday("730AM-430PM")
            .wednesday("730AM-800PM")
            .thursday("730AM-430PM")
            .friday("730AM-430PM")
            .saturday("-")
            .sunday("-")
            .operationalHoursSpecialInstructions(
                "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m. |")
            .pod("A")
            .mobile(false)
            .visn("21")
            .build();

    assertThat(
            VetCentersCollector.builder()
                .vastEntities(List.of(vastEntity))
                .websites(emptyMap())
                .build()
                .collect())
        .isEqualTo(
            List.of(
                DatamartFacility.builder()
                    .id("vc_0404V")
                    .type(va_facilities)
                    .attributes(
                        FacilityAttributes.builder()
                            .name("Minot Vet Center")
                            .facilityType(vet_center)
                            .latitude(new BigDecimal("48.20065600000004"))
                            .longitude(new BigDecimal("-101.29615999999999"))
                            .timeZone("America/Chicago")
                            .address(
                                Addresses.builder()
                                    .physical(
                                        Address.builder()
                                            .zip("58701-9630")
                                            .city("Minot")
                                            .state("ND")
                                            .address1("3300 South Broadway")
                                            .build())
                                    .build())
                            .phone(Phone.builder().fax("771-025-8107").main("701-852-0177").build())
                            .hours(
                                Hours.builder()
                                    .monday("730AM-430PM")
                                    .tuesday("730AM-430PM")
                                    .wednesday("730AM-800PM")
                                    .thursday("730AM-430PM")
                                    .friday("730AM-430PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .operationalHoursSpecialInstructions(
                                "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m. |")
                            .mobile(false)
                            .activeStatus(ActiveStatus.A)
                            .visn("21")
                            .build())
                    .build()));
  }
}
