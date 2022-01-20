package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceAddress;
import lombok.SneakyThrows;

public class DetailedServiceAddressSerializer extends NonEmptySerializer<DetailedServiceAddress> {

  public DetailedServiceAddressSerializer() {
    this(null);
  }

  public DetailedServiceAddressSerializer(Class<DetailedServiceAddress> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServiceAddress value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "building_name_number", value.buildingNameNumber());
    writeNonEmpty(jgen, "clinic_name", value.clinicName());
    writeNonEmpty(jgen, "wing_floor_or_room_number", value.wingFloorOrRoomNumber());
    writeNonEmpty(jgen, "address_line1", value.address1());
    writeNonEmpty(jgen, "address_line2", value.address2());
    writeNonEmpty(jgen, "city", value.city());
    writeNonEmpty(jgen, "state", value.state());
    writeNonEmpty(jgen, "zip_code", value.zipCode());
    writeNonEmpty(jgen, "country_code", value.countryCode());
    jgen.writeEndObject();
  }
}
