package gov.va.api.lighthouse.facilities.collector;

import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Builder
public class BenefitsTransformer {
  @NonNull ArcGisBenefits.Feature arcgisFacility;

  String csvWebsite;

  private Facility.FacilityAttributes attributes(ArcGisBenefits.Attributes attributes) {
    return Facility.FacilityAttributes.builder()
        .name(attributes.facilityName())
        .facilityType(Facility.FacilityType.va_benefits_facility)
        .classification(attributes.facilityType())
        .latitude(arcgisFacility.geometry().latitude())
        .longitude(arcgisFacility.geometry().longitude())
        .website(website(arcgisFacility.attributes().websiteUrl()))
        .address(
            Facility.Addresses.builder()
                .physical(
                    Facility.Address.builder()
                        .address1(attributes.address1())
                        .address2(attributes.address2())
                        .city(attributes.city())
                        .state(upperCase(attributes.state(), Locale.US))
                        .zip(attributes.zip())
                        .build())
                .build())
        .phone(Facility.Phone.builder().main(attributes.phone()).fax(attributes.fax()).build())
        .hours(
            Facility.Hours.builder()
                .monday(attributes.monday())
                .tuesday(attributes.tuesday())
                .wednesday(attributes.wednesday())
                .thursday(attributes.thursday())
                .friday(attributes.friday())
                .saturday(attributes.saturday())
                .sunday(attributes.sunday())
                .build())
        .services(services(attributes))
        .build();
  }

  Facility.Services services(ArcGisBenefits.Attributes attributes) {
    List<Facility.BenefitsService> benefitsServices = new ArrayList<>();
    if (yesnoToBoolean(attributes.applyingForBenefits())) {
      benefitsServices.add(Facility.BenefitsService.ApplyingForBenefits);
    }
    if (yesnoToBoolean(attributes.burialClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.BurialClaimAssistance);
    }
    if (yesnoToBoolean(attributes.disabilityClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.DisabilityClaimAssistance);
    }
    if (yesnoToBoolean(attributes.ebenefitsRegistration())) {
      benefitsServices.add(Facility.BenefitsService.eBenefitsRegistrationAssistance);
    }
    if (yesnoToBoolean(attributes.educationAndCareerCounseling())) {
      benefitsServices.add(Facility.BenefitsService.EducationAndCareerCounseling);
    }
    if (yesnoToBoolean(attributes.educationClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.EducationClaimAssistance);
    }
    if (yesnoToBoolean(attributes.familyMemberClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.FamilyMemberClaimAssistance);
    }
    if (yesnoToBoolean(attributes.homelessAssistance())) {
      benefitsServices.add(Facility.BenefitsService.HomelessAssistance);
    }
    if (yesnoToBoolean(attributes.vaHomeLoanAssistance())) {
      benefitsServices.add(Facility.BenefitsService.VAHomeLoanAssistance);
    }
    if (yesnoToBoolean(attributes.insuranceClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling);
    }
    if (yesnoToBoolean(attributes.integratedDisabilityEvaluationSystem())) {
      benefitsServices.add(Facility.BenefitsService.IntegratedDisabilityEvaluationSystemAssistance);
    }
    if (yesnoToBoolean(attributes.preDischargeClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.PreDischargeClaimAssistance);
    }
    if (yesnoToBoolean(attributes.transitionAssistance())) {
      benefitsServices.add(Facility.BenefitsService.TransitionAssistance);
    }
    if (yesnoToBoolean(attributes.updatingDirectDepositInformation())) {
      benefitsServices.add(Facility.BenefitsService.UpdatingDirectDepositInformation);
    }
    if (yesnoToBoolean(attributes.vocationalRehabilitationEmplo())) {
      benefitsServices.add(
          Facility.BenefitsService.VocationalRehabilitationAndEmploymentAssistance);
    }
    if (StringUtils.containsIgnoreCase(attributes.otherServices(), "PENSION")) {
      benefitsServices.add(Facility.BenefitsService.Pensions);
    }
    return Facility.Services.builder().benefits(benefitsServices).build();
  }

  Facility toFacility() {
    return Facility.builder()
        .id("vba_" + arcgisFacility.attributes().facilityNumber())
        .type(Facility.Type.va_facilities)
        .attributes(attributes(arcgisFacility.attributes()))
        .build();
  }

  String website(String arcgisWebsite) {
    /* ArcGIS returns a string NULL... We don't want to return that.*/
    return arcgisWebsite == null || arcgisWebsite.equalsIgnoreCase("NULL")
        ? csvWebsite
        : arcgisWebsite;
  }

  private boolean yesnoToBoolean(String yesNo) {
    /* Assume no if the answer is not an emphatic yes. */
    return yesNo.equalsIgnoreCase("YES");
  }
}
