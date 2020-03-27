package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import io.restassured.http.Method;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.junit.rules.ExternalResource;

@Slf4j
public class RequiresFacilities extends ExternalResource {

  /** This instance is synchronized and manages state for the entire test suite. */
  private static final LockAndLoad LOADER = new LockAndLoad();

  @Override
  protected void before() {
    LOADER.loadIfNecessary();
  }

  private static final class LockAndLoad {
    private boolean loaded;

    private boolean isTestFacilityAvailable() {
      var response =
          TestClients.facilities()
              .service()
              .requestSpecification()
              .accept("application/json")
              .header(systemDefinition().apikeyAsHeader())
              .request(
                  Method.GET,
                  TestClients.facilities().service().urlWithApiPath()
                      + "v0/facilities/"
                      + systemDefinition().facilitiesIds().facility());
      return response.statusCode() == 200;
    }

    @Synchronized
    public void loadIfNecessary() {
      if (loaded) {
        return;
      }
      if (!isTestFacilityAvailable()) {
        log.info("Facilities do not appear to be loaded. Loading now.");
        reloadFacilities();
      }
      loaded = true;
    }

    public void reloadFacilities() {
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
  }
}
