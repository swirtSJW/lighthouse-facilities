package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV0.quietlyMap;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.FacilityPair;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.SneakyThrows;

public class FacilitySamples {
  private final Map<String, Facility> facilities;

  private final Map<String, gov.va.api.lighthouse.facilities.api.v1.Facility> facilitiesV1;

  @SneakyThrows
  @Builder
  FacilitySamples(List<String> resources) {
    var mapper = FacilitiesJacksonConfigV0.createMapper();
    var mapperV1 = FacilitiesJacksonConfigV1.createMapper();

    facilities =
        resources.stream()
            .map(r -> getClass().getResourceAsStream(r))
            .map(in -> quietlyMap(mapper, in, FacilityReadResponse.class))
            .map(FacilityReadResponse::facility)
            .collect(toMap(Facility::id, Function.identity()));

    facilitiesV1 =
        resources.stream()
            .map(r -> getClass().getResourceAsStream(r))
            .map(
                in ->
                    quietlyMap(
                        mapperV1,
                        in,
                        gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse.class))
            .map(gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse::facility)
            .collect(
                toMap(gov.va.api.lighthouse.facilities.api.v1.Facility::id, Function.identity()));
  }

  static FacilitySamples defaultSamples() {
    return FacilitySamples.builder()
        .resources(List.of("/vha_691GB.json", "/vha_740GA.json", "/vha_757.json"))
        .build();
  }

  FacilityPair facility(String id) {
    var f = facilities.get(id);
    assertThat(f).describedAs(id).isNotNull();

    var fV1 = facilitiesV1.get(id);
    assertThat(fV1).describedAs(id).isNotNull();
    return FacilityPair.builder().v0(f).v1(fV1).build();
  }

  FacilityEntity facilityEntity(String id) {
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(id))
            .lastUpdated(Instant.now())
            .build(),
        facility(id));
  }

  GeoFacility geoFacility(String id) {
    var f = facilities.get(id);
    assertThat(f).describedAs(id).isNotNull();
    return GeoFacilityTransformerV0.builder().facility(f).build().toGeoFacility();
  }

  //  gov.va.api.lighthouse.facilities.api.v1.GeoFacility geoFacilityV1(String id) {
  //    var fV1 = facilitiesV1.get(id);
  //    assertThat(fV1).describedAs(id).isNotNull();
  //    return GeoFacilityTransformerV1.builder().facility(fV1).build().toGeoFacility();
  //  }
}
