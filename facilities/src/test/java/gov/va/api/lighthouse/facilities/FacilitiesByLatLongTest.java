package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesByLatLongTest {
  @Autowired private FacilityRepository repo;

  private FacilitiesControllerV0 controller() {
    return FacilitiesControllerV0.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("")
        .build();
  }

  @Test
  void geoFacilities() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    // Query for facilities without constraining to a specified radius
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    false,
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
    // Query for facilities within a 75 mile radius of (35.4423637, -119.77646693)
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("35.4423637"),
                    new BigDecimal("-119.77646693"),
                    new BigDecimal("75"),
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_691GB")))
                .build());
    // Query for facilities within a 50 mile radius of (29.112464, -80.7015994)
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("29.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("50"),
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(emptyList())
                .build());
  }

  @Test
  void geoFacilities_ids() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_757")))
                .build());
  }

  @Test
  void geoFacilities_invalidRadius() {
    // Query for facilities constrained to within a negative radius
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("-10"),
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    1));
  }

  @Test
  void geoFacilities_radiusOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    // Query for facilities within a 2500 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("2500"),
                    null,
                    null,
                    emptyList(),
                    null,
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
    // Query for facilities within a 2000 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("2000"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(
                    List.of(
                        FacilitySamples.defaultSamples().geoFacility("vha_757"),
                        FacilitySamples.defaultSamples().geoFacility("vha_740GA")))
                .build());
    // Query for facilities within a 1000 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("1000"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_757")))
                .build());
    // Query for facilities within a 500 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("500"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(emptyList())
                .build());
  }

  @Test
  void json_ids() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    null,
                    null,
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  void json_invalidRadius() {
    // Query for facilities constrained to within a negative radius
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("-10"),
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    1));
  }

  @Test
  void json_invalidService() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    List.of("unknown"),
                    null,
                    1,
                    1));
  }

  @Test
  void json_invalidType() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    "xxx",
                    null,
                    null,
                    1,
                    1));
  }

  @Test
  void json_noFilter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    null,
                    null,
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
  void json_perPageZero() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    // Query for facilities without constraining to a specified radius
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
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
    // Query for facilities within a 75 mile radius of (27.1745479800001, -97.6667188)
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("27.1745479800001"),
                    new BigDecimal("-97.6667188"),
                    new BigDecimal("75"),
                    null,
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
                            "http://foo/v0/facilities?lat=27.1745479800001&long=-97.6667188&radius=75&page=100&per_page=0")
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
                        .distances(emptyList())
                        .build())
                .build());
    // Query for facilities within a 50 mile radius of (29.112464, -80.7015994)
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("29.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("50"),
                    null,
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
                            "http://foo/v0/facilities?lat=29.112464&long=-80.7015994&radius=50&page=100&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(100)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(0)
                                .build())
                        .distances(emptyList())
                        .build())
                .build());
  }

  @Test
  void json_radiusOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    // Query for facilities within a 2500 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("2500"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA"),
                FacilitySamples.defaultSamples().facility("vha_691GB")));
    // Query for facilities within a 2000 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("2000"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA")));
    // Query for facilities within a 1000 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("1000"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
    // Query for facilities within a 500 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("500"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(emptyList());
  }

  @Test
  void json_serviceOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    List.of("primarycare"),
                    null,
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
  void json_typeAndService() {
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
                    null,
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    null,
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
  void json_typeOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    "HEALTH",
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA"),
                FacilitySamples.defaultSamples().facility("vha_691GB")));
  }

  @BeforeEach
  void setUp() {
    ServiceLinkHelper serviceLinkHelper = new ServiceLinkHelper();
    serviceLinkHelper.baseUrl("http://localhost:8085");
    serviceLinkHelper.basePath("/");
    ApplicationContext mockContext = mock(ApplicationContext.class);
    when(mockContext.getBean(ServiceLinkHelper.class)).thenReturn(serviceLinkHelper);
    ApplicationContextHolder contextHolder = new ApplicationContextHolder();
    contextHolder.setApplicationContext(mockContext);
  }
}
