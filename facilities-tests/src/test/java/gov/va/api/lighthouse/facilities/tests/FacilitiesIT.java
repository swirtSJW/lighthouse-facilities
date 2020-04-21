package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.health.sentinel.ExpectedResponse.logAllWithTruncatedBody;
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
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Slf4j
public class FacilitiesIT {
  @Rule public RequiresFacilities precondition = new RequiresFacilities();

  @Test
  @Category({AllFacilities.class})
  public void allAsCsv() {
    final String request = "v0/facilities/all";
    makeRequest("text/csv", request, 200);
  }

  @Test
  @Category({AllFacilities.class})
  public void allAsJson() {
    final String request = "v0/facilities/all";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(GeoFacilitiesResponse.class);
  }

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
                .header(SystemDefinitions.systemDefinition().apikeyAsHeader())
                .request(Method.GET, TestClients.facilities().service().urlWithApiPath() + request))
        .logAction(logAllWithTruncatedBody(2000))
        .expect(expectedStatus);
  }

  @Test
  @Category({FacilityById.class})
  public void readById() {
    final String facility = systemDefinition().facilitiesIds().facility();
    final String request = "v0/facilities/" + facility;
    makeRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilityReadResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilityReadResponse.class);
  }

  @Test
  @Category({SearchByBoundingBox.class})
  public void searchByBoundingBox() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox;
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category({SearchByBoundingBox.class})
  public void searchByBoundingBoxMutuallyExclusive() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&lat=" + latitude + "&long=" + longitude;
    makeRequest("application/vnd.geo+json", request, 400).expectValid(ApiError.class);
    makeRequest("application/json", request, 400).expectValid(ApiError.class);
  }

  @Test
  @Category(SearchByBoundingBox.class)
  public void searchByBoundingBoxWithServices() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&services[]=PrimaryCare";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByBoundingBox.class)
  public void searchByBoundingBoxWithType() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&type=health";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByBoundingBox.class)
  public void searchByBoundingBoxWithTypeAndServices() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&type=health&services[]=PrimaryCare";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category({SearchByIds.class})
  public void searchByIds() {
    final String facilities = systemDefinition().facilitiesIds().facilitiesList();
    final String request = "v0/facilities?ids=" + facilities;
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category({SearchByLatLong.class})
  public void searchByLatLong() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/facilities?lat=" + latitude + "&long=" + longitude;
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category({SearchByLatLong.class})
  public void searchByLatLongMutuallyExclusive() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String state = systemDefinition().facilitiesIds().state();
    final String request =
        "v0/facilities?lat=" + latitude + "&long=" + longitude + "&state=" + state;
    makeRequest("application/vnd.geo+json", request, 400).expectValid(ApiError.class);
    makeRequest("application/json", request, 400).expectValid(ApiError.class);
  }

  @Test
  @Category({SearchByLatLong.class})
  public void searchByLatLongWithIds() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String facilities = systemDefinition().facilitiesIds().facilitiesList();
    final String request =
        "v0/facilities?lat=" + latitude + "&long=" + longitude + "&ids=" + facilities;
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByLatLong.class)
  public void searchByLatLongWithServices() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request =
        "v0/facilities?lat=" + latitude + "&long=" + longitude + "&services[]=PrimaryCare";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByLatLong.class)
  public void searchByLatLongWithType() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/facilities?lat=" + latitude + "&long=" + longitude + "&type=health";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

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
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category({SearchByState.class})
  public void searchByState() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state;
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByState.class)
  public void searchByStateMutuallyExclusive() {
    final String state = systemDefinition().facilitiesIds().state();
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?state=" + state + "&zip=" + zip;
    makeRequest("application/vnd.geo+json", request, 400).expectValid(ApiError.class);
    makeRequest("application/json", request, 400).expectValid(ApiError.class);
  }

  @Test
  @Category(SearchByState.class)
  public void searchByStateWithServices() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&services[]=PrimaryCare";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByState.class)
  public void searchByStateWithType() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&type=health";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByState.class)
  public void searchByStateWithTypeAndServices() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&type=health&services[]=PrimaryCare";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category({SearchByZip.class})
  public void searchByZip() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip;
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByZip.class)
  public void searchByZipMutuallyExclusive() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?zip=" + zip + "&" + bbox;
    makeRequest("application/vnd.geo+json", request, 400).expectValid(ApiError.class);
    makeRequest("application/json", request, 400).expectValid(ApiError.class);
  }

  @Test
  @Category(SearchByZip.class)
  public void searchByZipWithServices() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&services[]=PrimaryCare";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByZip.class)
  public void searchByZipWithType() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&type=health";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  @Category(SearchByZip.class)
  public void searchByZipWithTypeAndServices() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&type=health&services[]=PrimaryCare";
    makeRequest("application/vnd.geo+json", request, 200).expectValid(GeoFacilitiesResponse.class);
    makeRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
  }
}
