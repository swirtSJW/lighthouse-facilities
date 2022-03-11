package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAdditionalInfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatusCode;
import lombok.SneakyThrows;

public class DatamartOperatingStatusDeserializer extends BaseDeserializer<OperatingStatus> {
  public DatamartOperatingStatusDeserializer() {
    this(null);
  }

  public DatamartOperatingStatusDeserializer(Class<OperatingStatus> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public OperatingStatus deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode codeNode = node.get("code");
    JsonNode additionalInfoNode = getAdditionalInfo(node);

    return OperatingStatus.builder()
        .code(isNotNull(codeNode) ? MAPPER.convertValue(codeNode, OperatingStatusCode.class) : null)
        .additionalInfo(isNotNull(additionalInfoNode) ? additionalInfoNode.asText() : null)
        .build();
  }
}
