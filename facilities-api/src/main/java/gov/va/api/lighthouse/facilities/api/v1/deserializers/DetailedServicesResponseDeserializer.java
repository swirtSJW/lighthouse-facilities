package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo.INVALID_SVC_ID;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse.DetailedServicesMetadata;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public class DetailedServicesResponseDeserializer
    extends StdDeserializer<DetailedServicesResponse> {
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
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);
    JsonNode dataNode = node.get("data");
    JsonNode linksNode = node.get("links");
    JsonNode metaNode = node.get("meta");

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return DetailedServicesResponse.builder()
        .data(
            dataNode != null
                ? filterOutInvalidDetailedServices(
                    createMapper().convertValue(dataNode, detailedServicesRef))
                : null)
        .links(linksNode != null ? createMapper().convertValue(linksNode, PageLinks.class) : null)
        .meta(
            metaNode != null
                ? createMapper().convertValue(metaNode, DetailedServicesMetadata.class)
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
