package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAppointmentLeadin;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFacilityDescription;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOnlineSchedulingAvailable;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPhoneNumbers;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getReferralRequired;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getServiceLocations;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWalkInsAccepted;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedService.INVALID_SVC_ID;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.api.common.deserializers.BaseDeserializer;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceLocation;
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public class DetailedServiceDeserializer extends BaseDeserializer<DetailedService> {
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

    // Read values using snake_case or camelCase representations
    JsonNode nameNode = node.get("name");
    JsonNode serviceIdNode = node.get("serviceId");
    JsonNode activeNode = node.get("active");
    JsonNode changedNode = node.get("changed");
    JsonNode descriptionFacilityNode = getFacilityDescription(node);
    JsonNode appointmentLeadInNode = getAppointmentLeadin(node);
    JsonNode onlineSchedulingAvailableNode = getOnlineSchedulingAvailable(node);
    JsonNode pathNode = node.get("path");
    JsonNode phoneNumbersNode = getPhoneNumbers(node);
    JsonNode referralRequiredNode = getReferralRequired(node);
    JsonNode serviceLocationsNode = getServiceLocations(node);
    JsonNode walkInsAcceptedNode = getWalkInsAccepted(node);

    String serviceName = isNotNull(nameNode) ? nameNode.asText() : null;
    String serviceId =
        isNotNull(serviceIdNode) && isRecognizedServiceId(serviceIdNode.asText())
            ? serviceIdNode.asText()
            : // Attempt to construct service id from service name
            isNull(serviceIdNode) && isRecognizedServiceName(serviceName)
                ? getServiceIdForRecognizedServiceName(serviceName)
                : INVALID_SVC_ID;

    TypeReference<List<AppointmentPhoneNumber>> appointmentNumbersRef = new TypeReference<>() {};
    TypeReference<List<DetailedServiceLocation>> serviceLocationsRef = new TypeReference<>() {};

    return DetailedService.builder()
        .serviceId(serviceId)
        .name(serviceName)
        .active(isNotNull(activeNode) ? activeNode.asBoolean() : false)
        .changed(isNotNull(changedNode) ? changedNode.asText() : null)
        .descriptionFacility(
            isNotNull(descriptionFacilityNode) ? descriptionFacilityNode.asText() : null)
        .appointmentLeadIn(isNotNull(appointmentLeadInNode) ? appointmentLeadInNode.asText() : null)
        .onlineSchedulingAvailable(
            isNotNull(onlineSchedulingAvailableNode)
                ? onlineSchedulingAvailableNode.asText()
                : null)
        .path(isNotNull(pathNode) ? pathNode.asText() : null)
        .phoneNumbers(
            isNotNull(phoneNumbersNode)
                ? MAPPER.convertValue(phoneNumbersNode, appointmentNumbersRef)
                : null)
        .referralRequired(isNotNull(referralRequiredNode) ? referralRequiredNode.asText() : null)
        .serviceLocations(
            isNotNull(serviceLocationsNode)
                ? MAPPER.convertValue(serviceLocationsNode, serviceLocationsRef)
                : null)
        .walkInsAccepted(isNotNull(walkInsAcceptedNode) ? walkInsAcceptedNode.asText() : null)
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
