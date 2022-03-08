package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService.ApplyingForBenefits;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.PrimaryCare;
import static gov.va.api.lighthouse.facilities.api.v1.FacilityTypedServiceUtil.getFacilityTypedServices;
import static gov.va.api.lighthouse.facilities.api.v1.NearbyResponse.Type.NearbyFacility;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.pssg.PathEncoder;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import gov.va.api.lighthouse.facilities.collector.InsecureRestTemplateProvider;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class NearbyV1Test {
  @Autowired FacilityRepository facilityRepository;

  @Autowired DriveTimeBandRepository driveTimeBandRepository;

  @Mock RestTemplate restTemplate = mock(RestTemplate.class);

  private NearbyControllerV1 _controller() {
    InsecureRestTemplateProvider restTemplateProvider = mock(InsecureRestTemplateProvider.class);
    when(restTemplateProvider.restTemplate()).thenReturn(restTemplate);
    return NearbyControllerV1.builder()
        .facilityRepository(facilityRepository)
        .driveTimeBandRepository(driveTimeBandRepository)
        .build();
  }

  @SneakyThrows
  private DriveTimeBandEntity _deprecatedPssgDriveTimeBandEntity(PssgDriveTimeBand band) {
    List<List<Double>> flatRings =
        band.geometry().rings().stream().flatMap(r -> r.stream()).collect(toList());
    return DriveTimeBandEntity.builder()
        .id(
            DriveTimeBandEntity.Pk.of(
                band.attributes().stationNumber(),
                band.attributes().fromBreak(),
                band.attributes().toBreak()))
        .minLongitude(flatRings.stream().mapToDouble(c -> c.get(0)).min().orElseThrow())
        .maxLongitude(flatRings.stream().mapToDouble(c -> c.get(0)).max().orElseThrow())
        .minLatitude(flatRings.stream().mapToDouble(c -> c.get(1)).min().orElseThrow())
        .maxLatitude(flatRings.stream().mapToDouble(c -> c.get(1)).max().orElseThrow())
        .band(JacksonConfig.createMapper().writeValueAsString(band))
        .build();
  }

  private PssgDriveTimeBand _diamondBand(
      String stationNumber, int fromMinutes, int toMinutes, int offset) {
    return PssgDriveTimeBand.builder()
        .attributes(
            PssgDriveTimeBand.Attributes.builder()
                .stationNumber(stationNumber)
                .fromBreak(fromMinutes)
                .toBreak(toMinutes)
                .build())
        .geometry(
            PssgDriveTimeBand.Geometry.builder()
                .rings(
                    List.of(
                        List.of(
                            PssgDriveTimeBand.coord(offset, offset + 2),
                            PssgDriveTimeBand.coord(offset + 1, offset),
                            PssgDriveTimeBand.coord(offset, offset - 2),
                            PssgDriveTimeBand.coord(offset - 1, offset))))
                .build())
        .build();
  }

  @SneakyThrows
  private DriveTimeBandEntity _entity(PssgDriveTimeBand band) {
    List<List<Double>> flatRings =
        band.geometry().rings().stream().flatMap(r -> r.stream()).collect(toList());
    return DriveTimeBandEntity.builder()
        .id(
            DriveTimeBandEntity.Pk.of(
                band.attributes().stationNumber(),
                band.attributes().fromBreak(),
                band.attributes().toBreak()))
        .minLongitude(flatRings.stream().mapToDouble(c -> c.get(0)).min().orElseThrow())
        .maxLongitude(flatRings.stream().mapToDouble(c -> c.get(0)).max().orElseThrow())
        .minLatitude(flatRings.stream().mapToDouble(c -> c.get(1)).min().orElseThrow())
        .maxLatitude(flatRings.stream().mapToDouble(c -> c.get(1)).max().orElseThrow())
        .band(PathEncoder.create().encodeToBase64(band))
        .build();
  }

  private Facility _facilityBenefits(String id) {
    return Facility.builder()
        .id(id)
        .attributes(
            Facility.FacilityAttributes.builder()
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .services(
                    Facility.Services.builder()
                        .benefits(
                            getFacilityTypedServices(
                                List.of(ApplyingForBenefits), "http://localhost:8085/v1/", id))
                        .link(buildServicesLink("http://localhost:8085/v1/", id))
                        .build())
                .build())
        .build();
  }

  private FacilityEntity _facilityEntity(DatamartFacility datamartFacility) {
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(datamartFacility.id()))
            .lastUpdated(Instant.now())
            .build(),
        datamartFacility);
  }

  private DatamartFacility _facilityHealth(String id) {
    DatamartFacility facilityV1 =
        FacilityTransformerV1.toVersionAgnostic(
            Facility.builder()
                .id(id)
                .attributes(
                    Facility.FacilityAttributes.builder()
                        .latitude(BigDecimal.ONE)
                        .longitude(BigDecimal.ONE)
                        .services(
                            Facility.Services.builder()
                                .health(
                                    getFacilityTypedServices(
                                        List.of(PrimaryCare), "http://localhost:8085/v1/", id))
                                .link(buildServicesLink("http://localhost:8085/v1/", id))
                                .build())
                        .build())
                .build());
    return facilityV1;
  }

  @Test
  void empty() {
    var baseUrl = "http://localhost:8085";
    var basePath = "/";
    ServiceLinkHelper serviceLinkHelper = new ServiceLinkHelper();
    serviceLinkHelper.baseUrl(baseUrl);
    serviceLinkHelper.basePath(basePath);
    ApplicationContext mockContext = mock(ApplicationContext.class);
    when(mockContext.getBean(ServiceLinkHelper.class)).thenReturn(serviceLinkHelper);
    ApplicationContextHolder contextHolder = new ApplicationContextHolder();
    contextHolder.setApplicationContext(mockContext);

    facilityRepository.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(emptyList())
                .meta(NearbyResponse.Meta.builder().bandVersion("Unknown").build())
                .build());
  }

  @Test
  void filterMaxDriveTime() {
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 50, 60, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, 50);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(emptyList())
                .meta(NearbyResponse.Meta.builder().bandVersion("Unknown").build())
                .build());
  }

  @Test
  void filterServices() {
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, List.of("primarycare"), null);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(
                    List.of(
                        NearbyResponse.Nearby.builder()
                            .id("vha_666")
                            .type(NearbyFacility)
                            .attributes(
                                NearbyResponse.NearbyAttributes.builder()
                                    .minTime(0)
                                    .maxTime(10)
                                    .build())
                            .build()))
                .meta(NearbyResponse.Meta.builder().bandVersion("Unknown").build())
                .build());
  }

  @Test
  void hit() {
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null);
    assertThat(response).isEqualTo(hitVha666());
  }

  NearbyResponse hitVha666() {
    return NearbyResponse.builder()
        .data(
            List.of(
                NearbyResponse.Nearby.builder()
                    .id("vha_666")
                    .type(NearbyFacility)
                    .attributes(
                        NearbyResponse.NearbyAttributes.builder().minTime(0).maxTime(10).build())
                    .build()))
        .meta(NearbyResponse.Meta.builder().bandVersion("Unknown").build())
        .build();
  }

  @Test
  void hitWithDeprecatedPssgDriveBands() {
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_deprecatedPssgDriveTimeBandEntity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(
        _deprecatedPssgDriveTimeBandEntity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null);
    assertThat(response).isEqualTo(hitVha666());
  }
}
