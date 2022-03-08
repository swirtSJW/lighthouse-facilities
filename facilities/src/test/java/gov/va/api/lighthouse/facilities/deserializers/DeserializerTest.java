package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.ServiceInfo.INVALID_SVC_ID;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.ApplicationContextHolder;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartTypedServiceUtil;
import gov.va.api.lighthouse.facilities.ServiceLinkHelper;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

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
  void deserializeFacilityWithInvalidDetailedServices() {
    DatamartFacility facility =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(FacilityAttributes.builder().detailedServices(emptyList()).build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithInvalidServices() {
    var linkerUrl = "http://localhost:8085/v1/";
    var facilityId = "vha_402";
    DatamartFacility facility =
        DatamartFacility.builder()
            .id(facilityId)
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                FacilityAttributes.builder()
                    .services(
                        DatamartFacility.Services.builder()
                            .link(buildServicesLink(linkerUrl, facilityId))
                            .lastUpdated(LocalDate.parse("2022-03-07"))
                            .build())
                    .build())
            .build();
    // No services
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
    // Invalid services express in V0 format
    facility
        .attributes()
        .services(
            DatamartFacility.Services.builder()
                .benefits(emptyList())
                .health(emptyList())
                .other(emptyList())
                .link(buildServicesLink(linkerUrl, facilityId))
                .lastUpdated(LocalDate.parse("2022-03-07"))
                .build());
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [\"foo\"],"
            + "\"health\": [\"bar\"],"
            + "\"other\": [\"baz\"],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
    // Invalid services expressed in mixed V0/V1 format
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [\"foo\"],"
            + "\"health\": [\"bar\"],"
            + "\"other\": [\"baz\"],"
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services\","
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
    // Invalid services expressed in V1 format
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [{"
            + "\"serviceId\":\"foo\","
            + "\"name\":\"Foo\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/foo\""
            + "}],"
            + "\"health\": [{"
            + "\"serviceId\":\"bar\","
            + "\"name\":\"Bar\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/bar\""
            + "}],"
            + "\"other\": [{"
            + "\"serviceId\":\"baz\","
            + "\"name\":\"Baz\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/baz\""
            + "}],"
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services\","
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
    // Invalid services expressed in V1 format, missing facility services link
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [{"
            + "\"serviceId\":\"foo\","
            + "\"name\":\"Foo\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/foo\""
            + "}],"
            + "\"health\": [{"
            + "\"serviceId\":\"bar\","
            + "\"name\":\"Bar\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/bar\""
            + "}],"
            + "\"other\": [{"
            + "\"serviceId\":\"baz\","
            + "\"name\":\"Baz\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/baz\""
            + "}],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithMixedDetailedServices() {
    DatamartFacility facility =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
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
                                .phoneNumbers(emptyList())
                                .serviceLocations(emptyList())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithServicesInV0Format() {
    var linkerUrl = "http://localhost:8085/v1/";
    var facilityId = "vha_402";
    DatamartFacility facility =
        DatamartFacility.builder()
            .id(facilityId)
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                FacilityAttributes.builder()
                    .services(
                        DatamartFacility.Services.builder()
                            .benefits(
                                DatamartTypedServiceUtil.getDatamartTypedServices(
                                    List.of(BenefitsService.Pensions), linkerUrl, facilityId))
                            .health(
                                DatamartTypedServiceUtil.getDatamartTypedServices(
                                    List.of(HealthService.Cardiology, HealthService.PrimaryCare),
                                    linkerUrl,
                                    facilityId))
                            .other(
                                DatamartTypedServiceUtil.getDatamartTypedServices(
                                    List.of(OtherService.OnlineScheduling), linkerUrl, facilityId))
                            .link(buildServicesLink(linkerUrl, facilityId))
                            .lastUpdated(LocalDate.parse("2022-03-07"))
                            .build())
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": ["
            + "\"Pensions\""
            + "],"
            + "\"health\": ["
            + "\"Cardiology\","
            + "\"PrimaryCare\""
            + "],"
            + "\"other\": ["
            + "\"OnlineScheduling\""
            + "],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithServicesInV1Format() {
    var linkerUrl = "http://localhost:8085/v1/";
    var facilityId = "vha_402";
    DatamartFacility facility =
        DatamartFacility.builder()
            .id(facilityId)
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                FacilityAttributes.builder()
                    .services(
                        DatamartFacility.Services.builder()
                            .benefits(
                                DatamartTypedServiceUtil.getDatamartTypedServices(
                                    List.of(BenefitsService.Pensions), linkerUrl, facilityId))
                            .health(
                                DatamartTypedServiceUtil.getDatamartTypedServices(
                                    List.of(HealthService.Cardiology, HealthService.PrimaryCare),
                                    linkerUrl,
                                    facilityId))
                            .other(
                                DatamartTypedServiceUtil.getDatamartTypedServices(
                                    List.of(OtherService.OnlineScheduling), linkerUrl, facilityId))
                            .link(buildServicesLink(linkerUrl, facilityId))
                            .lastUpdated(LocalDate.parse("2022-03-07"))
                            .build())
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [{"
            + "\"name\":\"Pensions\","
            + "\"serviceId\":\"pensions\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/pensions\""
            + "}],"
            + "\"health\": [{"
            + "\"name\":\"Cardiology\","
            + "\"serviceId\":\"cardiology\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/cardiology\""
            + "},"
            + "{"
            + "\"name\":\"PrimaryCare\","
            + "\"serviceId\":\"primaryCare\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/primaryCare\""
            + "}],"
            + "\"other\": [{"
            + "\"name\":\"OnlineScheduling\","
            + "\"serviceId\":\"onlineScheduling\","
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services/onlineScheduling\""
            + "}],"
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services\","
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
    // With links missing
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [{"
            + "\"name\":\"Pensions\","
            + "\"serviceId\":\"pensions\""
            + "}],"
            + "\"health\": [{"
            + "\"name\":\"Cardiology\","
            + "\"serviceId\":\"cardiology\""
            + "},"
            + "{"
            + "\"name\":\"PrimaryCare\","
            + "\"serviceId\":\"primaryCare\""
            + "}],"
            + "\"other\": [{"
            + "\"name\":\"OnlineScheduling\","
            + "\"serviceId\":\"onlineScheduling\""
            + "}],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
    // With only service ids listed
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [{"
            + "\"serviceId\":\"pensions\""
            + "}],"
            + "\"health\": [{"
            + "\"serviceId\":\"cardiology\""
            + "},"
            + "{"
            + "\"serviceId\":\"primaryCare\""
            + "}],"
            + "\"other\": [{"
            + "\"serviceId\":\"onlineScheduling\""
            + "}],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
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

  @BeforeEach
  void setUp() {
    ServiceLinkHelper mockHelper = mock(ServiceLinkHelper.class);
    when(mockHelper.baseUrl()).thenReturn("http://localhost:8085");
    when(mockHelper.basePath()).thenReturn("/");
    ApplicationContext mockContext = mock(ApplicationContext.class);
    when(mockContext.getBean(ServiceLinkHelper.class)).thenReturn(mockHelper);
    ApplicationContextHolder contextHolder = new ApplicationContextHolder();
    contextHolder.setApplicationContext(mockContext);
  }
}
