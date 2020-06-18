package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CdwControllerTest {
  @Autowired JdbcTemplate template;

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
            + "Extension VARCHAR"
            + ")");

    assertThat(new CdwController(template).mentalHealthContacts()).isEmpty();
  }

  @Test
  public void stopCodes() {
    template.execute(
        "CREATE TABLE App.VHA_Stop_Code_Wait_Times ("
            + "Sta6a VARCHAR,"
            + "PrimaryStopCode VARCHAR,"
            + "PrimaryStopCodeName VARCHAR,"
            + "AvgWaitTimeNew VARCHAR"
            + ")");

    template.execute(
        "INSERT INTO App.VHA_Stop_Code_Wait_Times ("
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

    template.execute(
        "CREATE ALIAS App.VHA_Stop_Code_Wait_Times_Paginated FOR"
            + " \"gov.va.api.lighthouse.facilitiescollector.CdwControllerTest.stopCodeWaitTimesPaginated\"");

    assertThat(new CdwController(template).stopCodes())
        .isEqualTo(
            List.of(
                ImmutableMap.of(
                    "STA6A",
                    "402GA",
                    "PRIMARYSTOPCODE",
                    "123",
                    "PRIMARYSTOPCODENAME",
                    "PRIMARY CARE/MEDICINE",
                    "AVGWAITTIMENEW",
                    "14.15")));
  }
}
