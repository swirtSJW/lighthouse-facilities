package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.sentinel.ServiceDefinition;
import io.restassured.http.Header;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
class SystemDefinition {
  @NonNull FacilitiesIds facilitiesIds;
  @NonNull ServiceDefinition facilities;
  @NonNull ServiceDefinition facilitiesManagement;
  @NonNull ServiceDefinition collector;
  @NonNull String clientkey;

  public Header clientkeyAsHeader() {
    return new Header("client-key", clientkey());
  }
}
