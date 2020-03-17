package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mapping to collect all facility information and output it in application/json format for
 * easy parsing.
 */
@SuppressWarnings("WeakerAccess")
@Validated
@RestController
@RequestMapping(
    value = {"/collect"},
    produces = {"application/json"})
@NoArgsConstructor
public class CollectController {

  /** Request Mapping for the /collect endpoint. */
  @SneakyThrows
  @GetMapping(value = {"/facilities"})
  public CollectorFacilitiesResponse collectFacilities() {
    return JacksonConfig.createMapper()
        .readValue(
            getClass().getResourceAsStream("/facilities.json"), CollectorFacilitiesResponse.class);
  }
}
