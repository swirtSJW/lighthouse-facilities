package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacilitiesResponse;
import lombok.SneakyThrows;

public class GeoFacilitiesResponseSerializer extends NonEmptySerializer<GeoFacilitiesResponse> {

  public GeoFacilitiesResponseSerializer() {
    this(null);
  }

  public GeoFacilitiesResponseSerializer(Class<GeoFacilitiesResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      GeoFacilitiesResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "type", value.type());
    writeNonEmpty(jgen, "features", value.features());
    jgen.writeEndObject();
  }
}
