package gov.va.api.lighthouse.facilities.api.v1;

import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.DetailedService.PatientWaitTime;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DetailedServiceResponseEmptyFieldsTest {

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(DetailedServiceResponse.builder().build().isEmpty()).isTrue();
    // Not empty
    assertThat(
            DetailedServiceResponse.builder()
                .data(
                    DetailedService.builder()
                        .serviceInfo(
                            DetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                                .serviceType(DetailedService.ServiceType.Health)
                                .build())
                        .waitTime(PatientWaitTime.builder().build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedServiceResponse.builder()
                .data(
                    DetailedService.builder()
                        .serviceInfo(
                            DetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                                .name("test")
                                .serviceType(DetailedService.ServiceType.Health)
                                .build())
                        .waitTime(PatientWaitTime.builder().build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
  }
}
