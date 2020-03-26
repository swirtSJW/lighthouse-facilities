package gov.va.api.lighthouse.facilitiescollector;

import com.google.common.collect.Streams;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

@Value
@AllArgsConstructor(staticName = "of")
public final class Populaterator {
  Db db;

  private static String baseDir() {
    return System.getProperty("basedir", ".");
  }

  /** Populate local database. */
  @SneakyThrows
  public static void main(String[] args) {
    if (Boolean.parseBoolean(System.getProperty("populator.h2", "true"))) {
      Populaterator.of(H2.builder().dbFile(baseDir() + "/target/mock-cdw").build()).populate();
    }
    if (Boolean.parseBoolean(System.getProperty("populator.sqlserver", "false"))) {
      Populaterator.of(
              SqlServer.builder()
                  .host("localhost")
                  .port("1533")
                  .user("SA")
                  .password("<YourStrong!Passw0rd>")
                  .collectorDatabase("fc")
                  .facilityDatabase("facility")
                  .build())
          .populate();
    }
  }

  /**
   * Bind to H2 by executing the following SQL.
   *
   * <pre>
   * CREATE ALIAS App.VHA_Stop_Code_Wait_Times_Paginated FOR
   *    gov.va.api.lighthouse.facilitiescollector.Populaterator#stopCodeWaitTimesPaginated
   * </pre>
   */
  @SneakyThrows
  @SuppressWarnings("unused")
  public static ResultSet stopCodeWaitTimesPaginated(Connection conn, int page, int count) {
    return conn.prepareStatement("SELECT * FROM APP.VHA_Stop_Code_Wait_Times").executeQuery();
  }

  @SneakyThrows
  private void bootstrap() {
    var conn = db.bootstrapConnection();
    if (conn.isEmpty()) {
      return;
    }
    for (var n : db.bootstrapDatabases()) {
      log("Creating database " + n);
      conn.get().prepareStatement("DROP DATABASE IF EXISTS " + n).execute();
      conn.get().prepareStatement("CREATE DATABASE " + n).execute();
    }
    conn.get().commit();
    conn.get().close();
  }

  @SneakyThrows
  private Connection createAppSchema(Connection connection) {
    log("Creating 'app' schema");
    connection.prepareStatement("CREATE SCHEMA APP").execute();
    return connection;
  }

  private void log(String msg) {
    System.out.println(db.name() + ": " + msg);
  }

  @SneakyThrows
  private void mentalHealthContacts(Connection connection) {
    log("Populating mental health contacts");
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
            new File(baseDir() + "/src/test/resources/mental-health-contact.csv"),
            StandardCharsets.UTF_8)) {
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

  /** Populate the database. */
  @SneakyThrows
  public void populate() {
    log("Populating " + db.name() + " database");
    bootstrap();
    log("Initializing facility collector database.");
    var connection = db.connection();
    createAppSchema(connection);
    mentalHealthContacts(connection);
    stopCodes(connection);
    connection.commit();
    connection.close();
    log("Good bye.");
  }

  @SneakyThrows
  private void stopCodes(Connection connection) {
    log("Populating codes");
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
            new File(baseDir() + "/src/test/resources/stop-code-wait-times.csv"),
            StandardCharsets.UTF_8)) {
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
    if (db.supportFunctions()) {
      connection
          .prepareStatement(
              "CREATE FUNCTION App.VHA_Stop_Code_Wait_Times_Paginated (@page int, @count int)"
                  + " RETURNS TABLE AS RETURN ( SELECT * from App.VHA_Stop_Code_Wait_Times )")
          .execute();
    }
  }

  /** Defines DB in terms the Populator will need. */
  public interface Db {
    /** If a bootstrap connection is provided, databases will be (re)created. */
    Optional<Connection> bootstrapConnection();

    /** The list of databases to create. */
    default List<String> bootstrapDatabases() {
      return List.of();
    }

    /** A connection to the database. */
    Connection connection();

    /** The name of this db configuration used for logging. */
    default String name() {
      return getClass().getSimpleName();
    }

    /** If this instance supports the use of CREATE FUNCTION. */
    boolean supportFunctions();
  }

  /** A definition for H2 instance. */
  @Value
  @Builder
  public static class H2 implements Db {
    String dbFile;

    @Override
    public Optional<Connection> bootstrapConnection() {
      return Optional.empty();
    }

    @Override
    @SneakyThrows
    public Connection connection() {
      new File(dbFile + ".mv.db").delete();
      new File(dbFile + ".trace.db").delete();
      return DriverManager.getConnection("jdbc:h2:" + dbFile, "sa", "sa");
    }

    @Override
    public boolean supportFunctions() {
      return false;
    }
  }

  /** A defintion for SqlServer instance. */
  @Value
  @Builder
  public static class SqlServer implements Db {
    String host;

    String port;

    String user;

    String password;

    String collectorDatabase;

    String facilityDatabase;

    @Override
    @SneakyThrows
    public Optional<Connection> bootstrapConnection() {
      String bootstrapUrl =
          String.format("jdbc:sqlserver://%s:%s;user=%S;password=%s", host, port, user, password);
      return Optional.of(DriverManager.getConnection(bootstrapUrl));
    }

    @Override
    public List<String> bootstrapDatabases() {
      return List.of(collectorDatabase, facilityDatabase);
    }

    @Override
    @SneakyThrows
    public Connection connection() {
      return DriverManager.getConnection(
          String.format(
              "jdbc:sqlserver://%s:%s;user=%S;password=%s;database=%s",
              host, port, user, password, collectorDatabase));
    }

    @Override
    public boolean supportFunctions() {
      return true;
    }
  }
}
