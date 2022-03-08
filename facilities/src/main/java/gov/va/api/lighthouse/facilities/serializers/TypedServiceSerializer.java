package gov.va.api.lighthouse.facilities.serializers;

import static gov.va.api.lighthouse.facilities.FacilityUtils.writeNonNull;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility.TypedService;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import java.util.Optional;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

public class TypedServiceSerializer extends StdSerializer<TypedService<? extends ServiceType>> {
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
    if (ObjectUtils.isEmpty(value.name())) {
      jgen.writeObjectField("name", Optional.ofNullable(value.name()).orElse(null));
    } else {
      writeNonNull(jgen, "name", value.name());
    }
    if (ObjectUtils.isEmpty(value.serviceId())) {
      jgen.writeObjectField(
          "serviceId",
          Optional.ofNullable(value.serviceId())
              .orElse(DatamartDetailedService.ServiceInfo.INVALID_SVC_ID));
    } else {
      writeNonNull(jgen, "serviceId", value.serviceId());
    }
    if (ObjectUtils.isEmpty(value.link())) {
      jgen.writeObjectField("link", Optional.ofNullable(value.link()).orElse(null));
    } else {
      writeNonNull(jgen, "link", value.link());
    }
    jgen.writeEndObject();
  }
}
