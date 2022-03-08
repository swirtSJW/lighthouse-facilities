package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo.INVALID_SVC_ID;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceLocation;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public class DetailedServiceDeserializer extends StdDeserializer<DetailedService> {
  private static final ObjectMapper MAPPER = createMapper();

  public DetailedServiceDeserializer() {
    this(null);
  }

  public DetailedServiceDeserializer(Class<DetailedService> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public DetailedService deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);
    JsonNode activeNode = node.get("active");
    JsonNode changedNode = node.get("changed");
    JsonNode descriptionFacilityNode = node.get("descriptionFacility");
    JsonNode appointmentLeadInNode = node.get("appointmentLeadIn");
    JsonNode onlineSchedulingAvailableNode = node.get("onlineSchedulingAvailable");
    JsonNode pathNode = node.get("path");
    JsonNode phoneNumbersNode = node.get("appointmentPhones");
    JsonNode referralRequiredNode = node.get("referralRequired");
    JsonNode serviceLocationsNode = node.get("serviceLocations");
    JsonNode walkInsAcceptedNode = node.get("walkInsAccepted");

    JsonNode serviceInfoNode = node.get("serviceInfo");
    JsonNode nameNode = serviceInfoNode != null ? serviceInfoNode.get("name") : node.get("name");
    final String serviceName =
        nameNode != null ? MAPPER.convertValue(nameNode, String.class) : null;
    JsonNode serviceIdNode =
        serviceInfoNode != null ? serviceInfoNode.get("serviceId") : node.get("serviceId");
    final String serviceId =
        serviceIdNode != null
                && isRecognizedServiceId(MAPPER.convertValue(serviceIdNode, String.class))
            ? MAPPER.convertValue(serviceIdNode, String.class)
            : // Attempt to construct service id from service name
            serviceIdNode == null && isRecognizedServiceName(serviceName)
                ? getServiceIdForRecognizedServiceName(serviceName)
                : INVALID_SVC_ID;
    JsonNode serviceTypeNode = serviceInfoNode != null ? serviceInfoNode.get("serviceType") : null;
    final ServiceType serviceType =
        serviceTypeNode != null
                && isRecognizedServiceType(MAPPER.convertValue(serviceTypeNode, String.class))
            ? ServiceType.fromString(MAPPER.convertValue(serviceTypeNode, String.class))
            : // Attempt to infer service type from service id
            !StringUtils.equals(serviceId, INVALID_SVC_ID)
                ? getServiceTypeForServiceId(serviceId)
                : // Default to Health service type
                ServiceType.Health;

    TypeReference<List<AppointmentPhoneNumber>> appointmentNumbersRef = new TypeReference<>() {};
    TypeReference<List<DetailedServiceLocation>> serviceLocationsRef = new TypeReference<>() {};

    return DetailedService.builder()
        .serviceInfo(
            ServiceInfo.builder()
                .name(serviceName)
                .serviceId(serviceId)
                .serviceType(serviceType)
                .build())
        .active(activeNode != null ? MAPPER.convertValue(activeNode, Boolean.class) : false)
        .changed(changedNode != null ? MAPPER.convertValue(changedNode, String.class) : null)
        .descriptionFacility(
            descriptionFacilityNode != null
                ? MAPPER.convertValue(descriptionFacilityNode, String.class)
                : null)
        .appointmentLeadIn(
            appointmentLeadInNode != null
                ? MAPPER.convertValue(appointmentLeadInNode, String.class)
                : null)
        .onlineSchedulingAvailable(
            onlineSchedulingAvailableNode != null
                ? MAPPER.convertValue(onlineSchedulingAvailableNode, String.class)
                : null)
        .path(pathNode != null ? MAPPER.convertValue(pathNode, String.class) : null)
        .phoneNumbers(
            phoneNumbersNode != null
                ? MAPPER.convertValue(phoneNumbersNode, appointmentNumbersRef)
                : emptyList())
        .referralRequired(
            referralRequiredNode != null
                ? MAPPER.convertValue(referralRequiredNode, String.class)
                : null)
        .serviceLocations(
            serviceLocationsNode != null
                ? MAPPER.convertValue(serviceLocationsNode, serviceLocationsRef)
                : emptyList())
        .walkInsAccepted(
            walkInsAcceptedNode != null
                ? MAPPER.convertValue(walkInsAcceptedNode, String.class)
                : null)
        .build();
  }

  private String getServiceIdForRecognizedServiceName(String name) {
    return uncapitalize(
        StringUtils.equals(name, "COVID-19 vaccines")
            ? HealthService.Covid19Vaccine.name()
            : Arrays.stream(HealthService.values())
                    .parallel()
                    .anyMatch(hs -> hs.name().equalsIgnoreCase(name))
                ? HealthService.fromString(name).name()
                : Arrays.stream(BenefitsService.values())
                        .parallel()
                        .anyMatch(bs -> bs.name().equalsIgnoreCase(name))
                    ? BenefitsService.fromString(name).name()
                    : Arrays.stream(OtherService.values())
                            .parallel()
                            .anyMatch(os -> os.name().equalsIgnoreCase(name))
                        ? OtherService.valueOf(name).name()
                        : INVALID_SVC_ID);
  }

  private ServiceType getServiceTypeForServiceId(String serviceId) {
    return Arrays.stream(HealthService.values())
            .parallel()
            .anyMatch(hs -> hs.name().equalsIgnoreCase(serviceId))
        ? ServiceType.Health
        : Arrays.stream(BenefitsService.values())
                .parallel()
                .anyMatch(bs -> bs.name().equalsIgnoreCase(serviceId))
            ? ServiceType.Benefits
            : Arrays.stream(OtherService.values())
                    .parallel()
                    .anyMatch(os -> os.name().equalsIgnoreCase(serviceId))
                ? ServiceType.Other
                : // Default to Health service type
                ServiceType.Health;
  }

  private boolean isRecognizedServiceId(String serviceId) {
    return Arrays.stream(HealthService.values())
            .parallel()
            .anyMatch(hs -> hs.name().equalsIgnoreCase(serviceId))
        || Arrays.stream(BenefitsService.values())
            .parallel()
            .anyMatch(bs -> bs.name().equalsIgnoreCase(serviceId))
        || Arrays.stream(OtherService.values())
            .parallel()
            .anyMatch(os -> os.name().equalsIgnoreCase(serviceId));
  }

  private boolean isRecognizedServiceName(String name) {
    return StringUtils.equals(name, "COVID-19 vaccines") || isRecognizedServiceId(name);
  }

  private boolean isRecognizedServiceType(String type) {
    return Arrays.stream(ServiceType.values())
        .parallel()
        .anyMatch(st -> st.name().equalsIgnoreCase(type));
  }
}
