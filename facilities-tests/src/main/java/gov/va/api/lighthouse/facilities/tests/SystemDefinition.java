package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.sentinel.ServiceDefinition;
import io.restassured.http.Header;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class SystemDefinition {
  @NotNull FacilitiesIds facilitiesIds;
  @NotNull ServiceDefinition facilities;
  @NotNull ServiceDefinition facilitiesManagement;
  @NotNull ServiceDefinition collector;
  @NotNull String clientkey;

  public Header clientkeyAsHeader() {
    return new Header("client-key", clientkey());
  }
}
