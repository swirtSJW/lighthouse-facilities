package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
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
    DetailedService detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(BenefitsService.Pensions.name()))
            .name(BenefitsService.Pensions.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"Pensions\"}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Pensions\",\"appointmentPhones\":[]}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(HealthService.DentalServices.name()))
            .name(HealthService.DentalServices.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"DentalServices\"}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\"}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"DentalServices\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"DentalServices\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
            .name(OtherService.OnlineScheduling.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"OnlineScheduling\"}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }
}
