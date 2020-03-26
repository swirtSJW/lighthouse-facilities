package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.sentinel.ServiceDefinition;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class SystemDefinition {
  @NotNull FacilitiesIds facilitiesIds;
  @NotNull ServiceDefinition facilities;
  @NotNull ServiceDefinition collector;
}
