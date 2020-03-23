package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class Facility {
  @NotNull String id;

  @NotNull Type type;

  @Valid @NotNull Attributes attributes;

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
    DentalServices,
    Dermatology,
    EmergencyCare,
    Gastroenterology,
    Gynecology,
    MentalHealthCare,
    Ophthalmology,
    Optometry,
    Orthopedics,
    PrimaryCare,
    SpecialtyCare,
    UrgentCare,
    Urology,
    WomensHealth
  }

  public enum OtherService {
    @JsonProperty("Online Scheduling")
    OnlineScheduling
  }

  public enum Type {
    va_facilities
  }

  public interface ServiceType {}

  @Value
  @Builder
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

  @Value
  @Builder
  public static final class Addresses {
    @Valid Address mailing;

    @Valid Address physical;
  }

  @Value
  @Builder
  public static final class Attributes {
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

    @Valid Addresses address;

    @Valid Phone phone;

    @Valid Hours hours;

    @Valid Services services;

    @Valid Satisfaction satisfaction;

    @Valid
    @JsonProperty("wait_times")
    WaitTimes waitTimes;

    Boolean mobile;

    @JsonProperty("active_status")
    ActiveStatus activeStatus;

    String visn;
  }

  /**
   * Standard hours of operation. Currently formatted as descriptive text suitable for display, with
   * no guarantee of a standard parseable format. Hours of operation may vary due to holidays or
   * other events.
   */
  @Value
  @Builder
  public static final class Hours {
    @JsonProperty("Monday")
    String mon;

    @JsonProperty("Tuesday")
    String tues;

    @JsonProperty("Wednesday")
    String wed;

    @JsonProperty("Thursday")
    String thurs;

    @JsonProperty("Friday")
    String fri;

    @JsonProperty("Saturday")
    String sat;

    @JsonProperty("Sunday")
    String sun;

    /* NO CAP(e)S! */
    String monday;

    String tuesday;

    String wednesday;

    String thursday;

    String friday;

    String saturday;

    String sunday;
  }

  @Value
  @Builder
  public static final class PatientSatisfaction {
    /**
     * % of Veterans who say they usually or always get an appointment when they need care right
     * away at a primary care location.
     */
    @JsonProperty("primary_care_urgent")
    BigDecimal primaryCareUrgent;

    /**
     * % of Veterans who say they usually or always get an appointment when they need it at a
     * primary care location.
     */
    @JsonProperty("primary_care_routine")
    BigDecimal primaryCareRoutine;

    /**
     * % of Veterans who say they usually or always get an appointment when they need care right
     * away at a specialty location.
     */
    @JsonProperty("specialty_care_urgent")
    BigDecimal specialtyCareUrgent;

    /**
     * % of Veterans who say they usually or always get an appointment when they need it at a
     * specialty location.
     */
    @JsonProperty("specialty_care_routine")
    BigDecimal specialtyCareRoutine;
  }

  @Value
  @Builder
  public static final class PatientWaitTime {
    @NotNull HealthService service;

    /**
     * Average number of days a Veteran who hasn't been to this location has to wait for a
     * non-urgent appointment.
     */
    @JsonProperty("new")
    BigDecimal newPatientWaitTime;

    /**
     * Average number of days a patient who has already been to this location has to wait for a
     * non-urgent appointment.
     */
    @JsonProperty("established")
    BigDecimal establishedPatientWaitTime;
  }

  @Value
  @Builder
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

  @Value
  @Builder
  public static final class Satisfaction {
    @Valid PatientSatisfaction health;

    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }

  @Value
  @Builder
  public static final class Services {
    List<OtherService> other;

    List<HealthService> health;

    List<BenefitsService> benefits;

    @JsonProperty("last_updated")
    LocalDate lastUpdated;
  }

  @Value
  @Builder
  public static final class WaitTimes {
    @Valid List<PatientWaitTime> health;

    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }
}
