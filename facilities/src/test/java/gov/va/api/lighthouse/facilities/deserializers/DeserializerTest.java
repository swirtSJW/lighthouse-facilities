package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceInfo.INVALID_SVC_ID;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.DeserializationContext;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceAddress;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceEmailContact;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceHours;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceLocation;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatusCode;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientSatisfaction;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientWaitTime;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import gov.va.api.lighthouse.facilities.DatamartFacility.Satisfaction;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DeserializerTest {
  @SneakyThrows
  private <T> void assertJson(String json, Class<T> expectedClass, T expectedValue) {
    assertThat(createMapper().readValue(json, expectedClass))
        .usingRecursiveComparison()
        .isEqualTo(expectedValue);
  }

  @Test
  @SneakyThrows
  void deserializeAddress() {
    Address address =
        Address.builder()
            .address1("122 Main St.")
            .address2("West Side Apt# 227")
            .address3("Suite 7")
            .city("Rochester")
            .state("NY")
            .zip("14623-1345")
            .build();
    assertJson(
        "{"
            + "\"address_1\":\"122 Main St.\","
            + "\"address_2\":\"West Side Apt# 227\","
            + "\"address_3\":\"Suite 7\","
            + "\"city\":\"Rochester\","
            + "\"state\":\"NY\","
            + "\"zip\":\"14623-1345\""
            + "}",
        Address.class,
        address);
    assertJson(
        "{"
            + "\"address1\":\"122 Main St.\","
            + "\"address2\":\"West Side Apt# 227\","
            + "\"address3\":\"Suite 7\","
            + "\"city\":\"Rochester\","
            + "\"state\":\"NY\","
            + "\"zip\":\"14623-1345\""
            + "}",
        Address.class,
        address);
    // Exceptions
    DeserializationContext mockDeserializationContext = mock(DeserializationContext.class);
    assertThatThrownBy(
            () ->
                new DatamartAddressDeserializer(null).deserialize(null, mockDeserializationContext))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DatamartDetailedService.ServiceType.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartCmsOverlayDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                    .name(BenefitsService.Pensions.name())
                    .serviceType(DatamartDetailedService.ServiceType.Benefits)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartDetailedServiceDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsFacilityAttributes() {
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DatamartDetailedService.ServiceType.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithInvalidDetailedServices() {
    DatamartCmsOverlay overlay = DatamartCmsOverlay.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithMixedDetailedServices() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DatamartDetailedService.ServiceType.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Smoking.name()))
                                .name(HealthService.Smoking.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"bin\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"bin\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"bin\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServiceAddress() {
    DetailedServiceAddress serviceAddress =
        DetailedServiceAddress.builder()
            .buildingNameNumber("Baxter Building")
            .clinicName("Baxter Clinic")
            .wingFloorOrRoomNumber("Wing East")
            .address1("122 Main St.")
            .address2("West Side Apt# 227")
            .city("Rochester")
            .state("NY")
            .zipCode("14623-1345")
            .countryCode("US")
            .build();
    assertJson(
        "{"
            + "\"building_name_number\":\"Baxter Building\","
            + "\"clinic_name\":\"Baxter Clinic\","
            + "\"wing_floor_or_room_number\":\"Wing East\","
            + "\"address_line1\":\"122 Main St.\","
            + "\"address_line2\":\"West Side Apt# 227\","
            + "\"city\":\"Rochester\","
            + "\"state\":\"NY\","
            + "\"zip_code\":\"14623-1345\","
            + "\"country_code\":\"US\""
            + "}",
        DetailedServiceAddress.class,
        serviceAddress);
    assertJson(
        "{"
            + "\"buildingNameNumber\":\"Baxter Building\","
            + "\"clinicName\":\"Baxter Clinic\","
            + "\"wingFloorOrRoomNumber\":\"Wing East\","
            + "\"addressLine1\":\"122 Main St.\","
            + "\"addressLine2\":\"West Side Apt# 227\","
            + "\"city\":\"Rochester\","
            + "\"state\":\"NY\","
            + "\"zipCode\":\"14623-1345\","
            + "\"countryCode\":\"US\""
            + "}",
        DetailedServiceAddress.class,
        serviceAddress);
    assertJson("{}", DetailedServiceAddress.class, DetailedServiceAddress.builder().build());
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartDetailedServiceAddressDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServiceEmailContact() {
    DetailedServiceEmailContact emailContact =
        DetailedServiceEmailContact.builder()
            .emailAddress("georgea@va.gov")
            .emailLabel("George Anderson")
            .build();
    assertJson(
        "{" + "\"email_address\":\"georgea@va.gov\"," + "\"email_label\":\"George Anderson\"" + "}",
        DetailedServiceEmailContact.class,
        emailContact);
    assertJson(
        "{" + "\"emailAddress\":\"georgea@va.gov\"," + "\"emailLabel\":\"George Anderson\"" + "}",
        DetailedServiceEmailContact.class,
        emailContact);
    emailContact = DetailedServiceEmailContact.builder().emailAddress("georgea@va.gov").build();
    assertJson(
        "{" + "\"email_address\":\"georgea@va.gov\"" + "}",
        DetailedServiceEmailContact.class,
        emailContact);
    assertJson(
        "{" + "\"emailAddress\":\"georgea@va.gov\"" + "}",
        DetailedServiceEmailContact.class,
        emailContact);
    emailContact = DetailedServiceEmailContact.builder().emailLabel("George Anderson").build();
    assertJson(
        "{" + "\"email_label\":\"George Anderson\"" + "}",
        DetailedServiceEmailContact.class,
        emailContact);
    assertJson(
        "{" + "\"emailLabel\":\"George Anderson\"" + "}",
        DetailedServiceEmailContact.class,
        emailContact);
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartDetailedServiceEmailContactDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServiceHours() {
    DetailedServiceHours serviceHours =
        DetailedServiceHours.builder()
            .monday("8:30AM-7:00PM")
            .tuesday("8:30AM-7:00PM")
            .wednesday("8:30AM-7:00PM")
            .thursday("8:30AM-7:00PM")
            .friday("8:30AM-7:00PM")
            .saturday("Closed")
            .sunday("Closed")
            .build();
    assertJson(
        "{"
            + "\"Monday\":\"8:30AM-7:00PM\","
            + "\"Tuesday\":\"8:30AM-7:00PM\","
            + "\"Wednesday\":\"8:30AM-7:00PM\","
            + "\"Thursday\":\"8:30AM-7:00PM\","
            + "\"Friday\":\"8:30AM-7:00PM\","
            + "\"Saturday\":\"Closed\","
            + "\"Sunday\":\"Closed\""
            + "}",
        DetailedServiceHours.class,
        serviceHours);
    assertJson(
        "{"
            + "\"monday\":\"8:30AM-7:00PM\","
            + "\"tuesday\":\"8:30AM-7:00PM\","
            + "\"wednesday\":\"8:30AM-7:00PM\","
            + "\"thursday\":\"8:30AM-7:00PM\","
            + "\"friday\":\"8:30AM-7:00PM\","
            + "\"saturday\":\"Closed\","
            + "\"sunday\":\"Closed\""
            + "}",
        DetailedServiceHours.class,
        serviceHours);
    serviceHours = DetailedServiceHours.builder().build();
    assertJson("{}", DetailedServiceHours.class, serviceHours);
    assertJson("{}", DetailedServiceHours.class, serviceHours);
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartDetailedServiceHoursDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServiceLocation() {
    DetailedServiceLocation serviceLocation =
        DetailedServiceLocation.builder()
            .serviceLocationAddress(
                DetailedServiceAddress.builder()
                    .buildingNameNumber("Baxter Building")
                    .clinicName("Baxter Clinic")
                    .wingFloorOrRoomNumber("Wing East")
                    .address1("122 Main St.")
                    .address2("West Side Apt# 227")
                    .city("Rochester")
                    .state("NY")
                    .zipCode("14623-1345")
                    .countryCode("US")
                    .build())
            .appointmentPhoneNumbers(
                List.of(
                    AppointmentPhoneNumber.builder()
                        .extension("567")
                        .label("Alt phone")
                        .number("556-565-1119")
                        .type("tel")
                        .build()))
            .emailContacts(
                List.of(
                    DetailedServiceEmailContact.builder()
                        .emailAddress("georgea@va.gov")
                        .emailLabel("George Anderson")
                        .build()))
            .facilityServiceHours(
                DetailedServiceHours.builder()
                    .monday("8:30AM-7:00PM")
                    .tuesday("8:30AM-7:00PM")
                    .wednesday("8:30AM-7:00PM")
                    .thursday("8:30AM-7:00PM")
                    .friday("8:30AM-7:00PM")
                    .saturday("Closed")
                    .sunday("Closed")
                    .build())
            .additionalHoursInfo("additional hours info")
            .build();
    assertJson(
        "{"
            + "\"service_location_address\":{"
            + "\"building_name_number\":\"Baxter Building\","
            + "\"clinic_name\":\"Baxter Clinic\","
            + "\"wing_floor_or_room_number\":\"Wing East\","
            + "\"address_line1\":\"122 Main St.\","
            + "\"address_line2\":\"West Side Apt# 227\","
            + "\"city\":\"Rochester\","
            + "\"state\":\"NY\","
            + "\"zip_code\":\"14623-1345\","
            + "\"country_code\":\"US\""
            + "},"
            + "\"appointment_phones\":["
            + "{"
            + "\"extension\":\"567\","
            + "\"label\":\"Alt phone\","
            + "\"number\":\"556-565-1119\","
            + "\"type\":\"tel\""
            + "}"
            + "],"
            + "\"email_contacts\":["
            + "{"
            + "\"email_address\":\"georgea@va.gov\","
            + "\"email_label\":\"George Anderson\""
            + "}"
            + "],"
            + "\"facility_service_hours\":{"
            + "\"Monday\":\"8:30AM-7:00PM\","
            + "\"Tuesday\":\"8:30AM-7:00PM\","
            + "\"Wednesday\":\"8:30AM-7:00PM\","
            + "\"Thursday\":\"8:30AM-7:00PM\","
            + "\"Friday\":\"8:30AM-7:00PM\","
            + "\"Saturday\":\"Closed\","
            + "\"Sunday\":\"Closed\""
            + "},"
            + "\"additional_hours_info\":\"additional hours info\""
            + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    assertJson(
        "{"
            + "\"serviceLocationAddress\":{"
            + "\"buildingNameNumber\":\"Baxter Building\","
            + "\"clinicName\":\"Baxter Clinic\","
            + "\"wingFloorOrRoomNumber\":\"Wing East\","
            + "\"addressLine1\":\"122 Main St.\","
            + "\"addressLine2\":\"West Side Apt# 227\","
            + "\"city\":\"Rochester\","
            + "\"state\":\"NY\","
            + "\"zipCode\":\"14623-1345\","
            + "\"countryCode\":\"US\""
            + "},"
            + "\"appointmentPhones\":["
            + "{"
            + "\"extension\":\"567\","
            + "\"label\":\"Alt phone\","
            + "\"number\":\"556-565-1119\","
            + "\"type\":\"tel\""
            + "}"
            + "],"
            + "\"emailContacts\":["
            + "{"
            + "\"emailAddress\":\"georgea@va.gov\","
            + "\"emailLabel\":\"George Anderson\""
            + "}"
            + "],"
            + "\"facilityServiceHours\":{"
            + "\"monday\":\"8:30AM-7:00PM\","
            + "\"tuesday\":\"8:30AM-7:00PM\","
            + "\"wednesday\":\"8:30AM-7:00PM\","
            + "\"thursday\":\"8:30AM-7:00PM\","
            + "\"friday\":\"8:30AM-7:00PM\","
            + "\"saturday\":\"Closed\","
            + "\"sunday\":\"Closed\""
            + "},"
            + "\"additionalHoursInfo\":\"additional hours info\""
            + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    serviceLocation =
        DetailedServiceLocation.builder()
            .appointmentPhoneNumbers(
                List.of(
                    AppointmentPhoneNumber.builder()
                        .extension("567")
                        .label("Alt phone")
                        .number("556-565-1119")
                        .type("tel")
                        .build()))
            .emailContacts(
                List.of(
                    DetailedServiceEmailContact.builder()
                        .emailAddress("georgea@va.gov")
                        .emailLabel("George Anderson")
                        .build()))
            .facilityServiceHours(
                DetailedServiceHours.builder()
                    .monday("8:30AM-7:00PM")
                    .tuesday("8:30AM-7:00PM")
                    .wednesday("8:30AM-7:00PM")
                    .thursday("8:30AM-7:00PM")
                    .friday("8:30AM-7:00PM")
                    .saturday("Closed")
                    .sunday("Closed")
                    .build())
            .additionalHoursInfo("additional hours info")
            .build();
    assertJson(
        "{"
            + "\"appointment_phones\":["
            + "{"
            + "\"extension\":\"567\","
            + "\"label\":\"Alt phone\","
            + "\"number\":\"556-565-1119\","
            + "\"type\":\"tel\""
            + "}"
            + "],"
            + "\"email_contacts\":["
            + "{"
            + "\"email_address\":\"georgea@va.gov\","
            + "\"email_label\":\"George Anderson\""
            + "}"
            + "],"
            + "\"facility_service_hours\":{"
            + "\"Monday\":\"8:30AM-7:00PM\","
            + "\"Tuesday\":\"8:30AM-7:00PM\","
            + "\"Wednesday\":\"8:30AM-7:00PM\","
            + "\"Thursday\":\"8:30AM-7:00PM\","
            + "\"Friday\":\"8:30AM-7:00PM\","
            + "\"Saturday\":\"Closed\","
            + "\"Sunday\":\"Closed\""
            + "},"
            + "\"additional_hours_info\":\"additional hours info\""
            + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    assertJson(
        "{"
            + "\"appointmentPhones\":["
            + "{"
            + "\"extension\":\"567\","
            + "\"label\":\"Alt phone\","
            + "\"number\":\"556-565-1119\","
            + "\"type\":\"tel\""
            + "}"
            + "],"
            + "\"emailContacts\":["
            + "{"
            + "\"emailAddress\":\"georgea@va.gov\","
            + "\"emailLabel\":\"George Anderson\""
            + "}"
            + "],"
            + "\"facilityServiceHours\":{"
            + "\"monday\":\"8:30AM-7:00PM\","
            + "\"tuesday\":\"8:30AM-7:00PM\","
            + "\"wednesday\":\"8:30AM-7:00PM\","
            + "\"thursday\":\"8:30AM-7:00PM\","
            + "\"friday\":\"8:30AM-7:00PM\","
            + "\"saturday\":\"Closed\","
            + "\"sunday\":\"Closed\""
            + "},"
            + "\"additionalHoursInfo\":\"additional hours info\""
            + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    serviceLocation =
        DetailedServiceLocation.builder()
            .appointmentPhoneNumbers(emptyList())
            .emailContacts(
                List.of(
                    DetailedServiceEmailContact.builder()
                        .emailAddress("georgea@va.gov")
                        .emailLabel("George Anderson")
                        .build()))
            .facilityServiceHours(
                DetailedServiceHours.builder()
                    .monday("8:30AM-7:00PM")
                    .tuesday("8:30AM-7:00PM")
                    .wednesday("8:30AM-7:00PM")
                    .thursday("8:30AM-7:00PM")
                    .friday("8:30AM-7:00PM")
                    .saturday("Closed")
                    .sunday("Closed")
                    .build())
            .additionalHoursInfo("additional hours info")
            .build();
    assertJson(
        "{"
            + "\"email_contacts\":["
            + "{"
            + "\"email_address\":\"georgea@va.gov\","
            + "\"email_label\":\"George Anderson\""
            + "}"
            + "],"
            + "\"facility_service_hours\":{"
            + "\"Monday\":\"8:30AM-7:00PM\","
            + "\"Tuesday\":\"8:30AM-7:00PM\","
            + "\"Wednesday\":\"8:30AM-7:00PM\","
            + "\"Thursday\":\"8:30AM-7:00PM\","
            + "\"Friday\":\"8:30AM-7:00PM\","
            + "\"Saturday\":\"Closed\","
            + "\"Sunday\":\"Closed\""
            + "},"
            + "\"additional_hours_info\":\"additional hours info\""
            + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    assertJson(
        "{"
            + "\"emailContacts\":["
            + "{"
            + "\"emailAddress\":\"georgea@va.gov\","
            + "\"emailLabel\":\"George Anderson\""
            + "}"
            + "],"
            + "\"facilityServiceHours\":{"
            + "\"monday\":\"8:30AM-7:00PM\","
            + "\"tuesday\":\"8:30AM-7:00PM\","
            + "\"wednesday\":\"8:30AM-7:00PM\","
            + "\"thursday\":\"8:30AM-7:00PM\","
            + "\"friday\":\"8:30AM-7:00PM\","
            + "\"saturday\":\"Closed\","
            + "\"sunday\":\"Closed\""
            + "},"
            + "\"additionalHoursInfo\":\"additional hours info\""
            + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    serviceLocation =
        DetailedServiceLocation.builder()
            .appointmentPhoneNumbers(emptyList())
            .emailContacts(emptyList())
            .facilityServiceHours(
                DetailedServiceHours.builder()
                    .monday("8:30AM-7:00PM")
                    .tuesday("8:30AM-7:00PM")
                    .wednesday("8:30AM-7:00PM")
                    .thursday("8:30AM-7:00PM")
                    .friday("8:30AM-7:00PM")
                    .saturday("Closed")
                    .sunday("Closed")
                    .build())
            .additionalHoursInfo("additional hours info")
            .build();
    assertJson(
        "{"
            + "\"facility_service_hours\":{"
            + "\"Monday\":\"8:30AM-7:00PM\","
            + "\"Tuesday\":\"8:30AM-7:00PM\","
            + "\"Wednesday\":\"8:30AM-7:00PM\","
            + "\"Thursday\":\"8:30AM-7:00PM\","
            + "\"Friday\":\"8:30AM-7:00PM\","
            + "\"Saturday\":\"Closed\","
            + "\"Sunday\":\"Closed\""
            + "},"
            + "\"additional_hours_info\":\"additional hours info\""
            + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    assertJson(
        "{"
            + "\"facilityServiceHours\":{"
            + "\"monday\":\"8:30AM-7:00PM\","
            + "\"tuesday\":\"8:30AM-7:00PM\","
            + "\"wednesday\":\"8:30AM-7:00PM\","
            + "\"thursday\":\"8:30AM-7:00PM\","
            + "\"friday\":\"8:30AM-7:00PM\","
            + "\"saturday\":\"Closed\","
            + "\"sunday\":\"Closed\""
            + "},"
            + "\"additionalHoursInfo\":\"additional hours info\""
            + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    serviceLocation =
        DetailedServiceLocation.builder()
            .appointmentPhoneNumbers(emptyList())
            .emailContacts(emptyList())
            .additionalHoursInfo("additional hours info")
            .build();
    assertJson(
        "{" + "\"additional_hours_info\":\"additional hours info\"" + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    assertJson(
        "{" + "\"additionalHoursInfo\":\"additional hours info\"" + "}",
        DetailedServiceLocation.class,
        serviceLocation);
    serviceLocation =
        DetailedServiceLocation.builder()
            .appointmentPhoneNumbers(emptyList())
            .emailContacts(emptyList())
            .build();
    assertJson("{}", DetailedServiceLocation.class, serviceLocation);
    assertJson("{}", DetailedServiceLocation.class, serviceLocation);
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartDetailedServiceLocationDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeFacilityAttributesWithInvalidDetailedServices() {
    FacilityAttributes attributes =
        FacilityAttributes.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityAttributesWithMixedDetailedServices() {
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DatamartDetailedService.ServiceType.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Smoking.name()))
                                .name(HealthService.Smoking.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeHealthCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Dental.name()))
                                .name(HealthService.Dental.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(uncapitalize(HealthService.Dental.name()))
                    .name(HealthService.Dental.name())
                    .serviceType(DatamartDetailedService.ServiceType.Health)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeHealthFacilityAttributes() {
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Dental.name()))
                                .name(HealthService.Dental.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeHours() {
    Hours hours =
        Hours.builder()
            .monday("8:30AM-7:00PM")
            .tuesday("8:30AM-7:00PM")
            .wednesday("8:30AM-7:00PM")
            .thursday("8:30AM-7:00PM")
            .friday("8:30AM-7:00PM")
            .saturday("Closed")
            .sunday("Closed")
            .build();
    assertJson(
        "{"
            + "\"Sunday\":\"Closed\","
            + "\"Monday\":\"8:30AM-7:00PM\","
            + "\"Tuesday\":\"8:30AM-7:00PM\","
            + "\"Wednesday\":\"8:30AM-7:00PM\","
            + "\"Thursday\":\"8:30AM-7:00PM\","
            + "\"Friday\":\"8:30AM-7:00PM\","
            + "\"Saturday\":\"Closed\""
            + "}",
        Hours.class,
        hours);
    assertJson(
        "{"
            + "\"sunday\":\"Closed\","
            + "\"monday\":\"8:30AM-7:00PM\","
            + "\"tuesday\":\"8:30AM-7:00PM\","
            + "\"wednesday\":\"8:30AM-7:00PM\","
            + "\"thursday\":\"8:30AM-7:00PM\","
            + "\"friday\":\"8:30AM-7:00PM\","
            + "\"saturday\":\"Closed\""
            + "}",
        Hours.class,
        hours);
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartHoursDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeInvalidDetailedService() {
    DatamartDetailedService invalidService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(INVALID_SVC_ID)
                    .name("foo")
                    .serviceType(DatamartDetailedService.ServiceType.Health)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("OnlineScheduling");
    invalidService.serviceInfo().serviceType(DatamartDetailedService.ServiceType.Other);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("baz");
    invalidService.serviceInfo().serviceType(DatamartDetailedService.ServiceType.Health);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"baz\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("Smoking");
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"Smoking\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        invalidService);
  }

  @Test
  @SneakyThrows
  void deserializeOperatingStatus() {
    OperatingStatus operatingStatus =
        OperatingStatus.builder()
            .code(OperatingStatusCode.NORMAL)
            .additionalInfo("additional info")
            .build();
    assertJson(
        "{" + "\"code\":\"NORMAL\"," + "\"additional_info\":\"additional info\"" + "}",
        OperatingStatus.class,
        operatingStatus);
    assertJson(
        "{" + "\"code\":\"NORMAL\"," + "\"additionalInfo\":\"additional info\"" + "}",
        OperatingStatus.class,
        operatingStatus);
    operatingStatus = OperatingStatus.builder().additionalInfo("additional info").build();
    assertJson(
        "{" + "\"additional_info\":\"additional info\"" + "}",
        OperatingStatus.class,
        operatingStatus);
    assertJson(
        "{" + "\"additionalInfo\":\"additional info\"" + "}",
        OperatingStatus.class,
        operatingStatus);
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartOperatingStatusDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeOtherCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                                .name(OtherService.OnlineScheduling.name())
                                .serviceType(DatamartDetailedService.ServiceType.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                    .name(OtherService.OnlineScheduling.name())
                    .serviceType(DatamartDetailedService.ServiceType.Other)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherFacilityAttributes() {
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                                .name(OtherService.OnlineScheduling.name())
                                .serviceType(DatamartDetailedService.ServiceType.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializePatientSatisfaction() {
    PatientSatisfaction patientSatisfaction =
        PatientSatisfaction.builder()
            .primaryCareRoutine(BigDecimal.valueOf(0.94))
            .specialtyCareRoutine(BigDecimal.valueOf(0.95))
            .primaryCareUrgent(BigDecimal.valueOf(0.95))
            .specialtyCareUrgent(BigDecimal.valueOf(0.91))
            .build();
    assertJson(
        "{"
            + "\"primary_care_urgent\":0.95,"
            + "\"primary_care_routine\":0.94,"
            + "\"specialty_care_urgent\":0.91,"
            + "\"specialty_care_routine\":0.95"
            + "}",
        PatientSatisfaction.class,
        patientSatisfaction);
    assertJson(
        "{"
            + "\"primaryCareUrgent\":0.95,"
            + "\"primaryCareRoutine\":0.94,"
            + "\"specialtyCareUrgent\":0.91,"
            + "\"specialtyCareRoutine\":0.95"
            + "}",
        PatientSatisfaction.class,
        patientSatisfaction);
    assertJson("{}", PatientSatisfaction.class, PatientSatisfaction.builder().build());
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartPatientSatisfactionDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializePhone() {
    Phone phone =
        Phone.builder()
            .fax("207-493-3877")
            .main("207-493-3800")
            .pharmacy("207-623-8411 x5770")
            .afterHours("844-750-8426")
            .patientAdvocate("207-623-5760")
            .mentalHealthClinic("207-623-8411 x 7490")
            .enrollmentCoordinator("207-623-8411 x5688")
            .build();
    assertJson(
        "{"
            + "\"fax\":\"207-493-3877\","
            + "\"main\":\"207-493-3800\","
            + "\"pharmacy\":\"207-623-8411 x5770\","
            + "\"after_hours\":\"844-750-8426\","
            + "\"patient_advocate\":\"207-623-5760\","
            + "\"mental_health_clinic\":\"207-623-8411 x 7490\","
            + "\"enrollment_coordinator\":\"207-623-8411 x5688\""
            + "}",
        Phone.class,
        phone);
    assertJson(
        "{"
            + "\"fax\":\"207-493-3877\","
            + "\"main\":\"207-493-3800\","
            + "\"pharmacy\":\"207-623-8411 x5770\","
            + "\"afterHours\":\"844-750-8426\","
            + "\"patientAdvocate\":\"207-623-5760\","
            + "\"mentalHealthClinic\":\"207-623-8411 x 7490\","
            + "\"enrollmentCoordinator\":\"207-623-8411 x5688\""
            + "}",
        Phone.class,
        phone);
    assertJson("{}", Phone.class, Phone.builder().build());
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartPhoneDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeSatisfaction() {
    Satisfaction satisfaction =
        Satisfaction.builder()
            .health(
                PatientSatisfaction.builder()
                    .primaryCareRoutine(BigDecimal.valueOf(0.94))
                    .specialtyCareRoutine(BigDecimal.valueOf(0.95))
                    .primaryCareUrgent(BigDecimal.valueOf(0.95))
                    .specialtyCareUrgent(BigDecimal.valueOf(0.91))
                    .build())
            .effectiveDate(LocalDate.parse("2022-02-20"))
            .build();
    assertJson(
        "{"
            + "\"health\":{"
            + "\"primary_care_urgent\":0.95,"
            + "\"primary_care_routine\":0.94,"
            + "\"specialty_care_urgent\":0.91,"
            + "\"specialty_care_routine\":0.95"
            + "},"
            + "\"effective_date\":\"2022-02-20\""
            + "}",
        Satisfaction.class,
        satisfaction);
    assertJson(
        "{"
            + "\"health\":{"
            + "\"primaryCareUrgent\":0.95,"
            + "\"primaryCareRoutine\":0.94,"
            + "\"specialtyCareUrgent\":0.91,"
            + "\"specialtyCareRoutine\":0.95"
            + "},"
            + "\"effectiveDate\":\"2022-02-20\""
            + "}",
        Satisfaction.class,
        satisfaction);
    assertJson("{}", Satisfaction.class, Satisfaction.builder().build());
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartSatisfactionDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeServices() {
    Services services =
        Services.builder()
            .benefits(List.of(BenefitsService.Pensions, BenefitsService.TransitionAssistance))
            .health(
                List.of(
                    HealthService.Covid19Vaccine,
                    HealthService.EmergencyCare,
                    HealthService.UrgentCare))
            .other(List.of(OtherService.OnlineScheduling))
            .lastUpdated(LocalDate.parse("2022-02-20"))
            .build();
    assertJson(
        "{"
            + "\"other\":["
            + "\"OnlineScheduling\""
            + "],"
            + "\"health\":["
            + "\"covid19Vaccine\","
            + "\"emergencyCare\","
            + "\"urgentCare\""
            + "],"
            + "\"benefits\":["
            + "\"Pensions\","
            + "\"TransitionAssistance\""
            + "],"
            + "\"last_updated\":\"2022-02-20\""
            + "}",
        Services.class,
        services);
    assertJson(
        "{"
            + "\"other\":["
            + "\"OnlineScheduling\""
            + "],"
            + "\"health\":["
            + "\"covid19Vaccine\","
            + "\"emergencyCare\","
            + "\"urgentCare\""
            + "],"
            + "\"benefits\":["
            + "\"Pensions\","
            + "\"TransitionAssistance\""
            + "],"
            + "\"lastUpdated\":\"2022-02-20\""
            + "}",
        Services.class,
        services);
    services =
        Services.builder().benefits(emptyList()).health(emptyList()).other(emptyList()).build();
    assertJson("{}", Services.class, services);
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartServicesDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }

  @Test
  @SneakyThrows
  void deserializeWaitTimes() {
    WaitTimes waitTimes =
        WaitTimes.builder()
            .health(
                List.of(
                    PatientWaitTime.builder()
                        .service(HealthService.Cardiology)
                        .newPatientWaitTime(BigDecimal.valueOf(32.047619))
                        .establishedPatientWaitTime(BigDecimal.valueOf(9.498175))
                        .build()))
            .effectiveDate(LocalDate.parse("2022-02-20"))
            .build();
    assertJson(
        "{"
            + "\"health\":["
            + "{"
            + "\"service\":\"cardiology\","
            + "\"new\":32.047619,"
            + "\"established\":9.498175"
            + "}"
            + "],"
            + "\"effective_date\":\"2022-02-20\""
            + "}",
        WaitTimes.class,
        waitTimes);
    assertJson(
        "{"
            + "\"health\":["
            + "{"
            + "\"service\":\"cardiology\","
            + "\"new\":32.047619,"
            + "\"established\":9.498175"
            + "}"
            + "],"
            + "\"effectiveDate\":\"2022-02-20\""
            + "}",
        WaitTimes.class,
        waitTimes);
    assertJson("{}", WaitTimes.class, WaitTimes.builder().health(emptyList()).build());
    // Exceptions
    assertThatThrownBy(
            () ->
                new DatamartWaitTimesDeserializer(null)
                    .deserialize(null, mock(DeserializationContext.class)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.core.JsonParser.getCodec()\" because \"jsonParser\" is null");
  }
}
