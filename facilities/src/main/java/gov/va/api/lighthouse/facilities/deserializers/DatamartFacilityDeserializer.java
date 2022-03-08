package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.ApplicationContextHolder;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.ServiceLinkHelper;
import lombok.SneakyThrows;

public class DatamartFacilityDeserializer
    extends BaseDatamartFacilityAttributesDeserializer<DatamartFacility> {

  public DatamartFacilityDeserializer() {
    this(null);
  }

  public DatamartFacilityDeserializer(Class<DatamartFacility> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public DatamartFacility deserialize(
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    JsonNode idNode = node.get("id");
    JsonNode typeNode = node.get("type");
    JsonNode attributesNode = node.get("attributes");

    ServiceLinkHelper serviceLinkHelper =
        ApplicationContextHolder.getContext().getBean(ServiceLinkHelper.class);
    String linkerUrl = buildLinkerUrlV1(serviceLinkHelper.baseUrl(), serviceLinkHelper.basePath());

    final String facilityId =
        (idNode != null) ? MAPPER.convertValue(idNode, String.class) : "Unknown";
    return DatamartFacility.builder()
        .id(facilityId)
        .type(typeNode != null ? MAPPER.convertValue(typeNode, DatamartFacility.Type.class) : null)
        .attributes(
            attributesNode != null
                ? deserializeDatamartFacilityAttributes(attributesNode, linkerUrl, facilityId)
                : null)
        .build();
  }
}
