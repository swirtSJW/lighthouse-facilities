package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import static gov.va.api.lighthouse.facilities.api.v0.DetailedService.INVALID_SVC_ID;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseListDeserializer<T> extends StdDeserializer<T> {
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
          .filter(x -> !x.serviceId().equals(INVALID_SVC_ID))
          .collect(Collectors.toList());
    }
    return null;
  }

  /** Filter out any non-Covid19 detailed services. */
  protected List<DetailedService> filterOutNonCovid19DetailedServices(
      List<DetailedService> detailedServices) {
    if (detailedServices != null) {
      // Filter out non-Covid19 detailed services for V0
      return detailedServices.stream()
          .filter(x -> x.serviceId().equals(uncapitalize(HealthService.Covid19Vaccine.name())))
          .collect(Collectors.toList());
    }
    return null;
  }
}
