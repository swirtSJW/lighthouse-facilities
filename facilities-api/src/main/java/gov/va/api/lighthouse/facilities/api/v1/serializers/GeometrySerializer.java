package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacility.Geometry;
import lombok.SneakyThrows;

public class GeometrySerializer extends NonEmptySerializer<Geometry> {

  public GeometrySerializer() {
    this(null);
  }

  public GeometrySerializer(Class<Geometry> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Geometry value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "type", value.type());
    writeNonEmpty(jgen, "coordinates", value.coordinates());
    jgen.writeEndObject();
  }
}
