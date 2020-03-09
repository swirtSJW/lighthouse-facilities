package gov.va.api.lighthouse.facilitiescdw;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public final class Populaterator {
  @SneakyThrows
  private static PreparedStatement insertionStatement(
      Connection connection, File file, String table) {
    Map<String, String> entries =
        new ObjectMapper().readValue(file, new TypeReference<Map<String, String>>() {});
    List<String> keys = new ArrayList<>(entries.size());
    List<String> values = new ArrayList<>(entries.size());
    for (Map.Entry<String, String> entry : entries.entrySet()) {
      keys.add(entry.getKey());
      values.add(entry.getValue());
    }

    PreparedStatement statement =
        connection.prepareStatement(
            "INSERT INTO "
                + table
                + " ("
                + keys.stream().map(k -> "\"" + k + "\"").collect(Collectors.joining(","))
                + ") VALUES ("
                + values.stream().map(v -> "?").collect(Collectors.joining(","))
                + ")");

    int paramIndex = 1;
    for (String val : values) {
      statement.setString(paramIndex, val);
      paramIndex++;
    }

    return statement;
  }

  /** Populate local database. */
  @SneakyThrows
  public static void main(String[] args) {
    String host = "localhost";
    String port = "1533";
    String user = "SA";
    String password = "<YourStrong!Passw0rd>";
    String database = "fc";

    String bootstrapUrl =
        String.format("jdbc:sqlserver://%s:%s;user=%S;password=%s", host, port, user, password);
    DriverManager.getConnection(bootstrapUrl)
        .prepareStatement("CREATE DATABASE " + database)
        .execute();

    String url =
        String.format(
            "jdbc:sqlserver://%s:%s;user=%S;password=%s;database=%s",
            host, port, user, password, database);
    Connection connection = DriverManager.getConnection(url);
    connection.prepareStatement("CREATE SCHEMA APP").execute();
    mentalHealthContacts(connection);
    stopCodes(connection);
  }

  @SneakyThrows
  private static void mentalHealthContacts(Connection connection) {
    connection
        .prepareStatement(
            "CREATE TABLE App.VHA_Mental_Health_Contact_Info_Source ("
                + "ID VARCHAR(MAX),"
                + "Region FLOAT,"
                + "VISN VARCHAR(MAX),"
                + "AdminParent FLOAT,"
                + "StationNumber VARCHAR(MAX),"
                + "MHClinicPhone FLOAT,"
                + "MHPhone VARCHAR(MAX),"
                + "Extension FLOAT,"
                + "OfficialStationName VARCHAR(MAX),"
                + "\"POC Email\" VARCHAR(MAX),"
                + "Status VARCHAR(MAX),"
                + "Modified VARCHAR(MAX),"
                + "Created VARCHAR(MAX),"
                + "AddedToOutbox VARCHAR(MAX)"
                + ")")
        .execute();

    for (File f : new File("src/test/resources/data/mental-health-contact").listFiles()) {
      insertionStatement(connection, f, "App.VHA_Mental_Health_Contact_Info_Source").execute();
    }

    connection
        .prepareStatement(
            "CREATE VIEW App.VHA_Mental_Health_Contact_Info AS"
                + " SELECT * FROM App.VHA_Mental_Health_Contact_Info_Source")
        .execute();
  }

  @SneakyThrows
  private static void stopCodes(Connection connection) {
    connection
        .prepareStatement(
            "CREATE TABLE App.VHA_Stop_Code_Wait_Times ("
                + "DIVISION_FCDMD VARCHAR(MAX),"
                + "CocClassification VARCHAR(MAX),"
                + "Sta6a VARCHAR(MAX),"
                + "PrimaryStopCode VARCHAR(MAX),"
                + "PrimaryStopCodeName VARCHAR(MAX),"
                + "NumberOfAppointmentsLinkedToConsult VARCHAR(MAX),"
                + "NumberOfLocations VARCHAR(MAX),"
                + "AvgWaitTimeNew VARCHAR(MAX)"
                + ")")
        .execute();

    for (File f : new File("src/test/resources/data/stop-code").listFiles()) {
      insertionStatement(connection, f, "App.VHA_Stop_Code_Wait_Times").execute();
    }

    connection
        .prepareStatement(
            "CREATE FUNCTION App.VHA_Stop_Code_Wait_Times_Paginated (@page int, @count int)"
                + " RETURNS TABLE AS RETURN ( SELECT * from App.VHA_Stop_Code_Wait_Times )")
        .execute();
  }
}
