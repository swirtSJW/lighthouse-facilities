package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse.DetailedServicesMetadata;
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
    JsonNode dataNode = node.get("data");
    JsonNode linksNode = node.get("links");
    JsonNode metaNode = node.get("meta");

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return DetailedServicesResponse.builder()
        .data(
            dataNode != null
                ? filterOutInvalidDetailedServices(
                    MAPPER.convertValue(dataNode, detailedServicesRef))
                : null)
        .links(linksNode != null ? MAPPER.convertValue(linksNode, PageLinks.class) : null)
        .meta(
            metaNode != null ? MAPPER.convertValue(metaNode, DetailedServicesMetadata.class) : null)
        .build();
  }
}
