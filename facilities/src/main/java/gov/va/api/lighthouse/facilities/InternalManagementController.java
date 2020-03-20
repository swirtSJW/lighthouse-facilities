package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(
    value = {"/internal/management"},
    produces = {"application/json"})
@Loggable
@NoArgsConstructor
@Builder
public class InternalManagementController {

  @GetMapping(value = {""})
  @ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
  public void collect() {
    // TODO go get the data
  }
}
