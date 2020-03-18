package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = {"/"})
@Loggable
@NoArgsConstructor
public class FacilitiesController {

  @GetMapping(value = {"/facilities"})
  public NoOpResponse facilities() {
    return NoOpResponse.builder().message("Facilities endpoint").build();
  }
}
