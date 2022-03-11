package gov.va.api.lighthouse.facilities.api.v0.deserializers;

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
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Addresses;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityType;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Hours;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Phone;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Satisfaction;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Services;
import gov.va.api.lighthouse.facilities.api.v0.Facility.WaitTimes;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility.Properties;
import java.util.List;
import lombok.SneakyThrows;

public class GeoFacilityPropertiesDeserializer extends BaseListDeserializer<Properties> {
  public GeoFacilityPropertiesDeserializer() {
    this(null);
  }

  public GeoFacilityPropertiesDeserializer(Class<Properties> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public Properties deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode idNode = node.get("id");
    JsonNode nameNode = node.get("name");
    JsonNode facilityTypeNode = getFacilityType(node);
    JsonNode classificationNode = node.get("classification");
    JsonNode websiteNode = node.get("website");
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

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return Properties.builder()
        .id(isNotNull(idNode) ? idNode.asText() : null)
        .name(isNotNull(nameNode) ? nameNode.asText() : null)
        .facilityType(
            isNotNull(facilityTypeNode)
                ? MAPPER.convertValue(facilityTypeNode, FacilityType.class)
                : null)
        .classification(isNotNull(classificationNode) ? classificationNode.asText() : null)
        .website(isNotNull(websiteNode) ? websiteNode.asText() : null)
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
