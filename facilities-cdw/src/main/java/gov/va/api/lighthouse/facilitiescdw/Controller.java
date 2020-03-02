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

  private final MentalHealthContactRepository mentalHealthContactRepository;

  private static <T> T checkNotNull(T maybe) {
    if (maybe == null) {
      throw new IllegalStateException("Expected non-null value");
    }
    return maybe;
  }

  /** Retrieve mental-health contacts. */
  @RequestMapping(
      value = "/mental-health-contact",
      produces = {
        "application/json",
      },
      method = RequestMethod.GET)
  public MentalHealthContactResponse mentalHealth() {
    List<MentalHealthContactResponse.Contact> contacts = new ArrayList<>();
    for (MentalHealthContactEntity entity : mentalHealthContactRepository.findAll()) {
      contacts.add(
          MentalHealthContactResponse.Contact.builder()
              .id(entity.id())
              .region(entity.region())
              .visn(entity.visn())
              .adminParent(entity.adminParent())
              .stationNumber(entity.stationNumber())
              .mhClinicPhone(entity.mhClinicPhone())
              .mhPhone(entity.mhPhone())
              .extension(entity.extension())
              .officialStationName(entity.officialStationName())
              .pocEmail(entity.pocEmail())
              .status(entity.status())
              .modified(entity.modified())
              .created(entity.created())
              .addedToOutbox(entity.addedToOutbox())
              .build());
    }
    return MentalHealthContactResponse.builder().contacts(contacts).build();
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
              values.add("'" + rsmd.getColumnName(i) + "'" + ":" + resultSet.getString(i));
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
