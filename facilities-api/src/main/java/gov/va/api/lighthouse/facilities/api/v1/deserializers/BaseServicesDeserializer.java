package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import static gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo.INVALID_SVC_ID;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.TypedService.INVALID_LINK;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Services;
import gov.va.api.lighthouse.facilities.api.v1.Facility.TypedService;
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

  private List<TypedService<BenefitsService>> deserializeFacilityBenefitsServices(
      @NonNull ArrayNode benefitsNode) {
    final ArrayList<TypedService<BenefitsService>> benefitsServices = new ArrayList<>();
    for (final Iterator<JsonNode> nodeIterator = benefitsNode.elements();
        nodeIterator.hasNext(); ) {
      JsonNode benefitsNodeService = nodeIterator.next();
      // Attempt to read benefits service as service object in list of service objects
      JsonNode nameNode = benefitsNodeService.get("name");
      JsonNode serviceIdNode = benefitsNodeService.get("serviceId");
      JsonNode linkNode = benefitsNodeService.get("link");
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
      String serviceName =
          (nameNode != null)
              ? MAPPER.convertValue(nameNode, String.class)
              : !serviceId.equals(INVALID_SVC_ID) && isRecognizedBenefitsServiceName(serviceId)
                  ? getBenefitsServiceNameForRecognizedServiceId(serviceId)
                  : null;
      if (!INVALID_SVC_ID.equals(serviceId)) {
        String link =
            (linkNode != null) ? MAPPER.convertValue(linkNode, String.class) : INVALID_LINK;
        benefitsServices.add(
            new TypedService<BenefitsService>(
                BenefitsService.fromString(serviceId), serviceName, link));
      }
    }
    return benefitsServices.stream().toList();
  }

  private List<TypedService<HealthService>> deserializeFacilityHealthServices(
      @NonNull ArrayNode healthNode) {
    final ArrayList<TypedService<HealthService>> healthServices = new ArrayList<>();
    for (final Iterator<JsonNode> nodeIterator = healthNode.elements(); nodeIterator.hasNext(); ) {
      JsonNode healthNodeService = nodeIterator.next();
      // Attempt to read health service as service object in list of service objects
      JsonNode nameNode = healthNodeService.get("name");
      JsonNode serviceIdNode = healthNodeService.get("serviceId");
      JsonNode linkNode = healthNodeService.get("link");
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
      String serviceName =
          (nameNode != null)
              ? MAPPER.convertValue(nameNode, String.class)
              : !serviceId.equals(INVALID_SVC_ID) && isRecognizedHealthServiceName(serviceId)
                  ? getHealthServiceNameForRecognizedServiceId(serviceId)
                  : null;
      if (!INVALID_SVC_ID.equals(serviceId)) {
        String link =
            (linkNode != null) ? MAPPER.convertValue(linkNode, String.class) : INVALID_LINK;
        healthServices.add(
            new TypedService<HealthService>(
                HealthService.fromString(serviceId), serviceName, link));
      }
    }
    return healthServices.stream().toList();
  }

  private List<TypedService<OtherService>> deserializeFacilityOtherServices(
      @NonNull ArrayNode otherNode) {
    final ArrayList<TypedService<OtherService>> otherServices = new ArrayList<>();
    for (final Iterator<JsonNode> nodeIterator = otherNode.elements(); nodeIterator.hasNext(); ) {
      JsonNode otherNodeService = nodeIterator.next();
      // Attempt to read other service as service object in list of service objects
      JsonNode nameNode = otherNodeService.get("name");
      JsonNode serviceIdNode = otherNodeService.get("serviceId");
      JsonNode linkNode = otherNodeService.get("link");
      String otherEnumValue = otherNodeService.asText();
      String serviceId =
          (serviceIdNode != null
                  && isRecognizedOtherServiceName(MAPPER.convertValue(serviceIdNode, String.class)))
              ? MAPPER.convertValue(serviceIdNode, String.class)
              : // Attempt to read other service as an enum value in an enumerated list
              isRecognizedOtherServiceName(otherEnumValue)
                  ? uncapitalize(otherEnumValue)
                  : INVALID_SVC_ID;
      String serviceName =
          (nameNode != null)
              ? MAPPER.convertValue(nameNode, String.class)
              : !serviceId.equals(INVALID_SVC_ID) && isRecognizedOtherServiceName(serviceId)
                  ? getOtherServiceNameForRecognizedServiceId(serviceId)
                  : null;
      if (!INVALID_SVC_ID.equals(serviceId)) {
        String link =
            (linkNode != null) ? MAPPER.convertValue(linkNode, String.class) : INVALID_LINK;
        otherServices.add(
            new TypedService<OtherService>(
                OtherService.valueOf(capitalize(serviceId)), serviceName, link));
      }
    }
    return otherServices.stream().toList();
  }

  protected Services deserializeFacilityServices(@NonNull JsonNode servicesNode) {
    JsonNode otherNode = servicesNode.get("other");
    JsonNode healthNode = servicesNode.get("health");
    JsonNode benefitsNode = servicesNode.get("benefits");
    JsonNode linkNode = servicesNode.get("link");
    JsonNode lastUpdatedNode = servicesNode.get("lastUpdated");
    return Services.builder()
        .other(otherNode != null ? deserializeFacilityOtherServices((ArrayNode) otherNode) : null)
        .health(
            healthNode != null ? deserializeFacilityHealthServices((ArrayNode) healthNode) : null)
        .benefits(
            benefitsNode != null
                ? deserializeFacilityBenefitsServices((ArrayNode) benefitsNode)
                : null)
        .link(linkNode != null ? MAPPER.convertValue(linkNode, String.class) : INVALID_LINK)
        .lastUpdated(
            lastUpdatedNode != null ? MAPPER.convertValue(lastUpdatedNode, LocalDate.class) : null)
        .build();
  }

  private String getBenefitsServiceNameForRecognizedServiceId(String serviceId) {
    return isRecognizedBenefitsServiceName(serviceId)
        ? BenefitsService.fromString(serviceId).name()
        : capitalize(serviceId);
  }

  private String getHealthServiceNameForRecognizedServiceId(String serviceId) {
    return isRecognizedHealthServiceName(serviceId)
        ? HealthService.fromString(serviceId).name()
        : capitalize(serviceId);
  }

  private String getOtherServiceNameForRecognizedServiceId(String serviceId) {
    return isRecognizedOtherServiceName(serviceId)
        ? OtherService.valueOf(capitalize(serviceId)).name()
        : capitalize(serviceId);
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
