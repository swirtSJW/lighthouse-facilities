package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import javax.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/** Helper class for V1 CMS overlay operating status and detailed services. */
@UtilityClass
public final class CmsOverlayHelperV1 extends CmsOverlayHelper {
  /** Obtain V1 facility operating status from JSON string. */
  @SneakyThrows
  public static Facility.OperatingStatus getOperatingStatus(
      @NotNull ObjectMapper mapper, String operatingStatus) {
    return (operatingStatus == null)
        ? null
        : mapper.readValue(operatingStatus, Facility.OperatingStatus.class);
  }

  /** Obtain JSON string representation of V1 facility operating status. */
  @SneakyThrows
  public static String serializeOperatingStatus(
      @NotNull ObjectMapper mapper, Facility.OperatingStatus operatingStatus) {
    return (operatingStatus == null) ? null : mapper.writeValueAsString(operatingStatus);
  }
}
