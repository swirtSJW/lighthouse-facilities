package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFridayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getMondayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSaturdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSundayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getThursdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getTuesdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWednesdayHours;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceHours;
import lombok.SneakyThrows;

public class DatamartDetailedServiceHoursDeserializer
    extends StdDeserializer<DetailedServiceHours> {

  private static final ObjectMapper MAPPER = createMapper();

  public DatamartDetailedServiceHoursDeserializer() {
    this(null);
  }

  public DatamartDetailedServiceHoursDeserializer(Class<DetailedServiceHours> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public DetailedServiceHours deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode mondayHoursNode = getMondayHours(node);
    JsonNode tuesdayHoursNode = getTuesdayHours(node);
    JsonNode wednesdayHoursNode = getWednesdayHours(node);
    JsonNode thursdayHoursNode = getThursdayHours(node);
    JsonNode fridayHoursNode = getFridayHours(node);
    JsonNode saturdayHoursNode = getSaturdayHours(node);
    JsonNode sundayHoursNode = getSundayHours(node);

    return DetailedServiceHours.builder()
        .monday(mondayHoursNode != null ? MAPPER.convertValue(mondayHoursNode, String.class) : null)
        .tuesday(
            tuesdayHoursNode != null ? MAPPER.convertValue(tuesdayHoursNode, String.class) : null)
        .wednesday(
            wednesdayHoursNode != null
                ? MAPPER.convertValue(wednesdayHoursNode, String.class)
                : null)
        .thursday(
            thursdayHoursNode != null ? MAPPER.convertValue(thursdayHoursNode, String.class) : null)
        .friday(fridayHoursNode != null ? MAPPER.convertValue(fridayHoursNode, String.class) : null)
        .saturday(
            saturdayHoursNode != null ? MAPPER.convertValue(saturdayHoursNode, String.class) : null)
        .sunday(sundayHoursNode != null ? MAPPER.convertValue(sundayHoursNode, String.class) : null)
        .build();
  }
}
