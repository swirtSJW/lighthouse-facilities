package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DeserializerTest {
  @SneakyThrows
  private <T> void assertJson(String json, Class<T> expectedClass, T expectedValue) {
    assertThat(createMapper().readValue(json, expectedClass)).isEqualTo(expectedValue);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceId(uncapitalize(BenefitsService.Pensions.name()))
            .name(BenefitsService.Pensions.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"Pensions\"}", DatamartDetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Pensions\",\"appointmentPhones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceId(uncapitalize(HealthService.Dental.name()))
            .name(HealthService.Dental.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"Dental\"}", DatamartDetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"dental\",\"name\":\"Dental\"}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Dental\",\"appointmentPhones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointmentPhones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Dental\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
            .name(OtherService.OnlineScheduling.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"OnlineScheduling\"}", DatamartDetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DatamartDetailedService.class,
        detailedService);
  }
}
