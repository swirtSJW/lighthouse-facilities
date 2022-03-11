package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getLastUpdated;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartServicesDeserializer extends BaseDeserializer<Services> {
  public DatamartServicesDeserializer() {
    this(null);
  }

  public DatamartServicesDeserializer(Class<Services> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public Services deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode benefitsNode = node.get("benefits");
    JsonNode healthNode = node.get("health");
    JsonNode otherNode = node.get("other");
    JsonNode lastUpdatedNode = getLastUpdated(node);

    TypeReference<List<BenefitsService>> benefitsRef = new TypeReference<>() {};
    TypeReference<List<HealthService>> healthRef = new TypeReference<>() {};
    TypeReference<List<OtherService>> otherRef = new TypeReference<>() {};

    return Services.builder()
        .benefits(isNotNull(benefitsNode) ? MAPPER.convertValue(benefitsNode, benefitsRef) : null)
        .health(isNotNull(healthNode) ? MAPPER.convertValue(healthNode, healthRef) : null)
        .other(isNotNull(otherNode) ? MAPPER.convertValue(otherNode, otherRef) : null)
        .lastUpdated(
            isNotNull(lastUpdatedNode)
                ? MAPPER.convertValue(lastUpdatedNode, LocalDate.class)
                : null)
        .build();
  }
}
