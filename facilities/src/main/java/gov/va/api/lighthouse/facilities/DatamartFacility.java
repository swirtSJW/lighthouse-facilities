package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatamartFacility {
  @NotNull String id;

  @NotNull Type type;

  @Valid @NotNull FacilityAttributes attributes;

  public enum ActiveStatus {
    A,
    T
  }

  public enum BenefitsService implements ServiceType {
    ApplyingForBenefits,
    BurialClaimAssistance,
    DisabilityClaimAssistance,
    eBenefitsRegistrationAssistance,
    EducationAndCareerCounseling,
    EducationClaimAssistance,
    FamilyMemberClaimAssistance,
    HomelessAssistance,
    InsuranceClaimAssistanceAndFinancialCounseling,
    IntegratedDisabilityEvaluationSystemAssistance,
    Pensions,
    PreDischargeClaimAssistance,
    TransitionAssistance,
    UpdatingDirectDepositInformation,
    VAHomeLoanAssistance,
    VocationalRehabilitationAndEmploymentAssistance
  }

  public enum FacilityType {
    va_benefits_facility,
    va_cemetery,
    va_health_facility,
    vet_center
  }

  public enum HealthService implements ServiceType {
    Audiology,
    Cardiology,
    CaregiverSupport,
    Covid19Vaccine,
    DentalServices,
    Dermatology,
    EmergencyCare,
    Gastroenterology,
    Gynecology,
    MentalHealthCare,
    Ophthalmology,
    Optometry,
    Orthopedics,
    Nutrition,
    Podiatry,
    PrimaryCare,
    SpecialtyCare,
    UrgentCare,
    Urology,
    WomensHealth
  }

  public enum OtherService implements ServiceType {
    OnlineScheduling
  }

  public enum Type {
    va_facilities
  }

  public enum OperatingStatusCode {
    NORMAL,
    NOTICE,
    LIMITED,
    CLOSED
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Address {
    @JsonProperty("address_1")
    String address1;

    @JsonProperty("address_2")
    String address2;

    @JsonProperty("address_3")
    String address3;

    String zip;

    String city;

    String state;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Addresses {
    @Valid Address mailing;

    @Valid Address physical;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class FacilityAttributes {
    @NotNull String name;

    @NotNull
    @JsonProperty("facility_type")
    FacilityType facilityType;

    String classification;

    String website;

    @NotNull
    @JsonProperty("lat")
    BigDecimal latitude;

    @NotNull
    @JsonProperty("long")
    BigDecimal longitude;

    @JsonProperty("time_zone")
    String timeZone;

    @Valid Addresses address;

    @Valid Phone phone;

    @Valid Hours hours;

    @JsonProperty("operational_hours_special_instructions")
    String operationalHoursSpecialInstructions;

    @Valid Services services;

    @Valid Satisfaction satisfaction;

    String parentId;

    @Valid
    @JsonProperty("wait_times")
    WaitTimes waitTimes;

    Boolean mobile;

    @JsonProperty("active_status")
    ActiveStatus activeStatus;

    @Valid
    @NotNull
    @JsonProperty(value = "operating_status", required = true)
    OperatingStatus operatingStatus;

    @JsonProperty(value = "detailed_services")
    List<@Valid DatamartDetailedService> detailedServices;

    String visn;

    public static final class FacilityAttributesBuilder {
      @JsonProperty("operationalHoursSpecialInstructions")
      public FacilityAttributes.FacilityAttributesBuilder instructions(String val) {
        return operationalHoursSpecialInstructions(val);
      }
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Hours {
    String sunday;

    String monday;

    String tuesday;

    String wednesday;

    String thursday;

    String friday;

    String saturday;

    public static final class HoursBuilder {
      @JsonProperty("Friday")
      public Hours.HoursBuilder fri(String val) {
        return friday(val);
      }

      @JsonProperty("Monday")
      public Hours.HoursBuilder mon(String val) {
        return monday(val);
      }

      @JsonProperty("Saturday")
      public Hours.HoursBuilder sat(String val) {
        return saturday(val);
      }

      @JsonProperty("Sunday")
      public Hours.HoursBuilder sun(String val) {
        return sunday(val);
      }

      @JsonProperty("Thursday")
      public Hours.HoursBuilder thurs(String val) {
        return thursday(val);
      }

      @JsonProperty("Tuesday")
      public Hours.HoursBuilder tues(String val) {
        return tuesday(val);
      }

      @JsonProperty("Wednesday")
      public Hours.HoursBuilder wed(String val) {
        return wednesday(val);
      }
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class OperatingStatus {
    @NotNull
    @JsonProperty(required = true)
    OperatingStatusCode code;

    @JsonProperty(value = "additional_info", required = false)
    @Size(max = 300)
    String additionalInfo;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class PatientSatisfaction {
    @JsonProperty("primary_care_urgent")
    BigDecimal primaryCareUrgent;

    @JsonProperty("primary_care_routine")
    BigDecimal primaryCareRoutine;

    @JsonProperty("specialty_care_urgent")
    BigDecimal specialtyCareUrgent;

    @JsonProperty("specialty_care_routine")
    BigDecimal specialtyCareRoutine;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class PatientWaitTime {
    @NotNull HealthService service;

    @JsonProperty("new")
    BigDecimal newPatientWaitTime;

    @JsonProperty("established")
    BigDecimal establishedPatientWaitTime;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Phone {
    String fax;

    String main;

    String pharmacy;

    @JsonProperty("after_hours")
    String afterHours;

    @JsonProperty("patient_advocate")
    String patientAdvocate;

    @JsonProperty("mental_health_clinic")
    String mentalHealthClinic;

    @JsonProperty("enrollment_coordinator")
    String enrollmentCoordinator;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Satisfaction {
    @Valid PatientSatisfaction health;

    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Services {
    List<OtherService> other;

    List<HealthService> health;

    List<BenefitsService> benefits;

    @JsonProperty("last_updated")
    LocalDate lastUpdated;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class WaitTimes {
    @Valid List<PatientWaitTime> health;

    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }
}
