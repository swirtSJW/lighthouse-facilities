package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddressLine1;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddressLine2;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getBuildingNameNumber;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getClinicName;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getCountryCode;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWingFloorOrRoomNumber;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getZipCode;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceAddress;
import lombok.SneakyThrows;

public class DatamartDetailedServiceAddressDeserializer
    extends BaseDeserializer<DetailedServiceAddress> {
  public DatamartDetailedServiceAddressDeserializer() {
    this(null);
  }

  public DatamartDetailedServiceAddressDeserializer(Class<DetailedServiceAddress> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public DetailedServiceAddress deserialize(
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode clinicNameNode = getClinicName(node);
    JsonNode addressLine1Node = getAddressLine1(node);
    JsonNode addressLine2Node = getAddressLine2(node);
    JsonNode buildingNameNumberNode = getBuildingNameNumber(node);
    JsonNode wingFloorOrRoomNumberNode = getWingFloorOrRoomNumber(node);
    JsonNode cityNode = node.get("city");
    JsonNode stateNode = node.get("state");
    JsonNode zipCodeNode = getZipCode(node);
    JsonNode countryCodeNode = getCountryCode(node);

    return DetailedServiceAddress.builder()
        .clinicName(isNotNull(clinicNameNode) ? clinicNameNode.asText() : null)
        .address1(isNotNull(addressLine1Node) ? addressLine1Node.asText() : null)
        .address2(isNotNull(addressLine2Node) ? addressLine2Node.asText() : null)
        .buildingNameNumber(
            isNotNull(buildingNameNumberNode) ? buildingNameNumberNode.asText() : null)
        .wingFloorOrRoomNumber(
            isNotNull(wingFloorOrRoomNumberNode) ? wingFloorOrRoomNumberNode.asText() : null)
        .city(isNotNull(cityNode) ? cityNode.asText() : null)
        .state(isNotNull(stateNode) ? stateNode.asText() : null)
        .zipCode(isNotNull(zipCodeNode) ? zipCodeNode.asText() : null)
        .countryCode(isNotNull(countryCodeNode) ? countryCodeNode.asText() : null)
        .build();
  }
}
