package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getActiveStatus;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getDetailedServices;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFacilityType;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOperationalHoursSpecialInstructions;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOpertingStatus;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getTimeZone;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWaitTimes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility.ActiveStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import gov.va.api.lighthouse.facilities.DatamartFacility.Satisfaction;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.math.BigDecimal;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartFacilityAttributesDeserializer
    extends BaseListDeserializer<FacilityAttributes> {
  public DatamartFacilityAttributesDeserializer() {
    this(null);
  }

  public DatamartFacilityAttributesDeserializer(Class<FacilityAttributes> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public FacilityAttributes deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode nameNode = node.get("name");
    JsonNode facilityTypeNode = getFacilityType(node);
    JsonNode classificationNode = node.get("classification");
    JsonNode websiteNode = node.get("website");
    JsonNode latitudeNode = node.get("lat");
    JsonNode longitudeNode = node.get("long");
    JsonNode timeZoneNode = getTimeZone(node);
    JsonNode addressNode = node.get("address");
    JsonNode phoneNode = node.get("phone");
    JsonNode hoursNode = node.get("hours");
    JsonNode operationalHoursSpecialInstructionsNode = getOperationalHoursSpecialInstructions(node);
    JsonNode servicesNode = node.get("services");
    JsonNode satisfactionNode = node.get("satisfaction");
    JsonNode waitTimesNode = getWaitTimes(node);
    JsonNode mobileNode = node.get("mobile");
    JsonNode activeStatusNode = getActiveStatus(node);
    JsonNode operatingStatusNode = getOpertingStatus(node);
    JsonNode detailedServicesNode = getDetailedServices(node);
    JsonNode visnNode = node.get("visn");

    TypeReference<List<DatamartDetailedService>> detailedServicesRef = new TypeReference<>() {};

    return FacilityAttributes.builder()
        .name(nameNode != null ? MAPPER.convertValue(nameNode, String.class) : null)
        .facilityType(
            facilityTypeNode != null
                ? MAPPER.convertValue(facilityTypeNode, FacilityType.class)
                : null)
        .classification(
            classificationNode != null
                ? MAPPER.convertValue(classificationNode, String.class)
                : null)
        .website(websiteNode != null ? MAPPER.convertValue(websiteNode, String.class) : null)
        .latitude(latitudeNode != null ? MAPPER.convertValue(latitudeNode, BigDecimal.class) : null)
        .longitude(
            longitudeNode != null ? MAPPER.convertValue(longitudeNode, BigDecimal.class) : null)
        .timeZone(timeZoneNode != null ? MAPPER.convertValue(timeZoneNode, String.class) : null)
        .address(addressNode != null ? MAPPER.convertValue(addressNode, Addresses.class) : null)
        .phone(phoneNode != null ? MAPPER.convertValue(phoneNode, Phone.class) : null)
        .hours(hoursNode != null ? MAPPER.convertValue(hoursNode, Hours.class) : null)
        .operationalHoursSpecialInstructions(
            operationalHoursSpecialInstructionsNode != null
                ? MAPPER.convertValue(operationalHoursSpecialInstructionsNode, String.class)
                : null)
        .services(servicesNode != null ? MAPPER.convertValue(servicesNode, Services.class) : null)
        .satisfaction(
            satisfactionNode != null
                ? MAPPER.convertValue(satisfactionNode, Satisfaction.class)
                : null)
        .waitTimes(
            waitTimesNode != null ? MAPPER.convertValue(waitTimesNode, WaitTimes.class) : null)
        .mobile(mobileNode != null ? MAPPER.convertValue(mobileNode, Boolean.class) : null)
        .activeStatus(
            activeStatusNode != null
                ? MAPPER.convertValue(activeStatusNode, ActiveStatus.class)
                : null)
        .operatingStatus(
            operatingStatusNode != null
                ? MAPPER.convertValue(operatingStatusNode, OperatingStatus.class)
                : null)
        .detailedServices(
            detailedServicesNode != null
                ? filterOutInvalidDetailedServices(
                    MAPPER.convertValue(detailedServicesNode, detailedServicesRef))
                : null)
        .visn(visnNode != null ? MAPPER.convertValue(visnNode, String.class) : null)
        .build();
  }
}
