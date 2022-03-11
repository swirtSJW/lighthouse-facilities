package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v0.DetailedServicesResponse.DetailedServicesMetadata;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import java.util.List;
import lombok.SneakyThrows;

public class DetailedServicesResponseDeserializer
    extends BaseListDeserializer<DetailedServicesResponse> {
  public DetailedServicesResponseDeserializer() {
    this(null);
  }

  public DetailedServicesResponseDeserializer(Class<DetailedServicesResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public DetailedServicesResponse deserialize(
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode dataNode = node.get("data");
    JsonNode linksNode = node.get("links");
    JsonNode metaNode = node.get("meta");

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return DetailedServicesResponse.builder()
        .data(
            isNotNull(dataNode)
                ? filterOutInvalidDetailedServices(
                    MAPPER.convertValue(dataNode, detailedServicesRef))
                : null)
        .links(isNotNull(linksNode) ? MAPPER.convertValue(linksNode, PageLinks.class) : null)
        .meta(
            isNotNull(metaNode)
                ? MAPPER.convertValue(metaNode, DetailedServicesMetadata.class)
                : null)
        .build();
  }
}
