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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class FacilitiesByLatLongTest {
  @Autowired private FacilityRepository repo;

  @Before
  public void _resetDatabase() {
    repo.deleteAll();
  }

  private FacilitiesController controller() {
    return FacilitiesController.builder()
        .facilityRepository(repo)
        .driveTimeBandRepository(mock(DriveTimeBandRepository.class))
        .baseUrl("http://foo/")
        .basePath("")
        .build();
  }

  @Test
  public void geoFacilities() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    "HEALTH",
                    List.of("primarycare"),
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(
                    List.of(
                        FacilitySamples.defaultSamples().geoFacility("vha_757"),
                        FacilitySamples.defaultSamples().geoFacility("vha_740GA"),
                        FacilitySamples.defaultSamples().geoFacility("vha_691GB")))
                .build());
  }

  @Test(expected = ExceptionsV0.InvalidParameter.class)
  public void json_invalidService() {
    controller()
        .jsonFacilitiesByLatLong(
            new BigDecimal("28.112464"),
            new BigDecimal("-80.7015994"),
            null,
            List.of("unknown"),
            1,
            1);
  }

  @Test(expected = ExceptionsV0.InvalidParameter.class)
  public void json_invalidType() {
    controller()
        .jsonFacilitiesByLatLong(
            new BigDecimal("28.112464"), new BigDecimal("-80.7015994"), "xxx", null, 1, 1);
  }

  @Test
  public void json_noFilter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"), new BigDecimal("-80.7015994"), null, null, 1, 10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA"),
                FacilitySamples.defaultSamples().facility("vha_691GB")));
  }

  @Test
  public void json_perPageZero() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"), new BigDecimal("-80.7015994"), null, null, 100, 0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?lat=28.112464&long=-80.7015994&page=100&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(100)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(3)
                                .build())
                        .distances(emptyList())
                        .build())
                .build());
  }

  @Test
  public void json_serviceOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    List.of("primarycare"),
                    1,
                    10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA"),
                FacilitySamples.defaultSamples().facility("vha_691GB")));
  }

  @Test
  public void json_typeAndService() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    String linkBase =
        "http://foo/v0/facilities?lat=28.112464&long=-80.7015994&services%5B%5D=primarycare&type=HEALTH";
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    "HEALTH",
                    List.of("primarycare"),
                    1,
                    10))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(
                    List.of(
                        FacilitySamples.defaultSamples().facility("vha_757"),
                        FacilitySamples.defaultSamples().facility("vha_740GA"),
                        FacilitySamples.defaultSamples().facility("vha_691GB")))
                .links(
                    PageLinks.builder()
                        .self(linkBase + "&page=1&per_page=10")
                        .first(linkBase + "&page=1&per_page=10")
                        .last(linkBase + "&page=1&per_page=10")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(10)
                                .totalPages(1)
                                .totalEntries(3)
                                .build())
                        .distances(
                            List.of(
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_757")
                                    .distance(new BigDecimal("829.69"))
                                    .build(),
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_740GA")
                                    .distance(new BigDecimal("1050.77"))
                                    .build(),
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_691GB")
                                    .distance(new BigDecimal("2333.84"))
                                    .build()))
                        .build())
                .build());
  }

  @Test
  public void json_typeOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    "HEALTH",
                    emptyList(),
                    1,
                    10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA"),
                FacilitySamples.defaultSamples().facility("vha_691GB")));
  }
}
