package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEmailAddress;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEmailLabel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceEmailContact;
import lombok.SneakyThrows;

public class DatamartDetailedServiceEmailContactDeserializer
    extends BaseDeserializer<DetailedServiceEmailContact> {
  public DatamartDetailedServiceEmailContactDeserializer() {
    this(null);
  }

  public DatamartDetailedServiceEmailContactDeserializer(Class<DetailedServiceEmailContact> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public DetailedServiceEmailContact deserialize(
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode emailAddressNode = getEmailAddress(node);
    JsonNode emailLabelNode = getEmailLabel(node);

    return DetailedServiceEmailContact.builder()
        .emailAddress(isNotNull(emailAddressNode) ? emailAddressNode.asText() : null)
        .emailLabel(isNotNull(emailLabelNode) ? emailLabelNode.asText() : null)
        .build();
  }
}
