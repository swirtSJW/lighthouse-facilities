package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartCmsOverlayDeserializer extends BaseListDeserializer<DatamartCmsOverlay> {
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
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);
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
}
