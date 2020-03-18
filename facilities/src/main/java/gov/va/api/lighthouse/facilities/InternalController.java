package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(
    value = {"/internal"},
    produces = {"application/json"})
@Loggable
@NoArgsConstructor
public class InternalController {

  @GetMapping(value = {"/management"})
  public NoOpResponse internal() {
    return NoOpResponse.builder().message("Internal endpoint").build();
  }
}
