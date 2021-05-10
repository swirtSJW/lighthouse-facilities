package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;

import gov.va.api.lighthouse.facilities.api.telehealth.TelehealthBody;
import gov.va.api.lighthouse.facilities.api.telehealth.TelehealthResponse;
import java.util.Optional;
import javax.validation.Valid;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/v0")
public class TelehealthController {

  // todo needs facility or telehealth repo for full implementation
  private final FacilityRepository repository;

  @Builder
  TelehealthController(@Autowired FacilityRepository facilityRepository) {
    this.repository = facilityRepository;
  }

  @GetMapping(
      value = "/telehealth",
      produces = {"application/json"})
  TelehealthResponse telehealthById(@RequestParam(value = "id") String id) {
    // todo search for id via telehealth entity
    // todo remove stub
    return TelehealthResponse.builder().stub(id + "stub").build();
  }

  @PostMapping(
      value = "/telehealth/{id}/update",
      produces = "application/json",
      consumes = "application/json")
  ResponseEntity<Void> updateTelehealth(
      @PathVariable("id") String id, @Valid @RequestBody TelehealthBody telehealthBody) {

    Optional<FacilityEntity> existingEntity =
        repository.findById(FacilityEntity.Pk.fromIdString(id));
    if (existingEntity.isEmpty()) {
      log.info("Received Unknown Facility ID ({}) for Telehealth update.", sanitize(id));
      return ResponseEntity.accepted().build();
    }

    // Todo new telehealth entity needed
    System.out.println(id + telehealthBody.stub());
    return ResponseEntity.ok().build();
  }
}
