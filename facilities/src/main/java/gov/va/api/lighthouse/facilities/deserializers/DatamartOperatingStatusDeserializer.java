package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAdditionalInfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatusCode;
import lombok.SneakyThrows;

public class DatamartOperatingStatusDeserializer extends StdDeserializer<OperatingStatus> {

  private static final ObjectMapper MAPPER = createMapper();

  public DatamartOperatingStatusDeserializer() {
    this(null);
  }

  public DatamartOperatingStatusDeserializer(Class<OperatingStatus> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public OperatingStatus deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode codeNode = node.get("code");
    JsonNode additionalInfoNode = getAdditionalInfo(node);

    return OperatingStatus.builder()
        .code(codeNode != null ? MAPPER.convertValue(codeNode, OperatingStatusCode.class) : null)
        .additionalInfo(
            additionalInfoNode != null
                ? MAPPER.convertValue(additionalInfoNode, String.class)
                : null)
        .build();
  }
}
