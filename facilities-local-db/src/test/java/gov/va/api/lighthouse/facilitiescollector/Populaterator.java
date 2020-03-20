package gov.va.api.lighthouse.facilitiescollector;

import com.google.common.collect.Streams;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public final class Populaterator {
  @SneakyThrows
  public static Connection createAppSchema(
      String host, String port, String user, String password, String collectorDatabase) {
    String url =
        String.format(
            "jdbc:sqlserver://%s:%s;user=%S;password=%s;database=%s",
            host, port, user, password, collectorDatabase);
    Connection connection = DriverManager.getConnection(url);
    System.out.println("Creating 'app' schema");
    connection.prepareStatement("CREATE SCHEMA APP").execute();
    return connection;
  }

  /** Populate local database. */
  @SneakyThrows
  public static void main(String[] args) {
    String host = "localhost";
    String port = "1533";
    String user = "SA";
    String password = "<YourStrong!Passw0rd>";
    String collectorDatabase = "fc";
    String facilityDatabase = "facility";
    System.out.println(
        "Creating facility database '"
            + facilityDatabase
            + "' and facility collector database '"
            + collectorDatabase
            + "'");
    String bootstrapUrl =
        String.format("jdbc:sqlserver://%s:%s;user=%S;password=%s", host, port, user, password);
    Connection bootstrap = DriverManager.getConnection(bootstrapUrl);
    bootstrap.prepareStatement("CREATE DATABASE " + collectorDatabase).execute();
    bootstrap.prepareStatement("CREATE DATABASE " + facilityDatabase).execute();
    System.out.println("Initializing facility collector database.");
    Connection connection = createAppSchema(host, port, user, password, collectorDatabase);
    mentalHealthContacts(connection);
    stopCodes(connection);
    System.out.println("Good bye.");
    System.exit(0);
  }

  @SneakyThrows
  private static void mentalHealthContacts(Connection connection) {
    System.out.println("Populating mental health contacts");
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
    try (InputStreamReader reader =
        new FileReader(
            new File("src/test/resources/mental-health-contact.csv"), StandardCharsets.UTF_8)) {
      Iterable<CSVRecord> rows = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
      for (CSVRecord row : rows) {
        try (PreparedStatement statement =
            connection.prepareStatement(
                "INSERT INTO App.VHA_Mental_Health_Contact_Info_Source ("
                    + "ID,"
                    + "Region,"
                    + "VISN,"
                    + "AdminParent,"
                    + "StationNumber,"
                    + "MHClinicPhone,"
                    + "MHPhone,"
                    + "Extension,"
                    + "OfficialStationName,"
                    + "\"POC Email\","
                    + "Status,"
                    + "Modified,"
                    + "Created,"
                    + "AddedToOutbox"
                    + ") VALUES ("
                    + IntStream.range(0, 14).mapToObj(v -> "?").collect(Collectors.joining(","))
                    + ")")) {
          int paramIndex = 1;
          for (Object val : row) {
            statement.setObject(
                paramIndex, String.valueOf(val).equalsIgnoreCase("null") ? null : val);
            paramIndex++;
          }
          statement.execute();
        }
      }
    }
    connection
        .prepareStatement(
            "CREATE VIEW App.VHA_Mental_Health_Contact_Info AS"
                + " SELECT * FROM App.VHA_Mental_Health_Contact_Info_Source")
        .execute();
  }

  @SneakyThrows
  private static void stopCodes(Connection connection) {
    System.out.println("Populating codes");
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
    try (InputStreamReader reader =
        new FileReader(
            new File("src/test/resources/stop-code-wait-times.csv"), StandardCharsets.UTF_8)) {
      Iterable<CSVRecord> rows = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
      Streams.stream(rows)
          .parallel()
          .forEach(
              row -> {
                try (PreparedStatement statement =
                    connection.prepareStatement(
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
                            + IntStream.range(0, 8)
                                .mapToObj(v -> "?")
                                .collect(Collectors.joining(","))
                            + ")")) {
                  int paramIndex = 1;
                  for (Object val : row) {
                    statement.setObject(paramIndex, val);
                    paramIndex++;
                  }
                  statement.execute();
                } catch (Throwable tr) {
                  throw new RuntimeException(tr);
                }
              });
    }
    connection
        .prepareStatement(
            "CREATE FUNCTION App.VHA_Stop_Code_Wait_Times_Paginated (@page int, @count int)"
                + " RETURNS TABLE AS RETURN ( SELECT * from App.VHA_Stop_Code_Wait_Times )")
        .execute();
  }
}
