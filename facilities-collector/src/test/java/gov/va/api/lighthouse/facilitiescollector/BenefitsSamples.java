package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BenefitsSamples {
  @AllArgsConstructor(staticName = "create")
  public static class ArcGis {
    public ArcGisBenefits arcgisBenefits() {
      return ArcGisBenefits.builder()
          .features(
              List.of(
                  ArcGisBenefits.Feature.builder()
                      .attributes(attributes())
                      .geometry(
                          ArcGisBenefits.Geometry.builder()
                              .latitude(new BigDecimal("-73.776232849999985"))
                              .longitude(new BigDecimal("42.651408840000045"))
                              .build())
                      .build()))
          .build();
    }

    private ArcGisBenefits.Attributes attributes() {
      return ArcGisBenefits.Attributes.builder()
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
          .burialClaimAssistance("NO")
          .disabilityClaimAssistance("YES")
          .ebenefitsRegistration("YES")
          .educationAndCareerCounseling("YES")
          .educationClaimAssistance("NO")
          .familyMemberClaimAssistance("NO")
          .homelessAssistance("YES")
          .vaHomeLoanAssistance("NO")
          .insuranceClaimAssistance("NO")
          .integratedDisabilityEvaluationSystem("NO")
          .preDischargeClaimAssistance("YES")
          .transitionAssistance("NO")
          .updatingDirectDepositInformation("NO")
          .vocationalRehabilitationEmplo("NO")
          .otherServices("We got pensions for days.")
          .websiteUrl("NULL")
          .build();
    }
  }

  @AllArgsConstructor(staticName = "create")
  public static class Facilities {
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
              Facility.Services.builder()
                  .benefits(
                      List.of(
                          Facility.BenefitsService.ApplyingForBenefits,
                          Facility.BenefitsService.DisabilityClaimAssistance,
                          Facility.BenefitsService.eBenefitsRegistrationAssistance,
                          Facility.BenefitsService.EducationAndCareerCounseling,
                          Facility.BenefitsService.HomelessAssistance,
                          Facility.BenefitsService.PreDischargeClaimAssistance,
                          Facility.BenefitsService.Pensions))
                  .build())
          .build();
    }

    public List<Facility> benefitsFacilities() {
      return List.of(
          Facility.builder()
              .id("vba_306e")
              .type(Facility.Type.va_facilities)
              .attributes(attributes())
              .build());
    }
  }
}
