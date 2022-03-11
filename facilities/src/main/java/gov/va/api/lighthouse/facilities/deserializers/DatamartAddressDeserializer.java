package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddress1;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddress2;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddress3;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import lombok.SneakyThrows;

public class DatamartAddressDeserializer extends BaseDeserializer<Address> {
  public DatamartAddressDeserializer() {
    this(null);
  }

  public DatamartAddressDeserializer(Class<Address> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Address deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode address1Node = getAddress1(node);
    JsonNode address2Node = getAddress2(node);
    JsonNode address3Node = getAddress3(node);
    JsonNode cityNode = node.get("city");
    JsonNode stateNode = node.get("state");
    JsonNode zipNode = node.get("zip");

    return Address.builder()
        .address1(isNotNull(address1Node) ? address1Node.asText() : null)
        .address2(isNotNull(address2Node) ? address2Node.asText() : null)
        .address3(isNotNull(address3Node) ? address3Node.asText() : null)
        .city(isNotNull(cityNode) ? cityNode.asText() : null)
        .state(isNotNull(stateNode) ? stateNode.asText() : null)
        .zip(isNotNull(zipNode) ? zipNode.asText() : null)
        .build();
  }
}
