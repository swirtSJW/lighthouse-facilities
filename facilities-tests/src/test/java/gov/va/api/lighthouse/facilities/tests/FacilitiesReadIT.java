package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.makeRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RequiresFacilitiesExtension.class)
public class FacilitiesReadIT {
  private static String readPath() {
    return "v0/facilities/" + systemDefinition().facilitiesIds().facility();
  }

  @Test
  void readById_geoJson() {
    makeRequest("application/geo+json", readPath(), 200).expectValid(GeoFacilityReadResponse.class);
  }

  @Test
  void readById_json() {
    makeRequest("application/json", readPath(), 200).expectValid(FacilityReadResponse.class);
  }

  @Test
  void readById_noAccept() {
    // default to application/json
    makeRequest(null, readPath(), 200).expectValid(FacilityReadResponse.class);
  }

  @Test
  void readById_vndGeoJson() {
    makeRequest("application/vnd.geo+json", readPath(), 200)
        .expectValid(GeoFacilityReadResponse.class);
  }
}
