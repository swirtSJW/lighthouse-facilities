package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FacilitiesByZipTest {
  @Autowired private FacilityRepository repo;

  private FacilitiesController controller() {
    return FacilitiesController.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  public void geoFacilitiesByZip() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().geoFacilitiesByZip("43219", "HEALTH", List.of("urology"), 1, 1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_757")))
                .build());
  }

  @Test(expected = ExceptionsV0.InvalidParameter.class)
  public void jsonFacilitiesByZip_invalidService() {
    controller().jsonFacilitiesByZip("33333", null, List.of("unknown"), 1, 1);
  }

  @Test(expected = ExceptionsV0.InvalidParameter.class)
  public void jsonFacilitiesByZip_invalidType() {
    controller().jsonFacilitiesByZip("33333", "xxx", null, 1, 1);
  }

  @Test
  public void jsonFacilitiesByZip_noFilter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByZip("43219", null, null, 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  public void jsonFacilitiesByZip_perPageZero() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByZip("43219", null, null, 100, 0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v0/facilities?zip=43219&page=100&per_page=0")
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
  public void jsonFacilitiesByZip_serviceOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByZip("43219", null, List.of("urology"), 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  public void jsonFacilitiesByZip_typeAndService() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByZip("43219", "HEALTH", List.of("primarycare"), 1, 1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facility("vha_757")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v0/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v0/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
                        .last(
                            "http://foo/bp/v0/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
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
  public void jsonFacilitiesByZip_typeOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByZip("43219", "HEALTH", emptyList(), 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }
}
