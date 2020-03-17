package gov.va.api.lighthouse.facilities.api.facilities;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.Test;

public class SerializeOnlyTest {
  @Test
  @SneakyThrows
  public void searchAllIsValidGeoFacilitiesResponse() {
    GeoFacilitiesResponse all =
        createMapper()
            .readValue(
                getClass().getResourceAsStream("/facilities-search-all.json"),
                GeoFacilitiesResponse.class);
    assertThat(all).isExactlyInstanceOf(GeoFacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByLatLongIsValidFacilitiesResponse() {
    FacilitiesSearchResponse latlong =
        createMapper()
            .readValue(
                getClass().getResourceAsStream("/search-lat-long.json"),
                FacilitiesSearchResponse.class);
    assertThat(latlong).isExactlyInstanceOf(FacilitiesSearchResponse.class);
  }
}
