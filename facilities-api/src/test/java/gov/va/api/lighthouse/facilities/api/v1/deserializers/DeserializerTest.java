package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo.INVALID_SVC_ID;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v1.FacilityTypedServiceUtil;
import java.time.LocalDate;
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
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DetailedService.ServiceType.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceInfo(
                ServiceInfo.builder()
                    .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                    .name(BenefitsService.Pensions.name())
                    .serviceType(DetailedService.ServiceType.Benefits)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedServicesResponse() {
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DetailedService.ServiceType.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithInvalidDetailedServices() {
    CmsOverlay overlay = CmsOverlay.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"bar\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithMixedDetailedServices() {
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DetailedService.ServiceType.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Smoking.name()))
                                .name(HealthService.Smoking.name())
                                .serviceType(DetailedService.ServiceType.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServicesResponseWithInvalidDetailedServices() {
    DetailedServicesResponse response =
        DetailedServicesResponse.builder().data(emptyList()).build();
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServicesResponseWithMixedDetailedServices() {
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                                .name(BenefitsService.Pensions.name())
                                .serviceType(DetailedService.ServiceType.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Smoking.name()))
                                .name(HealthService.Smoking.name())
                                .serviceType(DetailedService.ServiceType.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithInvalidServices() {
    var linkerUrl = "http://localhost:8085/v1/";
    var facilityId = "vha_402";
    Facility facility =
        Facility.builder()
            .id(facilityId)
            .type(Facility.Type.va_facilities)
            .attributes(
                Facility.FacilityAttributes.builder()
                    .services(
                        Facility.Services.builder()
                            .link(Facility.TypedService.INVALID_LINK)
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
            + "\"lastUpdated\":\"2022-03-07\""
            + "}}}",
        Facility.class,
        facility);
    // Invalid services express in V0 format
    facility
        .attributes()
        .services(
            Facility.Services.builder()
                .benefits(emptyList())
                .health(emptyList())
                .other(emptyList())
                .link(Facility.TypedService.INVALID_LINK)
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
            + "\"lastUpdated\":\"2022-03-07\""
            + "}}}",
        Facility.class,
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
            + "\"lastUpdated\":\"2022-03-07\""
            + "}}}",
        Facility.class,
        facility);
    // Invalid services expressed in mixed V0/V1 format
    facility.attributes().services().link(buildServicesLink(linkerUrl, facilityId));
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [\"foo\"],"
            + "\"health\": [\"bar\"],"
            + "\"other\": [\"baz\"],"
            + "\"link\":\"http://localhost:8085/v1/facilities/vha_402/services\","
            + "\"lastUpdated\":\"2022-03-07\""
            + "}}}",
        Facility.class,
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
            + "\"lastUpdated\":\"2022-03-07\""
            + "}}}",
        Facility.class,
        facility);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithServicesInV1Format() {
    var linkerUrl = "http://localhost:8085/v1/";
    var facilityId = "vha_402";
    Facility facility =
        Facility.builder()
            .id(facilityId)
            .type(Facility.Type.va_facilities)
            .attributes(
                Facility.FacilityAttributes.builder()
                    .services(
                        Facility.Services.builder()
                            .benefits(
                                FacilityTypedServiceUtil.getFacilityTypedServices(
                                    List.of(BenefitsService.Pensions), linkerUrl, facilityId))
                            .health(
                                FacilityTypedServiceUtil.getFacilityTypedServices(
                                    List.of(HealthService.Cardiology, HealthService.PrimaryCare),
                                    linkerUrl,
                                    facilityId))
                            .other(
                                FacilityTypedServiceUtil.getFacilityTypedServices(
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
            + "\"lastUpdated\":\"2022-03-07\""
            + "}}}",
        Facility.class,
        facility);
    // With links missing
    facility
        .attributes()
        .services(
            Facility.Services.builder()
                .benefits(
                    List.of(
                        FacilityTypedServiceUtil.getFacilityTypedService(
                                BenefitsService.Pensions, linkerUrl, facilityId)
                            .link(Facility.TypedService.INVALID_LINK)))
                .health(
                    List.of(
                        FacilityTypedServiceUtil.getFacilityTypedService(
                                HealthService.Cardiology, linkerUrl, facilityId)
                            .link(Facility.TypedService.INVALID_LINK),
                        FacilityTypedServiceUtil.getFacilityTypedService(
                                HealthService.PrimaryCare, linkerUrl, facilityId)
                            .link(Facility.TypedService.INVALID_LINK)))
                .other(
                    List.of(
                        FacilityTypedServiceUtil.getFacilityTypedService(
                                OtherService.OnlineScheduling, linkerUrl, facilityId)
                            .link(Facility.TypedService.INVALID_LINK)))
                .link(Facility.TypedService.INVALID_LINK)
                .lastUpdated(LocalDate.parse("2022-03-07"))
                .build());
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
            + "\"lastUpdated\":\"2022-03-07\""
            + "}}}",
        Facility.class,
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
            + "\"lastUpdated\":\"2022-03-07\""
            + "}}}",
        Facility.class,
        facility);
  }

  @Test
  @SneakyThrows
  void deserializeHealthCmsOverlay() {
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Dental.name()))
                                .name(HealthService.Dental.name())
                                .serviceType(DetailedService.ServiceType.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceInfo(
                ServiceInfo.builder()
                    .serviceId(uncapitalize(HealthService.Dental.name()))
                    .name(HealthService.Dental.name())
                    .serviceType(DetailedService.ServiceType.Health)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedServicesResponse() {
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(HealthService.Dental.name()))
                                .name(HealthService.Dental.name())
                                .serviceType(DetailedService.ServiceType.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":[" + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeInvalidDetailedService() {
    DetailedService invalidService =
        DetailedService.builder()
            .serviceInfo(
                ServiceInfo.builder()
                    .serviceId(INVALID_SVC_ID)
                    .name("foo")
                    .serviceType(DetailedService.ServiceType.Health)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}",
        DetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("OnlineScheduling");
    invalidService.serviceInfo().serviceType(DetailedService.ServiceType.Other);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("baz");
    invalidService.serviceInfo().serviceType(DetailedService.ServiceType.Health);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"baz\",\"serviceType\":\"bar\"}}",
        DetailedService.class,
        invalidService);
    invalidService.serviceInfo().name("Smoking");
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"Smoking\",\"serviceType\":\"baz\"}}",
        DetailedService.class,
        invalidService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherCmsOverlay() {
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                                .name(OtherService.OnlineScheduling.name())
                                .serviceType(DetailedService.ServiceType.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceInfo(
                ServiceInfo.builder()
                    .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                    .name(OtherService.OnlineScheduling.name())
                    .serviceType(DetailedService.ServiceType.Other)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedServicesResponse() {
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceInfo(
                            ServiceInfo.builder()
                                .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                                .name(OtherService.OnlineScheduling.name())
                                .serviceType(DetailedService.ServiceType.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }
}
