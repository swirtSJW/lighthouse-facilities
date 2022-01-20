package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceLocation;
import lombok.SneakyThrows;

public class DetailedServiceLocationSerializer extends NonEmptySerializer<DetailedServiceLocation> {

  public DetailedServiceLocationSerializer() {
    this(null);
  }

  public DetailedServiceLocationSerializer(Class<DetailedServiceLocation> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServiceLocation value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "service_location_address", value.serviceLocationAddress());
    writeNonEmpty(jgen, "appointment_phones", value.appointmentPhoneNumbers());
    writeNonEmpty(jgen, "email_contacts", value.emailContacts());
    writeNonEmpty(jgen, "facility_service_hours", value.facilityServiceHours());
    writeNonEmpty(jgen, "additional_hours_info", value.additionalHoursInfo());
    jgen.writeEndObject();
  }
}
