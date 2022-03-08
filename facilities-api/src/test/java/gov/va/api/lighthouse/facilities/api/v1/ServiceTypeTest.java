package gov.va.api.lighthouse.facilities.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class ServiceTypeTest {
  @Test
  void facilityBenefitsServices() {
    BenefitsService[] benefitsServices = BenefitsService.values();
    for (BenefitsService bs : benefitsServices) {
      assertThat(BenefitsService.fromString(StringUtils.uncapitalize(bs.name())))
          .isEqualTo(BenefitsService.valueOf(bs.name()));
    }
  }

  @Test
  void facilityHealthServices() {
    HealthService[] healthServices = HealthService.values();
    for (HealthService hs : healthServices) {
      assertThat(HealthService.fromString(StringUtils.uncapitalize(hs.name())))
          .isEqualTo(HealthService.valueOf(hs.name()));
    }

    assertThat(HealthService.fromString("COVID-19 vaccines"))
        .isEqualTo(HealthService.Covid19Vaccine);
    assertThat(HealthService.fromString("covid-19 vaccines"))
        .isEqualTo(HealthService.Covid19Vaccine);
    assertThat(HealthService.fromString("MentalHealthCare")).isEqualTo(HealthService.MentalHealth);
    assertThat(HealthService.fromString("mentalHealthCare")).isEqualTo(HealthService.MentalHealth);
    assertThat(HealthService.fromString("DentalServices")).isEqualTo(HealthService.Dental);
    assertThat(HealthService.fromString("dentalServices")).isEqualTo(HealthService.Dental);
  }
}
