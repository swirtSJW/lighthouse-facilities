package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityAttributes;
import lombok.SneakyThrows;

public class FacilityAttributesSerializer extends NonEmptySerializer<FacilityAttributes> {

  public FacilityAttributesSerializer() {
    this(null);
  }

  public FacilityAttributesSerializer(Class<FacilityAttributes> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(FacilityAttributes value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "name", value.name());
    writeNonEmpty(jgen, "facility_type", value.facilityType());
    writeNonEmpty(jgen, "classification", value.classification());
    writeNonEmpty(jgen, "website", value.website());
    writeNonEmpty(jgen, "lat", value.latitude());
    writeNonEmpty(jgen, "long", value.longitude());
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
    writeNonEmpty(jgen, "visn", value.visn());
    writeNonEmpty(jgen, "parent", value.parent());
    jgen.writeEndObject();
  }
}
