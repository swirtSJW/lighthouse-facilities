package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.PatientSatisfaction;
import lombok.SneakyThrows;

public class PatientSatisfactionSerializer extends NonEmptySerializer<PatientSatisfaction> {

  public PatientSatisfactionSerializer() {
    this(null);
  }

  public PatientSatisfactionSerializer(Class<PatientSatisfaction> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      PatientSatisfaction value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "primary_care_urgent", value.primaryCareUrgent());
    writeNonEmpty(jgen, "primary_care_routine", value.primaryCareRoutine());
    writeNonEmpty(jgen, "specialty_care_urgent", value.specialtyCareUrgent());
    writeNonEmpty(jgen, "specialty_care_routine", value.specialtyCareRoutine());
    jgen.writeEndObject();
  }
}
