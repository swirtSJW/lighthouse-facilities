package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.model.BenefitsService;
import gov.va.api.lighthouse.facilities.api.model.Services;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BenefitsTransformerTest {

  @Test
  void benefitsServices() {

    // Note the BenefitsSamples class defines the valid facility services for this test. AND WE GET
    // THEM ALL!!!
    assertThat(tx().services())
        .isEqualTo(
            facilityService(
                List.of(
                    BenefitsService.ApplyingForBenefits,
                    BenefitsService.DisabilityClaimAssistance,
                    BenefitsService.eBenefitsRegistrationAssistance,
                    BenefitsService.EducationAndCareerCounseling,
                    BenefitsService.EducationClaimAssistance,
                    BenefitsService.FamilyMemberClaimAssistance,
                    BenefitsService.HomelessAssistance,
                    BenefitsService.VAHomeLoanAssistance,
                    BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling,
                    BenefitsService.IntegratedDisabilityEvaluationSystemAssistance,
                    BenefitsService.PreDischargeClaimAssistance,
                    BenefitsService.TransitionAssistance,
                    BenefitsService.UpdatingDirectDepositInformation,
                    BenefitsService.VocationalRehabilitationAndEmploymentAssistance,
                    BenefitsService.Pensions)));
  }

  private Services facilityService(List<BenefitsService> services) {
    return Services.builder().benefits(services).build();
  }

  @Test
  void toFacility() {
    assertThat(tx().toFacility())
        .isEqualTo(BenefitsSamples.Facilities.create().benefitsFacilities().get(0));
  }

  @Test
  void transformerPrioritizesWebsiteFromCdw() {
    String cdw = "https://shanktopus.com/vha/facility";
    String csv = "https://shanktofake.com/nope";
    assertThat(tx().website(null)).isNull();
    assertThat(tx(csv).website(cdw)).isEqualTo(cdw);
  }

  private BenefitsTransformer tx() {
    return tx(null);
  }

  private BenefitsTransformer tx(String csvWebsite) {
    return BenefitsTransformer.builder()
        .cdwFacility(BenefitsSamples.Cdw.create().cdwBenefits())
        .csvWebsite(csvWebsite)
        .build();
  }

  @Test
  void websiteInCsvReturnsValueWhenCdwIsNull() {
    String url = "https://shanktopus.com/vha/facility";
    assertThat(tx(url).toFacility().attributes().website()).isEqualTo(url);
  }
}
