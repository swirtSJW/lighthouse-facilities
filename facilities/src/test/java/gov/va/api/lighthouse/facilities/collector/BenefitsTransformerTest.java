package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BenefitsTransformerTest {
  @Test
  void benefitsServices() {
    assertThat(
            tx().services(ArcGisBenefits.Attributes.builder().burialClaimAssistance("YES").build()))
        .isEqualTo(facilityService(Facility.BenefitsService.BurialClaimAssistance));
    assertThat(
            tx().services(
                    ArcGisBenefits.Attributes.builder().educationClaimAssistance("YES").build()))
        .isEqualTo(facilityService(Facility.BenefitsService.EducationClaimAssistance));
    assertThat(
            tx().services(
                    ArcGisBenefits.Attributes.builder().familyMemberClaimAssistance("YES").build()))
        .isEqualTo(facilityService(Facility.BenefitsService.FamilyMemberClaimAssistance));
    assertThat(
            tx().services(ArcGisBenefits.Attributes.builder().vaHomeLoanAssistance("YES").build()))
        .isEqualTo(facilityService(Facility.BenefitsService.VAHomeLoanAssistance));
    assertThat(
            tx().services(
                    ArcGisBenefits.Attributes.builder().insuranceClaimAssistance("YES").build()))
        .isEqualTo(
            facilityService(
                Facility.BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling));
    assertThat(
            tx().services(
                    ArcGisBenefits.Attributes.builder()
                        .integratedDisabilityEvaluationSystem("YES")
                        .build()))
        .isEqualTo(
            facilityService(
                Facility.BenefitsService.IntegratedDisabilityEvaluationSystemAssistance));
    assertThat(
            tx().services(ArcGisBenefits.Attributes.builder().transitionAssistance("YES").build()))
        .isEqualTo(facilityService(Facility.BenefitsService.TransitionAssistance));
    assertThat(
            tx().services(
                    ArcGisBenefits.Attributes.builder()
                        .updatingDirectDepositInformation("YES")
                        .build()))
        .isEqualTo(facilityService(Facility.BenefitsService.UpdatingDirectDepositInformation));
    assertThat(
            tx().services(
                    ArcGisBenefits.Attributes.builder()
                        .vocationalRehabilitationEmplo("YES")
                        .build()))
        .isEqualTo(
            facilityService(
                Facility.BenefitsService.VocationalRehabilitationAndEmploymentAssistance));
    assertThat(
            tx().services(
                    ArcGisBenefits.Attributes.builder()
                        .otherServices("You want pensions? We got em!")
                        .build()))
        .isEqualTo(facilityService(Facility.BenefitsService.Pensions));
  }

  private Facility.Services facilityService(Facility.BenefitsService service) {
    return Facility.Services.builder().benefits(List.of(service)).build();
  }

  @Test
  void toFacility() {
    assertThat(tx().toFacility())
        .isEqualTo(BenefitsSamples.Facilities.create().benefitsFacilities().get(0));
  }

  @Test
  void transformerPrioritizesWebsiteFromArcGis() {
    String arcgis = "https://shanktopus.com/vha/facility";
    String csv = "https://shanktofake.com/nope";
    assertThat(tx().website(null)).isNull();
    assertThat(tx(csv).website(arcgis)).isEqualTo(arcgis);
  }

  private BenefitsTransformer tx() {
    return tx(null);
  }

  private BenefitsTransformer tx(String csvWebsite) {
    return BenefitsTransformer.builder()
        .arcgisFacility(BenefitsSamples.ArcGis.create().arcgisBenefits().features().get(0))
        .csvWebsite(csvWebsite)
        .build();
  }

  @Test
  void websiteInCsvReturnsValueWhenArcGisIsNull() {
    String url = "https://shanktopus.com/vha/facility";
    assertThat(tx(url).toFacility().attributes().website()).isEqualTo(url);
  }
}
