package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.facilities.NearbyFacility;
import gov.va.api.lighthouse.facilities.tests.categories.NearbyAddress;
import gov.va.api.lighthouse.facilities.tests.categories.NearbyLatLong;
import io.restassured.http.Header;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Ignore
public class NearbyIT {
  private Response makeRequest(String acceptHeader, String request) {
    return TestClients.facilties()
        .service()
        .requestSpecification()
        .accept(acceptHeader)
        .header(vetsApiFacilitiesApikey())
        .request(Method.GET, TestClients.facilties().service().urlWithApiPath() + request);
  }

  @Test
  @Category({NearbyAddress.class})
  public void searchByAddress() {
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
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(NearbyFacility.class);
  }

  @Test
  @Category({NearbyAddress.class})
  public void searchByAddressWithDriveTime() {
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
            + "&drive_time=100";
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(NearbyFacility.class);
  }

  @Test
  @Category({NearbyLatLong.class})
  public void searchByLatLong() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude;
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(NearbyFacility.class);
  }

  @Test
  @Category({NearbyLatLong.class})
  public void searchByLatLongWithDriveTime() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude + "&drive_time=100";
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(NearbyFacility.class);
  }

  private Header vetsApiFacilitiesApikey() {
    return new Header("apikey", System.getProperty("vets-api-facilities-apikey", "not-supplied"));
  }
}
