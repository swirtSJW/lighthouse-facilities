package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceInfo.INVALID_SVC_ID;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAppointmentLeadin;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFacilityDescription;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOnlineSchedulingAvailable;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPhoneNumbers;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getReferralRequired;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getServiceLocations;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWalkInsAccepted;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceLocation;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.PatientWaitTime;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceInfo;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceType;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public class DatamartDetailedServiceDeserializer extends StdDeserializer<DatamartDetailedService> {

  private static final ObjectMapper MAPPER = createMapper();

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
    JsonNode descriptionFacilityNode = getFacilityDescription(node);
    JsonNode appointmentLeadInNode = getAppointmentLeadin(node);
    JsonNode onlineSchedulingAvailableNode = getOnlineSchedulingAvailable(node);
    JsonNode pathNode = node.get("path");
    JsonNode phoneNumbersNode = getPhoneNumbers(node);
    JsonNode referralRequiredNode = getReferralRequired(node);
    JsonNode serviceLocationsNode = getServiceLocations(node);
    JsonNode walkInsAcceptedNode = getWalkInsAccepted(node);

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
    JsonNode waitTimeNode = node.get("waitTime");
    JsonNode newNode = waitTimeNode != null ? waitTimeNode.get("new") : null;
    final BigDecimal newPatientWaitTime =
        newNode != null ? createMapper().convertValue(newNode, BigDecimal.class) : null;
    JsonNode establishedNode = waitTimeNode != null ? waitTimeNode.get("established") : null;
    final BigDecimal establishedPatientWaitTime =
        establishedNode != null
            ? createMapper().convertValue(establishedNode, BigDecimal.class)
            : null;
    JsonNode effectiveDateNode = waitTimeNode != null ? waitTimeNode.get("effectiveDate") : null;
    final LocalDate effectiveDate =
        establishedNode != null
            ? createMapper().convertValue(effectiveDateNode, LocalDate.class)
            : null;

    TypeReference<List<AppointmentPhoneNumber>> appointmentNumbersRef = new TypeReference<>() {};
    TypeReference<List<DetailedServiceLocation>> serviceLocationsRef = new TypeReference<>() {};

    return DatamartDetailedService.builder()
        .serviceInfo(
            ServiceInfo.builder()
                .name(serviceName)
                .serviceId(serviceId)
                .serviceType(serviceType)
                .build())
        .waitTime(
            PatientWaitTime.builder()
                .newPatientWaitTime(newPatientWaitTime)
                .establishedPatientWaitTime(establishedPatientWaitTime)
                .effectiveDate(effectiveDate)
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
