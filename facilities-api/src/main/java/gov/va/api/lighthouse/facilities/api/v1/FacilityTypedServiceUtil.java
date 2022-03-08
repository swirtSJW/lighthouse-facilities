package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildTypedServiceLink;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.Facility.TypedService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class FacilityTypedServiceUtil {
  /** Method for constructing facility typed service. */
  public static <T extends ServiceType> TypedService<T> getFacilityTypedService(
      @NonNull T serviceEnumValue, @NonNull String linkedUrl, @NonNull String facilityId) {
    return getFacilityTypedService(
        serviceEnumValue, serviceEnumValue.name(), linkedUrl, facilityId);
  }

  /** Method for constructing facility typed service. */
  public static <T extends ServiceType> TypedService<T> getFacilityTypedService(
      @NonNull T serviceEnumValue,
      @NonNull String serviceName,
      @NonNull String linkedUrl,
      @NonNull String facilityId) {
    return new TypedService<T>(
        serviceEnumValue,
        serviceName,
        buildTypedServiceLink(linkedUrl, facilityId, uncapitalize(serviceEnumValue.name())));
  }

  /** Method for constructing list of facility typed services. */
  public static <T extends ServiceType> List<TypedService<T>> getFacilityTypedServices(
      @NonNull List<T> serviceEnumValues, @NonNull String linkedUrl, @NonNull String facilityId) {
    return serviceEnumValues.stream()
        .map(
            ts -> {
              return getFacilityTypedService(ts, linkedUrl, facilityId);
            })
        .collect(Collectors.toList());
  }
}
