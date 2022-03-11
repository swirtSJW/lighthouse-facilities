package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFridayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getMondayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSaturdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSundayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getThursdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getTuesdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWednesdayHours;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import lombok.SneakyThrows;

public class DatamartHoursDeserializer extends BaseDeserializer<Hours> {
  public DatamartHoursDeserializer() {
    this(null);
  }

  public DatamartHoursDeserializer(Class<Hours> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Hours deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode mondayHoursNode = getMondayHours(node);
    JsonNode tuesdayHoursNode = getTuesdayHours(node);
    JsonNode wednesdayHoursNode = getWednesdayHours(node);
    JsonNode thursdayHoursNode = getThursdayHours(node);
    JsonNode fridayHoursNode = getFridayHours(node);
    JsonNode saturdayHoursNode = getSaturdayHours(node);
    JsonNode sundayHoursNode = getSundayHours(node);

    return Hours.builder()
        .monday(isNotNull(mondayHoursNode) ? mondayHoursNode.asText() : null)
        .tuesday(isNotNull(tuesdayHoursNode) ? tuesdayHoursNode.asText() : null)
        .wednesday(isNotNull(wednesdayHoursNode) ? wednesdayHoursNode.asText() : null)
        .thursday(isNotNull(thursdayHoursNode) ? thursdayHoursNode.asText() : null)
        .friday(isNotNull(fridayHoursNode) ? fridayHoursNode.asText() : null)
        .saturday(isNotNull(saturdayHoursNode) ? saturdayHoursNode.asText() : null)
        .sunday(isNotNull(sundayHoursNode) ? sundayHoursNode.asText() : null)
        .build();
  }
}
