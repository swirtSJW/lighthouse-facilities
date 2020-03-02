package gov.va.api.lighthouse.facilitiescdw;

import gov.va.api.lighthouse.facilitiescdw.StopCodeResponse.StopCode;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class Controller {
  private final MentalHealthContactRepository mentalHealthContactRepository;

  private final JdbcTemplate jdbc;

  /** Retrieve mental-health contacts. */
  @RequestMapping(
      value = "/mental-health-contact",
      produces = {
        "application/json",
      },
      method = RequestMethod.GET)
  public MentalHealthContactResponse mentalHealthContacts() {
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

  /** Retrieve stop codes. */
  @RequestMapping(
      value = "/stop-code",
      produces = {
        "application/json",
      },
      method = RequestMethod.GET)
  public StopCodeResponse stopCodes() {
    List<StopCode> stopCodes =
        jdbc.query(
            "SELECT * FROM App.VHA_Stop_Code_Wait_Times_Paginated(1, 100)",
            (RowMapper<StopCodeResponse.StopCode>)
                (resultSet, rowNum) ->
                    StopCodeResponse.StopCode.builder()
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
    return StopCodeResponse.builder().stopCodes(stopCodes).build();
  }
}
