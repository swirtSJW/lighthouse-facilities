package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceInfo.INVALID_SVC_ID;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceLocation;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceInfo;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceType;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public class DatamartDetailedServiceDeserializer extends StdDeserializer<DatamartDetailedService> {
  public DatamartDetailedServiceDeserializer() {
    this(null);
  }

  public DatamartDetailedServiceDeserializer(Class<DatamartDetailedService> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public DatamartDetailedService deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);
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
    JsonNode nameNode = serviceInfoNode != null ? serviceInfoNode.get("name") : null;
    final String serviceName =
        nameNode != null ? createMapper().convertValue(nameNode, String.class) : null;
    JsonNode serviceIdNode = serviceInfoNode != null ? serviceInfoNode.get("serviceId") : null;
    final String serviceId =
        serviceIdNode != null
                && isRecognizedServiceId(createMapper().convertValue(serviceIdNode, String.class))
            ? createMapper().convertValue(serviceIdNode, String.class)
            : // Attempt to construct service id from service name
            serviceIdNode == null && isRecognizedServiceName(serviceName)
                ? getServiceIdForRecognizedServiceName(serviceName)
                : INVALID_SVC_ID;
    JsonNode serviceTypeNode = serviceInfoNode != null ? serviceInfoNode.get("serviceType") : null;
    final ServiceType serviceType =
        serviceTypeNode != null
                && isRecognizedServiceType(
                    createMapper().convertValue(serviceTypeNode, String.class))
            ? ServiceType.fromString(createMapper().convertValue(serviceTypeNode, String.class))
            : // Attempt to infer service type from service id
            !StringUtils.equals(serviceId, INVALID_SVC_ID)
                ? getServiceTypeForServiceId(serviceId)
                : // Default to Health service type
                ServiceType.Health;

    TypeReference<List<AppointmentPhoneNumber>> appointmentNumbersRef = new TypeReference<>() {};
    TypeReference<List<DetailedServiceLocation>> serviceLocationsRef = new TypeReference<>() {};

    return DatamartDetailedService.builder()
        .serviceInfo(
            ServiceInfo.builder()
                .name(serviceName)
                .serviceId(serviceId)
                .serviceType(serviceType)
                .build())
        .active(activeNode != null ? createMapper().convertValue(activeNode, Boolean.class) : false)
        .changed(
            changedNode != null ? createMapper().convertValue(changedNode, String.class) : null)
        .descriptionFacility(
            descriptionFacilityNode != null
                ? createMapper().convertValue(descriptionFacilityNode, String.class)
                : null)
        .appointmentLeadIn(
            appointmentLeadInNode != null
                ? createMapper().convertValue(appointmentLeadInNode, String.class)
                : null)
        .onlineSchedulingAvailable(
            onlineSchedulingAvailableNode != null
                ? createMapper().convertValue(onlineSchedulingAvailableNode, String.class)
                : null)
        .path(pathNode != null ? createMapper().convertValue(pathNode, String.class) : null)
        .phoneNumbers(
            phoneNumbersNode != null
                ? createMapper().convertValue(phoneNumbersNode, appointmentNumbersRef)
                : emptyList())
        .referralRequired(
            referralRequiredNode != null
                ? createMapper().convertValue(referralRequiredNode, String.class)
                : null)
        .serviceLocations(
            serviceLocationsNode != null
                ? createMapper().convertValue(serviceLocationsNode, serviceLocationsRef)
                : emptyList())
        .walkInsAccepted(
            walkInsAcceptedNode != null
                ? createMapper().convertValue(walkInsAcceptedNode, String.class)
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
                    ? BenefitsService.valueOf(name).name()
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
    return StringUtils.equals(name, CMS_OVERLAY_SERVICE_NAME_COVID_19)
        || isRecognizedServiceId(name);
  }

  private boolean isRecognizedServiceType(String type) {
    return Arrays.stream(ServiceType.values())
        .parallel()
        .anyMatch(st -> st.name().equalsIgnoreCase(type));
  }
}
