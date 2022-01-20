package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceHours;
import lombok.SneakyThrows;

public class DetailedServiceHoursSerializer extends NonEmptySerializer<DetailedServiceHours> {

  public DetailedServiceHoursSerializer() {
    this(null);
  }

  public DetailedServiceHoursSerializer(Class<DetailedServiceHours> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServiceHours value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "Monday", value.monday());
    writeNonEmpty(jgen, "Tuesday", value.tuesday());
    writeNonEmpty(jgen, "Wednesday", value.wednesday());
    writeNonEmpty(jgen, "Thursday", value.thursday());
    writeNonEmpty(jgen, "Friday", value.friday());
    writeNonEmpty(jgen, "Saturday", value.saturday());
    writeNonEmpty(jgen, "Sunday", value.sunday());
    jgen.writeEndObject();
  }
}
