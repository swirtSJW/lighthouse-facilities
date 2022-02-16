package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceInfo.INVALID_SVC_ID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public class DatamartCmsOverlayDeserializer extends StdDeserializer<DatamartCmsOverlay> {
  public DatamartCmsOverlayDeserializer() {
    this(null);
  }

  public DatamartCmsOverlayDeserializer(Class<DatamartCmsOverlay> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public DatamartCmsOverlay deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);
    JsonNode operatingStatusNode = node.get("operating_status");
    JsonNode detailedServicesNode = node.get("detailed_services");

    TypeReference<List<DatamartDetailedService>> detailedServicesRef = new TypeReference<>() {};

    return DatamartCmsOverlay.builder()
        .operatingStatus(
            operatingStatusNode != null
                ? createMapper().convertValue(operatingStatusNode, OperatingStatus.class)
                : null)
        .detailedServices(
            detailedServicesNode != null
                ? filterOutInvalidDetailedServices(
                    createMapper().convertValue(detailedServicesNode, detailedServicesRef))
                : null)
        .build();
  }

  private List<DatamartDetailedService> filterOutInvalidDetailedServices(
      List<DatamartDetailedService> detailedServices) {
    if (detailedServices != null) {
      // Filter out detailed services containing unrecognized service id
      return detailedServices.stream()
          .filter(x -> !x.serviceInfo().serviceId().equals(INVALID_SVC_ID))
          .collect(Collectors.toList());
    }
    return null;
  }
}
