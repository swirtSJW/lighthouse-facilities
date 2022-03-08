package gov.va.api.lighthouse.facilities.deserializers;

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
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.math.BigDecimal;
import java.util.List;
import lombok.NonNull;

public abstract class BaseDatamartFacilityAttributesDeserializer<T>
    extends BaseDatamartServicesDeserializer<T> {
  public BaseDatamartFacilityAttributesDeserializer(Class<T> t) {
    super(t);
  }

  public abstract T deserialize(JsonParser jp, DeserializationContext deserializationContext);

  @SuppressWarnings("unchecked")
  protected FacilityAttributes deserializeDatamartFacilityAttributes(
      @NonNull JsonNode attributesNode, @NonNull String linkerUrl, @NonNull String facilityId) {
    JsonNode nameNode = attributesNode.get("name");
    JsonNode facilityTypeNode = attributesNode.get("facility_type");
    JsonNode classificationNode = attributesNode.get("classification");
    JsonNode websiteNode = attributesNode.get("website");
    JsonNode latNode = attributesNode.get("lat");
    JsonNode longNode = attributesNode.get("long");
    JsonNode timeZoneNode = attributesNode.get("time_zone");
    JsonNode addressNode = attributesNode.get("address");
    JsonNode phoneNode = attributesNode.get("phone");
    JsonNode hoursNode = attributesNode.get("hours");
    JsonNode operationalHoursSpecialInstructionsNode =
        attributesNode.get("operational_hours_special_instructions");
    JsonNode servicesNode = attributesNode.get("services");
    JsonNode satisfactionNode = attributesNode.get("satisfaction");
    JsonNode waitTimesNode = attributesNode.get("wait_times");
    JsonNode mobileNode = attributesNode.get("mobile");
    JsonNode activeStatusNode = attributesNode.get("active_status");
    JsonNode operatingStatusNode = attributesNode.get("operating_status");
    JsonNode detailedServicesNode = attributesNode.get("detailed_services");
    JsonNode visnNode = attributesNode.get("visn");
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
        .latitude(latNode != null ? MAPPER.convertValue(latNode, BigDecimal.class) : null)
        .longitude(longNode != null ? MAPPER.convertValue(longNode, BigDecimal.class) : null)
        .timeZone(timeZoneNode != null ? MAPPER.convertValue(timeZoneNode, String.class) : null)
        .address(addressNode != null ? MAPPER.convertValue(addressNode, Addresses.class) : null)
        .phone(phoneNode != null ? MAPPER.convertValue(phoneNode, Phone.class) : null)
        .hours(hoursNode != null ? MAPPER.convertValue(hoursNode, Hours.class) : null)
        .operationalHoursSpecialInstructions(
            operationalHoursSpecialInstructionsNode != null
                ? MAPPER.convertValue(operationalHoursSpecialInstructionsNode, String.class)
                : null)
        .services(
            servicesNode != null
                ? deserializeDatamartFacilityServices(servicesNode, linkerUrl, facilityId)
                : null)
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
