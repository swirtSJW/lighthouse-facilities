package gov.va.api.lighthouse.facilities.api.v1;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class GeoFacilitiesResponseEmptyFieldsTest {

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(GeoFacilitiesResponse.builder().build().isEmpty()).isTrue();
    assertThat(GeoFacilitiesResponse.builder().features(emptyList()).build().isEmpty()).isTrue();
    // Not empty
    assertThat(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacilitiesResponse.builder()
                .features(List.of(GeoFacility.builder().type(GeoFacility.Type.Feature).build()))
                .build()
                .isEmpty())
        .isFalse();
  }
}
