package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getDetailedServices;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOpertingStatus;

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

    // Read values using snake_case or camelCase representations
    JsonNode operatingStatusNode = getOpertingStatus(node);
    JsonNode detailedServicesNode = getDetailedServices(node);

    TypeReference<List<DatamartDetailedService>> detailedServicesRef = new TypeReference<>() {};

    return DatamartCmsOverlay.builder()
        .operatingStatus(
            isNotNull(operatingStatusNode)
                ? MAPPER.convertValue(operatingStatusNode, OperatingStatus.class)
                : null)
        .detailedServices(
            isNotNull(detailedServicesNode)
                ? filterOutInvalidDetailedServices(
                    MAPPER.convertValue(detailedServicesNode, detailedServicesRef))
                : null)
        .build();
  }
}
