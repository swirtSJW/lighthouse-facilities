package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEffectiveDate;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientWaitTime;
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartWaitTimesDeserializer extends StdDeserializer<WaitTimes> {

  private static final ObjectMapper MAPPER = createMapper();

  public DatamartWaitTimesDeserializer() {
    this(null);
  }

  public DatamartWaitTimesDeserializer(Class<WaitTimes> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public WaitTimes deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode healthNode = node.get("health");
    JsonNode effectiveDateNode = getEffectiveDate(node);

    TypeReference<List<PatientWaitTime>> healthRef = new TypeReference<>() {};

    return WaitTimes.builder()
        .health(healthNode != null ? MAPPER.convertValue(healthNode, healthRef) : emptyList())
        .effectiveDate(
            effectiveDateNode != null
                ? MAPPER.convertValue(effectiveDateNode, LocalDate.class)
                : null)
        .build();
  }
}
