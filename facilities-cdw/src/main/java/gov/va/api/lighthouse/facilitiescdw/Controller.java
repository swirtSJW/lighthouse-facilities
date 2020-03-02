package gov.va.api.lighthouse.facilitiescdw;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class Controller {
  private final EntityManager entityManager;

  private final MentalHealthContactRepository mentalHealthContactRepository;

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
    List<StopCodeEntity> entities =
        entityManager.createNamedQuery("allStopCodes", StopCodeEntity.class).getResultList();

    for (StopCodeEntity e : entities) {
      log.info(e.toString());
    }

    List<StopCodeResponse.StopCode> stopCodes = new ArrayList<>();
    for (StopCodeEntity entity : entities) {
      StopCodeEntity.StopCodeRow row = entity.row();
      if (row == null) {
        continue;
      }
      stopCodes.add(
          StopCodeResponse.StopCode.builder()
              .divisionFcdmd(row.divisionFcdmd())
              .cocClassification(row.cocClassification())
              .sta6a(row.sta6a())
              .primaryStopCode(row.primaryStopCode())
              .primaryStopCodeName(row.primaryStopCodeName())
              .numberOfAppointmentsLinkedToConsult(row.numberOfAppointmentsLinkedToConsult())
              .numberOfLocations(row.numberOfLocations())
              .avgWaitTimeNew(row.avgWaitTimeNew())
              .build());
    }
    return StopCodeResponse.builder().stopCodes(stopCodes).build();
  }
}
