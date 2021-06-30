package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesByZipV1Test {
  @Autowired private FacilityRepository repo;

  private FacilitiesControllerV1 controller() {
    return FacilitiesControllerV1.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  void geoFacilitiesByZip() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().geoFacilitiesByZip("43219", "HEALTH", List.of("urology"), false, 1, 1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacilityV1("vha_757")))
                .build());
  }

  @Test
  void jsonFacilitiesByZip_invalidService() {
    assertThrows(
        ExceptionsV0.InvalidParameter.class,
        () -> controller().jsonFacilitiesByZip("33333", null, List.of("unknown"), null, 1, 1));
  }

  @Test
  void jsonFacilitiesByZip_invalidType() {
    assertThrows(
        ExceptionsV0.InvalidParameter.class,
        () -> controller().jsonFacilitiesByZip("33333", "xxx", null, null, 1, 1));
  }

  @Test
  void jsonFacilitiesByZip_noFilter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByZip("43219", null, null, null, 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757").v1));
  }

  @Test
  void jsonFacilitiesByZip_perPageZero() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByZip("43219", null, null, null, 100, 0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v1/facilities?zip=43219&page=100&per_page=0")
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
  void jsonFacilitiesByZip_serviceOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller().jsonFacilitiesByZip("43219", null, List.of("urology"), null, 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757").v1));
  }

  @Test
  void jsonFacilitiesByZip_typeAndService() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller().jsonFacilitiesByZip("43219", "HEALTH", List.of("primarycare"), null, 1, 1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facility("vha_757").v1))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
                        .last(
                            "http://foo/bp/v1/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
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
  void jsonFacilitiesByZip_typeOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByZip("43219", "HEALTH", emptyList(), null, 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757").v1));
  }
}
