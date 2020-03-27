package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;

public class FacilitiesControllerTest {
  FacilityRepository fr = mock(FacilityRepository.class);

  DriveTimeBandRepository dbr = mock(DriveTimeBandRepository.class);

  @Test
  public void all() {
    FacilitySamples samples = FacilitySamples.defaultSamples();
    when(fr.findAll())
        .thenReturn(
            List.of(
                samples.facilityEntity("vha_691GB"),
                samples.facilityEntity("vha_740GA"),
                samples.facilityEntity("vha_757")));
    var actual = controller().all();
    assertThat(actual.features()).hasSize(3);
  }

  private FacilitiesController controller() {
    return FacilitiesController.builder()
        .facilityRepository(fr)
        .driveTimeBandRepository(dbr)
        .baseUrl("http://foo/")
        .build();
  }

  @Test
  public void geoFacilitiesByBoundingBox() {
    when(fr.findAll(
            FacilityRepository.BBoxSpecification.builder()
                .minLongitude(new BigDecimal("-120"))
                .maxLongitude(new BigDecimal("-80"))
                .minLatitude(new BigDecimal("20"))
                .maxLatitude(new BigDecimal("40"))
                .facilityType(FacilityEntity.Type.vha)
                .services(Set.of(Facility.HealthService.Urology))
                .build()))
        .thenReturn(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(
            controller()
                .geoFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    "HEALTH",
                    List.of("urology"),
                    1,
                    1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_757")))
                .build());
  }

  @Test
  public void geoFacilitiesByIds() {
    when(fr.findByIdIn(
            List.of(
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB"),
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "740GA"),
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "757"))))
        .thenReturn(
            List.of(
                FacilitySamples.defaultSamples().facilityEntity("vha_757"),
                FacilitySamples.defaultSamples().facilityEntity("vha_740GA"),
                FacilitySamples.defaultSamples().facilityEntity("vha_691GB")));
    assertThat(controller().geoFacilitiesByIds("x,vha_691GB,,x,,vha_740GA,vha_757", 2, 1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_740GA")))
                .build());
  }

  @Test(expected = ExceptionsV0.InvalidParameter.class)
  public void jsonFacilitiesByBoundingBox_invalidBBox() {
    controller()
        .jsonFacilitiesByBoundingBox(
            List.of(
                new BigDecimal("-80"),
                new BigDecimal("20"),
                new BigDecimal("-120"),
                new BigDecimal("40"),
                BigDecimal.ZERO),
            null,
            null,
            1,
            1);
  }

  @Test(expected = ExceptionsV0.InvalidParameter.class)
  public void jsonFacilitiesByBoundingBox_invalidService() {
    controller()
        .jsonFacilitiesByBoundingBox(
            List.of(
                new BigDecimal("-80"),
                new BigDecimal("20"),
                new BigDecimal("-120"),
                new BigDecimal("40")),
            null,
            List.of("unknown"),
            1,
            1);
  }

  @Test(expected = ExceptionsV0.InvalidParameter.class)
  public void jsonFacilitiesByBoundingBox_invalidType() {
    controller()
        .jsonFacilitiesByBoundingBox(
            List.of(
                new BigDecimal("-80"),
                new BigDecimal("20"),
                new BigDecimal("-120"),
                new BigDecimal("40")),
            "xxx",
            null,
            1,
            1);
  }

