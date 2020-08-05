package gov.va.api.lighthouse.facilities.collector;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Stopwatch;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Builder
@Slf4j
public class BenefitsCollector {
  private final String arcgisUrl;

  private final Map<String, String> websites;

  private final RestTemplate restTemplate;

  /** Collects and transforms all benefits into a list of facilities. */
  public Collection<Facility> collect() {
    try {
      return requestArcGisBenefits().features().stream()
          .map(
              facility ->
                  BenefitsTransformer.builder()
                      .arcgisFacility(facility)
                      .csvWebsite(websites.get("vba_" + facility.attributes().facilityNumber()))
                      .build()
                      .toFacility())
          .collect(toList());
    } catch (Exception e) {
      throw new CollectorExceptions.BenefitsCollectorException(e);
    }
  }

  /** Requests ArcGIS VA_Benefits_Facilities in application/json. */
  @SneakyThrows
  private ArcGisBenefits requestArcGisBenefits() {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    String url =
        UriComponentsBuilder.fromHttpUrl(
                arcgisUrl
                    + "/aqgBd3l68G8hEFFE/ArcGIS/rest/services/VBA_Facilities/FeatureServer/0/query")
            .queryParam("f", "json")
            .queryParam("inSR", "4326")
            .queryParam("outSR", "4326")
            .queryParam("orderByFields", "Facility_Number")
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
    ArcGisBenefits benefits =
        JacksonConfig.createMapper().readValue(arcgisResponse, ArcGisBenefits.class);
    log.info(
        "Loading benefits facilities took {} millis for {} features",
        totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
        benefits.features().size());
    return benefits;
  }
}
