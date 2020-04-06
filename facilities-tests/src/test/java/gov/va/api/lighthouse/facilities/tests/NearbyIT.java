package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import gov.va.api.lighthouse.facilities.tests.categories.NearbyAddress;
import gov.va.api.lighthouse.facilities.tests.categories.NearbyLatLong;
import io.restassured.http.Header;
import io.restassured.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Slf4j
public class NearbyIT {
  private ExpectedResponse makeRequest(
      String acceptHeader, String request, Integer expectedStatus) {
    log.info(
        "Expect {} with accept header ({}) is status code ({})",
        TestClients.facilities().service().apiPath() + request,
        acceptHeader,
        expectedStatus);
    return ExpectedResponse.of(
            TestClients.facilities()
                .service()
                .requestSpecification()
                .accept(acceptHeader)
                .header(vetsApiFacilitiesApikey())
                .request(Method.GET, TestClients.facilities().service().urlWithApiPath() + request))
        .expect(expectedStatus);
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
    makeRequest("application/json", request, 200).expectValid(NearbyResponse.class);
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
            + "&drive_time=90";
    makeRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  @Category({NearbyLatLong.class})
  public void searchByLatLong() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude;
    makeRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  @Category({NearbyLatLong.class})
  public void searchByLatLongWithDriveTime() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude + "&drive_time=90";
    makeRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  private Header vetsApiFacilitiesApikey() {
    return new Header("apikey", System.getProperty("vets-api-facilities-apikey", "not-supplied"));
  }
}
