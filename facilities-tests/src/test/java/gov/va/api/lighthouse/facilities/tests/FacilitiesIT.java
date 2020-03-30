package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.tests.categories.AllFacilities;
import gov.va.api.lighthouse.facilities.tests.categories.FacilityById;
import gov.va.api.lighthouse.facilities.tests.categories.SearchByBoundingBox;
import gov.va.api.lighthouse.facilities.tests.categories.SearchByIds;
import gov.va.api.lighthouse.facilities.tests.categories.SearchByLatLong;
import gov.va.api.lighthouse.facilities.tests.categories.SearchByState;
import gov.va.api.lighthouse.facilities.tests.categories.SearchByZip;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class FacilitiesIT {

  @Rule public RequiresFacilities precondition = new RequiresFacilities();

  @Test
  @Category({AllFacilities.class})
  public void allAsCsv() {
    final String request = "v0/facilities/all";
    ExpectedResponse.of(makeRequest("text/csv", request)).expect(200);
  }

  @Test
  @Category({AllFacilities.class})
  public void allAsJson() {
    final String request = "v0/facilities/all";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
  }

  private Response makeRequest(String acceptHeader, String request) {
    return TestClients.facilities()
        .service()
        .requestSpecification()
        .accept(acceptHeader)
        .header(SystemDefinitions.systemDefinition().apikeyAsHeader())
        .request(Method.GET, TestClients.facilities().service().urlWithApiPath() + request);
  }

  @Test
  @Category({FacilityById.class})
  public void readById() {
    final String facility = systemDefinition().facilitiesIds().facility();
    final String request = "v0/facilities/" + facility;
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilityReadResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilityReadResponse.class);
  }

  @Test
  @Category({SearchByBoundingBox.class})
  public void searchByBoundingBox() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox;
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByBoundingBox.class)
  public void searchByBoundingBoxWithServices() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&services[]=PrimaryCare";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByBoundingBox.class)
  public void searchByBoundingBoxWithType() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&type=health";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByBoundingBox.class)
  public void searchByBoundingBoxWithTypeAndServices() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&type=health&services[]=PrimaryCare";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category({SearchByIds.class})
  public void searchByIds() {
    final String facilities = systemDefinition().facilitiesIds().facilitiesList();
    final String request = "v0/facilities?ids=" + facilities;
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category({SearchByLatLong.class})
  public void searchByLatLong() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/facilities?lat=" + latitude + "&long=" + longitude;
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category(SearchByLatLong.class)
  public void searchByLatLongWithServicesMissingType() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request =
        "v0/facilities?lat=" + latitude + "&long=" + longitude + "&services[]=PrimaryCare";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(400)
        .expectValid(ApiError.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(400)
        .expectValid(ApiError.class);
  }

  @Ignore
  @Test
  @Category(SearchByLatLong.class)
  public void searchByLatLongWithType() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/facilities?lat=" + latitude + "&long=" + longitude + "&type=health";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category(SearchByLatLong.class)
  public void searchByLatLongWithTypeAndServices() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request =
        "v0/facilities?lat="
            + latitude
            + "&long="
            + longitude
            + "&type=health&services[]=PrimaryCare";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category({SearchByState.class})
  public void searchByState() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state;
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category(SearchByState.class)
  public void searchByStateWithServicesMissingType() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&services[]=PrimaryCare";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(400)
        .expectValid(ApiError.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(400)
        .expectValid(ApiError.class);
  }

  @Ignore
  @Test
  @Category(SearchByState.class)
  public void searchByStateWithType() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&type=health";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category(SearchByState.class)
  public void searchByStateWithTypeAndServices() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&type=health&services[]=PrimaryCare";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category({SearchByZip.class})
  public void searchByZip() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip;
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category(SearchByZip.class)
  public void searchByZipWithServicesMissingType() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&services[]=PrimaryCare";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(400)
        .expectValid(ApiError.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(400)
        .expectValid(ApiError.class);
  }

  @Ignore
  @Test
  @Category(SearchByZip.class)
  public void searchByZipWithType() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&type=health";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }

  @Ignore
  @Test
  @Category(SearchByZip.class)
  public void searchByZipWithTypeAndServices() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&type=health&services[]=PrimaryCare";
    ExpectedResponse.of(makeRequest("application/vnd.geo+json", request))
        .expect(200)
        .expectValid(GeoFacilitiesResponse.class);
    ExpectedResponse.of(makeRequest("application/json", request))
        .expect(200)
        .expectValid(FacilitiesResponse.class);
  }
}
