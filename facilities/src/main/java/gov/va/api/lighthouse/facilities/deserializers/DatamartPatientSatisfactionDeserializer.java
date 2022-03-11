package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPrimaryCareRoutine;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPrimaryCareUrgent;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSpecialtyCareRoutine;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSpecialtyCareUrgent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientSatisfaction;
import java.math.BigDecimal;
import lombok.SneakyThrows;

public class DatamartPatientSatisfactionDeserializer extends BaseDeserializer<PatientSatisfaction> {
  public DatamartPatientSatisfactionDeserializer() {
    this(null);
  }

  public DatamartPatientSatisfactionDeserializer(Class<PatientSatisfaction> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public PatientSatisfaction deserialize(
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode primaryCareUrgentNode = getPrimaryCareUrgent(node);
    JsonNode specialtyCareUrgentNode = getSpecialtyCareUrgent(node);
    JsonNode primaryCareRoutineNode = getPrimaryCareRoutine(node);
    JsonNode specialtyCareRoutineNode = getSpecialtyCareRoutine(node);

    return PatientSatisfaction.builder()
        .primaryCareUrgent(
            isNotNull(primaryCareUrgentNode)
                ? MAPPER.convertValue(primaryCareUrgentNode, BigDecimal.class)
                : null)
        .specialtyCareUrgent(
            isNotNull(specialtyCareUrgentNode)
                ? MAPPER.convertValue(specialtyCareUrgentNode, BigDecimal.class)
                : null)
        .primaryCareRoutine(
            isNotNull(primaryCareRoutineNode)
                ? MAPPER.convertValue(primaryCareRoutineNode, BigDecimal.class)
                : null)
        .specialtyCareRoutine(
            isNotNull(specialtyCareRoutineNode)
                ? MAPPER.convertValue(specialtyCareRoutineNode, BigDecimal.class)
                : null)
        .build();
  }
}
