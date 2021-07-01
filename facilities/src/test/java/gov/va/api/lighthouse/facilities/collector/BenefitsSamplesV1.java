package gov.va.api.lighthouse.facilities.collector;

import gov.va.api.lighthouse.facilities.api.model.BenefitsService;
import gov.va.api.lighthouse.facilities.api.model.Services;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
class BenefitsSamplesV1 {

  @AllArgsConstructor(staticName = "create")
  static final class Cdw {
    CdwBenefits cdwBenefits() {
      return CdwBenefits.builder()
          .facilityName("Shanktopus VAMC")
          .facilityNumber("306e")
          .facilityType("VAMC")
          .address1("8 Shanktopus Lane")
          .address2("Apartment 8")
          .city("North")
          .state("sd")
          .zip("12208")
          .fax("123-456-7890")
          .phone("123-789-0456")
          .monday("8AM-8PM")
          .tuesday("8AM-8PM")
          .wednesday("8AM-8PM")
          .thursday("8AM-8PM")
          .friday("8AM-8PM")
          .saturday("Closed")
          .sunday("Closed")
          .applyingForBenefits("YES")
          .disabilityClaimAssistance("YES")
          .ebenefitsRegistration("YES")
          .educationAndCareerCounseling("YES")
          .educationClaimAssistance("YES")
          .familyMemberClaimAssistance("YES")
          .homelessAssistance("YES")
          .vaHomeLoanAssistance("YES")
          .insuranceClaimAssistance("YES")
          .integratedDisabilityEvaluationSystem("YES")
          .preDischargeClaimAssistance("YES")
          .transitionAssistance("YES")
          .updatingDirectDepositInformation("YES")
          .vocationalRehabilitationEmplo("YES")
          .otherServices("We got pensions for days.")
          .websiteUrl("NULL")
          .latitude(new BigDecimal("-73.776232849999985"))
          .longitude(new BigDecimal("42.651408840000045"))
          .build();
    }
  }

  @AllArgsConstructor(staticName = "create")
  static final class Facilities {
    private Facility.Address address() {
      return Facility.Address.builder()
          .address1("8 Shanktopus Lane")
          .address2("Apartment 8")
          .city("North")
          .state("SD")
          .zip("12208")
          .build();
    }

    private Facility.FacilityAttributes attributes() {
      return Facility.FacilityAttributes.builder()
          .name("Shanktopus VAMC")
          .facilityType(Facility.FacilityType.va_benefits_facility)
          .classification("VAMC")
          .latitude(new BigDecimal("-73.776232849999985"))
          .longitude(new BigDecimal("42.651408840000045"))
          .timeZone("Antarctica/Syowa")
          .address(Facility.Addresses.builder().physical(address()).build())
          .phone(Facility.Phone.builder().main("123-789-0456").fax("123-456-7890").build())
          .hours(
              Facility.Hours.builder()
                  .monday("8AM-8PM")
                  .tuesday("8AM-8PM")
                  .wednesday("8AM-8PM")
                  .thursday("8AM-8PM")
                  .friday("8AM-8PM")
                  .saturday("Closed")
                  .sunday("Closed")
                  .build())
          .services(
              Services.builder()
                  .benefits(
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
                          BenefitsService.Pensions))
                  .build())
          .build();
    }

    List<Facility> benefitsFacilities() {
      return List.of(
          Facility.builder()
              .id("vba_306e")
              .type(Facility.Type.va_facilities)
              .attributes(attributes())
              .build());
    }
  }
}
