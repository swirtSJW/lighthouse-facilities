package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEffectiveDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientWaitTime;
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartWaitTimesDeserializer extends BaseDeserializer<WaitTimes> {
  public DatamartWaitTimesDeserializer() {
    this(null);
  }

  public DatamartWaitTimesDeserializer(Class<WaitTimes> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public WaitTimes deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode healthNode = node.get("health");
    JsonNode effectiveDateNode = getEffectiveDate(node);

    TypeReference<List<PatientWaitTime>> healthRef = new TypeReference<>() {};

    return WaitTimes.builder()
        .health(isNotNull(healthNode) ? MAPPER.convertValue(healthNode, healthRef) : null)
        .effectiveDate(
            isNotNull(effectiveDateNode)
                ? MAPPER.convertValue(effectiveDateNode, LocalDate.class)
                : null)
        .build();
  }
}
