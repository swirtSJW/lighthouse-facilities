package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAdditionalHoursInfo;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEmailContacts;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFacilityServiceHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPhoneNumbers;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getServiceLocationAddress;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceAddress;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceEmailContact;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceHours;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceLocation;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartDetailedServiceLocationDeserializer
    extends BaseDeserializer<DetailedServiceLocation> {
  public DatamartDetailedServiceLocationDeserializer() {
    this(null);
  }

  public DatamartDetailedServiceLocationDeserializer(Class<DetailedServiceLocation> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public DetailedServiceLocation deserialize(
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

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
            isNotNull(additionalHoursInfoNode) ? additionalHoursInfoNode.asText() : null)
        .emailContacts(
            isNotNull(emailContactsNode)
                ? MAPPER.convertValue(emailContactsNode, emailContactsRef)
                : null)
        .facilityServiceHours(
            isNotNull(facilityServiceHoursNode)
                ? MAPPER.convertValue(facilityServiceHoursNode, DetailedServiceHours.class)
                : null)
        .appointmentPhoneNumbers(
            isNotNull(appointmentPhoneNumbersNode)
                ? MAPPER.convertValue(appointmentPhoneNumbersNode, phoneNumbersRef)
                : null)
        .serviceLocationAddress(
            isNotNull(serviceLocationAddressNode)
                ? MAPPER.convertValue(serviceLocationAddressNode, DetailedServiceAddress.class)
                : null)
        .build();
  }
}
