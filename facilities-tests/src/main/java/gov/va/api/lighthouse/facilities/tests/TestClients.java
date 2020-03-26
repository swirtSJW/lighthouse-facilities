package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.BasicTestClient;
import gov.va.api.health.sentinel.TestClient;
import lombok.experimental.UtilityClass;

/* Test clients for interacting with different services in a {@link SystemDefinition}. */
@UtilityClass
public class TestClients {
  static TestClient collector() {
    return BasicTestClient.builder()
        .service(SystemDefinitions.systemDefinition().collector())
        .mapper(JacksonConfig::createMapper)
        .build();
  }

  static TestClient facilties() {
    return BasicTestClient.builder()
        .service(SystemDefinitions.systemDefinition().facilities())
        .mapper(JacksonConfig::createMapper)
        .build();
  }
}
