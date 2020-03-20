package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class CdwControllerTest {
  @Autowired private JdbcTemplate template;

  @SneakyThrows
  @SuppressWarnings("unused")
  public static ResultSet stopCodeWaitTimesPaginated(Connection conn, int page, int count) {
    return conn.prepareStatement("SELECT * FROM APP.VHA_Stop_Code_Wait_Times").executeQuery();
  }

  @Test
  public void mentalHealthContacts() {
    template.execute(
        "CREATE TABLE App.VHA_Mental_Health_Contact_Info ("
            + "StationNumber VARCHAR,"
            + "MHPhone VARCHAR,"
            + "Extension FLOAT"
            + ")");

    template.execute(
        "INSERT INTO App.VHA_Mental_Health_Contact_Info ("
            + "StationNumber,"
            + "MHPhone,"
            + "Extension"
            + ") VALUES ("
            + "'503GA',"
            + "'867-5309',"
            + "5555"
            + ")");

    assertThat(new CdwController(template).mentalHealthContacts())
        .isEqualTo(
            List.of(
                CdwController.MentalHealthContact.builder()
                    .stationNumber("503GA")
                    .mhPhone("867-5309")
                    .extension("5555.0")
                    .build()));
  }

  @Test
  public void stopCodes() {
    template.execute(
        "CREATE TABLE App.VHA_Stop_Code_Wait_Times ("
            + "DIVISION_FCDMD VARCHAR,"
            + "CocClassification VARCHAR,"
            + "Sta6a VARCHAR,"
            + "PrimaryStopCode VARCHAR,"
            + "PrimaryStopCodeName VARCHAR,"
            + "NumberOfAppointmentsLinkedToConsult VARCHAR,"
            + "NumberOfLocations VARCHAR,"
            + "AvgWaitTimeNew VARCHAR"
            + ")");

    template.execute(
        "INSERT INTO App.VHA_Stop_Code_Wait_Times ("
            + "DIVISION_FCDMD,"
            + "CocClassification,"
            + "Sta6a,"
            + "PrimaryStopCode,"
            + "PrimaryStopCodeName,"
            + "NumberOfAppointmentsLinkedToConsult,"
            + "NumberOfLocations,"
            + "AvgWaitTimeNew"
            + ") VALUES ("
            + "'(503GA) Melbourne, FL',"
            + "'Primary Care CBOC',"
            + "'402GA',"
            + "'123',"
            + "'PRIMARY CARE/MEDICINE',"
            + "'99',"
            + "'3',"
            + "'14.15'"
            + ")");

    template.execute(
        "CREATE ALIAS App.VHA_Stop_Code_Wait_Times_Paginated FOR"
            + " \"gov.va.api.lighthouse.facilitiescollector.CdwControllerTest.stopCodeWaitTimesPaginated\"");

    assertThat(new CdwController(template).stopCodes())
        .isEqualTo(
            List.of(
                CdwController.StopCode.builder()
                    .divisionFcdmd("(503GA) Melbourne, FL")
                    .cocClassification("Primary Care CBOC")
                    .sta6a("402GA")
                    .primaryStopCode("123")
                    .primaryStopCodeName("PRIMARY CARE/MEDICINE")
                    .numberOfAppointmentsLinkedToConsult("99")
                    .numberOfLocations("3")
                    .avgWaitTimeNew("14.15")
                    .build()));
  }
}
