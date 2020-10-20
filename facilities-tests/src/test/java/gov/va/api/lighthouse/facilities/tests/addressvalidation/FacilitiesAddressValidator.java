package gov.va.api.lighthouse.facilities.tests.addressvalidation;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacilitiesAddressValidator {

  private static final String BASE_ADDRESS_VALIDATION_URI = "https://sandbox-api.va.gov";
  private static final String BASE_FACILITIES_RETRIEVAL_URI = "https://api.va.gov";

  private static final String[] STATE_ABBREVIATIONS = {
    "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS",
    "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY",
    "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV",
    "WI", "WY"
  };

  private static final String US_COUNTRY_CODE = "US";

  @SneakyThrows
  private static Response doAddressValidation(AddressValidationRequest addressValidationRequest) {
    return RestAssured.given()
        .baseUri(BASE_ADDRESS_VALIDATION_URI)
        .relaxedHTTPSValidation()
        .header("apikey", System.getProperty("addressApiKey", "unset"))
        .contentType("application/json")
        .body(JacksonConfig.createMapper().writeValueAsString(addressValidationRequest))
        .request(Method.POST, "https://sandbox-api.va.gov/services/address_validation/v0/validate");
  }

  private static boolean isValidUsState(String state) {

    for (String s : STATE_ABBREVIATIONS) {
      if (s.equals(state)) {
        return true;
      }
    }
    return false;
  }

  @SneakyThrows
  public static void main(String[] args) {
    log.info("Beginning facility retrieval for address validation!");
    GeoFacilitiesResponse allFacilitiesResponse =
        ExpectedResponse.of(
                RestAssured.given()
                    .baseUri(BASE_FACILITIES_RETRIEVAL_URI)
                    .relaxedHTTPSValidation()
                    .header("apikey", System.getProperty("apikey", "unset"))
                    .request(
                        Method.GET, "https://api.va.gov/services/va_facilities/v0/facilities/all"))
            .expect(200)
            .expectValid(GeoFacilitiesResponse.class);
    int invalidAddressCount = 0;
    if (allFacilitiesResponse.features() != null) {
      log.info("Beginning facility address validation!");
      for (GeoFacility facility : allFacilitiesResponse.features()) {

        if (facility.properties().address().physical() != null) {

          String primaryZip = "";
          String secondaryZip = "";

          int zipLength = 0;

          if (!isBlank(facility.properties().address().physical().zip())) {
            zipLength = facility.properties().address().physical().zip().length();
          }

          if (zipLength == 5) {
            primaryZip = facility.properties().address().physical().zip();
          } else if (zipLength == 10) {
            // remove the dash
            primaryZip = facility.properties().address().physical().zip().substring(0, 5);
            secondaryZip = facility.properties().address().physical().zip().substring(6);
          }

          String countryCode = US_COUNTRY_CODE;
          String state = facility.properties().address().physical().state();
          // If the facility is not located in a United States state, assume the state is the
          // country code
          if (!isValidUsState(state)) {
            countryCode = state;
          }

          AddressValidationRequest addressValidationRequest =
              AddressValidationRequest.builder()
                  .requestAddress(
                      AddressValidationRequest.RequestAddress.builder()
                          .address1(facility.properties().address().physical().address1())
                          .address2(facility.properties().address().physical().address2())
                          .address3(facility.properties().address().physical().address3())
                          .city(facility.properties().address().physical().city())
                          .requestCountry(
                              AddressValidationRequest.RequestCountry.builder()
                                  .countryCode(countryCode)
                                  .build())
                          .stateProvince(
                              AddressValidationRequest.StateProvince.builder().code(state).build())
                          .zipCode5(primaryZip)
                          .zipCode4(secondaryZip)
                          .build())
                  .build();
          var response = doAddressValidation(addressValidationRequest);
          while (response.getStatusCode() == 429) {
            log.info("Rate limiting hit! Sleeping for 20 seconds and trying again!");
            Thread.sleep(20000);
            response = doAddressValidation(addressValidationRequest);
          }
          AddressValidationResponse addressValidationResponse =
              ExpectedResponse.of(response).expectValid(AddressValidationResponse.class);
          if (response.getStatusCode() == 200) {
            if (addressValidationResponse.addressMetaData().confidenceScore < 50) {
              log.info(
                  "Low confidence in facility {} address: {} ",
                  facility,
                  addressValidationResponse);
              invalidAddressCount++;
            }
          } else {
            log.error(
                "Error code {} for facility {}. See error message(s) in response below: ",
                response.getStatusCode(),
                facility);
            log.error("Error response: {}", addressValidationResponse);
            invalidAddressCount++;
          }
        } else {
          log.error("Error for facility {}. Address is null and invalid!", facility);
          invalidAddressCount++;
        }
      }
    }
    log.info("Completed address validation! Invalid Address Count: {}", invalidAddressCount);
  }
}
