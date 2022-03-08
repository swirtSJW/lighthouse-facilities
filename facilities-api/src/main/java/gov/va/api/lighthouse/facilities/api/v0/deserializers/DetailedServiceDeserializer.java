package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v0.DetailedService.INVALID_SVC_ID;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceLocation;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
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
    JsonNode descriptionFacilityNode = node.get("description_facility");
    JsonNode appointmentLeadInNode = node.get("appointment_leadin");
    JsonNode onlineSchedulingAvailableNode = node.get("online_scheduling_available");
    JsonNode pathNode = node.get("path");
    JsonNode phoneNumbersNode = node.get("appointment_phones");
    JsonNode referralRequiredNode = node.get("referral_required");
    JsonNode serviceLocationsNode = node.get("service_locations");
    JsonNode walkInsAcceptedNode = node.get("walk_ins_accepted");

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

    TypeReference<List<AppointmentPhoneNumber>> appointmentNumbersRef = new TypeReference<>() {};
    TypeReference<List<DetailedServiceLocation>> serviceLocationsRef = new TypeReference<>() {};

    return DetailedService.builder()
        .serviceId(serviceId)
        .name(serviceName)
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
}
