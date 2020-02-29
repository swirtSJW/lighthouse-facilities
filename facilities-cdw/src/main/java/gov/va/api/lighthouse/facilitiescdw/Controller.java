package gov.va.api.lighthouse.facilitiescdw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = {"/", "/api"})
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class Controller {
  private final JdbcTemplate jdbc;

  private static <T> T checkNotNull(T maybe) {
    if (maybe == null) {
      throw new IllegalStateException("Expected non-null value");
    }
    return maybe;
  }

  @RequestMapping(
      value = "/mental-health-contact",
      produces = {
        "text/plain" // "application/json",
      },
      method = RequestMethod.GET)
  public ResponseEntity<String> mentalHealth() {
    return ResponseEntity.ok().body(mentalHealthContactInfo());
  }

  @SneakyThrows
  private String mentalHealthContactInfo() {
    List<String> results = new ArrayList<>();
    try (Connection connection = checkNotNull(jdbc.getDataSource()).getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement("SELECT * FROM App.VHA_Mental_Health_Contact_Info")) {
        // statement.setString(1, icn);
        try (ResultSet resultSet = statement.executeQuery()) {
          ResultSetMetaData rsmd = resultSet.getMetaData();
          int columnCount = rsmd.getColumnCount();
          while (resultSet.next()) {
            List<String> values = new ArrayList<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
              values.add(rsmd.getColumnName(i) + ":" + resultSet.getString(i));
            }
            results.add(values.stream().collect(Collectors.joining(" ")));
          }
        }
      }
      return "SELECT * FROM App.VHA_Mental_Health_Contact_Info\n"
          + results.stream().collect(Collectors.joining("\n"));
    }
  }

  /** Query stop code wait times. */
  @SneakyThrows
  public String stopCodeWaitTimes() {
    List<String> results = new ArrayList<>();
    try (Connection connection = checkNotNull(jdbc.getDataSource()).getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              "SELECT * FROM App.VHA_Stop_Code_Wait_Times_Paginated(1, 100)")) {
        // statement.setString(1, icn);
        try (ResultSet resultSet = statement.executeQuery()) {
          ResultSetMetaData rsmd = resultSet.getMetaData();
          int columnCount = rsmd.getColumnCount();
          while (resultSet.next()) {
            List<String> values = new ArrayList<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
              values.add(rsmd.getColumnName(i) + ":" + resultSet.getString(i));
            }
            results.add(values.stream().collect(Collectors.joining(" ")));
          }
        }
      }
      return "SELECT * FROM App.VHA_Stop_Code_Wait_Times_Paginated(1, 100)\n"
          + results.stream().collect(Collectors.joining("\n"));
    }
  }

  @RequestMapping(
      value = "/stop-code",
      produces = {
        "text/plain" // "application/json",
      },
      method = RequestMethod.GET)
  public ResponseEntity<String> stopCodes() {
    return ResponseEntity.ok().body(stopCodeWaitTimes());
  }
}
// "{? = call getDob(?)}"
// SELECT * FROM App.VHA_Stop_Code_Wait_Times_Paginated(1, 100)
// OIT_Lighthouse2.App.VHA_Mental_Health_Contact_Info
// return "{call [" + schema + "].[" + storedProcedure + "](?,?,?,?,?,?,?)}";
// private final String schema;
// private final String storedProcedure;
// public SqlResourceRepository(
// JdbcTemplate jdbc
// //  @Value("${cdw.schema:App}") String schema,
// //  @Value("${cdw.stored-procedure:prc_Entity_Return}") String storedProcedure
// ) {
// this.jdbc = jdbc;
// // this.schema = Checks.argumentMatches(schema, "[A-Za-z0-9_]+");
// //  this.storedProcedure = storedProcedure;
// }
// try (CallableStatement cs =
// connection.prepareCall("{call [app].[VHA_Stop_Code_Wait_Times_Paginated](?,?)}"))
// {
// cs.closeOnCompletion();
// cs.setObject(1, 1, Types.TINYINT);
// cs.setObject(2, 100, Types.TINYINT);
// // cs.registerOutParameter(Index.RESPONSE_XML, Types.);
// int result = cs.executeUpdate();
// System.out.println("result: " + result);
// // Clob clob = (Clob) cs.getObject(Index.RESPONSE_XML);
// // return clob.getSubString(1, (int) clob.length());
// }
