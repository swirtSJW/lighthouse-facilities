package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

  /** Mental-health contacts for debugging. */
  @RequestMapping(
      value = "/mental-health-contact",
      produces = "application/json",
      method = RequestMethod.GET)
  public List<MentalHealthContact> mentalHealthContacts() {
    return jdbc.query(
        "SELECT StationNumber, MHPhone, Extension FROM App.VHA_Mental_Health_Contact_Info",
        (RowMapper<MentalHealthContact>)
            (resultSet, rowNum) ->
                MentalHealthContact.builder()
                    .stationNumber(resultSet.getString("StationNumber"))
                    .mhPhone(resultSet.getString("MHPhone"))
                    .extension(resultSet.getString("Extension"))
                    .build());
  }

  /** Stop codes for debugging. */
  @RequestMapping(
      value = "/stop-code",
      produces = "application/json",
      method = RequestMethod.GET)
  public List<StopCode> stopCodes() {
    return jdbc.query(
        "SELECT DIVISION_FCDMD, CocClassification, Sta6a, PrimaryStopCode, PrimaryStopCodeName,"
            + " NumberOfAppointmentsLinkedToConsult, NumberOfLocations, AvgWaitTimeNew"
            + " FROM App.VHA_Stop_Code_Wait_Times_Paginated(1, 99999)",
        (RowMapper<StopCode>)
            (resultSet, rowNum) ->
                StopCode.builder()
                    .divisionFcdmd(resultSet.getString("DIVISION_FCDMD"))
                    .cocClassification(resultSet.getString("CocClassification"))
                    .sta6a(resultSet.getString("Sta6a"))
                    .primaryStopCode(resultSet.getString("PrimaryStopCode"))
                    .primaryStopCodeName(resultSet.getString("PrimaryStopCodeName"))
                    .numberOfAppointmentsLinkedToConsult(
                        resultSet.getString("NumberOfAppointmentsLinkedToConsult"))
                    .numberOfLocations(resultSet.getString("NumberOfLocations"))
                    .avgWaitTimeNew(resultSet.getString("AvgWaitTimeNew"))
                    .build());
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class MentalHealthContact {
    String stationNumber;

    String mhPhone;

    String extension;
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class StopCode {
    String divisionFcdmd;

    String cocClassification;

    String sta6a;

    String primaryStopCode;

    String primaryStopCodeName;

    String numberOfAppointmentsLinkedToConsult;

    String numberOfLocations;

    String avgWaitTimeNew;
  }
}
