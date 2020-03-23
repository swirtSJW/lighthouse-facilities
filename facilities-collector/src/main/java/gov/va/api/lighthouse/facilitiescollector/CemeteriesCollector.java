package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Builder
public class CemeteriesCollector {
  @NonNull private final String arcgisUrl;

  @NonNull private final Map<String, String> websites;

  @NonNull private final RestTemplate restTemplate;

  /** Collects and transforms all benefits into a list of facilities. */
  @SneakyThrows
  public Collection<Facility> collect() {
    return requestArcGisCemeteries().features().stream()
        .map(
            facility ->
                CemeteriesTransformer.builder()
                    .arcgisFacility(facility)
                    .csvWebsite(websites.get("nca_" + facility.attributes().siteId()))
                    .build()
                    .toFacility())
        .collect(Collectors.toList());
  }

  /** Requests ArcGIS VA_Cemeteries_Facilities in application/json. */
  @SneakyThrows
  private ArcGisCemeteries requestArcGisCemeteries() {
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
    return JacksonConfig.createMapper().readValue(arcgisResponse, ArcGisCemeteries.class);
  }
}
