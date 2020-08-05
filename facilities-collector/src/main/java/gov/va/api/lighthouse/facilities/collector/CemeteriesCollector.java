package gov.va.api.lighthouse.facilities.collector;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import com.google.common.base.Stopwatch;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Builder
@Slf4j
public class CemeteriesCollector {
  @NonNull private final String arcgisUrl;

  @NonNull private final Map<String, String> websites;

  @NonNull private final RestTemplate restTemplate;

  /** Collects and transforms all national cemeteries into a list of facilities. */
  public Collection<Facility> collect() {
    try {
      return requestArcGisCemeteries().features().stream()
          .filter(c -> !equalsIgnoreCase(c.attributes().siteType(), "office"))
          .map(
              facility ->
                  CemeteriesTransformer.builder()
                      .arcgisFacility(facility)
                      .csvWebsite(websites.get("nca_" + facility.attributes().siteId()))
                      .build()
                      .toFacility())
          .collect(toList());
    } catch (Exception e) {
      throw new CollectorExceptions.CemeteriesCollectorException(e);
    }
  }

  /** Requests ArcGIS VA_Cemeteries_Facilities in application/json. */
  @SneakyThrows
  private ArcGisCemeteries requestArcGisCemeteries() {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    String url =
        UriComponentsBuilder.fromHttpUrl(
                arcgisUrl
                    + "/aqgBd3l68G8hEFFE/ArcGIS/rest/services/NCA_Facilities/FeatureServer/0/query")
            .queryParam("f", "json")
            .queryParam("inSR", "4326")
            .queryParam("outSR", "4326")
            .queryParam("orderByFields", "SITE_ID")
            .queryParam("outFields", "*")
            .queryParam("resultOffset", 0)
            .queryParam("returnCountOnly", false)
            .queryParam("returnDistinctValues", false)
            .queryParam("returnGeometry", true)
            .queryParam("where", "1=1")
            .build()
            .toUriString();
    /*
     *  ArcGIS returns a response in text/plain, so we need to deserialize as a string.
     */
    String arcgisResponse =
        restTemplate
            .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
            .getBody();
    ArcGisCemeteries cemeteries =
        JacksonConfig.createMapper().readValue(arcgisResponse, ArcGisCemeteries.class);
    log.info(
        "Loading cemeteries took {} millis for {} features",
        totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
        cemeteries.features().size());
    return cemeteries;
  }
}
