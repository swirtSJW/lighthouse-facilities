package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildTypedServiceLink;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import gov.va.api.lighthouse.facilities.DatamartFacility.TypedService;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class DatamartTypedServiceUtil {
  /** Method for constructing datamart facility typed service. */
  public static <T extends ServiceType> TypedService<T> getDatamartTypedService(
      @NonNull T serviceEnumValue, @NonNull String linkerUrl, @NonNull String facilityId) {
    return getDatamartTypedService(
        serviceEnumValue, serviceEnumValue.name(), linkerUrl, facilityId);
  }

  /** Method for constructing datamart facility typed service. */
  public static <T extends ServiceType> TypedService<T> getDatamartTypedService(
      @NonNull T serviceEnumValue,
      @NonNull String serviceName,
      @NonNull String linkerUrl,
      @NonNull String facilityId) {
    return new TypedService<T>(
        serviceEnumValue,
        serviceName,
        buildTypedServiceLink(linkerUrl, facilityId, uncapitalize(serviceEnumValue.name())));
  }

  /** Method for constructing list of datamart facility typed services. */
  public static <T extends ServiceType> List<TypedService<T>> getDatamartTypedServices(
      @NonNull List<T> serviceEnumValues, @NonNull String linkerUrl, @NonNull String facilityId) {
    return serviceEnumValues.stream()
        .map(
            ts -> {
              return getDatamartTypedService(ts, linkerUrl, facilityId);
            })
        .collect(Collectors.toList());
  }
}
