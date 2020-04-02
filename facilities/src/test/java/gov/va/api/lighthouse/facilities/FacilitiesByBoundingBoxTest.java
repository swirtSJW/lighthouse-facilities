package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class FacilitiesByBoundingBoxTest {
  @Autowired private FacilityRepository repo;

  private FacilitiesController controller() {
    return FacilitiesController.builder()
        .facilityRepository(repo)
        .driveTimeBandRepository(mock(DriveTimeBandRepository.class))
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  public void geoFacilities() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
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

  @Test(expected = ExceptionsV0.InvalidParameter.class)
  public void json_invalidBBox() {
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
  public void json_invalidService() {
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
  public void json_invalidType() {
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
  public void json_noFilter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
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
  public void json_perPageZero() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
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
                            "http://foo/bp/v0/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&page=100&per_page=0")
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
  public void json_serviceOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
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
  public void json_typeAndService() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));

    String linkBase =
        "http://foo/bp/v0/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&services%5B%5D=primarycare&type=HEALTH";
    assertThat(
            controller()
                .jsonFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    "HEALTH",
                    List.of("primarycare"),
                    2,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facility("vha_691GB")))
                .links(
                    PageLinks.builder()
                        .self(linkBase + "&page=2&per_page=1")
                        .first(linkBase + "&page=1&per_page=1")
                        .prev(linkBase + "&page=1&per_page=1")
                        .last(linkBase + "&page=2&per_page=1")
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
  public void json_typeOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
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
}
