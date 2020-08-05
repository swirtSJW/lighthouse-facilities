package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class InternalCollectorControllerTest {
  @Autowired JdbcTemplate template;

  @Autowired TestEntityManager testEntityManager;

  @Test
  void mentalHealthContacts() {
    template.execute(
        "CREATE TABLE App.VHA_Mental_Health_Contact_Info ("
            + "StationNumber VARCHAR,"
            + "MHPhone VARCHAR,"
            + "Extension VARCHAR"
            + ")");

    template.execute(
        "INSERT INTO App.VHA_Mental_Health_Contact_Info ("
            + "StationNumber,"
            + "MHPhone,"
            + "Extension"
            + ") VALUES ("
            + "'999',"
            + "'800-867-5309',"
            + "'1234'"
            + ")");

    assertThat(new InternalCollectorController(template).mentalHealthContacts())
        .isEqualTo(
            List.of(
                Map.of("STATIONNUMBER", "999", "MHPHONE", "800-867-5309", "EXTENSION", "1234")));
  }

  @Test
  @SneakyThrows
  void stopCodes() {
    template.execute(
        "CREATE TABLE App.VSSC_ClinicalServices ("
            + "Sta6a VARCHAR,"
            + "PrimaryStopCode VARCHAR,"
            + "PrimaryStopCodeName VARCHAR,"
            + "AvgWaitTimeNew VARCHAR"
            + ")");

    template.execute(
        "INSERT INTO App.VSSC_ClinicalServices ("
            + "Sta6a,"
            + "PrimaryStopCode,"
            + "PrimaryStopCodeName,"
            + "AvgWaitTimeNew"
            + ") VALUES ("
            + "'402GA',"
            + "'123',"
            + "'PRIMARY CARE/MEDICINE',"
            + "'14.15'"
            + ")");

    assertThat(new InternalCollectorController(template).stopCodes())
        .isEqualTo(
            List.of(
                Map.of(
                    "STA6A",
                    "402GA",
                    "PRIMARYSTOPCODE",
                    "123",
                    "PRIMARYSTOPCODENAME",
                    "PRIMARY CARE/MEDICINE",
                    "AVGWAITTIMENEW",
                    "14.15")));
  }

  @Test
  @SneakyThrows
  void vast() {
    testEntityManager.persistAndFlush(
        VastEntity.builder().vastId(1L).stationNumber("123").stationName("Some VAMC").build());
    assertThat(Iterables.getOnlyElement(new InternalCollectorController(template).vast()))
        .containsAllEntriesOf(Map.of("VAST_ID", "1", "STA_NO", "123", "STATION_NAME", "Some VAMC"));
  }
}
