package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.List;
import lombok.SneakyThrows;

public class CmsOverlayDeserializer extends BaseListDeserializer<CmsOverlay> {
  public CmsOverlayDeserializer() {
    this(null);
  }

  public CmsOverlayDeserializer(Class<CmsOverlay> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public CmsOverlay deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);
    JsonNode operatingStatusNode = node.get("operatingStatus");
    JsonNode detailedServicesNode = node.get("detailedServices");

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return CmsOverlay.builder()
        .operatingStatus(
            operatingStatusNode != null
                ? MAPPER.convertValue(operatingStatusNode, Facility.OperatingStatus.class)
                : null)
        .detailedServices(
            detailedServicesNode != null
                ? filterOutInvalidDetailedServices(
                    MAPPER.convertValue(detailedServicesNode, detailedServicesRef))
                : null)
        .build();
  }
}
