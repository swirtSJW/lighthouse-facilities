package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.pssg.PathEncoder;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NearbyTest {
  @Autowired FacilityRepository facilityRepository;

  @Autowired DriveTimeBandRepository driveTimeBandRepository;

  @Mock RestTemplate restTemplate = mock(RestTemplate.class);

  private NearbyController _controller() {
    return NearbyController.builder()
        .facilityRepository(facilityRepository)
        .driveTimeBandRepository(driveTimeBandRepository)
        .restTemplate(restTemplate)
        .bingKey("bingKey")
        .bingUrl("http://bing")
        .baseUrl("http://foo")
        .basePath("bp")
        .build();
  }

  @SneakyThrows
  private DriveTimeBandEntity _deprecatedPssgDriveTimeBandEntity(PssgDriveTimeBand band) {
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
                        .benefits(List.of(Facility.BenefitsService.ApplyingForBenefits))
                        .build())
                .build())
        .build();
  }

  private FacilityEntity _facilityEntity(Facility fac) {
    return FacilityManagementController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(fac.id()))
            .lastUpdated(Instant.now())
            .build(),
        fac);
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

  @Test
  @SneakyThrows
  public void address() {
    when(restTemplate.exchange(
            startsWith("http://bing"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            BingResponse.builder()
                                .resourceSets(
                                    List.of(
                                        BingResponse.ResourceSet.builder().build(),
                                        BingResponse.ResourceSet.builder()
                                            .resources(
                                                List.of(
                                                    BingResponse.Resource.builder().build(),
                                                    BingResponse.Resource.builder()
                                                        .point(BingResponse.Point.builder().build())
                                                        .build(),
                                                    BingResponse.Resource.builder()
                                                        .point(
                                                            BingResponse.Point.builder()
                                                                .coordinates(
                                                                    List.of(BigDecimal.ZERO))
                                                                .build())
                                                        .build(),
                                                    BingResponse.Resource.builder()
                                                        .point(
                                                            BingResponse.Point.builder()
                                                                .coordinates(
                                                                    List.of(
                                                                        new BigDecimal("-0.1"),
                                                                        new BigDecimal("0.1")))
                                                                .build())
                                                        .build()))
                                            .build()))
                                .build()))));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller()
            .nearbyAddress(
                "505 N John Rodes Blvd", "Melbourne", "FL", "32934", null, null, null, 1, 1);
    String linkBase =
        "http://foo/bp/v0/nearby?city=Melbourne&state=FL&street_address=505+N+John+Rodes+Blvd&zip=32934";
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
                        .self(linkBase + "&page=1&per_page=1")
                        .first(linkBase + "&page=1&per_page=1")
                        .last(linkBase + "&page=1&per_page=1")
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
  public void address_bingException() {
    when(restTemplate.exchange(
            startsWith("http://bing"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenThrow(new IllegalStateException("Google instead?"));
    assertThrows(
        ExceptionsV0.BingException.class,
        () ->
            _controller()
                .nearbyAddress(
                    "505 N John Rodes Blvd", "Melbourne", "FL", "32934", null, null, null, 1, 1));
  }

  @Test
  @SneakyThrows
  public void address_bingNoResults() {
    when(restTemplate.exchange(
            startsWith("http://bing"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            BingResponse.builder()
                                .resourceSets(
                                    List.of(
                                        BingResponse.ResourceSet.builder().build(),
                                        BingResponse.ResourceSet.builder()
                                            .resources(
                                                List.of(
                                                    BingResponse.Resource.builder().build(),
                                                    BingResponse.Resource.builder()
                                                        .point(BingResponse.Point.builder().build())
                                                        .build(),
                                                    BingResponse.Resource.builder()
                                                        .point(
                                                            BingResponse.Point.builder()
                                                                .coordinates(
                                                                    List.of(BigDecimal.ZERO))
                                                                .build())
                                                        .build()))
                                            .build()))
                                .build()))));
    assertThrows(
        ExceptionsV0.BingException.class,
        () ->
            _controller()
                .nearbyAddress(
                    "505 N John Rodes Blvd", "Melbourne", "FL", "32934", null, null, null, 1, 1));
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
    assertThat(response).isEqualTo(hitVha666());
  }

  public NearbyResponse hitVha666() {
    return NearbyResponse.builder()
        .data(
            List.of(
                NearbyResponse.Nearby.builder()
                    .id("vha_666")
                    .type(NearbyResponse.Type.NearbyFacility)
                    .attributes(
                        NearbyResponse.NearbyAttributes.builder().minTime(0).maxTime(10).build())
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
        .build();
  }

  @Test
  public void hitWithDeprecatedPssgDriveBands() {
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_666")));
    facilityRepository.save(_facilityEntity(_facilityHealth("vha_777")));
    driveTimeBandRepository.save(_deprecatedPssgDriveTimeBandEntity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(
        _deprecatedPssgDriveTimeBandEntity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null, null, 1, 1);
    assertThat(response).isEqualTo(hitVha666());
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
