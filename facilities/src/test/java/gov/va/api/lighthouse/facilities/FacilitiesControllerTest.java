package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FacilitiesControllerTest {
  @Mock FacilityRepository fr;
  @Mock DriveTimeBandRepository dbr;

  @Test
  void all() {
    FacilitySamples samples = FacilitySamples.defaultSamples();
    when(fr.findAll())
        .thenReturn(
            List.of(
                samples.facilityEntity("vha_691GB"),
                samples.facilityEntity("vha_740GA"),
                samples.facilityEntity("vha_757")));
    var actual = controller().all();
    assertThat(actual.features()).hasSize(3);
  }

  FacilitiesController controller() {
    return FacilitiesController.builder()
        .facilityRepository(fr)
        .driveTimeBandRepository(dbr)
        .build();
  }

  @Test
  void nearby() {
    // currently not implemented
    assertThat(controller().nearby(1.23, 4.56)).isNull();
  }

  @Test
  @SneakyThrows
  public void read() {
    Facility facility = FacilitySamples.defaultSamples().facility("vha_691GB");
    FacilityEntity entity = FacilitySamples.defaultSamples().facilityEntity("vha_691GB");
    when(fr.findById(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB")))
        .thenReturn(Optional.of(entity));
    assertThat(controller().readJson("vha_691GB"))
        .isEqualTo(FacilityReadResponse.builder().facility(facility).build());
  }

  @Test
  @SneakyThrows
  public void readGeoJson() {
    GeoFacility geo = FacilitySamples.defaultSamples().geoFacility("vha_691GB");
    FacilityEntity entity = FacilitySamples.defaultSamples().facilityEntity("vha_691GB");
    when(fr.findById(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB")))
        .thenReturn(Optional.of(entity));
    assertThat(controller().readGeoJson("vha_691GB")).isEqualTo(GeoFacilityReadResponse.of(geo));
  }
}
