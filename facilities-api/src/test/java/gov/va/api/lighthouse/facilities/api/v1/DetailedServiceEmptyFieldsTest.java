package gov.va.api.lighthouse.facilities.api.v1;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(DetailedService.builder().serviceId("test").build().isEmpty()).isFalse();
    String blank = "   ";
    assertThat(DetailedService.builder().serviceId("test").name(blank).build().isEmpty()).isFalse();
    assertThat(
            DetailedService.builder()
                .serviceId("test")
                .serviceLocations(emptyList())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder().serviceId("test").appointmentLeadIn(blank).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.builder().serviceId("test").changed(blank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceId("test")
                .descriptionFacility(blank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceId("test")
                .onlineSchedulingAvailable(blank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(DetailedService.builder().serviceId("test").path(blank).build().isEmpty()).isFalse();
    assertThat(
            DetailedService.builder().serviceId("test").phoneNumbers(emptyList()).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder().serviceId("test").referralRequired(blank).build().isEmpty())
        .isFalse();
    assertThat(DetailedService.builder().serviceId("test").walkInsAccepted(blank).build().isEmpty())
        .isFalse();
    String nonBlank = "test";
    assertThat(DetailedService.builder().serviceId("test").name(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceId("test")
                .appointmentLeadIn(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(DetailedService.builder().serviceId("test").changed(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceId("test")
                .descriptionFacility(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceId("test")
                .onlineSchedulingAvailable(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(DetailedService.builder().serviceId("test").path(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceId("test")
                .referralRequired(nonBlank)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder().serviceId("test").walkInsAccepted(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(
            DetailedService.builder()
                .serviceId("test")
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
                .serviceId("test")
                .serviceLocations(
                    List.of(
                        DetailedService.DetailedServiceLocation.builder()
                            .additionalHoursInfo("additional hours info")
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
  }
}
