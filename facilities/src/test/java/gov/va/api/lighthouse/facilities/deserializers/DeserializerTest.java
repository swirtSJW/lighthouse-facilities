package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceInfo.INVALID_SVC_ID;
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
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                    .name(BenefitsService.Pensions.name())
                    .serviceType(DatamartDetailedService.ServiceType.Benefits)
                    .build())
            .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}",
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
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DatamartDetailedService.ServiceType.Benefits)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithInvalidDetailedServices() {
    DatamartCmsOverlay overlay = DatamartCmsOverlay.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
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
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DatamartDetailedService.ServiceType.Benefits)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Smoking.name()))
                                .name(HealthService.Smoking.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"bin\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"bin\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"bin\"},\"appointment_phones\":[],\"service_locations\":[]}"
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
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointment_phones\":[],\"service_locations\":[]}"
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
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DatamartDetailedService.ServiceType.Benefits)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Smoking.name()))
                                .name(HealthService.Smoking.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[],\"service_locations\":[]}"
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
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Dental.name()))
                                .name(HealthService.Dental.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(uncapitalize(HealthService.Dental.name()))
                    .name(HealthService.Dental.name())
                    .serviceType(DatamartDetailedService.ServiceType.Health)
                    .build())
            .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}",
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
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Dental.name()))
                                .name(HealthService.Dental.name())
                                .serviceType(DatamartDetailedService.ServiceType.Health)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeInvalidDetailedService() {
    DatamartDetailedService invalidService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(INVALID_SVC_ID)
                    .name("foo")
                    .serviceType(DatamartDetailedService.ServiceType.Health)
                    .build())
            .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("OnlineScheduling");
    invalidService.serviceInfo().serviceType(DatamartDetailedService.ServiceType.Other);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("baz");
    invalidService.serviceInfo().serviceType(DatamartDetailedService.ServiceType.Health);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"baz\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("Smoking");
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"Smoking\",\"serviceType\":\"health\"}}",
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
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                                .name(OtherService.OnlineScheduling.name())
                                .serviceType(DatamartDetailedService.ServiceType.Other)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                    .name(OtherService.OnlineScheduling.name())
                    .serviceType(DatamartDetailedService.ServiceType.Other)
                    .build())
            .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}",
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
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                                .name(OtherService.OnlineScheduling.name())
                                .serviceType(DatamartDetailedService.ServiceType.Other)
                                .build())
                        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }
}
