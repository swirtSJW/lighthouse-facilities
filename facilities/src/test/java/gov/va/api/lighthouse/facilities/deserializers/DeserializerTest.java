package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.INVALID_SVC_ID;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DeserializerTest {
  @SneakyThrows
  private <T> void assertJson(String json, Class<T> expectedClass, T expectedValue) {
    assertThat(createMapper().readValue(json, expectedClass))
        .usingRecursiveComparison()
        .isEqualTo(expectedValue);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\"}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\",\"appointment_phones\":[]}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
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
        "{\"name\":\"Pensions\",\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsFacilityAttributes() {
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\",\"appointment_phones\":[]}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithInvalidDetailedServices() {
    DatamartCmsOverlay overlay = DatamartCmsOverlay.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"bar\"}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"baz\",\"appointment_phones\":[]}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"bar\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithMixedDetailedServices() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Smoking.name()))
                        .name(HealthService.Smoking.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\"},"
            + "{\"name\":\"Smoking\"},"
            + "{\"name\":\"foo\"}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\"}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityAttributesWithInvalidDetailedServices() {
    FacilityAttributes attributes =
        FacilityAttributes.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"bar\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"baz\",\"appointment_phones\":[]}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"bar\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityAttributesWithMixedDetailedServices() {
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Smoking.name()))
                        .name(HealthService.Smoking.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\"},"
            + "{\"name\":\"Smoking\"},"
            + "{\"name\":\"foo\"}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\"}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeHealthCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Dental.name()))
                        .name(HealthService.Dental.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Dental\"}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"dental\",\"name\":\"Dental\"}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Dental\",\"appointment_phones\":[]}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Dental\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
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
        "{\"name\":\"Dental\",\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Dental\",\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeHealthFacilityAttributes() {
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Dental.name()))
                        .name(HealthService.Dental.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Dental\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"dental\",\"name\":\"Dental\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Dental\",\"appointment_phones\":[]}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Dental\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeInvalidDetailedService() {
    DatamartDetailedService invalidService =
        DatamartDetailedService.builder()
            .serviceId(INVALID_SVC_ID)
            .name("foo")
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"foo\"}", DatamartDetailedService.class, invalidService);
    invalidService.name("OnlineScheduling");
    assertJson(
        "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}",
        DatamartDetailedService.class,
        invalidService);
    invalidService.name("baz");
    assertJson(
        "{\"serviceId\":\"foo\",\"name\":\"baz\"}", DatamartDetailedService.class, invalidService);
    invalidService.name("Smoking");
    assertJson(
        "{\"serviceId\":\"bar\",\"name\":\"Smoking\"}",
        DatamartDetailedService.class,
        invalidService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                        .name(OtherService.OnlineScheduling.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"OnlineScheduling\"}" + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
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
        "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherFacilityAttributes() {
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                        .name(OtherService.OnlineScheduling.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"OnlineScheduling\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }
}
