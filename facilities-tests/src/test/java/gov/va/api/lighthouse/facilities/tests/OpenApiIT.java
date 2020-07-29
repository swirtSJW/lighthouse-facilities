package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;

import org.junit.jupiter.api.Test;

public class OpenApiIT {
  @Test
  void openApiJson() {
    facilitiesRequest(null, "v0/facilities/openapi.json", 200);
    facilitiesRequest(null, "docs/v0/api", 200);
  }
}
