package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ServiceType;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    when(fr.findAll())
        .thenReturn(
            List.of(
                garbageFacilityEntity("trash1"),
                garbageFacilityEntity("trash2"),
                garbageFacilityEntity("trash3")));
    var actual = controller().all();
    assertThat(actual).hasSize(3);
  }

  FacilitiesController controller() {
    return FacilitiesController.builder()
        .facilityRepository(fr)
        .driveTimeBandRepository(dbr)
        .build();
  }

  @Test
  public void garbage() {
    controller().makeSomeGarbage("123");
    controller().makeSomeGarbage("456");
    controller().makeSomeGarbage("789");
  }

  private FacilityEntity garbageFacilityEntity(String stationNumber) {
    Random r = new SecureRandom();
    HealthService[] healthyBois = HealthService.values();
    Set<ServiceType> services = new HashSet<>();
    services.add(healthyBois[r.nextInt(healthyBois.length)]);
    services.add(healthyBois[r.nextInt(healthyBois.length)]);
    FacilityEntity hotGarbage =
        FacilityEntity.typeSafeBuilder()
            .id(FacilityEntity.Pk.of(FacilityEntity.Type.vha, stationNumber))
            .state(r.nextBoolean() ? "FL" : "South")
            .zip("3290" + r.nextInt(10))
            .longitude(r.nextDouble())
            .latitude(r.nextDouble())
            .servicesTypes(services)
            .facility("{\"stationNumber\":\"" + stationNumber + "\",\"gargbage\":\"hot\"}")
            .build();
    return hotGarbage;
  }

  @Test
  void nearby() {
    // currently not implemented
    assertThat(controller().nearby(1.23, 4.56)).isNull();
  }
}
