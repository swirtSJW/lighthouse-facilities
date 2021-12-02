package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.SneakyThrows;

/** Base class for CMS overlay helpers. */
public abstract class CmsOverlayHelper {
  /** Obtain list of detailed services from JSON string. */
  @SneakyThrows
  public static List<DetailedService> getDetailedServices(
      @NotNull ObjectMapper mapper, String detailedServices) {
    return (detailedServices == null)
        ? null
        : List.of(mapper.readValue(detailedServices, DetailedService[].class));
  }

  /** Obtain JSON string representation of detailed service list. */
  @SneakyThrows
  public static String serializeDetailedServices(
      @NotNull ObjectMapper mapper, List<DetailedService> detailedServices) {
    return (detailedServices == null || detailedServices.isEmpty())
        ? null
        : mapper.writeValueAsString(detailedServices);
  }
}
