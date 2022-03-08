package gov.va.api.lighthouse.facilities;

import java.util.Arrays;
import lombok.NonNull;

abstract class BaseVersionedTransformer {
  protected static boolean checkHealthServiceNameChange(
      DatamartFacility.HealthService healthService) {
    return DatamartFacility.HealthService.MentalHealth.equals(healthService)
        || DatamartFacility.HealthService.Dental.equals(healthService);
  }

  protected static boolean checkHealthServiceNameChange(
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthService) {
    return gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare.equals(
            healthService)
        || gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.DentalServices.equals(
            healthService);
  }

  protected static boolean checkHealthServiceNameChange(
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService healthService) {
    return gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth.equals(
            healthService)
        || gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Dental.equals(
            healthService);
  }

  protected static boolean containsValueOfName(@NonNull Enum<?>[] values, @NonNull String name) {
    return Arrays.stream(values).parallel().anyMatch(e -> e.name().equalsIgnoreCase(name));
  }
}
