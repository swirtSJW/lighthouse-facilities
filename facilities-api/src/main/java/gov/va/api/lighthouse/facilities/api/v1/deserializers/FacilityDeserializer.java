package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import lombok.SneakyThrows;

public class FacilityDeserializer extends BaseFacilityAttributesDeserializer<Facility> {
  public FacilityDeserializer() {
    this(null);
  }

  public FacilityDeserializer(Class<Facility> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Facility deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    JsonNode idNode = node.get("id");
    JsonNode typeNode = node.get("type");
    JsonNode attributesNode = node.get("attributes");

    return Facility.builder()
        .id(idNode != null ? MAPPER.convertValue(idNode, String.class) : "Unknown")
        .type(typeNode != null ? MAPPER.convertValue(typeNode, Facility.Type.class) : null)
        .attributes(attributesNode != null ? deserializeFacilityAttributes(attributesNode) : null)
        .build();
  }
}
