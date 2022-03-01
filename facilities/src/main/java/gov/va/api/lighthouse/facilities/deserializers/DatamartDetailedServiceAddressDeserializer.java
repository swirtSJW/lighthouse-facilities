package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddressLine1;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddressLine2;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getBuildingNameNumber;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getClinicName;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getCountryCode;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWingFloorOrRoomNumber;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getZipCode;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceAddress;
import lombok.SneakyThrows;

public class DatamartDetailedServiceAddressDeserializer
    extends StdDeserializer<DetailedServiceAddress> {

  private static final ObjectMapper MAPPER = createMapper();

  public DatamartDetailedServiceAddressDeserializer() {
    this(null);
  }

  public DatamartDetailedServiceAddressDeserializer(Class<DetailedServiceAddress> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public DetailedServiceAddress deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

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
        .clinicName(
            clinicNameNode != null ? MAPPER.convertValue(clinicNameNode, String.class) : null)
        .address1(
            addressLine1Node != null ? MAPPER.convertValue(addressLine1Node, String.class) : null)
        .address2(
            addressLine2Node != null ? MAPPER.convertValue(addressLine2Node, String.class) : null)
        .buildingNameNumber(
            buildingNameNumberNode != null
                ? MAPPER.convertValue(buildingNameNumberNode, String.class)
                : null)
        .wingFloorOrRoomNumber(
            wingFloorOrRoomNumberNode != null
                ? MAPPER.convertValue(wingFloorOrRoomNumberNode, String.class)
                : null)
        .city(cityNode != null ? MAPPER.convertValue(cityNode, String.class) : null)
        .state(stateNode != null ? MAPPER.convertValue(stateNode, String.class) : null)
        .zipCode(zipCodeNode != null ? MAPPER.convertValue(zipCodeNode, String.class) : null)
        .countryCode(
            countryCodeNode != null ? MAPPER.convertValue(countryCodeNode, String.class) : null)
        .build();
  }
}
