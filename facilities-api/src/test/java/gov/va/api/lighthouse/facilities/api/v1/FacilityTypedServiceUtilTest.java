package gov.va.api.lighthouse.facilities.api.v1;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityTypedServiceUtilTest {
  @Test
  @SneakyThrows
  void exceptions() {
    assertThatThrownBy(() -> FacilityTypedServiceUtil.getFacilityTypedService(null, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("serviceEnumValue is marked non-null but is null");
    assertThatThrownBy(
            () ->
                FacilityTypedServiceUtil.getFacilityTypedService(
                    Facility.HealthService.Cardiology, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("linkedUrl is marked non-null but is null");
    assertThatThrownBy(
            () ->
                FacilityTypedServiceUtil.getFacilityTypedService(
                    Facility.HealthService.Cardiology, "http://localhost:8085/v1/", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("facilityId is marked non-null but is null");

    assertThatThrownBy(
            () -> FacilityTypedServiceUtil.getFacilityTypedService(null, null, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("serviceEnumValue is marked non-null but is null");
    assertThatThrownBy(
            () ->
                FacilityTypedServiceUtil.getFacilityTypedService(
                    Facility.HealthService.Cardiology, null, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("serviceName is marked non-null but is null");
    assertThatThrownBy(
            () ->
                FacilityTypedServiceUtil.getFacilityTypedService(
                    Facility.HealthService.Cardiology,
                    Facility.HealthService.Cardiology.name(),
                    null,
                    null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("linkedUrl is marked non-null but is null");
    assertThatThrownBy(
            () ->
                FacilityTypedServiceUtil.getFacilityTypedService(
                    Facility.HealthService.Cardiology,
                    Facility.HealthService.Cardiology.name(),
                    "http://localhost:8085/v1/",
                    null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("facilityId is marked non-null but is null");

    assertThatThrownBy(() -> FacilityTypedServiceUtil.getFacilityTypedServices(null, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("serviceEnumValues is marked non-null but is null");
    assertThatThrownBy(
            () ->
                FacilityTypedServiceUtil.getFacilityTypedServices(
                    List.of(Facility.HealthService.Cardiology), null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("linkedUrl is marked non-null but is null");
    assertThatThrownBy(
            () ->
                FacilityTypedServiceUtil.getFacilityTypedServices(
                    List.of(Facility.HealthService.Cardiology), "http://localhost:8085/v1/", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("facilityId is marked non-null but is null");
  }
}
