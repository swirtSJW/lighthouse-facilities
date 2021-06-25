package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfig.quietlyMap;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacility;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.SneakyThrows;

public class FacilitySamplesV1 {
  private final Map<String, Facility> facilities;

  @SneakyThrows
  @Builder
  FacilitySamplesV1(List<String> resources) {
    var mapper = FacilitiesJacksonConfig.createMapper();
    facilities =
        resources.stream()
            .map(r -> getClass().getResourceAsStream(r))
            .map(in -> quietlyMap(mapper, in, FacilityReadResponse.class))
            .map(FacilityReadResponse::facility)
            .collect(toMap(Facility::id, Function.identity()));
  }

  static FacilitySamplesV1 defaultSamples() {
    return FacilitySamplesV1.builder()
        .resources(List.of("/vha_691GB.json", "/vha_740GA.json", "/vha_757.json"))
        .build();
  }

  Facility facility(String id) {
    var f = facilities.get(id);
    assertThat(f).describedAs(id).isNotNull();
    return f;
  }

  FacilityEntity facilityEntity(String id) {
    return InternalFacilitiesController.populateV1(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(id))
            .lastUpdated(Instant.now())
            .build(),
        facility(id));
  }

  GeoFacility geoFacility(String id) {
    return GeoFacilityTransformerV1.builder().facility(facility(id)).build().toGeoFacility();
  }
}
