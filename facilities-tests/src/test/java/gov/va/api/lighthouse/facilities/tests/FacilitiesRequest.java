package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.health.sentinel.ExpectedResponse.logAllWithTruncatedBody;

import gov.va.api.health.sentinel.ExpectedResponse;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
final class FacilitiesRequest {
  static ExpectedResponse facilitiesRequest(
      String acceptHeader, String request, Integer expectedStatus) {
    log.info(
        "Expect {} with accept header ({}) is status code ({})",
        TestClients.facilities().service().apiPath() + request,
        acceptHeader,
        expectedStatus);
    RequestSpecification spec = TestClients.facilities().service().requestSpecification();
    if (acceptHeader != null) {
      spec = spec.accept(acceptHeader);
    }
    return ExpectedResponse.of(
            spec.request(Method.GET, TestClients.facilities().service().urlWithApiPath() + request))
        .logAction(logAllWithTruncatedBody(2000))
        .expect(expectedStatus);
  }
}
