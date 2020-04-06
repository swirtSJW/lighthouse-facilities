package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;

import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Slf4j
@Builder
public class CmsOverlayController {

  @PostMapping(
      value = "/v0/facilities/{id}/cms-overlay",
      produces = "application/json",
      consumes = "application/json")
  public void saveOverlay(@PathVariable("id") String id, @RequestBody CmsOverlay overlay) {
    log.info("{}, kthxbye. {}", sanitize(id), sanitize(overlay.toString()));
  }
}
