package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.ApplyingForBenefits;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.DisabilityClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.EducationAndCareerCounseling;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.EducationClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.FamilyMemberClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.HomelessAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.IntegratedDisabilityEvaluationSystemAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.Pensions;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.PreDischargeClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.TransitionAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.UpdatingDirectDepositInformation;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.VAHomeLoanAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.VocationalRehabilitationAndEmploymentAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.eBenefitsRegistrationAssistance;
import static gov.va.api.lighthouse.facilities.DatamartTypedServiceUtil.getDatamartTypedServices;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import java.lang.reflect.Method;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class BenefitsTransformerTest {
  @Test
  void benefitsServices() {
    // Note the BenefitsSamples class defines the valid facility services for this test. AND WE GET
    // THEM ALL!!!
    var linkerUrl = "http://localhost:8085/v1";
    var facilityId = "vba_306e";
    assertThat(tx().services(linkerUrl, facilityId))
        .isEqualTo(
            facilityService(
                getDatamartTypedServices(
                    List.of(
                        ApplyingForBenefits,
                        DisabilityClaimAssistance,
                        eBenefitsRegistrationAssistance,
                        EducationAndCareerCounseling,
                        EducationClaimAssistance,
                        FamilyMemberClaimAssistance,
                        HomelessAssistance,
                        VAHomeLoanAssistance,
                        InsuranceClaimAssistanceAndFinancialCounseling,
                        IntegratedDisabilityEvaluationSystemAssistance,
                        PreDischargeClaimAssistance,
                        TransitionAssistance,
                        UpdatingDirectDepositInformation,
                        VocationalRehabilitationAndEmploymentAssistance,
                        Pensions),
                    linkerUrl,
                    facilityId),
                linkerUrl,
                facilityId));
  }

  @Test
  @SneakyThrows
  public void blankPhone() {
    Method phoneMethod = BenefitsTransformer.class.getDeclaredMethod("phone", null);
    phoneMethod.setAccessible(true);
    BenefitsTransformer benefitsTransformer =
        BenefitsTransformer.builder().cdwFacility(CdwBenefits.builder().build()).build();
    assertThat(phoneMethod.invoke(benefitsTransformer)).isNull();
  }

  private Services facilityService(
      @NonNull List<DatamartFacility.TypedService<BenefitsService>> services,
      @NonNull String linkerUrl,
      @NonNull String facilityId) {
    return Services.builder()
        .benefits(services)
        .link(buildServicesLink(linkerUrl, facilityId))
        .build();
  }

  @Test
  void toFacility() {
    var linkerUrl = "http://localhost:8085/v1/";
    var facilityId = "vba_306e";
    assertThat(tx().toDatamartFacility(linkerUrl, facilityId))
        .isEqualTo(
            BenefitsSamples.Facilities.create().benefitsFacilities(linkerUrl, facilityId).get(0));
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
    var url = "https://shanktopus.com/vha/facility";
    var linkerUrl = "http://localhost:8085/v1/";
    var facilityId = "vba_306e";
    assertThat(tx(url).toDatamartFacility(linkerUrl, facilityId).attributes().website())
        .isEqualTo(url);
  }
}
