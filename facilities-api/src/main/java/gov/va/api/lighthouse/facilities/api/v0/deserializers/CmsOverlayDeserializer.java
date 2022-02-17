package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import java.util.List;
import lombok.SneakyThrows;

public class CmsOverlayDeserializer extends BaseDeserializer<CmsOverlay> {
  public CmsOverlayDeserializer() {
    this(null);
  }

  public CmsOverlayDeserializer(Class<CmsOverlay> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public CmsOverlay deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);
    JsonNode operatingStatusNode = node.get("operating_status");
    JsonNode detailedServicesNode = node.get("detailed_services");

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return CmsOverlay.builder()
        .operatingStatus(
            operatingStatusNode != null
                ? createMapper().convertValue(operatingStatusNode, OperatingStatus.class)
                : null)
        .detailedServices(
            filterOutNonCovid19DetailedServices(
                detailedServicesNode != null
                    ? filterOutInvalidDetailedServices(
                        createMapper().convertValue(detailedServicesNode, detailedServicesRef))
                    : null))
        .build();
  }
}
