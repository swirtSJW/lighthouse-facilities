package gov.va.api.lighthouse.facilities.api.v1;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.DetailedService.PatientWaitTime;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DetailedServiceEmptyFieldsTest {
  @Test
  @SneakyThrows
  void emptyAppointmentPhoneNumbers() {
    assertThat(DetailedService.AppointmentPhoneNumber.builder().build().isEmpty()).isTrue();
    // Blank values
    String blank = "   ";
    assertThat(DetailedService.AppointmentPhoneNumber.builder().number(blank).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.AppointmentPhoneNumber.builder().label(blank).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.AppointmentPhoneNumber.builder().type(blank).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.AppointmentPhoneNumber.builder().extension(blank).build().isEmpty())
        .isTrue();
    // Non-blank values
    String nonBlank = "test";
    assertThat(DetailedService.AppointmentPhoneNumber.builder().number(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.AppointmentPhoneNumber.builder().label(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.AppointmentPhoneNumber.builder().type(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.AppointmentPhoneNumber.builder().extension(nonBlank).build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyDetailedServiceAddress() {
    assertThat(DetailedService.DetailedServiceAddress.builder().build().isEmpty()).isTrue();
    // Blank values
    String blank = "   ";
    assertThat(DetailedService.DetailedServiceAddress.builder().address1(blank).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceAddress.builder().address2(blank).build().isEmpty())
        .isTrue();
    assertThat(
            DetailedService.DetailedServiceAddress.builder()
                .buildingNameNumber(blank)
                .build()
                .isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceAddress.builder().clinicName(blank).build().isEmpty())
        .isTrue();
    assertThat(
            DetailedService.DetailedServiceAddress.builder()
                .wingFloorOrRoomNumber(blank)
                .build()
                .isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceAddress.builder().city(blank).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceAddress.builder().state(blank).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceAddress.builder().zipCode(blank).build().isEmpty())
        .isTrue();
    assertThat(
            DetailedService.DetailedServiceAddress.builder().countryCode(blank).build().isEmpty())
        .isTrue();
    // Non-blank values
    String nonBlank = "test";
    assertThat(
            DetailedService.DetailedServiceAddress.builder().address1(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceAddress.builder().address2(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceAddress.builder()
                .buildingNameNumber(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceAddress.builder().clinicName(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceAddress.builder()
                .wingFloorOrRoomNumber(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceAddress.builder().city(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceAddress.builder().state(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceAddress.builder().zipCode(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceAddress.builder()
                .countryCode(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyDetailedServiceEmailContact() {
    assertThat(DetailedService.DetailedServiceEmailContact.builder().build().isEmpty()).isTrue();
    // Blank values
    String blank = "   ";
    assertThat(
            DetailedService.DetailedServiceEmailContact.builder()
                .emailLabel(blank)
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            DetailedService.DetailedServiceEmailContact.builder()
                .emailAddress(blank)
                .build()
                .isEmpty())
        .isTrue();
    // Non-blank values
    String nonBlank = "test";
    assertThat(
            DetailedService.DetailedServiceEmailContact.builder()
                .emailLabel(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceEmailContact.builder()
                .emailAddress(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyDetailedServiceHours() {
    assertThat(DetailedService.DetailedServiceHours.builder().build().isEmpty()).isTrue();
    // Blank values
    String hours = "   ";
    assertThat(DetailedService.DetailedServiceHours.builder().monday(hours).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceHours.builder().tuesday(hours).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceHours.builder().wednesday(hours).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceHours.builder().thursday(hours).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceHours.builder().friday(hours).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceHours.builder().saturday(hours).build().isEmpty())
        .isTrue();
    assertThat(DetailedService.DetailedServiceHours.builder().sunday(hours).build().isEmpty())
        .isTrue();
    // Non-blank values
    hours = "9AM-5PM";
    assertThat(DetailedService.DetailedServiceHours.builder().monday(hours).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceHours.builder().tuesday(hours).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceHours.builder().wednesday(hours).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceHours.builder().thursday(hours).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceHours.builder().friday(hours).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceHours.builder().saturday(hours).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.DetailedServiceHours.builder().sunday(hours).build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyDetailedServiceLocation() {
    // Empty
    assertThat(DetailedService.DetailedServiceLocation.builder().build().isEmpty()).isTrue();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .serviceLocationAddress(DetailedService.DetailedServiceAddress.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .facilityServiceHours(DetailedService.DetailedServiceHours.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .additionalHoursInfo("   ")
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .emailContacts(emptyList())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .appointmentPhoneNumbers(emptyList())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .additionalHoursInfo("additional hours info")
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .emailContacts(
                    List.of(
                        DetailedService.DetailedServiceEmailContact.builder()
                            .emailAddress("georgea@va.gov")
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .facilityServiceHours(
                    DetailedService.DetailedServiceHours.builder().monday("9AM-5PM").build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .appointmentPhoneNumbers(
                    List.of(
                        DetailedService.AppointmentPhoneNumber.builder()
                            .number("937-268-6511")
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.DetailedServiceLocation.builder()
                .serviceLocationAddress(
                    DetailedService.DetailedServiceAddress.builder().city("Melbourne").build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    // Not empty
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    String blank = "   ";
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .name(blank)
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())
                .serviceLocations(emptyList())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()

                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .appointmentLeadIn(blank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .changed(blank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()

                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .descriptionFacility(blank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .onlineSchedulingAvailable(blank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())
                .path(blank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())
                .phoneNumbers(emptyList())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())
                .referralRequired(blank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()

                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .walkInsAccepted(blank)
                .build()
                .isEmpty())
        .isFalse();
    String nonBlank = "test";
    assertThat(
            DetailedService.builder()

                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .name(nonBlank)
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()

                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .appointmentLeadIn(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()

                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .changed(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()

                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .descriptionFacility(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()

                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .onlineSchedulingAvailable(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .path(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .referralRequired(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())
                .walkInsAccepted(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())
                .phoneNumbers(
                    List.of(
                        DetailedService.AppointmentPhoneNumber.builder()
                            .number("937-268-6511")
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    DetailedService.ServiceInfo.builder()
                        .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                        .serviceType(DetailedService.ServiceType.Health)
                        .build())

                .serviceLocations(
                    List.of(
                        DetailedService.DetailedServiceLocation.builder()
                            .additionalHoursInfo("additional hours info")
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  void patientWaitTime() {
    assertThat(DetailedService.PatientWaitTime.builder().build().isEmpty()).isTrue();
    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    ServiceInfo.builder()
                        .serviceId(uncapitalize(HealthService.Cardiology.name()))
                        .serviceType(ServiceType.Health)
                        .build())
                .waitTime(PatientWaitTime.builder().build())
                .build()
                .waitTime())
        .usingRecursiveComparison()
        .isEqualTo(DetailedService.PatientWaitTime.builder().build());

    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    ServiceInfo.builder()
                        .serviceId(uncapitalize(HealthService.Cardiology.name()))
                        .serviceType(ServiceType.Health)
                        .build())
                .waitTime(
                    PatientWaitTime.builder()
                        .newPatientWaitTime(BigDecimal.valueOf(14.583333))
                        .establishedPatientWaitTime(BigDecimal.valueOf(8.076923))
                        .effectiveDate(LocalDate.parse("2020-03-09"))
                        .build())
                .build()
                .waitTime())
        .usingRecursiveComparison()
        .isEqualTo(
            PatientWaitTime.builder()
                .newPatientWaitTime(BigDecimal.valueOf(14.583333))
                .establishedPatientWaitTime(BigDecimal.valueOf(8.076923))
                .effectiveDate(LocalDate.parse("2020-03-09"))
                .build());

    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    ServiceInfo.builder()
                        .serviceId(uncapitalize(HealthService.Cardiology.name()))
                        .serviceType(ServiceType.Health)
                        .build())
                .waitTime(
                    PatientWaitTime.builder()
                        .newPatientWaitTime(BigDecimal.valueOf(14.583333))
                        .effectiveDate(LocalDate.parse("2020-03-09"))
                        .build())
                .build()
                .waitTime())
        .usingRecursiveComparison()
        .isEqualTo(
            PatientWaitTime.builder()
                .newPatientWaitTime(BigDecimal.valueOf(14.583333))
                .effectiveDate(LocalDate.parse("2020-03-09"))
                .build());

    assertThat(
            DetailedService.builder()
                .serviceInfo(
                    ServiceInfo.builder()
                        .serviceId(uncapitalize(HealthService.Cardiology.name()))
                        .serviceType(ServiceType.Health)
                        .build())
                .waitTime(
                    PatientWaitTime.builder()
                        .newPatientWaitTime(BigDecimal.valueOf(14.583333))
                        .effectiveDate(LocalDate.parse("2020-03-09"))
                        .build())
                .build()
                .waitTime()
                .establishedPatientWaitTime())
        .isNull();
  }
}
