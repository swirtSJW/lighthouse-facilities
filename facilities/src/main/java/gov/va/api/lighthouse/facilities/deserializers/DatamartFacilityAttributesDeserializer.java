package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getActiveStatus;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getDetailedServices;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFacilityType;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOperationalHoursSpecialInstructions;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOpertingStatus;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getTimeZone;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWaitTimes;

import com.fasterxml.jackson.core.JsonParser;
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
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

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
        .name(isNotNull(nameNode) ? nameNode.asText() : null)
        .facilityType(
            isNotNull(facilityTypeNode)
                ? MAPPER.convertValue(facilityTypeNode, FacilityType.class)
                : null)
        .classification(isNotNull(classificationNode) ? classificationNode.asText() : null)
        .website(isNotNull(websiteNode) ? websiteNode.asText() : null)
        .latitude(
            isNotNull(latitudeNode) ? MAPPER.convertValue(latitudeNode, BigDecimal.class) : null)
        .longitude(
            isNotNull(longitudeNode) ? MAPPER.convertValue(longitudeNode, BigDecimal.class) : null)
        .timeZone(isNotNull(timeZoneNode) ? timeZoneNode.asText() : null)
        .address(isNotNull(addressNode) ? MAPPER.convertValue(addressNode, Addresses.class) : null)
        .phone(isNotNull(phoneNode) ? MAPPER.convertValue(phoneNode, Phone.class) : null)
        .hours(isNotNull(hoursNode) ? MAPPER.convertValue(hoursNode, Hours.class) : null)
        .operationalHoursSpecialInstructions(
            isNotNull(operationalHoursSpecialInstructionsNode)
                ? operationalHoursSpecialInstructionsNode.asText()
                : null)
        .services(
            isNotNull(servicesNode) ? MAPPER.convertValue(servicesNode, Services.class) : null)
        .satisfaction(
            isNotNull(satisfactionNode)
                ? MAPPER.convertValue(satisfactionNode, Satisfaction.class)
                : null)
        .waitTimes(
            isNotNull(waitTimesNode) ? MAPPER.convertValue(waitTimesNode, WaitTimes.class) : null)
        .mobile(isNotNull(mobileNode) ? mobileNode.asBoolean() : null)
        .activeStatus(
            isNotNull(activeStatusNode)
                ? MAPPER.convertValue(activeStatusNode, ActiveStatus.class)
                : null)
        .operatingStatus(
            isNotNull(operatingStatusNode)
                ? MAPPER.convertValue(operatingStatusNode, OperatingStatus.class)
                : null)
        .detailedServices(
            isNotNull(detailedServicesNode)
                ? filterOutInvalidDetailedServices(
                    MAPPER.convertValue(detailedServicesNode, detailedServicesRef))
                : null)
        .visn(isNotNull(visnNode) ? visnNode.asText() : null)
        .build();
  }
}
