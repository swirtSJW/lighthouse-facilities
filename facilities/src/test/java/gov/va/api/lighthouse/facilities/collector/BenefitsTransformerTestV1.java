package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BenefitsTransformerTestV1 {

  @Test
  void benefitsServices() {

    // Note the BenefitsSamples class defines the valid facility services for this test. AND WE GET
    // THEM ALL!!!
    assertThat(tx().services())
        .isEqualTo(
            facilityService(
                List.of(
                    Facility.BenefitsService.ApplyingForBenefits,
                    Facility.BenefitsService.DisabilityClaimAssistance,
                    Facility.BenefitsService.eBenefitsRegistrationAssistance,
                    Facility.BenefitsService.EducationAndCareerCounseling,
                    Facility.BenefitsService.EducationClaimAssistance,
                    Facility.BenefitsService.FamilyMemberClaimAssistance,
                    Facility.BenefitsService.HomelessAssistance,
                    Facility.BenefitsService.VAHomeLoanAssistance,
                    Facility.BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling,
                    Facility.BenefitsService.IntegratedDisabilityEvaluationSystemAssistance,
                    Facility.BenefitsService.PreDischargeClaimAssistance,
                    Facility.BenefitsService.TransitionAssistance,
                    Facility.BenefitsService.UpdatingDirectDepositInformation,
                    Facility.BenefitsService.VocationalRehabilitationAndEmploymentAssistance,
                    Facility.BenefitsService.Pensions)));
  }

  private Facility.Services facilityService(List<Facility.BenefitsService> services) {
    return Facility.Services.builder().benefits(services).build();
  }

  @Test
  void toFacility() {
    assertThat(tx().toFacility())
        .isEqualTo(BenefitsSamplesV1.Facilities.create().benefitsFacilities().get(0));
  }

  @Test
  void transformerPrioritizesWebsiteFromCdw() {
    String cdw = "https://shanktopus.com/vha/facility";
    String csv = "https://shanktofake.com/nope";
    assertThat(tx().website(null)).isNull();
    assertThat(tx(csv).website(cdw)).isEqualTo(cdw);
  }

  private BenefitsTransformerV1 tx() {
    return tx(null);
  }

  private BenefitsTransformerV1 tx(String csvWebsite) {
    return BenefitsTransformerV1.builder()
        .cdwFacility(BenefitsSamplesV1.Cdw.create().cdwBenefits())
        .csvWebsite(csvWebsite)
        .build();
  }

  @Test
  void websiteInCsvReturnsValueWhenCdwIsNull() {
    String url = "https://shanktopus.com/vha/facility";
    assertThat(tx(url).toFacility().attributes().website()).isEqualTo(url);
  }
}