  @Test
  public void jsonFacilitiesByBoundingBox_noFilter() {
    when(fr.findAll(
            FacilityRepository.BBoxSpecification.builder()
                .minLongitude(new BigDecimal("-120"))
                .maxLongitude(new BigDecimal("-80"))
                .minLatitude(new BigDecimal("20"))
                .maxLatitude(new BigDecimal("40"))
                .build()))
        .thenReturn(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(
            controller()
                .jsonFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    null,
                    null,
                    1,
                    1)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  public void jsonFacilitiesByBoundingBox_perPageZero() {
    when(fr.findAll(
            FacilityRepository.BBoxSpecification.builder()
                .minLongitude(new BigDecimal("-120"))
                .maxLongitude(new BigDecimal("-80"))
                .minLatitude(new BigDecimal("20"))
                .maxLatitude(new BigDecimal("40"))
                .build()))
        .thenReturn(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(
            controller()
                .jsonFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    null,
                    null,
                    100,
                    0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&page=100&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(100)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void jsonFacilitiesByBoundingBox_serviceOnly() {
    when(fr.findAll(
            FacilityRepository.BBoxSpecification.builder()
                .minLongitude(new BigDecimal("-120"))
                .maxLongitude(new BigDecimal("-80"))
                .minLatitude(new BigDecimal("20"))
                .maxLatitude(new BigDecimal("40"))
                .services(Set.of(Facility.HealthService.Urology))
                .build()))
        .thenReturn(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(
            controller()
                .jsonFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    null,
                    List.of("urology"),
                    1,
                    1)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  public void jsonFacilitiesByBoundingBox_typeAndService() {
    when(fr.findAll(
            FacilityRepository.BBoxSpecification.builder()
                .minLongitude(new BigDecimal("-120"))
                .maxLongitude(new BigDecimal("-80"))
                .minLatitude(new BigDecimal("20"))
                .maxLatitude(new BigDecimal("40"))
                .facilityType(FacilityEntity.Type.vha)
                .services(Set.of(Facility.HealthService.Urology))
                .build()))
        .thenReturn(
            List.of(
                FacilitySamples.defaultSamples().facilityEntity("vha_691GB"),
                FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(
            controller()
                .jsonFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    "HEALTH",
                    List.of("urology"),
                    2,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facility("vha_691GB")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&services%5B%5D=urology&type=HEALTH&page=2&per_page=1")
                        .first(
                            "http://foo/v0/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&services%5B%5D=urology&type=HEALTH&page=1&per_page=1")
                        .prev(
                            "http://foo/v0/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&services%5B%5D=urology&type=HEALTH&page=1&per_page=1")
                        .last(
                            "http://foo/v0/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&services%5B%5D=urology&type=HEALTH&page=2&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
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

  @Test
  public void jsonFacilitiesByBoundingBox_typeOnly() {
    when(fr.findAll(
            FacilityRepository.BBoxSpecification.builder()
                .minLongitude(new BigDecimal("-120"))
                .maxLongitude(new BigDecimal("-80"))
                .minLatitude(new BigDecimal("20"))
                .maxLatitude(new BigDecimal("40"))
                .facilityType(FacilityEntity.Type.vha)
                .build()))
        .thenReturn(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(
            controller()
                .jsonFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    "HEALTH",
                    emptyList(),
                    1,
                    1)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  public void jsonFacilitiesByIds() {
    when(fr.findByIdIn(
            List.of(
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB"),
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "740GA"),
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "757"))))
        .thenReturn(
            List.of(
                FacilitySamples.defaultSamples().facilityEntity("vha_740GA"),
                FacilitySamples.defaultSamples().facilityEntity("vha_691GB"),
                FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(controller().jsonFacilitiesByIds("x,vha_691GB,,x,,vha_740GA,vha_757", 2, 1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facility("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=2&per_page=1")
                        .first(
                            "http://foo/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=1&per_page=1")
                        .prev(
                            "http://foo/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=1&per_page=1")
                        .next(
                            "http://foo/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=3&per_page=1")
                        .last(
                            "http://foo/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=3&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(2)
                                .entriesPerPage(1)
                                .totalPages(3)
                                .totalEntries(3)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void jsonFacilitiesByIds_perPageZero() {
    when(fr.findByIdIn(
            List.of(
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB"),
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "740GA"),
                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "757"))))
        .thenReturn(
            List.of(
                FacilitySamples.defaultSamples().facilityEntity("vha_691GB"),
                FacilitySamples.defaultSamples().facilityEntity("vha_740GA"),
                FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(controller().jsonFacilitiesByIds("x,vha_691GB,,x,,vha_740GA,vha_757", 2, 0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=2&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(2)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(3)
                                .build())
                        .build())
                .build());
  }

  @Test
  public void nearby() {
    // currently not implemented
    assertThat(controller().nearby(1.23, 4.56)).isNull();
  }

  @Test
  public void readGeoJson() {
    GeoFacility geo = FacilitySamples.defaultSamples().geoFacility("vha_691GB");
    FacilityEntity entity = FacilitySamples.defaultSamples().facilityEntity("vha_691GB");
    when(fr.findById(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB")))
        .thenReturn(Optional.of(entity));
    assertThat(controller().readGeoJson("vha_691GB")).isEqualTo(GeoFacilityReadResponse.of(geo));
  }

  @Test
  public void readJson() {
    Facility facility = FacilitySamples.defaultSamples().facility("vha_691GB");
    FacilityEntity entity = FacilitySamples.defaultSamples().facilityEntity("vha_691GB");
    when(fr.findById(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB")))
        .thenReturn(Optional.of(entity));
    assertThat(controller().readJson("vha_691GB"))
        .isEqualTo(FacilityReadResponse.builder().facility(facility).build());
  }

  @Test(expected = ExceptionsV0.NotFound.class)
  public void readJson_malformed() {
    controller().readJson("xxx");
  }

  @Test(expected = ExceptionsV0.NotFound.class)
  public void readJson_notFound() {
    controller().readJson("vha_691GB");
  }
}
