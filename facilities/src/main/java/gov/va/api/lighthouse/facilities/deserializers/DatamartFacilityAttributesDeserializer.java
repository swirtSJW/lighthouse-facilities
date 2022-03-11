package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.INVALID_SVC_ID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
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
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public class DatamartFacilityAttributesDeserializer extends StdDeserializer<FacilityAttributes> {
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

    JsonNode nameNode = node.get("name");
    JsonNode facilityTypeNode = node.get("facility_type");
    JsonNode classificationNode = node.get("classification");
    JsonNode websiteNode = node.get("website");
    JsonNode latitudeNode = node.get("lat");
    JsonNode longitudeNode = node.get("long");
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

    TypeReference<List<DatamartDetailedService>> detailedServicesRef = new TypeReference<>() {};

    return FacilityAttributes.builder()
        .name(nameNode != null ? createMapper().convertValue(nameNode, String.class) : null)
        .facilityType(
            facilityTypeNode != null
                ? createMapper().convertValue(facilityTypeNode, FacilityType.class)
                : null)
        .classification(
            classificationNode != null
                ? createMapper().convertValue(classificationNode, String.class)
                : null)
        .website(
            websiteNode != null ? createMapper().convertValue(websiteNode, String.class) : null)
        .latitude(
            latitudeNode != null
                ? createMapper().convertValue(latitudeNode, BigDecimal.class)
                : null)
        .longitude(
            longitudeNode != null
                ? createMapper().convertValue(longitudeNode, BigDecimal.class)
                : null)
        .timeZone(
            timeZoneNode != null ? createMapper().convertValue(timeZoneNode, String.class) : null)
        .address(
            addressNode != null ? createMapper().convertValue(addressNode, Addresses.class) : null)
        .phone(phoneNode != null ? createMapper().convertValue(phoneNode, Phone.class) : null)
        .hours(hoursNode != null ? createMapper().convertValue(hoursNode, Hours.class) : null)
        .operationalHoursSpecialInstructions(
            operationalHoursSpecialInstructionsNode != null
                ? createMapper().convertValue(operationalHoursSpecialInstructionsNode, String.class)
                : null)
        .services(
            servicesNode != null ? createMapper().convertValue(servicesNode, Services.class) : null)
        .satisfaction(
            satisfactionNode != null
                ? createMapper().convertValue(satisfactionNode, Satisfaction.class)
                : null)
        .waitTimes(
            waitTimesNode != null
                ? createMapper().convertValue(waitTimesNode, WaitTimes.class)
                : null)
        .mobile(mobileNode != null ? createMapper().convertValue(mobileNode, Boolean.class) : null)
        .activeStatus(
            activeStatusNode != null
                ? createMapper().convertValue(activeStatusNode, ActiveStatus.class)
                : null)
        .operatingStatus(
            operatingStatusNode != null
                ? createMapper().convertValue(operatingStatusNode, OperatingStatus.class)
                : null)
        .detailedServices(
            detailedServicesNode != null
                ? filterOutInvalidDetailedServices(
                    createMapper().convertValue(detailedServicesNode, detailedServicesRef))
                : null)
        .visn(visnNode != null ? createMapper().convertValue(visnNode, String.class) : null)
        .build();
  }

  private List<DatamartDetailedService> filterOutInvalidDetailedServices(
      List<DatamartDetailedService> detailedServices) {
    if (detailedServices != null) {
      // Filter out detailed services containing unrecognized service id
      return detailedServices.stream()
          .filter(x -> !x.serviceId().equals(INVALID_SVC_ID))
          .collect(Collectors.toList());
    }
    return null;
  }
}
