package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(
    value = {"/nearby"},
    produces = {"application/json"})
@Loggable
@NoArgsConstructor
public class NearbyController {

  @GetMapping(params = {"lat", "lng"})
  public NoOpResponse nearby(
      @RequestParam(name = "lat", required = true) String lat,
      @RequestParam(name = "lng", required = true) String lng) {
    return NoOpResponse.builder().message("Nearby endpoint").build();
  }
}
