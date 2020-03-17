package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Facility {
  @NotNull String id;

  @NotNull Type type;

  @Valid @NotNull Attributes attributes;

  public enum ActiveStatus {
    A,
    T
  }

  public enum BenefitsService {
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
    va_health_facility,
    va_benefits_facility,
    va_cemetery,
    vet_center
  }

  public enum HealthService {
    DentalServices,
    PrimaryCare,
    MentalHealthCare,
    UrgentCare,
    EmergencyCare,
    Audiology,
    Cardiology,
    Dermatology,
    Gastroenterology,
    Gynecology,
    Ophthalmology,
    Optometry,
    Orthopedics,
    SpecialtyCare,
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

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Address {
    @JsonProperty("address_1")
    String address1;

    @JsonProperty("address_2")
    String address2;

    @JsonProperty("address_3")
    String address3;

    String city;

    String state;

    String zip;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Addresses {
    private Address mailing;

    private Address physical;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Attributes {
    @NotNull String name;

    @JsonProperty("facility_type")
    @NotNull
    FacilityType facilityType;

    String classification;

    @JsonProperty("lat")
    @NotNull
    BigDecimal latitude;

    @JsonProperty("long")
    @NotNull
    BigDecimal longitude;

    String website;

    Addresses address;

    Phone phone;

    Hours hours;

    Services services;

    Satisfaction satisfaction;

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
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Hours {
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
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class PatientSatisfaction {
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
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class PatientWaitTime {
    HealthService service;

    /**
     * The average number of days a Veteran who hasn’t been to this location has to wait for a
     * non-urgent appointment.
     */
    @JsonProperty("new")
    BigDecimal newPatientWaitTime;

    /**
     * The average number of days a patient who has already been to this location has to wait for a
     * non-urgent appointment.
     */
    @JsonProperty("established")
    BigDecimal establishedPatientWaitTime;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Phone {
    String main;

    String fax;

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
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Satisfaction {
    PatientSatisfaction health;

    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Services {
    List<OtherService> other;

    List<HealthService> health;

    List<BenefitsService> benefits;

    @JsonProperty("last_updated")
    LocalDate lastUpdated;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class WaitTimes {
    List<PatientWaitTime> health;

    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }
}
