package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesByBoundingBoxV1Test {
  @Autowired private FacilityRepository repo;

  private FacilitiesControllerV1 controller() {
    return FacilitiesControllerV1.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  void geoFacilities() {
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
                    false,
                    1,
                    1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacilityV1("vha_757")))
                .build());
  }

  @Test
  void json_invalidBBox() {
    assertThrows(
        ExceptionsV0.InvalidParameter.class,
        () ->
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
                    null,
                    1,
                    1));
  }

  @Test
  void json_invalidService() {
    assertThrows(
        ExceptionsV0.InvalidParameter.class,
        () ->
            controller()
                .jsonFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    null,
                    List.of("unknown"),
                    null,
                    1,
                    1));
  }

  @Test
  void json_invalidType() {
    assertThrows(
        ExceptionsV0.InvalidParameter.class,
        () ->
            controller()
                .jsonFacilitiesByBoundingBox(
                    List.of(
                        new BigDecimal("-80"),
                        new BigDecimal("20"),
                        new BigDecimal("-120"),
                        new BigDecimal("40")),
                    "xxx",
                    null,
                    null,
                    1,
                    1));
  }

  @Test
  void json_noFilter() {
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
                    null,
                    1,
                    1)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757").v1()));
  }

  @Test
  void json_perPageZero() {
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
                    null,
                    100,
                    0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&page=100&per_page=0")
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
  void json_serviceOnly() {
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
                    null,
                    1,
                    1)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757").v1()));
  }

  @Test
  void json_typeAndService() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    String linkBase =
        "http://foo/bp/v1/facilities?bbox%5B%5D=-80&bbox%5B%5D=20&bbox%5B%5D=-120&bbox%5B%5D=40&services%5B%5D=primarycare&type=HEALTH";
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
                    null,
                    2,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facility("vha_691GB").v1()))
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
  void json_typeOnly() {
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
                    null,
                    1,
                    1)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757").v1()));
  }
}
