package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo.INVALID_SVC_ID;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public class CmsOverlayDeserializer extends StdDeserializer<CmsOverlay> {
  public CmsOverlayDeserializer() {
    this(null);
  }

  public CmsOverlayDeserializer(Class<CmsOverlay> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public CmsOverlay deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);
    JsonNode operatingStatusNode = node.get("operatingStatus");
    JsonNode detailedServicesNode = node.get("detailedServices");

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return CmsOverlay.builder()
        .operatingStatus(
            operatingStatusNode != null
                ? createMapper().convertValue(operatingStatusNode, Facility.OperatingStatus.class)
                : null)
        .detailedServices(
            detailedServicesNode != null
                ? filterOutInvalidDetailedServices(
                    createMapper().convertValue(detailedServicesNode, detailedServicesRef))
                : null)
        .build();
  }

  private List<DetailedService> filterOutInvalidDetailedServices(
      List<DetailedService> detailedServices) {
    if (detailedServices != null) {
      // Filter out detailed services containing unrecognized service id
      return detailedServices.stream()
          .filter(x -> !x.serviceInfo().serviceId().equals(INVALID_SVC_ID))
          .collect(Collectors.toList());
    }
    return null;
  }
}
