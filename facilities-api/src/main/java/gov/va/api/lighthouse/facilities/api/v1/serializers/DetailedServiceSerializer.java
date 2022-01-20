package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import lombok.SneakyThrows;

public class DetailedServiceSerializer extends NonEmptySerializer<DetailedService> {

  public DetailedServiceSerializer() {
    this(null);
  }

  public DetailedServiceSerializer(Class<DetailedService> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(DetailedService value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "name", value.name());
    writeNonEmpty(jgen, "appointment_leadin", value.appointmentLeadIn());
    writeNonEmpty(jgen, "appointment_phones", value.phoneNumbers());
    writeNonEmpty(jgen, "online_scheduling_available", value.onlineSchedulingAvailable());
    writeNonEmpty(jgen, "referral_required", value.referralRequired());
    writeNonEmpty(jgen, "walk_ins_accepted", value.walkInsAccepted());
    writeNonEmpty(jgen, "service_locations", value.serviceLocations());
    writeNonEmpty(jgen, "path", value.path());
    jgen.writeEndObject();
  }
}
