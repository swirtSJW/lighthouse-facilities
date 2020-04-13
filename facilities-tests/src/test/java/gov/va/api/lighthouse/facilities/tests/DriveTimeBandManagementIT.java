package gov.va.api.lighthouse.facilities.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.tests.categories.DriveTimeBandManagement;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(DriveTimeBandManagement.class)
public class DriveTimeBandManagementIT {

  @Test
  @SneakyThrows
  public void canUpdateRecords() {
    List<PssgDriveTimeBand> bands =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResource("/pssg-drive-time-bands-0.json"),
                new TypeReference<List<PssgDriveTimeBand>>() {});
    PssgDriveTimeBand favorite = bands.get(0);
    favorite.attributes().stationNumber("tmp" + Instant.now().getEpochSecond());
    String name =
        favorite.attributes().stationNumber()
            + "-"
            + favorite.attributes().fromBreak()
            + "-"
            + favorite.attributes().toBreak();
    TestClients.facilitiesManagement().get("internal/management/bands/{name}", name).expect(404);
    TestClients.facilitiesManagement().post("internal/management/bands", bands).expect(200);
    var actual =
        TestClients.facilitiesManagement()
            .get("internal/management/bands/{name}", name)
            .expect(200)
            .expectValid(Map.class);
    assertThat(actual.get("stationNumber")).isEqualTo(favorite.attributes().stationNumber());
    var allIds =
        TestClients.facilitiesManagement()
            .get("internal/management/bands")
            .expect(200)
            .expectListOf(String.class);
    assertThat(allIds).contains(name);
  }
}
