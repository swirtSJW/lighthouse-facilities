package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.Facility.TypedService;
import lombok.SneakyThrows;

public class TypedServiceSerializer
    extends NonEmptySerializer<TypedService<? extends ServiceType>> {

  public TypedServiceSerializer() {
    this(null);
  }

  public TypedServiceSerializer(Class<TypedService<? extends ServiceType>> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      TypedService<? extends ServiceType> value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "name", value.name());
    writeNonEmpty(jgen, "serviceId", value.serviceId());
    writeNonEmpty(jgen, "link", value.link());
    jgen.writeEndObject();
  }
}
