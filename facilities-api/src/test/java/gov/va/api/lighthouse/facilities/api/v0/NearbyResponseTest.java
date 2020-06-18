package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class NearbyResponseTest {
  @SneakyThrows
  private void assertReadable(String json) {
    NearbyResponse f =
        createMapper().readValue(getClass().getResourceAsStream(json), NearbyResponse.class);
    assertThat(f).isEqualTo(sample());
  }

  private NearbyResponse.Nearby nearbyFacility(
      String facilityId, Integer minTime, Integer maxTime, String related) {
    return NearbyResponse.Nearby.builder()
        .id(facilityId)
        .type(NearbyResponse.Type.NearbyFacility)
        .attributes(
            NearbyResponse.NearbyAttributes.builder().minTime(minTime).maxTime(maxTime).build())
        .relationships(
            NearbyResponse.Relationships.builder()
                .vaFacility(
                    NearbyResponse.VaFacility.builder()
                        .links(NearbyResponse.Links.builder().related(related).build())
                        .build())
                .build())
        .build();
  }

  private NearbyResponse sample() {
    return NearbyResponse.builder()
        .links(
            PageLinks.builder()
                .related(
                    "/services/va_facilities/v0/facilities?ids=vha_548GC,vha_548GF,vha_548QA,vha_548GA,vha_548GE,vha_675,vha_675GA,vha_675GB,vha_675GD,vha_675GF,vha_675GG,vha_675QB,vha_675QC,vha_675QD,vha_675QE,vha_675GC")
                .self(
                    "https://dev-api.vets.gov/services/va_facilities/v0/nearby?drive_time=100&lat=28.112464&lng=-80.7015994")
                .first(
                    "https://dev-api.vets.gov/services/va_facilities/v0/nearby?drive_time=100&lat=28.112464&lng=-80.7015994&page=1&per_page=20")
                .last(
                    "https://dev-api.vets.gov/services/va_facilities/v0/nearby?drive_time=100&lat=28.112464&lng=-80.7015994&page=1&per_page=20")
                .build())
        .meta(
            NearbyResponse.NearbyMetadata.builder()
                .pagination(
                    Pagination.builder()
                        .currentPage(1)
                        .entriesPerPage(20)
                        .totalPages(1)
                        .totalEntries(16)
                        .build())
                .build())
        .data(
            List.of(
                nearbyFacility(
                    "vha_548GC", 70, 80, "/services/va_facilities/v0/facilities/vha_548GC"),
                nearbyFacility(
                    "vha_548GF", 80, 90, "/services/va_facilities/v0/facilities/vha_548GF"),
                nearbyFacility(
                    "vha_548QA", 60, 70, "/services/va_facilities/v0/facilities/vha_548QA"),
                nearbyFacility(
                    "vha_548GA", 50, 60, "/services/va_facilities/v0/facilities/vha_548GA"),
                nearbyFacility(
                    "vha_548GE", 50, 60, "/services/va_facilities/v0/facilities/vha_548GE"),
                nearbyFacility("vha_675", 50, 60, "/services/va_facilities/v0/facilities/vha_675"),
                nearbyFacility(
                    "vha_675GA", 10, 20, "/services/va_facilities/v0/facilities/vha_675GA"),
                nearbyFacility(
                    "vha_675GB", 70, 80, "/services/va_facilities/v0/facilities/vha_675GB"),
                nearbyFacility(
                    "vha_675GD", 80, 90, "/services/va_facilities/v0/facilities/vha_675GD"),
                nearbyFacility(
                    "vha_675GF", 80, 90, "/services/va_facilities/v0/facilities/vha_675GF"),
                nearbyFacility(
                    "vha_675GG", 60, 70, "/services/va_facilities/v0/facilities/vha_675GG"),
                nearbyFacility(
                    "vha_675QB", 70, 80, "/services/va_facilities/v0/facilities/vha_675QB"),
                nearbyFacility(
                    "vha_675QC", 70, 80, "/services/va_facilities/v0/facilities/vha_675QC"),
                nearbyFacility(
                    "vha_675QD", 60, 70, "/services/va_facilities/v0/facilities/vha_675QD"),
                nearbyFacility(
                    "vha_675QE", 60, 70, "/services/va_facilities/v0/facilities/vha_675QE"),
                nearbyFacility(
                    "vha_675GC", 60, 70, "/services/va_facilities/v0/facilities/vha_675GC")))
        .build();
  }

  @Test
  @SneakyThrows
  public void unmarshallSample() {
    assertReadable("/nearby.json");
  }
}
