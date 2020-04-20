package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.FacilityEntity.Pk;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import java.util.Optional;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Builder
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayController {
  private final FacilityRepository repository;

  @InitBinder
  void initDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  /** Saves CMS overlay data for known stations. */
  @PostMapping(
      value = "/v0/facilities/{id}/cms-overlay",
      produces = "application/json",
      consumes = "application/json")
  @SneakyThrows
  public void saveOverlay(@PathVariable("id") String id, @Valid @RequestBody CmsOverlay overlay) {
    Optional<FacilityEntity> existingEntity = repository.findById(Pk.fromIdString(id));
    if (existingEntity.isEmpty()) {
      throw new ExceptionsV0.NotFound(id);
    }
    existingEntity
        .get()
        .cmsOverlay(FacilitiesJacksonConfig.createMapper().writeValueAsString(overlay));
    repository.save(existingEntity.get());
  }
}
