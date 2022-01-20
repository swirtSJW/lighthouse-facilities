package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacility;
import lombok.SneakyThrows;

public class GeoFacilitySerializer extends NonEmptySerializer<GeoFacility> {

  public GeoFacilitySerializer() {
    this(null);
  }

  public GeoFacilitySerializer(Class<GeoFacility> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(GeoFacility value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "type", value.type());
    writeNonEmpty(jgen, "geometry", value.geometry());
    writeNonEmpty(jgen, "properties", value.properties());
    jgen.writeEndObject();
  }
}
