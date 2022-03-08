package gov.va.api.lighthouse.facilities.api.v0.deserializers;

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
import gov.va.api.lighthouse.facilities.api.v0.Facility.WaitTimes;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility.Properties;
import java.util.List;
import lombok.SneakyThrows;

public class GeoFacilityPropertiesDeserializer extends BaseServicesDeserializer<Properties> {
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

    JsonNode idNode = node.get("id");
    JsonNode nameNode = node.get("name");
    JsonNode facilityTypeNode = node.get("facility_type");
    JsonNode classificationNode = node.get("classification");
    JsonNode websiteNode = node.get("website");
    JsonNode timeZoneNode = node.get("time_zone");
    JsonNode addressNode = node.get("address");
    JsonNode phoneNode = node.get("phone");
    JsonNode hoursNode = node.get("hours");
    JsonNode operationalHoursSpecialInstructionsNode =
        node.get("operational_hours_special_instructions");
    JsonNode servicesNode = node.get("services");
    JsonNode satisfactionNode = node.get("satisfaction");
    JsonNode waitTimesNode = node.get("wait_times");
    JsonNode mobileNode = node.get("mobile");
    JsonNode activeStatusNode = node.get("active_status");
    JsonNode operatingStatusNode = node.get("operating_status");
    JsonNode detailedServicesNode = node.get("detailed_services");
    JsonNode visnNode = node.get("visn");

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return Properties.builder()
        .id(idNode != null ? MAPPER.convertValue(idNode, String.class) : null)
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
        .timeZone(timeZoneNode != null ? MAPPER.convertValue(timeZoneNode, String.class) : null)
        .address(addressNode != null ? MAPPER.convertValue(addressNode, Addresses.class) : null)
        .phone(phoneNode != null ? MAPPER.convertValue(phoneNode, Phone.class) : null)
        .hours(hoursNode != null ? MAPPER.convertValue(hoursNode, Hours.class) : null)
        .operationalHoursSpecialInstructions(
            operationalHoursSpecialInstructionsNode != null
                ? MAPPER.convertValue(operationalHoursSpecialInstructionsNode, String.class)
                : null)
        .services(servicesNode != null ? deserializeFacilityServices(servicesNode) : null)
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
