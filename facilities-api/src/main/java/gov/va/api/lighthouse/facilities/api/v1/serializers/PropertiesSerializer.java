package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacility.Properties;
import lombok.SneakyThrows;

public class PropertiesSerializer extends NonEmptySerializer<Properties> {

  public PropertiesSerializer() {
    this(null);
  }

  public PropertiesSerializer(Class<Properties> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Properties value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "id", value.id());
    writeNonEmpty(jgen, "name", value.name());
    writeNonEmpty(jgen, "facility_type", value.facilityType());
    writeNonEmpty(jgen, "classification", value.classification());
    writeNonEmpty(jgen, "website", value.website());
    writeNonEmpty(jgen, "time_zone", value.timeZone());
    writeNonEmpty(jgen, "address", value.address());
    writeNonEmpty(jgen, "phone", value.phone());
    writeNonEmpty(jgen, "hours", value.hours());
    writeNonEmpty(
        jgen,
        "operational_hours_special_instructions",
        value.operationalHoursSpecialInstructions());
    writeNonEmpty(jgen, "services", value.services());
    writeNonEmpty(jgen, "satisfaction", value.satisfaction());
    writeNonEmpty(jgen, "wait_times", value.waitTimes());
    writeNonEmpty(jgen, "mobile", value.mobile());
    writeNonEmpty(jgen, "active_status", value.activeStatus());
    writeNonEmpty(jgen, "operating_status", value.operatingStatus());
    writeNonEmpty(jgen, "detailed_services", value.detailedServices());
    writeNonEmpty(jgen, "visn", value.visn());
    jgen.writeEndObject();
  }
}
