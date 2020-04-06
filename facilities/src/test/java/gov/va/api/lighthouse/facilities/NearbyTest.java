package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Attributes;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Geometry;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class NearbyTest {
  @Autowired private FacilityRepository facilityRepository;

  @Autowired private DriveTimeBandRepository driveTimeBandRepository;

  private FacilitiesController _controller() {
    return FacilitiesController.builder()
        .facilityRepository(facilityRepository)
        .driveTimeBandRepository(driveTimeBandRepository)
        .baseUrl("http://foo")
        .basePath("bp")
        .build();
  }

  private PssgDriveTimeBand _diamondBand(
      String stationNumber, int fromMinutes, int toMinutes, int offset) {
    return PssgDriveTimeBand.builder()
        .attributes(
            Attributes.builder()
                .stationNumber(stationNumber)
                .fromBreak(fromMinutes)
                .toBreak(toMinutes)
                .build())
        .geometry(
            Geometry.builder()
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
        band.geometry().rings().stream().flatMap(r -> r.stream()).collect(Collectors.toList());
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

  private Facility _facilityBenefits(String id) {
    return Facility.builder()
        .id(id)
        .attributes(
            Facility.FacilityAttributes.builder()
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .services(
                    Facility.Services.builder()
                        .benefits(List.of(Facility.BenefitsService.ApplyingForBenefits))
                        .build())
                .build())
        .build();
  }

  private FacilityEntity _facilityEntity(Facility fac) {
    return FacilityManagementController.populate(
        FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString(fac.id())).build(), fac);
  }

  private Facility _facilityHealth(String id) {
    return Facility.builder()
        .id(id)
        .attributes(
            Facility.FacilityAttributes.builder()
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .services(
                    Facility.Services.builder()
                        .health(List.of(Facility.HealthService.PrimaryCare))
                        .build())
                .build())
        .build();
  }

  @Before
  public void _resetDatabase() {
    driveTimeBandRepository.deleteAll();
  }

  @Test
  public void empty() {
    facilityRepository.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null, null, 1, 1);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=1")
                        .first("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=1")
                        .last("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=1")
                        .build())
                .meta(
                    NearbyResponse.NearbyMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(0)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void filterMaxDriveTime() {
    facilityRepository.save(_facilityEntity(_facilityBenefits("vba_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 50, 60, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null, 50, 1, 1);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v0/nearby?drive_time=50&lat=0&lng=0&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v0/nearby?drive_time=50&lat=0&lng=0&page=1&per_page=1")
                        .last("http://foo/bp/v0/nearby?drive_time=50&lat=0&lng=0&page=1&per_page=1")
                        .build())
                .meta(
                    NearbyResponse.NearbyMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(0)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void filterServices() {
    facilityRepository.save(_facilityEntity(_facilityBenefits("vba_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller()
            .nearbyLatLong(
                BigDecimal.ZERO, BigDecimal.ZERO, null, List.of("primarycare"), null, 1, 1);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(
                    List.of(
                        NearbyResponse.Nearby.builder()
                            .id("vha_666")
                            .type(NearbyResponse.Type.NearbyFacility)
                            .attributes(
                                NearbyResponse.NearbyAttributes.builder()
                                    .minTime(0)
                                    .maxTime(10)
                                    .build())
                            .relationships(
                                NearbyResponse.Relationships.builder()
                                    .vaFacility(
                                        NearbyResponse.VaFacility.builder()
                                            .links(
                                                NearbyResponse.Links.builder()
                                                    .related("http://foo/bp/v0/facilities/vha_666")
                                                    .build())
                                            .build())
                                    .build())
                            .build()))
                .links(
                    PageLinks.builder()
                        .related("http://foo/bp/v0/facilities?ids=vha_666")
                        .self(
                            "http://foo/bp/v0/nearby?lat=0&lng=0&services%5B%5D=primarycare&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v0/nearby?lat=0&lng=0&services%5B%5D=primarycare&page=1&per_page=1")
                        .last(
                            "http://foo/bp/v0/nearby?lat=0&lng=0&services%5B%5D=primarycare&page=1&per_page=1")
                        .build())
                .meta(
                    NearbyResponse.NearbyMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void filterType() {
    facilityRepository.save(_facilityEntity(_facilityBenefits("vba_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, "benefits", null, null, 1, 1);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(
                    List.of(
                        NearbyResponse.Nearby.builder()
                            .id("vba_666")
                            .type(NearbyResponse.Type.NearbyFacility)
                            .attributes(
                                NearbyResponse.NearbyAttributes.builder()
                                    .minTime(0)
                                    .maxTime(10)
                                    .build())
                            .relationships(
                                NearbyResponse.Relationships.builder()
                                    .vaFacility(
                                        NearbyResponse.VaFacility.builder()
                                            .links(
                                                NearbyResponse.Links.builder()
                                                    .related("http://foo/bp/v0/facilities/vba_666")
                                                    .build())
                                            .build())
                                    .build())
                            .build()))
                .links(
                    PageLinks.builder()
                        .related("http://foo/bp/v0/facilities?ids=vba_666")
                        .self("http://foo/bp/v0/nearby?lat=0&lng=0&type=benefits&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v0/nearby?lat=0&lng=0&type=benefits&page=1&per_page=1")
                        .last("http://foo/bp/v0/nearby?lat=0&lng=0&type=benefits&page=1&per_page=1")
                        .build())
                .meta(
                    NearbyResponse.NearbyMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void hit() {
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null, null, 1, 1);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(
                    List.of(
                        NearbyResponse.Nearby.builder()
                            .id("vha_666")
                            .type(NearbyResponse.Type.NearbyFacility)
                            .attributes(
                                NearbyResponse.NearbyAttributes.builder()
                                    .minTime(0)
                                    .maxTime(10)
                                    .build())
                            .relationships(
                                NearbyResponse.Relationships.builder()
                                    .vaFacility(
                                        NearbyResponse.VaFacility.builder()
                                            .links(
                                                NearbyResponse.Links.builder()
                                                    .related("http://foo/bp/v0/facilities/vha_666")
                                                    .build())
                                            .build())
                                    .build())
                            .build()))
                .links(
                    PageLinks.builder()
                        .related("http://foo/bp/v0/facilities?ids=vha_666")
                        .self("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=1")
                        .first("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=1")
                        .last("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=1")
                        .build())
                .meta(
                    NearbyResponse.NearbyMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void perPageZero() {
    facilityRepository.save(_facilityEntity(_facilityBenefits("vba_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null, null, 1, 0);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=0")
                        .build())
                .meta(
                    NearbyResponse.NearbyMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(2)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void sameStationNumber() {
    facilityRepository.save(_facilityEntity(_facilityBenefits("vba_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null, null, 2, 1);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(
                    List.of(
                        NearbyResponse.Nearby.builder()
                            .id("vha_666")
                            .type(NearbyResponse.Type.NearbyFacility)
                            .attributes(
                                NearbyResponse.NearbyAttributes.builder()
                                    .minTime(0)
                                    .maxTime(10)
                                    .build())
                            .relationships(
                                NearbyResponse.Relationships.builder()
                                    .vaFacility(
                                        NearbyResponse.VaFacility.builder()
                                            .links(
                                                NearbyResponse.Links.builder()
                                                    .related("http://foo/bp/v0/facilities/vha_666")
                                                    .build())
                                            .build())
                                    .build())
                            .build()))
                .links(
                    PageLinks.builder()
                        .related("http://foo/bp/v0/facilities?ids=vha_666")
                        .self("http://foo/bp/v0/nearby?lat=0&lng=0&page=2&per_page=1")
                        .first("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=1")
                        .prev("http://foo/bp/v0/nearby?lat=0&lng=0&page=1&per_page=1")
                        .last("http://foo/bp/v0/nearby?lat=0&lng=0&page=2&per_page=1")
                        .build())
                .meta(
                    NearbyResponse.NearbyMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(2)
                                .entriesPerPage(1)
                                .totalPages(2)
                                .totalEntries(2)
                                .build())
                        .build())
                .build());
  }
}
