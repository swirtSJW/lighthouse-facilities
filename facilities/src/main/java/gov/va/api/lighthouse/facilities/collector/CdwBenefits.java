package gov.va.api.lighthouse.facilities.collector;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CdwBenefits {
  String facilityName;

  String facilityNumber;

  String facilityType;

  String address1;

  String address2;

  String city;

  String state;

  String zip;

  String fax;

  String phone;

  String monday;

  String tuesday;

  String wednesday;

  String thursday;

  String friday;

  String saturday;

  String sunday;

  String applyingForBenefits;

  String burialClaimAssistance;

  String disabilityClaimAssistance;

  String ebenefitsRegistration;

  String educationAndCareerCounseling;

  String educationClaimAssistance;

  String familyMemberClaimAssistance;

  String homelessAssistance;

  String vaHomeLoanAssistance;

  String insuranceClaimAssistance;

  String integratedDisabilityEvaluationSystem;

  String preDischargeClaimAssistance;

  String transitionAssistance;

  String updatingDirectDepositInformation;

  String vocationalRehabilitationEmplo;

  String otherServices;

  String websiteUrl;

  BigDecimal longitude;

  BigDecimal latitude;
}
