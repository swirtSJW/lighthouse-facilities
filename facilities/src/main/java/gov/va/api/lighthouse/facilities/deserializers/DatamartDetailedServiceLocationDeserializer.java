package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAdditionalHoursInfo;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEmailContacts;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFacilityServiceHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPhoneNumbers;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getServiceLocationAddress;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceAddress;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceEmailContact;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceHours;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceLocation;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartDetailedServiceLocationDeserializer
    extends StdDeserializer<DetailedServiceLocation> {

  private static final ObjectMapper MAPPER = createMapper();

  public DatamartDetailedServiceLocationDeserializer() {
    this(null);
  }

  public DatamartDetailedServiceLocationDeserializer(Class<DetailedServiceLocation> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public DetailedServiceLocation deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode additionalHoursInfoNode = getAdditionalHoursInfo(node);
    JsonNode emailContactsNode = getEmailContacts(node);
    JsonNode facilityServiceHoursNode = getFacilityServiceHours(node);
    JsonNode appointmentPhoneNumbersNode = getPhoneNumbers(node);
    JsonNode serviceLocationAddressNode = getServiceLocationAddress(node);

    TypeReference<List<DetailedServiceEmailContact>> emailContactsRef = new TypeReference<>() {};
    TypeReference<List<AppointmentPhoneNumber>> phoneNumbersRef = new TypeReference<>() {};

    return DetailedServiceLocation.builder()
        .additionalHoursInfo(
            additionalHoursInfoNode != null
                ? MAPPER.convertValue(additionalHoursInfoNode, String.class)
                : null)
        .emailContacts(
            emailContactsNode != null
                ? MAPPER.convertValue(emailContactsNode, emailContactsRef)
                : emptyList())
        .facilityServiceHours(
            facilityServiceHoursNode != null
                ? MAPPER.convertValue(facilityServiceHoursNode, DetailedServiceHours.class)
                : null)
        .appointmentPhoneNumbers(
            appointmentPhoneNumbersNode != null
                ? MAPPER.convertValue(appointmentPhoneNumbersNode, phoneNumbersRef)
                : emptyList())
        .serviceLocationAddress(
            serviceLocationAddressNode != null
                ? MAPPER.convertValue(serviceLocationAddressNode, DetailedServiceAddress.class)
                : null)
        .build();
  }
}
