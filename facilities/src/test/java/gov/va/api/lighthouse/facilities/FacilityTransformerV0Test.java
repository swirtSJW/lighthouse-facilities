package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartTypedServiceUtil.getDatamartTypedServices;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static java.util.Collections.emptyList;
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
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityTransformerV0Test extends BaseFacilityTransformerTest {
  private DatamartFacility datamartFacility() {
    return datamartFacility(
        List.of(
            DatamartFacility.BenefitsService.EducationClaimAssistance,
            DatamartFacility.BenefitsService.FamilyMemberClaimAssistance),
        List.of(
            DatamartFacility.HealthService.PrimaryCare,
            DatamartFacility.HealthService.UrgentCare,
            DatamartFacility.HealthService.EmergencyCare),
        List.of(DatamartFacility.OtherService.OnlineScheduling),
        "http://localhost:8085/v0/",
        "vha_123GA");
  }

  private DatamartFacility datamartFacility(
      @NonNull List<DatamartFacility.BenefitsService> benefitsServices,
      @NonNull List<DatamartFacility.HealthService> healthServices,
      @NonNull List<DatamartFacility.OtherService> otherServices,
      @NonNull String linkerUrl,
      @NonNull String facilityId) {
    return DatamartFacility.builder()
        .id(facilityId)
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
                        .benefits(getDatamartTypedServices(benefitsServices, linkerUrl, facilityId))
                        .other(getDatamartTypedServices(otherServices, linkerUrl, facilityId))
                        .health(getDatamartTypedServices(healthServices, linkerUrl, facilityId))
                        .link(buildServicesLink(linkerUrl, facilityId))
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
                    List.of(
                        DatamartDetailedService.builder()
                            .active(true)
                            .serviceInfo(
                                DatamartDetailedService.ServiceInfo.builder()
                                    .serviceId(
                                        uncapitalize(
                                            DatamartFacility.HealthService.Covid19Vaccine.name()))
                                    .name("COVID-19 vaccines")
                                    .serviceType(DatamartDetailedService.ServiceType.Health)
                                    .build())
                            .path("https://www.melbourne.va.gov/services/covid-19-vaccines.asp")
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
                                                DatamartDetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("georgea@va.gov")
                                                    .emailLabel("George Anderson")
                                                    .build(),
                                                DatamartDetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("john.doe@va.gov")
                                                    .emailLabel("John Doe")
                                                    .build(),
                                                DatamartDetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("jane.doe@va.gov")
                                                    .emailLabel("Jane Doe")
                                                    .build()))
                                        .appointmentPhoneNumbers(
                                            List.of(
                                                DatamartDetailedService.AppointmentPhoneNumber
                                                    .builder()
                                                    .number("932-934-6731")
                                                    .type("tel")
                                                    .label("Main Phone")
                                                    .extension("3245")
                                                    .build(),
                                                DatamartDetailedService.AppointmentPhoneNumber
                                                    .builder()
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
                            .build(),
                        DatamartDetailedService.builder()
                            .active(true)
                            .serviceInfo(
                                DatamartDetailedService.ServiceInfo.builder()
                                    .serviceId(
                                        uncapitalize(
                                            DatamartFacility.HealthService.Cardiology.name()))
                                    .name(DatamartFacility.HealthService.Cardiology.name())
                                    .serviceType(DatamartDetailedService.ServiceType.Health)
                                    .build())
                            .path("https://www.melbourne.va.gov/services/cardiology.asp")
                            .phoneNumbers(
                                List.of(
                                    DatamartDetailedService.AppointmentPhoneNumber.builder()
                                        .number("924-268-4253")
                                        .label("Main phone")
                                        .type("tel")
                                        .extension("71432")
                                        .build(),
                                    DatamartDetailedService.AppointmentPhoneNumber.builder()
                                        .number("321-726-6526")
                                        .label("After hours phone")
                                        .type("tel")
                                        .extension("17525")
                                        .build()))
                            .walkInsAccepted("true")
                            .referralRequired("false")
                            .appointmentLeadIn(
                                "Do not consume caffeinated beverages 24 hours prior to your appointment.")
                            .descriptionFacility("cardiology facility description")
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
                                                DatamartDetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("georgea@va.gov")
                                                    .emailLabel("George Anderson")
                                                    .build(),
                                                DatamartDetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("john.doe@va.gov")
                                                    .emailLabel("John Doe")
                                                    .build(),
                                                DatamartDetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("jane.doe@va.gov")
                                                    .emailLabel("Jane Doe")
                                                    .build()))
                                        .appointmentPhoneNumbers(
                                            List.of(
                                                DatamartDetailedService.AppointmentPhoneNumber
                                                    .builder()
                                                    .number("932-934-6731")
                                                    .type("tel")
                                                    .label("Main Phone")
                                                    .extension("3245")
                                                    .build(),
                                                DatamartDetailedService.AppointmentPhoneNumber
                                                    .builder()
                                                    .number("956-862-6651")
                                                    .type("mobile")
                                                    .label("Mobile phone")
                                                    .build()))
                                        .serviceLocationAddress(
                                            DatamartDetailedService.DetailedServiceAddress.builder()
                                                .address1("2513 Irving Street, Northwest")
                                                .buildingNameNumber("Baxter Building")
                                                .city("Washington")
                                                .state("DC")
                                                .zipCode("20422-0001")
                                                .countryCode("US")
                                                .clinicName("Walter Read Medical Facility")
                                                .wingFloorOrRoomNumber("Wing East")
                                                .build())
                                        .build()))
                            .changed("2021-02-04T22:36:49+00:00")
                            .build()))
                .operationalHoursSpecialInstructions("test special instructions")
                .build())
        .build();
  }

  @Test
  public void datamartFacilityRoundtrip() {
    DatamartFacility datamartFacility = datamartFacility();
    assertThat(
            FacilityTransformerV0.toVersionAgnostic(
                FacilityTransformerV0.toFacility(datamartFacility), "http://localhost:8085/v0/"))
        .usingRecursiveComparison()
        .isEqualTo(datamartFacility);
  }

  private Facility facility() {
    return facility(
        List.of(
            Facility.BenefitsService.EducationClaimAssistance,
            Facility.BenefitsService.FamilyMemberClaimAssistance),
        List.of(
            Facility.HealthService.PrimaryCare,
            Facility.HealthService.UrgentCare,
            Facility.HealthService.EmergencyCare),
        List.of(Facility.OtherService.OnlineScheduling));
  }

  private Facility facility(
      @NonNull List<Facility.BenefitsService> benefitsServices,
      @NonNull List<Facility.HealthService> healthServices,
      @NonNull List<Facility.OtherService> otherServices) {
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
                        .benefits(benefitsServices)
                        .other(otherServices)
                        .health(healthServices)
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
                    List.of(
                        DetailedService.builder()
                            .active(true)
                            .name("COVID-19 vaccines")
                            .serviceId(uncapitalize(Facility.HealthService.Covid19Vaccine.name()))
                            .path("https://www.melbourne.va.gov/services/covid-19-vaccines.asp")
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
                                                DetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("georgea@va.gov")
                                                    .emailLabel("George Anderson")
                                                    .build(),
                                                DetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("john.doe@va.gov")
                                                    .emailLabel("John Doe")
                                                    .build(),
                                                DetailedService.DetailedServiceEmailContact
                                                    .builder()
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
                            .build(),
                        DetailedService.builder()
                            .active(true)
                            .name(Facility.HealthService.Cardiology.name())
                            .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                            .path("https://www.melbourne.va.gov/services/cardiology.asp")
                            .phoneNumbers(
                                List.of(
                                    DetailedService.AppointmentPhoneNumber.builder()
                                        .number("924-268-4253")
                                        .label("Main phone")
                                        .type("tel")
                                        .extension("71432")
                                        .build(),
                                    DetailedService.AppointmentPhoneNumber.builder()
                                        .number("321-726-6526")
                                        .label("After hours phone")
                                        .type("tel")
                                        .extension("17525")
                                        .build()))
                            .walkInsAccepted("true")
                            .referralRequired("false")
                            .appointmentLeadIn(
                                "Do not consume caffeinated beverages 24 hours prior to your appointment.")
                            .descriptionFacility("cardiology facility description")
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
                                                DetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("georgea@va.gov")
                                                    .emailLabel("George Anderson")
                                                    .build(),
                                                DetailedService.DetailedServiceEmailContact
                                                    .builder()
                                                    .emailAddress("john.doe@va.gov")
                                                    .emailLabel("John Doe")
                                                    .build(),
                                                DetailedService.DetailedServiceEmailContact
                                                    .builder()
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
                                                .address1("2513 Irving Street, Northwest")
                                                .buildingNameNumber("Baxter Building")
                                                .city("Washington")
                                                .state("DC")
                                                .zipCode("20422-0001")
                                                .countryCode("US")
                                                .clinicName("Walter Read Medical Facility")
                                                .wingFloorOrRoomNumber("Wing East")
                                                .build())
                                        .build()))
                            .changed("2021-02-04T22:36:49+00:00")
                            .build()))
                .operationalHoursSpecialInstructions("test special instructions")
                .build())
        .build();
  }

  @Test
  public void facilityRoundtrip() {
    Facility facility = facility();
    assertThat(
            FacilityTransformerV0.toFacility(
                FacilityTransformerV0.toVersionAgnostic(facility, "http://localhost:8085/v0/")))
        .usingRecursiveComparison()
        .isEqualTo(facility);
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
    Facility facility = facility();
    assertThat(
            FacilityTransformerV0.toFacility(
                FacilityTransformerV1.toVersionAgnostic(
                    FacilityTransformerV1.toFacility(
                        FacilityTransformerV0.toVersionAgnostic(
                            facility, "http://localhost:8085/v0/")))))
        .usingRecursiveComparison()
        .ignoringFields("attributes.detailedServices")
        .isEqualTo(facility);
  }

  /**
   * Revisit this test once final determination has been made concerning SpecialtyCare and V1 FAPI.
   */
  @Test
  public void losslessFacilityVisitorRoundtripWithMultipleHealthServices() {
    Facility facilityWithSpecialtyCare =
        facility(
            emptyList(),
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare,
                Facility.HealthService.MentalHealthCare,
                Facility.HealthService.DentalServices,
                Facility.HealthService.SpecialtyCare),
            emptyList());
    Facility facilityWithoutSpecialtyCare =
        facility(
            emptyList(),
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare,
                Facility.HealthService.MentalHealthCare,
                Facility.HealthService.DentalServices,
                Facility.HealthService.SpecialtyCare),
            emptyList());
    assertThat(
            FacilityTransformerV0.toFacility(
                FacilityTransformerV1.toVersionAgnostic(
                    FacilityTransformerV1.toFacility(
                        FacilityTransformerV0.toVersionAgnostic(
                            facilityWithSpecialtyCare, "http://localhost:8085/v0/")))))
        .usingRecursiveComparison()
        .ignoringFields("attributes.detailedServices")
        .isEqualTo(facilityWithoutSpecialtyCare);
  }

  @Test
  @SneakyThrows
  public void nullArgs() {
    assertThrows(NullPointerException.class, () -> FacilityTransformerV0.toFacility(null));
    assertThrows(
        NullPointerException.class, () -> FacilityTransformerV0.toVersionAgnostic(null, null));
    final Method transformDatmartFacilityBenefitsServiceMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toFacilityBenefitsTypedService", DatamartFacility.TypedService.class);
    transformDatmartFacilityBenefitsServiceMethod.setAccessible(true);
    DatamartFacility.BenefitsService nullBenefits = null;
    assertThatThrownBy(
            () -> transformDatmartFacilityBenefitsServiceMethod.invoke(null, nullBenefits))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException(
                "datamartFacilityTypedBenefitsService is marked non-null but is null"));
    final Method transformFacilityBenefitsServiceMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toVersionAgnosticFacilityBenefitsTypedService",
            Facility.BenefitsService.class,
            String.class,
            String.class);
    transformFacilityBenefitsServiceMethod.setAccessible(true);
    Facility.BenefitsService nullBenefitsV0 = null;
    String nullLinkerUrl = null;
    String nullFacilityId = null;
    assertThatThrownBy(
            () ->
                transformFacilityBenefitsServiceMethod.invoke(
                    null, nullBenefitsV0, nullLinkerUrl, nullFacilityId))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException("facilityBenefitsService is marked non-null but is null"));
    final Method transformDatmartFacilityHealthServiceMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toFacilityHealthService", DatamartFacility.HealthService.class);
    transformDatmartFacilityHealthServiceMethod.setAccessible(true);
    DatamartFacility.HealthService nullHealth = null;
    assertThatThrownBy(() -> transformDatmartFacilityHealthServiceMethod.invoke(null, nullHealth))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException(
                "datamartFacilityHealthService is marked non-null but is null"));
    final Method transformFacilityHealthServiceMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toVersionAgnosticFacilityHealthService", Facility.HealthService.class);
    transformFacilityHealthServiceMethod.setAccessible(true);
    Facility.HealthService nullHealthV0 = null;
    assertThatThrownBy(() -> transformFacilityHealthServiceMethod.invoke(null, nullHealthV0))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new NullPointerException("facilityHealthService is marked non-null but is null"));
    final Method transformDatmartFacilityServicesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toFacilityServices", DatamartFacility.Services.class);
    transformDatmartFacilityServicesMethod.setAccessible(true);
    DatamartFacility.Services nullServices = null;
    assertThat(transformDatmartFacilityServicesMethod.invoke(null, nullServices))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Services.builder().build());
    final Method transformFacilityServicesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toVersionAgnosticFacilityServices",
            Facility.Services.class,
            String.class,
            String.class);
    transformFacilityServicesMethod.setAccessible(true);
    Facility.Services nullServicesV0 = null;
    assertThatThrownBy(
            () ->
                transformFacilityServicesMethod.invoke(
                    null, nullServicesV0, nullLinkerUrl, nullFacilityId))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new NullPointerException("linkUrl is marked non-null but is null"));
    final Method transformDatmartFacilitySatisfactionMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toFacilitySatisfaction", DatamartFacility.Satisfaction.class);
    transformDatmartFacilitySatisfactionMethod.setAccessible(true);
    DatamartFacility.Satisfaction nullSatisfaction = null;
    assertThat(transformDatmartFacilitySatisfactionMethod.invoke(null, nullSatisfaction))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Satisfaction.builder().build());
    final Method transformFacilitySatisfactionMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toVersionAgnosticFacilitySatisfaction", Facility.Satisfaction.class);
    transformFacilitySatisfactionMethod.setAccessible(true);
    Facility.Satisfaction nullSatisfactionV0 = null;
    assertThat(transformFacilitySatisfactionMethod.invoke(null, nullSatisfactionV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Satisfaction.builder().build());
    final Method transformDatmartFacilityPhoneMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toFacilityPhone", DatamartFacility.Phone.class);
    transformDatmartFacilityPhoneMethod.setAccessible(true);
    DatamartFacility.Satisfaction nullPhone = null;
    assertThat(transformDatmartFacilityPhoneMethod.invoke(null, nullPhone))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Phone.builder().build());
    final Method transformFacilityPhoneMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toVersionAgnosticFacilityPhone", Facility.Phone.class);
    transformFacilityPhoneMethod.setAccessible(true);
    Facility.Satisfaction nullPhoneV0 = null;
    assertThat(transformFacilityPhoneMethod.invoke(null, nullPhoneV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Phone.builder().build());
    final Method transformDatmartFacilityHoursMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toFacilityHours", DatamartFacility.Hours.class);
    transformDatmartFacilityHoursMethod.setAccessible(true);
    DatamartFacility.Hours nullHours = null;
    assertThat(transformDatmartFacilityHoursMethod.invoke(null, nullHours))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Hours.builder().build());
    final Method transformFacilityHoursMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toVersionAgnosticFacilityHours", Facility.Hours.class);
    transformFacilityHoursMethod.setAccessible(true);
    Facility.Hours nullHoursV0 = null;
    assertThat(transformFacilityHoursMethod.invoke(null, nullHoursV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Hours.builder().build());
    final Method transformDatmartFacilityAddressesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toFacilityAddresses", DatamartFacility.Addresses.class);
    transformDatmartFacilityAddressesMethod.setAccessible(true);
    DatamartFacility.Addresses nullAddresses = null;
    assertThat(transformDatmartFacilityAddressesMethod.invoke(null, nullAddresses))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Addresses.builder().build());
    final Method transformFacilityAddressesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toVersionAgnosticFacilityAddresses", Facility.Addresses.class);
    transformFacilityAddressesMethod.setAccessible(true);
    Facility.Addresses nullAddressesV0 = null;
    assertThat(transformFacilityAddressesMethod.invoke(null, nullAddressesV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Addresses.builder().build());
    final Method transformDatmartFacilityWaitTimesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toFacilityWaitTimes", DatamartFacility.WaitTimes.class);
    transformDatmartFacilityWaitTimesMethod.setAccessible(true);
    DatamartFacility.WaitTimes nullWaitTimes = null;
    assertThat(transformDatmartFacilityWaitTimesMethod.invoke(null, nullWaitTimes))
        .usingRecursiveComparison()
        .isEqualTo(Facility.WaitTimes.builder().build());
    final Method transformFacilityWaitTimesMethod =
        FacilityTransformerV0.class.getDeclaredMethod(
            "toVersionAgnosticFacilityWaitTimes", Facility.WaitTimes.class);
    transformFacilityWaitTimesMethod.setAccessible(true);
    Facility.WaitTimes nullWaitTimesV0 = null;
    assertThat(transformFacilityWaitTimesMethod.invoke(null, nullWaitTimesV0))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.WaitTimes.builder().build());
  }

  @Test
  public void transformDatamartFacility() {
    Facility expected = facility();
    DatamartFacility datamartFacility = datamartFacility();
    assertThat(FacilityTransformerV0.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  public void transformEmptyFacility() {
    Facility facility = Facility.builder().build();
    DatamartFacility datamartFacility = DatamartFacility.builder().build();
    assertThat(FacilityTransformerV0.toVersionAgnostic(facility, "http://localhost:8085/v0/"))
        .usingRecursiveComparison()
        .isEqualTo(datamartFacility);
    assertThat(FacilityTransformerV0.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }

  @Test
  public void transformFacility() {
    DatamartFacility expected = datamartFacility();
    Facility facility = facility();
    assertThat(FacilityTransformerV0.toVersionAgnostic(facility, "http://localhost:8085/v0/"))
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
    assertThat(FacilityTransformerV0.toVersionAgnostic(facility, "http://localhost:8085/v0/"))
        .usingRecursiveComparison()
        .isEqualTo(datamartFacility);
    assertThat(FacilityTransformerV0.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }
}
