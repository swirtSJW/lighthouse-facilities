package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEffectiveDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientSatisfaction;
import gov.va.api.lighthouse.facilities.DatamartFacility.Satisfaction;
import java.time.LocalDate;
import lombok.SneakyThrows;

public class DatamartSatisfactionDeserializer extends BaseDeserializer<Satisfaction> {
  public DatamartSatisfactionDeserializer() {
    this(null);
  }

  public DatamartSatisfactionDeserializer(Class<Satisfaction> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Satisfaction deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode healthNode = node.get("health");
    JsonNode effectiveDateNode = getEffectiveDate(node);

    return Satisfaction.builder()
        .health(
            isNotNull(healthNode)
                ? MAPPER.convertValue(healthNode, PatientSatisfaction.class)
                : null)
        .effectiveDate(
            isNotNull(effectiveDateNode)
                ? MAPPER.convertValue(effectiveDateNode, LocalDate.class)
                : null)
        .build();
  }
}
