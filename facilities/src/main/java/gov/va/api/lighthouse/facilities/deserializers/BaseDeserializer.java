package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public abstract class BaseDeserializer<T> extends StdDeserializer<T> {
  protected static final ObjectMapper MAPPER = createMapper();

  public BaseDeserializer(Class<T> t) {
    super(t);
  }

  public abstract T deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext);

  protected boolean isNotNull(JsonNode node) {
    return !isNull(node);
  }

  protected boolean isNull(JsonNode node) {
    return node == null || node.isNull();
  }
}
