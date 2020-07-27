package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import io.restassured.http.Method;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class RequiresFacilitiesExtension implements BeforeAllCallback {
  private static boolean loaded;

  private static boolean isTestFacilityAvailable() {
    var response =
        TestClients.facilities()
            .service()
            .requestSpecification()
            .accept("application/json")
            .request(
                Method.GET,
                TestClients.facilities().service().urlWithApiPath()
                    + "v0/facilities/"
                    + systemDefinition().facilitiesIds().facility());
    return response.statusCode() == 200;
  }

  /** Statically synchronized to manage state for the entire test suite. */
  @Synchronized
  private static void loadIfNecessary() {
    if (loaded) {
      return;
    }
    if (!isTestFacilityAvailable()) {
      log.info("Facilities do not appear to be loaded. Loading now.");
      reloadFacilities();
    }
    loaded = true;
  }

  private static void reloadFacilities() {
    var response =
        TestClients.facilities()
            .service()
            .requestSpecification()
            .header(systemDefinition().clientkeyAsHeader())
            .log()
            .uri()
            .request(
                Method.GET,
                TestClients.facilitiesManagement().service().urlWithApiPath()
                    + "internal/management/reload");
    if (response.statusCode() != 200) {
      log.warn(
          "Facility loading appears to have failed with status {}\n{}",
          response.statusCode(),
          response.getBody().prettyPrint());
    }
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    loadIfNecessary();
  }
}
