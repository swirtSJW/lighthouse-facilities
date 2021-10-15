package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.collector.CsvLoader.loadWebsites;

import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class CovidServiceUpdater {
  public static final String CMS_OVERLAY_SERVICE_NAME_COVID_19 = "COVID-19 vaccines";

  private static final String COVID_CSV_WEBSITES_RESOURCE_NAME = "COVID-19-Facility-URLs.csv";

  /** Utility method for updating Covid related service URLs for facilities. */
  @SneakyThrows
  public static List<DetailedService> updateServiceUrlPaths(
      String id, List<DetailedService> detailedServices) {
    for (final DetailedService service : detailedServices) {
      if (service.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
        Map<String, String> websites = loadWebsites(COVID_CSV_WEBSITES_RESOURCE_NAME);
        service.path(websites.get(id));
        log.info("Covid URL updated for facility {}", sanitize(id));
      }
    }
    return detailedServices;
  }
}
