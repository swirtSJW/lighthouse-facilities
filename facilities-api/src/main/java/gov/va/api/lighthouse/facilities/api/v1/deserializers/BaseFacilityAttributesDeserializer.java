package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.api.v1.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses;
import gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Hours;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Phone;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Satisfaction;
import gov.va.api.lighthouse.facilities.api.v1.Facility.WaitTimes;
import java.math.BigDecimal;
import java.util.List;
import lombok.NonNull;

public abstract class BaseFacilityAttributesDeserializer<T> extends BaseServicesDeserializer<T> {
  public BaseFacilityAttributesDeserializer(Class<T> t) {
    super(t);
  }

  public abstract T deserialize(JsonParser jp, DeserializationContext deserializationContext);

  @SuppressWarnings("unchecked")
  protected FacilityAttributes deserializeFacilityAttributes(@NonNull JsonNode attributesNode) {
    JsonNode nameNode = attributesNode.get("name");
    JsonNode facilityTypeNode = attributesNode.get("facilityType");
    JsonNode classificationNode = attributesNode.get("classification");
    JsonNode websiteNode = attributesNode.get("website");
    JsonNode latNode = attributesNode.get("lat");
    JsonNode longNode = attributesNode.get("long");
    JsonNode timeZoneNode = attributesNode.get("timeZone");
    JsonNode addressNode = attributesNode.get("address");
    JsonNode phoneNode = attributesNode.get("phone");
    JsonNode hoursNode = attributesNode.get("hours");
    JsonNode operationalHoursSpecialInstructionsNode =
        attributesNode.get("operationalHoursSpecialInstructions");
    JsonNode servicesNode = attributesNode.get("services");
    JsonNode satisfactionNode = attributesNode.get("satisfaction");
    JsonNode waitTimesNode = attributesNode.get("waitTimes");
    JsonNode mobileNode = attributesNode.get("mobile");
    JsonNode activeStatusNode = attributesNode.get("activeStatus");
    JsonNode operatingStatusNode = attributesNode.get("operatingStatus");
    JsonNode visnNode = attributesNode.get("visn");

    TypeReference<List<String>> operatingInstructionsRef = new TypeReference<>() {};

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
        .latitude(latNode != null ? MAPPER.convertValue(latNode, BigDecimal.class) : null)
        .longitude(longNode != null ? MAPPER.convertValue(longNode, BigDecimal.class) : null)
        .timeZone(timeZoneNode != null ? MAPPER.convertValue(timeZoneNode, String.class) : null)
        .address(addressNode != null ? MAPPER.convertValue(addressNode, Addresses.class) : null)
        .phone(phoneNode != null ? MAPPER.convertValue(phoneNode, Phone.class) : null)
        .hours(hoursNode != null ? MAPPER.convertValue(hoursNode, Hours.class) : null)
        .operationalHoursSpecialInstructions(
            operationalHoursSpecialInstructionsNode != null
                ? MAPPER.convertValue(
                    operationalHoursSpecialInstructionsNode, operatingInstructionsRef)
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
        .visn(visnNode != null ? MAPPER.convertValue(visnNode, String.class) : null)
        .build();
  }
}
