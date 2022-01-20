package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse.NearbyAttributes;
import lombok.SneakyThrows;

public class NearbyAttributesSerializer extends NonEmptySerializer<NearbyAttributes> {

  public NearbyAttributesSerializer() {
    this(null);
  }

  public NearbyAttributesSerializer(Class<NearbyAttributes> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(NearbyAttributes value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "min_time", value.minTime());
    writeNonEmpty(jgen, "max_time", value.maxTime());
    jgen.writeEndObject();
  }
}
