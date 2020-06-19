package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.makeRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import org.junit.jupiter.api.Test;

public class NearbyIT {
  @Test
  void searchByAddress() {
    final String streetAddress = systemDefinition().facilitiesIds().streetAddress();
    final String city = systemDefinition().facilitiesIds().city();
    final String state = systemDefinition().facilitiesIds().state();
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request =
        "v0/nearby?street_address="
            + streetAddress
            + "&city="
            + city
            + "&state="
            + state
            + "&zip="
            + zip;
    makeRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  void searchByAddressWithDriveTime() {
    final String streetAddress = systemDefinition().facilitiesIds().streetAddress();
    final String city = systemDefinition().facilitiesIds().city();
    final String state = systemDefinition().facilitiesIds().state();
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request =
        "v0/nearby?street_address="
            + streetAddress
            + "&city="
            + city
            + "&state="
            + state
            + "&zip="
            + zip
            + "&drive_time=90";
    makeRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  void searchByLatLong() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude;
    makeRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  void searchByLatLongWithDriveTime() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude + "&drive_time=90";
    makeRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }
}
