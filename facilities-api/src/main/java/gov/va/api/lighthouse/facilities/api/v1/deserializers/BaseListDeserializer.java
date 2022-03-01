package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo.INVALID_SVC_ID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseListDeserializer<T> extends StdDeserializer<T> {

  protected static final ObjectMapper MAPPER = createMapper();

  public BaseListDeserializer() {
    this(null);
  }

  public BaseListDeserializer(Class<T> t) {
    super(t);
  }

  public abstract T deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext);

  /** Filter out any detailed services with an invalid service id. */
  protected List<DetailedService> filterOutInvalidDetailedServices(
      List<DetailedService> detailedServices) {
    if (detailedServices != null) {
      // Filter out detailed services containing unrecognized service id
      return detailedServices.stream()
          .filter(x -> !x.serviceInfo().serviceId().equals(INVALID_SVC_ID))
          .collect(Collectors.toList());
    }
    return null;
  }
}
