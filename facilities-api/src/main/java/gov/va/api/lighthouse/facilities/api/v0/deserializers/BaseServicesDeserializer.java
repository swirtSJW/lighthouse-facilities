package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import static gov.va.api.lighthouse.facilities.api.v0.DetailedService.INVALID_SVC_ID;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableList;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Services;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;

public abstract class BaseServicesDeserializer<T> extends BaseListDeserializer<T> {
  public BaseServicesDeserializer(Class<T> t) {
    super(t);
  }

  public abstract T deserialize(JsonParser jp, DeserializationContext deserializationContext);

  private List<BenefitsService> deserializeFacilityBenefitsServices(
      @NonNull ArrayNode benefitsNode) {
    final ArrayList<BenefitsService> benefitsServices = new ArrayList<>();
    for (final Iterator<JsonNode> nodeIterator = benefitsNode.elements();
        nodeIterator.hasNext(); ) {
      JsonNode benefitsNodeService = nodeIterator.next();
      // Attempt to read benefits service as new service object in list of service objects
      JsonNode serviceIdNode = benefitsNodeService.get("serviceId");
      String benefitsEnumValue = benefitsNodeService.asText();
      String serviceId =
          (serviceIdNode != null
                  && isRecognizedBenefitsServiceName(
                      MAPPER.convertValue(serviceIdNode, String.class)))
              ? MAPPER.convertValue(serviceIdNode, String.class)
              : // Attempt to read benefits service as an enum value in an enumerated list
              isRecognizedBenefitsServiceName(benefitsEnumValue)
                  ? uncapitalize(benefitsEnumValue)
                  : INVALID_SVC_ID;
      if (!INVALID_SVC_ID.equals(serviceId)) {
        benefitsServices.add(BenefitsService.fromString(serviceId));
      }
    }
    return ImmutableList.copyOf(benefitsServices);
  }

  private List<HealthService> deserializeFacilityHealthServices(@NonNull ArrayNode healthNode) {
    final ArrayList<HealthService> healthServices = new ArrayList<>();
    for (final Iterator<JsonNode> nodeIterator = healthNode.elements(); nodeIterator.hasNext(); ) {
      JsonNode healthNodeService = nodeIterator.next();
      // Attempt to read health service as new service object in list of service objects
      JsonNode serviceIdNode = healthNodeService.get("serviceId");
      String healthEnumValue = healthNodeService.asText();
      String serviceId =
          (serviceIdNode != null
                  && isRecognizedHealthServiceName(
                      MAPPER.convertValue(serviceIdNode, String.class)))
              ? MAPPER.convertValue(serviceIdNode, String.class)
              : // Attempt to read health service as an enum value in an enumerated list
              isRecognizedHealthServiceName(healthEnumValue)
                  ? uncapitalize(healthEnumValue)
                  : INVALID_SVC_ID;
      if (!INVALID_SVC_ID.equals(serviceId)) {
        healthServices.add(HealthService.fromString(serviceId));
      }
    }
    return ImmutableList.copyOf(healthServices);
  }

  private List<OtherService> deserializeFacilityOtherServices(@NonNull ArrayNode otherNode) {
    final ArrayList<OtherService> otherServices = new ArrayList<>();
    for (final Iterator<JsonNode> nodeIterator = otherNode.elements(); nodeIterator.hasNext(); ) {
      JsonNode otherNodeService = nodeIterator.next();
      // Attempt to read other service as new service object in list of service objects
      JsonNode serviceIdNode = otherNodeService.get("serviceId");
      String otherEnumValue = otherNodeService.asText();
      String serviceId =
          (serviceIdNode != null
                  && isRecognizedOtherServiceName(MAPPER.convertValue(serviceIdNode, String.class)))
              ? MAPPER.convertValue(serviceIdNode, String.class)
              : // Attempt to read other service as an enum value in an enumerated list
              isRecognizedOtherServiceName(otherEnumValue)
                  ? uncapitalize(otherEnumValue)
                  : INVALID_SVC_ID;
      if (!INVALID_SVC_ID.equals(serviceId)) {
        otherServices.add(OtherService.valueOf(capitalize(serviceId)));
      }
    }
    return ImmutableList.copyOf(otherServices);
  }

  protected Services deserializeFacilityServices(@NonNull JsonNode servicesNode) {
    JsonNode otherNode = servicesNode.get("other");
    JsonNode healthNode = servicesNode.get("health");
    JsonNode benefitsNode = servicesNode.get("benefits");
    JsonNode lastUpdatedNode = servicesNode.get("last_updated");
    return Services.builder()
        .other(otherNode != null ? deserializeFacilityOtherServices((ArrayNode) otherNode) : null)
        .health(
            healthNode != null ? deserializeFacilityHealthServices((ArrayNode) healthNode) : null)
        .benefits(
            benefitsNode != null
                ? deserializeFacilityBenefitsServices((ArrayNode) benefitsNode)
                : null)
        .lastUpdated(
            lastUpdatedNode != null ? MAPPER.convertValue(lastUpdatedNode, LocalDate.class) : null)
        .build();
  }

  private boolean isRecognizedBenefitsServiceName(String serviceId) {
    return Arrays.stream(BenefitsService.values())
        .parallel()
        .anyMatch(bs -> bs.name().equalsIgnoreCase(serviceId));
  }

  private boolean isRecognizedHealthServiceName(String serviceId) {
    return Arrays.stream(HealthService.values())
        .parallel()
        .anyMatch(hs -> hs.name().equalsIgnoreCase(serviceId));
  }

  private boolean isRecognizedOtherServiceName(String serviceId) {
    return Arrays.stream(OtherService.values())
        .parallel()
        .anyMatch(os -> os.name().equalsIgnoreCase(serviceId));
  }
}
