package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.SentinelProperties;
import gov.va.api.health.sentinel.ServiceDefinition;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/**
 * {@link SystemDefinition}s for different environments. {@link #systemDefinition()} method provides
 * the appropriate implementation for the current environment.
 */
@UtilityClass
class SystemDefinitions {

  /** Service definitions for local testing. */
  private static SystemDefinition local() {
    String url = "http://localhost";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 8080, "/"))
        .facilitiesIds(facilitiesIds())
        .build();
  }

  private static ServiceDefinition serviceDefinition(
      String name, String url, int port, String apiPath) {
    return ServiceDefinition.builder()
        .url(SentinelProperties.optionUrl(name, url))
        .port(port)
        .apiPath(SentinelProperties.optionApiPath(name, apiPath))
        .accessToken(() -> Optional.ofNullable(null))
        .build();
  }

  /** Return the applicable system definition for the current environment. */
  static SystemDefinition systemDefinition() {
    switch (Environment.get()) {
      case QA:
      case STAGING:
      case PROD:
      case STAGING_LAB:
      case LAB:
      case LOCAL:
        return local();
      default:
        throw new IllegalArgumentException("Unknown sentinel environment: " + Environment.get());
    }
  }

  private FacilitiesIds facilitiesIds() {
    return FacilitiesIds.builder()
        .facility("vba_322b")
        .facilitiesList("nca_055,nca_s1001,vba_322b,vc_0101V,vha_402GA")
        .latitude("28.112464")
        .longitude("-80.7015994")
        .bbox("bbox[]=-81.47&bbox[]=27.48&bbox[]=-79.97&bbox[]=28.98")
        .zip("32934")
        .state("FL")
        .city("Melbourne")
        .streetAddress("505 N John Rodes Blvd")
        .build();
  }
}
