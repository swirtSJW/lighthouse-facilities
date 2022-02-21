package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityTransformerV0Test extends BaseFacilityTransformerTest {
  private DatamartFacility datamartFacility(
      List<DatamartFacility.HealthService> healthForServices,
      List<DatamartFacility.HealthService> healthForDetailedServices,
      boolean isActive) {
    return DatamartFacility.builder()
        .id("vha_123GA")
        .type(DatamartFacility.Type.va_facilities)
        .attributes(
            DatamartFacility.FacilityAttributes.builder()
                .facilityType(DatamartFacility.FacilityType.va_health_facility)
                .address(
                    DatamartFacility.Addresses.builder()
                        .mailing(
                            DatamartFacility.Address.builder()
                                .address1("505 N John Rodes Blvd")
                                .city("Melbourne")
                                .state("FL")
                                .zip("32934")
                                .build())
                        .physical(
                            DatamartFacility.Address.builder()
                                .address1("505 N John Rodes Blvd")
                                .city("Melbourne")
                                .state("FL")
                                .zip("32934")
                                .build())
                        .build())
                .hours(
                    DatamartFacility.Hours.builder()
                        .sunday("Closed")
                        .monday("9AM-5PM")
                        .tuesday("9AM-5PM")
                        .wednesday("9AM-5PM")
                        .thursday("9AM-5PM")
                        .friday("9AM-5PM")
                        .saturday("Closed")
                        .build())
                .latitude(BigDecimal.valueOf(99.99))
                .longitude(BigDecimal.valueOf(123.45))
                .name("test_name")
                .phone(
                    DatamartFacility.Phone.builder()
                        .main("202-555-1212")
                        .pharmacy("202-555-1213")
                        .patientAdvocate("202-555-1214")
                        .fax("202-555-1215")
                        .afterHours("202-555-1216")
                        .mentalHealthClinic("202-555-1217")
                        .enrollmentCoordinator("202-555-1218")
                        .build())
                .website("http://test.facilities.website.gov")
                .classification("test_classification")
                .timeZone("America/New_York")
                .mobile(false)
                .services(
                    DatamartFacility.Services.builder()
                        .health(healthForServices)
                        .lastUpdated(LocalDate.parse("2018-01-01"))
                        .build())
                .activeStatus(DatamartFacility.ActiveStatus.A)
                .visn("20")
                .satisfaction(
                    DatamartFacility.Satisfaction.builder()
                        .health(
                            DatamartFacility.PatientSatisfaction.builder()
                                .primaryCareRoutine(BigDecimal.valueOf(0.85))
                                .primaryCareUrgent(BigDecimal.valueOf(0.86))
                                .specialtyCareRoutine(BigDecimal.valueOf(0.87))
                                .specialtyCareUrgent(BigDecimal.valueOf(0.88))
                                .build())
                        .effectiveDate(LocalDate.parse("2018-02-01"))
                        .build())
                .waitTimes(
                    DatamartFacility.WaitTimes.builder()
                        .health(
                            List.of(
                                DatamartFacility.PatientWaitTime.builder()
                                    .service(DatamartFacility.HealthService.Cardiology)
                                    .establishedPatientWaitTime(BigDecimal.valueOf(5))
                                    .newPatientWaitTime(BigDecimal.valueOf(10))
                                    .build(),
                                DatamartFacility.PatientWaitTime.builder()
                                    .service(DatamartFacility.HealthService.Covid19Vaccine)
                                    .establishedPatientWaitTime(BigDecimal.valueOf(4))
                                    .newPatientWaitTime(BigDecimal.valueOf(9))
                                    .build()))
                        .effectiveDate(LocalDate.parse("2018-03-05"))
                        .build())
                .operatingStatus(
                    DatamartFacility.OperatingStatus.builder()
                        .code(DatamartFacility.OperatingStatusCode.NORMAL)
                        .additionalInfo("additional operating status info")
                        .build())
                .detailedServices(
                    healthForDetailedServices != null
                        ? getDatamartDetailedServices(healthForDetailedServices, isActive)
                        : null)
                .operationalHoursSpecialInstructions("test special instructions")
                .build())
        .build();
  }

  @Test
  public void datamartFacilityRoundtrip() {
    DatamartFacility datamartFacility =
        datamartFacility(
            List.of(
                DatamartFacility.HealthService.PrimaryCare,
                DatamartFacility.HealthService.UrgentCare,
                DatamartFacility.HealthService.EmergencyCare),
            List.of(DatamartFacility.HealthService.Covid19Vaccine),
            true);
    assertThat(
            FacilityTransformerV0.toVersionAgnostic(
                FacilityTransformerV0.toFacility(datamartFacility)))
        .usingRecursiveComparison()
        .isEqualTo(datamartFacility);
  }

  private Facility facility(
      List<Facility.HealthService> healthForServices,
      List<Facility.HealthService> healthForDetailedServices,
      boolean isActive) {
    return Facility.builder()
        .id("vha_123GA")
        .type(Facility.Type.va_facilities)
        .attributes(
            Facility.FacilityAttributes.builder()
                .facilityType(Facility.FacilityType.va_health_facility)
                .address(
                    Facility.Addresses.builder()
                        .mailing(
                            Facility.Address.builder()
                                .address1("505 N John Rodes Blvd")
                                .city("Melbourne")
                                .state("FL")
                                .zip("32934")
                                .build())
                        .physical(
                            Facility.Address.builder()
                                .address1("505 N John Rodes Blvd")
                                .city("Melbourne")
                                .state("FL")
                                .zip("32934")
                                .build())
                        .build())
                .hours(
                    Facility.Hours.builder()
                        .sunday("Closed")
                        .monday("9AM-5PM")
                        .tuesday("9AM-5PM")
                        .wednesday("9AM-5PM")
                        .thursday("9AM-5PM")
                        .friday("9AM-5PM")
                        .saturday("Closed")
                        .build())
                .latitude(BigDecimal.valueOf(99.99))
                .longitude(BigDecimal.valueOf(123.45))
                .name("test_name")
                .phone(
                    Facility.Phone.builder()
                        .main("202-555-1212")
                        .pharmacy("202-555-1213")
                        .patientAdvocate("202-555-1214")
                        .fax("202-555-1215")
                        .afterHours("202-555-1216")
                        .mentalHealthClinic("202-555-1217")
                        .enrollmentCoordinator("202-555-1218")
                        .build())
                .website("http://test.facilities.website.gov")
                .classification("test_classification")
                .timeZone("America/New_York")
                .mobile(false)
                .services(
                    Facility.Services.builder()
                        .health(healthForServices)
                        .lastUpdated(LocalDate.parse("2018-01-01"))
                        .build())
                .activeStatus(Facility.ActiveStatus.A)
                .visn("20")
                .satisfaction(
                    Facility.Satisfaction.builder()
                        .health(
                            Facility.PatientSatisfaction.builder()
                                .primaryCareRoutine(BigDecimal.valueOf(0.85))
                                .primaryCareUrgent(BigDecimal.valueOf(0.86))
                                .specialtyCareRoutine(BigDecimal.valueOf(0.87))
                                .specialtyCareUrgent(BigDecimal.valueOf(0.88))
                                .build())
                        .effectiveDate(LocalDate.parse("2018-02-01"))
                        .build())
                .waitTimes(
                    Facility.WaitTimes.builder()
                        .health(
                            List.of(
                                Facility.PatientWaitTime.builder()
                                    .service(Facility.HealthService.Cardiology)
                                    .establishedPatientWaitTime(BigDecimal.valueOf(5))
                                    .newPatientWaitTime(BigDecimal.valueOf(10))
                                    .build(),
                                Facility.PatientWaitTime.builder()
                                    .service(Facility.HealthService.Covid19Vaccine)
                                    .establishedPatientWaitTime(BigDecimal.valueOf(4))
                                    .newPatientWaitTime(BigDecimal.valueOf(9))
                                    .build()))
                        .effectiveDate(LocalDate.parse("2018-03-05"))
                        .build())
                .operatingStatus(
                    Facility.OperatingStatus.builder()
                        .code(Facility.OperatingStatusCode.NORMAL)
                        .additionalInfo("additional operating status info")
                        .build())
                .detailedServices(
                    healthForDetailedServices != null
                        ? getDetailedServices(healthForDetailedServices, isActive)
                        : null)
                .operationalHoursSpecialInstructions("test special instructions")
                .build())
        .build();
  }

  @Test
  public void facilityRoundtrip() {
    Facility facility =
        facility(
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare),
            List.of(Facility.HealthService.Covid19Vaccine),
            true);
    assertThat(FacilityTransformerV0.toFacility(FacilityTransformerV0.toVersionAgnostic(facility)))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }

  private DatamartDetailedService getDatamartDetailedService(
      @NonNull DatamartFacility.HealthService healthService, boolean isActive) {
    return DatamartDetailedService.builder()
        .active(isActive)
        .name(
            healthService.name().equals(DatamartFacility.HealthService.Covid19Vaccine.name())
                ? "COVID-19 vaccines"
                : healthService.name())
        .serviceId(uncapitalize(healthService.name()))
        .path("https://path/to/service/goodness")
        .phoneNumbers(
            List.of(
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .number("937-268-6511")
                    .label("Main phone")
                    .type("tel")
                    .extension("71234")
                    .build(),
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .number("321-213-4253")
                    .label("After hours phone")
                    .type("tel")
                    .extension("12345")
                    .build()))
        .walkInsAccepted("true")
        .referralRequired("false")
        .appointmentLeadIn(
            "Your VA health care team will contact you if you???re eligible to get a vaccine "
                + "during this time. As the supply of vaccine increases, we'll work with our care "
                + "teams to let Veterans know their options.")
        .descriptionFacility("facility description")
        .onlineSchedulingAvailable("true")
        .serviceLocations(
            List.of(
                DatamartDetailedService.DetailedServiceLocation.builder()
                    .additionalHoursInfo(
                        "Location hours times may vary depending on staff availability")
                    .facilityServiceHours(
                        DatamartDetailedService.DetailedServiceHours.builder()
                            .sunday("Closed")
                            .monday("9AM-5PM")
                            .tuesday("9AM-5PM")
                            .wednesday("9AM-5PM")
                            .thursday("9AM-5PM")
                            .friday("9AM-5PM")
                            .saturday("Closed")
                            .build())
                    .emailContacts(
                        List.of(
                            DatamartDetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("georgea@va.gov")
                                .emailLabel("George Anderson")
                                .build(),
                            DatamartDetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("john.doe@va.gov")
                                .emailLabel("John Doe")
                                .build(),
                            DatamartDetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("jane.doe@va.gov")
                                .emailLabel("Jane Doe")
                                .build()))
                    .appointmentPhoneNumbers(
                        List.of(
                            DatamartDetailedService.AppointmentPhoneNumber.builder()
                                .number("932-934-6731")
                                .type("tel")
                                .label("Main Phone")
                                .extension("3245")
                                .build(),
                            DatamartDetailedService.AppointmentPhoneNumber.builder()
                                .number("956-862-6651")
                                .type("mobile")
                                .label("Mobile phone")
                                .build()))
                    .serviceLocationAddress(
                        DatamartDetailedService.DetailedServiceAddress.builder()
                            .address1("50 Irving Street, Northwest")
                            .buildingNameNumber("Baxter Building")
                            .city("Washington")
                            .state("DC")
                            .zipCode("20422-0001")
                            .countryCode("US")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .build())
                    .build()))
        .changed("2021-02-04T22:36:49+00:00")
        .build();
  }

  private List<DatamartDetailedService> getDatamartDetailedServices(
      @NonNull List<DatamartFacility.HealthService> healthServices, boolean isActive) {
    return healthServices.stream()
        .map(
            hs -> {
              return getDatamartDetailedService(hs, isActive);
            })
        .collect(Collectors.toList());
  }

  private DetailedService getDetailedService(
      @NonNull Facility.HealthService healthService, boolean isActive) {
    return DetailedService.builder()
        .active(isActive)
        .name(
            healthService.name().equals(Facility.HealthService.Covid19Vaccine.name())
                ? "COVID-19 vaccines"
                : healthService.name())
        .serviceId(uncapitalize(healthService.name()))
        .path("https://path/to/service/goodness")
        .phoneNumbers(
            List.of(
                DetailedService.AppointmentPhoneNumber.builder()
                    .number("937-268-6511")
                    .label("Main phone")
                    .type("tel")
                    .extension("71234")
                    .build(),
                DetailedService.AppointmentPhoneNumber.builder()
                    .number("321-213-4253")
                    .label("After hours phone")
                    .type("tel")
                    .extension("12345")
                    .build()))
        .walkInsAccepted("true")
        .referralRequired("false")
        .appointmentLeadIn(
            "Your VA health care team will contact you if you???re eligible to get a vaccine "
                + "during this time. As the supply of vaccine increases, we'll work with our care "
                + "teams to let Veterans know their options.")
        .descriptionFacility("facility description")
        .onlineSchedulingAvailable("true")
        .serviceLocations(
            List.of(
                DetailedService.DetailedServiceLocation.builder()
                    .additionalHoursInfo(
                        "Location hours times may vary depending on staff availability")
                    .facilityServiceHours(
                        DetailedService.DetailedServiceHours.builder()
                            .sunday("Closed")
                            .monday("9AM-5PM")
                            .tuesday("9AM-5PM")
                            .wednesday("9AM-5PM")
                            .thursday("9AM-5PM")
                            .friday("9AM-5PM")
                            .saturday("Closed")
                            .build())
                    .emailContacts(
                        List.of(
                            DetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("georgea@va.gov")
                                .emailLabel("George Anderson")
                                .build(),
                            DetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("john.doe@va.gov")
                                .emailLabel("John Doe")
                                .build(),
                            DetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("jane.doe@va.gov")
                                .emailLabel("Jane Doe")
                                .build()))
                    .appointmentPhoneNumbers(
                        List.of(
                            DetailedService.AppointmentPhoneNumber.builder()
                                .number("932-934-6731")
                                .type("tel")
                                .label("Main Phone")
                                .extension("3245")
                                .build(),
                            DetailedService.AppointmentPhoneNumber.builder()
                                .number("956-862-6651")
                                .type("mobile")
                                .label("Mobile phone")
                                .build()))
                    .serviceLocationAddress(
                        DetailedService.DetailedServiceAddress.builder()
                            .address1("50 Irving Street, Northwest")
                            .buildingNameNumber("Baxter Building")
                            .city("Washington")
                            .state("DC")
                            .zipCode("20422-0001")
                            .countryCode("US")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .build())
                    .build()))
        .changed("2021-02-04T22:36:49+00:00")
        .build();
  }

  private List<DetailedService> getDetailedServices(
      @NonNull List<Facility.HealthService> healthServices, boolean isActive) {
    return healthServices.stream()
        .map(
            hs -> {
              return getDetailedService(hs, isActive);
            })
        .collect(Collectors.toList());
  }

  @Test
  void healthServiceRoundTripUsingServiceId() {
    for (String json :
        List.of(
            "\"audiology\"",
            "\"cardiology\"",
            "\"caregiverSupport\"",
            "\"covid19Vaccine\"",
            "\"dental\"",
            "\"dermatology\"",
            "\"emergencyCare\"",
            "\"gastroenterology\"",
            "\"gynecology\"",
            "\"mentalHealth\"",
            "\"ophthalmology\"",
            "\"optometry\"",
            "\"orthopedics\"",
            "\"nutrition\"",
            "\"podiatry\"",
            "\"primaryCare\"",
            "\"specialtyCare\"",
            "\"urgentCare\"",
            "\"urology\"",
            "\"womensHealth\"")) {
      // Convert to FAPI V0 Health Service
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthServiceV0 =
          convertToHealthServiceV0(json);
      // Convert to Datamart Health Service
      String jsonHealthService = convertToJson(healthServiceV0);
      DatamartFacility.HealthService datamartHealthService =
          convertToDatamartHealthService(jsonHealthService);
      // Convert to FAPI V1 Health Service
      jsonHealthService = convertToJson(datamartHealthService);
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService healthServiceV1 =
          convertToHealthServiceV1(jsonHealthService);
      // Convert back to FAPI V0 Health Service and compare beginning to end
      jsonHealthService = convertToJson(healthServiceV1);
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthService =
          convertToHealthServiceV0(jsonHealthService);
      assertThat(healthService).isEqualTo(healthServiceV0);
    }
  }

  @Test
  void healthServiceRoundTripUsingServiceName() {
    for (String json :
        List.of(
            "\"Audiology\"",
            "\"Cardiology\"",
            "\"CaregiverSupport\"",
            "\"Covid19Vaccine\"",
            "\"DentalServices\"",
            "\"Dermatology\"",
            "\"EmergencyCare\"",
            "\"Gastroenterology\"",
            "\"Gynecology\"",
            "\"MentalHealthCare\"",
            "\"Ophthalmology\"",
            "\"Optometry\"",
            "\"Orthopedics\"",
            "\"Nutrition\"",
            "\"Podiatry\"",
            "\"PrimaryCare\"",
            "\"SpecialtyCare\"",
            "\"UrgentCare\"",
            "\"Urology\"",
            "\"WomensHealth\"")) {
      // Convert to FAPI V0 Health Service
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthServiceV0 =
          convertToHealthServiceV0(json);
      // Convert to Datamart Health Service
      String jsonHealthService = convertToJson(healthServiceV0);
      DatamartFacility.HealthService datamartHealthService =
          convertToDatamartHealthService(jsonHealthService);
      // Convert to FAPI V1 Health Service
      jsonHealthService = convertToJson(datamartHealthService);
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService healthServiceV1 =
          convertToHealthServiceV1(jsonHealthService);
      // Convert back to FAPI V0 Health Service and compare beginning to end
      jsonHealthService = convertToJson(healthServiceV1);
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthService =
          convertToHealthServiceV0(jsonHealthService);
      assertThat(healthService).isEqualTo(healthServiceV0);
    }
  }

  @Test
  public void losslessFacilityVisitorRoundtrip() {
    Facility facility =
        facility(
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare),
            List.of(Facility.HealthService.Covid19Vaccine),
            true);
    assertThat(
            FacilityTransformerV0.toFacility(
                FacilityTransformerV1.toVersionAgnostic(
                    FacilityTransformerV1.toFacility(
                        FacilityTransformerV0.toVersionAgnostic(facility)))))
        .usingRecursiveComparison()
        .ignoringFields("attributes.detailedServices")
        .ignoringFields("attributes.activeStatus")
        .isEqualTo(facility);
  }

  @Test
  public void nonLosslessFacilityVisitorRoundtrip() {
    Facility facilityWithSpecialtyCare =
        facility(
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare),
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare,
                Facility.HealthService.MentalHealthCare,
                Facility.HealthService.DentalServices,
                Facility.HealthService.SpecialtyCare),
            true);
    Facility facilityWithoutSpecialtyCare =
        facility(
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare),
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare,
                Facility.HealthService.MentalHealthCare,
                Facility.HealthService.DentalServices,
                Facility.HealthService.SpecialtyCare),
            true);
    assertThat(
            FacilityTransformerV0.toFacility(
                FacilityTransformerV1.toVersionAgnostic(
                    FacilityTransformerV1.toFacility(
                        FacilityTransformerV0.toVersionAgnostic(facilityWithSpecialtyCare)))))
        .usingRecursiveComparison()
        .ignoringFields("attributes.detailedServices")
        .ignoringFields("attributes.activeStatus")
        .isEqualTo(facilityWithoutSpecialtyCare);
    DatamartFacility facilityWithMoreThanJustCovid =
        datamartFacility(
            List.of(
                DatamartFacility.HealthService.PrimaryCare,
                DatamartFacility.HealthService.UrgentCare,
                DatamartFacility.HealthService.EmergencyCare),
            List.of(
                DatamartFacility.HealthService.Covid19Vaccine,
                DatamartFacility.HealthService.UrgentCare,
                DatamartFacility.HealthService.EmergencyCare,
                DatamartFacility.HealthService.Cardiology),
            true);
    DatamartFacility facilityWithOnlyCovid =
        datamartFacility(
            List.of(
                DatamartFacility.HealthService.PrimaryCare,
                DatamartFacility.HealthService.UrgentCare,
                DatamartFacility.HealthService.EmergencyCare),
            List.of(DatamartFacility.HealthService.Covid19Vaccine),
            true);
    DatamartFacility facilityWithNoDetailedServices =
        datamartFacility(
            List.of(
                DatamartFacility.HealthService.PrimaryCare,
                DatamartFacility.HealthService.UrgentCare,
                DatamartFacility.HealthService.EmergencyCare),
            null,
            true);
    // V1 Facilities no longer contain detailed services in their facility attributes
    assertThat(
            FacilityTransformerV1.toVersionAgnostic(
                FacilityTransformerV1.toFacility(facilityWithMoreThanJustCovid)))
        .usingRecursiveComparison()
        .ignoringFields("attributes.activeStatus")
        .isEqualTo(facilityWithNoDetailedServices);
    // V0 Facilities still contain detailed services in their facility attributes
    assertThat(
            FacilityTransformerV0.toVersionAgnostic(
                FacilityTransformerV0.toFacility(facilityWithMoreThanJustCovid)))
        .usingRecursiveComparison()
        .isEqualTo(facilityWithOnlyCovid);
  }

  @Test
  @SneakyThrows
  public void nullArgs() {
    assertThrows(NullPointerException.class, () -> FacilityTransformerV0.toFacility(null));
    assertThrows(NullPointerException.class, () -> FacilityTransformerV0.toVersionAgnostic(null));
    final Method transformDatmartFacilityBenefitsServiceMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityBenefitsService", DatamartFacility.BenefitsService.class);
    transformDatmartFacilityBenefitsServiceMethod.setAccessible(true);
    DatamartFacility.BenefitsService nullBenefits = null;
    assertThatThrownBy(
            () -> transformDatmartFacilityBenefitsServiceMethod.invoke(null, nullBenefits))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException(
                "datamartFacilityBenefitsService is marked non-null but is null"));
    final Method transformFacilityBenefitsServiceMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityBenefitsService", Facility.BenefitsService.class);
    transformFacilityBenefitsServiceMethod.setAccessible(true);
    Facility.BenefitsService nullBenefitsV0 = null;
    assertThatThrownBy(() -> transformFacilityBenefitsServiceMethod.invoke(null, nullBenefitsV0))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException("facilityBenefitsService is marked non-null but is null"));
    final Method transformDatmartFacilityHealthServiceMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityHealthService", DatamartFacility.HealthService.class);
    transformDatmartFacilityHealthServiceMethod.setAccessible(true);
    DatamartFacility.HealthService nullHealth = null;
    assertThatThrownBy(() -> transformDatmartFacilityHealthServiceMethod.invoke(null, nullHealth))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException(
                "datamartFacilityHealthService is marked non-null but is null"));
    final Method transformFacilityHealthServiceMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityHealthService", Facility.HealthService.class);
    transformFacilityHealthServiceMethod.setAccessible(true);
    Facility.HealthService nullHealthV0 = null;
    assertThatThrownBy(() -> transformFacilityHealthServiceMethod.invoke(null, nullHealthV0))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new NullPointerException("facilityHealthService is marked non-null but is null"));
    final Method transformDatmartFacilityServicesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityServices", DatamartFacility.Services.class);
    transformDatmartFacilityServicesMethod.setAccessible(true);
    DatamartFacility.Services nullServices = null;
    assertThat(transformDatmartFacilityServicesMethod.invoke(null, nullServices))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Services.builder().build());
    final Method transformFacilityServicesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityServices", Facility.Services.class);
    transformFacilityServicesMethod.setAccessible(true);
    Facility.Services nullServicesV0 = null;
    assertThat(transformFacilityServicesMethod.invoke(null, nullServicesV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Services.builder().build());
    final Method transformDatmartFacilitySatisfactionMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilitySatisfaction", DatamartFacility.Satisfaction.class);
    transformDatmartFacilitySatisfactionMethod.setAccessible(true);
    DatamartFacility.Satisfaction nullSatisfaction = null;
    assertThat(transformDatmartFacilitySatisfactionMethod.invoke(null, nullSatisfaction))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Satisfaction.builder().build());
    final Method transformFacilitySatisfactionMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilitySatisfaction", Facility.Satisfaction.class);
    transformFacilitySatisfactionMethod.setAccessible(true);
    Facility.Satisfaction nullSatisfactionV0 = null;
    assertThat(transformFacilitySatisfactionMethod.invoke(null, nullSatisfactionV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Satisfaction.builder().build());
    final Method transformDatmartFacilityPhoneMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityPhone", DatamartFacility.Phone.class);
    transformDatmartFacilityPhoneMethod.setAccessible(true);
    DatamartFacility.Satisfaction nullPhone = null;
    assertThat(transformDatmartFacilityPhoneMethod.invoke(null, nullPhone))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Phone.builder().build());
    final Method transformFacilityPhoneMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityPhone", Facility.Phone.class);
    transformFacilityPhoneMethod.setAccessible(true);
    Facility.Satisfaction nullPhoneV0 = null;
    assertThat(transformFacilityPhoneMethod.invoke(null, nullPhoneV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Phone.builder().build());
    final Method transformDatmartFacilityHoursMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityHours", DatamartFacility.Hours.class);
    transformDatmartFacilityHoursMethod.setAccessible(true);
    DatamartFacility.Hours nullHours = null;
    assertThat(transformDatmartFacilityHoursMethod.invoke(null, nullHours))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Hours.builder().build());
    final Method transformFacilityHoursMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityHours", Facility.Hours.class);
    transformFacilityHoursMethod.setAccessible(true);
    Facility.Hours nullHoursV0 = null;
    assertThat(transformFacilityHoursMethod.invoke(null, nullHoursV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Hours.builder().build());
    final Method transformDatmartFacilityAddressesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityAddresses", DatamartFacility.Addresses.class);
    transformDatmartFacilityAddressesMethod.setAccessible(true);
    DatamartFacility.Addresses nullAddresses = null;
    assertThat(transformDatmartFacilityAddressesMethod.invoke(null, nullAddresses))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Addresses.builder().build());
    final Method transformFacilityAddressesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityAddresses", Facility.Addresses.class);
    transformFacilityAddressesMethod.setAccessible(true);
    Facility.Addresses nullAddressesV0 = null;
    assertThat(transformFacilityAddressesMethod.invoke(null, nullAddressesV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Addresses.builder().build());
    final Method transformDatmartFacilityWaitTimesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityWaitTimes", DatamartFacility.WaitTimes.class);
    transformDatmartFacilityWaitTimesMethod.setAccessible(true);
    DatamartFacility.WaitTimes nullWaitTimes = null;
    assertThat(transformDatmartFacilityWaitTimesMethod.invoke(null, nullWaitTimes))
        .usingRecursiveComparison()
        .isEqualTo(Facility.WaitTimes.builder().build());
    final Method transformFacilityWaitTimesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "transformFacilityWaitTimes", Facility.WaitTimes.class);
    transformFacilityWaitTimesMethod.setAccessible(true);
    Facility.WaitTimes nullWaitTimesV0 = null;
    assertThat(transformFacilityWaitTimesMethod.invoke(null, nullWaitTimesV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.WaitTimes.builder().build());
  }

  @Test
  public void transformDatamartFacility() {
    Facility expected =
        facility(
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare),
            List.of(Facility.HealthService.Covid19Vaccine),
            true);
    DatamartFacility datamartFacility =
        datamartFacility(
            List.of(
                DatamartFacility.HealthService.PrimaryCare,
                DatamartFacility.HealthService.UrgentCare,
                DatamartFacility.HealthService.EmergencyCare),
            List.of(DatamartFacility.HealthService.Covid19Vaccine),
            true);
    assertThat(FacilityTransformerV0.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  public void transformEmptyFacility() {
    Facility facility = Facility.builder().build();
    DatamartFacility datamartFacility = DatamartFacility.builder().build();
    assertThat(FacilityTransformerV0.toVersionAgnostic(facility))
        .usingRecursiveComparison()
        .isEqualTo(datamartFacility);
    assertThat(FacilityTransformerV0.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }

  @Test
  public void transformFacility() {
    DatamartFacility expected =
        datamartFacility(
            List.of(
                DatamartFacility.HealthService.PrimaryCare,
                DatamartFacility.HealthService.UrgentCare,
                DatamartFacility.HealthService.EmergencyCare),
            List.of(DatamartFacility.HealthService.Covid19Vaccine),
            true);
    Facility facility =
        facility(
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare),
            List.of(Facility.HealthService.Covid19Vaccine),
            true);
    assertThat(FacilityTransformerV0.toVersionAgnostic(facility))
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  public void transformFacilityWithEmptyAttributes() {
    Facility facility =
        Facility.builder().id("vha_123GA").type(Facility.Type.va_facilities).build();
    DatamartFacility datamartFacility =
        DatamartFacility.builder()
            .id("vha_123GA")
            .type(DatamartFacility.Type.va_facilities)
            .build();
    assertThat(FacilityTransformerV0.toVersionAgnostic(facility))
        .usingRecursiveComparison()
        .isEqualTo(datamartFacility);
    assertThat(FacilityTransformerV0.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }
}
