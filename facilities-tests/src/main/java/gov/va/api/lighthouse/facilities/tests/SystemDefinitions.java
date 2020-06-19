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
  /** Service definitions for lab testing. */
  private static SystemDefinition lab() {
    String url = "http://blue.lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesManagement(serviceDefinition("facilities-management", url, 443, "/facilities/"))
        .collector(serviceDefinition("facilities-collector", url, 443, "/facilities-collector/"))
        .facilitiesIds(facilitiesIds())
        .apikey(System.getProperty("apikey", "not-specified"))
        .clientkey(System.getProperty("client-key", "not-specified"))
        .build();
  }

  /** Service definitions for local testing. */
  private static SystemDefinition local() {
    String url = "http://localhost";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 8085, "/"))
        .facilitiesManagement(serviceDefinition("facilities-management", url, 8085, "/"))
        .collector(serviceDefinition("facilities-collector", url, 8080, "/"))
        .facilitiesIds(facilitiesIds())
        .apikey(System.getProperty("apikey", "not-needed"))
        .clientkey(System.getProperty("client-key", "not-needed"))
        .build();
  }

  /** Service definitions for production testing. */
  private static SystemDefinition production() {
    String url = "http://blue.production.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesManagement(serviceDefinition("facilities-management", url, 443, "/facilities/"))
        .collector(serviceDefinition("facilities-collector", url, 443, "/facilities-collector/"))
        .facilitiesIds(facilitiesIds())
        .apikey(System.getProperty("apikey", "not-specified"))
        .clientkey(System.getProperty("client-key", "not-specified"))
        .build();
  }

  /** Service definitions for qa testing. */
  private static SystemDefinition qa() {
    String url = "http://blue.qa.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesManagement(serviceDefinition("facilities-management", url, 443, "/facilities/"))
        .collector(serviceDefinition("facilities-collector", url, 443, "/facilities-collector/"))
        .facilitiesIds(facilitiesIds())
        .apikey(System.getProperty("apikey", "not-specified"))
        .clientkey(System.getProperty("client-key", "not-specified"))
        .build();
  }

  private static ServiceDefinition serviceDefinition(
      String name, String url, int port, String apiPath) {
    return ServiceDefinition.builder()
        .url(SentinelProperties.optionUrl(name, url))
        .port(port)
        .apiPath(SentinelProperties.optionApiPath(name, apiPath))
        .accessToken(() -> Optional.empty())
        .build();
  }

  /** Service definitions for staging testing. */
  private static SystemDefinition staging() {
    String url = "http://blue.staging.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesManagement(serviceDefinition("facilities-management", url, 443, "/facilities/"))
        .collector(serviceDefinition("facilities-collector", url, 443, "/facilities-collector/"))
        .facilitiesIds(facilitiesIds())
        .apikey(System.getProperty("apikey", "not-specified"))
        .clientkey(System.getProperty("client-key", "not-specified"))
        .build();
  }

  /** Service definitions for staging-lab testing. */
  private static SystemDefinition stagingLab() {
    String url = "http://blue.staging-lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesManagement(serviceDefinition("facilities-management", url, 443, "/facilities/"))
        .collector(serviceDefinition("facilities-collector", url, 443, "/facilities-collector/"))
        .facilitiesIds(facilitiesIds())
        .apikey(System.getProperty("apikey", "not-specified"))
        .clientkey(System.getProperty("client-key", "not-specified"))
        .build();
  }

  /** Return the applicable system definition for the current environment. */
  static SystemDefinition systemDefinition() {
    switch (Environment.get()) {
      case LOCAL:
        return local();
      case QA:
        return qa();
      case STAGING:
        return staging();
      case PROD:
        return production();
      case STAGING_LAB:
        return stagingLab();
      case LAB:
        return lab();
      default:
        throw new IllegalArgumentException(
            "Unsupported sentinel environment: " + Environment.get());
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
