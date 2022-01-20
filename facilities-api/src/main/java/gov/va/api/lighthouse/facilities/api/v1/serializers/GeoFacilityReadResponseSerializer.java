package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacilityReadResponse;
import lombok.SneakyThrows;

public class GeoFacilityReadResponseSerializer extends NonEmptySerializer<GeoFacilityReadResponse> {

  public GeoFacilityReadResponseSerializer() {
    this(null);
  }

  public GeoFacilityReadResponseSerializer(Class<GeoFacilityReadResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      GeoFacilityReadResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "type", value.type());
    writeNonEmpty(jgen, "geometry", value.geometry());
    writeNonEmpty(jgen, "properties", value.properties());
    jgen.writeEndObject();
  }
}
