package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Loggable
@Validated
@RestController
@RequestMapping(value = "/")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class CdwController {
  private final JdbcTemplate jdbc;

  private static <T> T notNull(T maybe) {
    if (maybe == null) {
      throw new IllegalStateException("Expected non-null value");
    }
    return maybe;
  }

  @SneakyThrows
  private List<Map<String, String>> allResults(String sql) {
    try (Connection connection = notNull(jdbc.getDataSource()).getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
      List<Map<String, String>> results = new ArrayList<>();
      ResultSetMetaData rsmd = resultSet.getMetaData();
      int columnCount = rsmd.getColumnCount();
      while (resultSet.next()) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 1; i <= columnCount; i++) {
          map.put(rsmd.getColumnName(i), resultSet.getString(i));
        }
        results.add(map);
      }
      return results;
    }
  }

  /** Mental-health contacts for debugging. */
  @RequestMapping(
      value = "/mental-health-contact",
      produces = "application/json",
      method = RequestMethod.GET)
  public List<Map<String, String>> mentalHealthContacts() {
    try {
      return allResults("SELECT * FROM App.VHA_Mental_Health_Contact_Info");
    } catch (Exception ex) {
      throw new CdwException(ex);
    }
  }

  /** Stop codes for debugging. */
  @SneakyThrows
  @RequestMapping(value = "/stop-code", produces = "application/json", method = RequestMethod.GET)
  public List<Map<String, String>> stopCodes() {
    try {
      return allResults("SELECT * FROM App.VHA_Stop_Code_Wait_Times_Paginated(1, 999999)");
    } catch (Exception ex) {
      throw new CdwException(ex);
    }
  }

  static final class CdwException extends RuntimeException {
    public CdwException(Throwable cause) {
      super(cause);
    }
  }
}
