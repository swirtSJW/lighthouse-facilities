package gov.va.api.lighthouse.facilitiescollector;

import com.google.common.base.Stopwatch;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
final class VetCentersCollector {
  @NonNull final String baseUrl;

  @NonNull final RestTemplate restTemplate;

  @NonNull final Map<String, String> websites;

  @SneakyThrows
  private ArcGisVetCenters loadArcGis() {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    String url =
        UriComponentsBuilder.fromHttpUrl(
                baseUrl
                    + "aqgBd3l68G8hEFFE/ArcGIS/rest/services/VHA_VetCenters/FeatureServer/0/query")
            .queryParam("f", "json")
            .queryParam("inSR", "4326")
            .queryParam("orderByFields", "stationno")
            .queryParam("outFields", "*")
            .queryParam("outSR", "4326")
            .queryParam("resultOffset", "0")
            .queryParam("returnCountOnly", "false")
            .queryParam("returnDistinctValues", "false")
            .queryParam("returnGeometry", "true")
            .queryParam("where", "1=1")
            .build()
            .toUriString();
    // ArcGIS returns text/plain response
    String response =
        restTemplate
            .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
            .getBody();
    ArcGisVetCenters vetCenters =
        JacksonConfig.createMapper().readValue(response, ArcGisVetCenters.class);
    log.info(
        "Loading vet centers took {} millis for {} features",
        totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
        vetCenters.features().size());
    return vetCenters;
  }

  Collection<Facility> vetCenters() {
    return loadArcGis().features().stream()
        .filter(Objects::nonNull)
        .map(gis -> VetCenterTransformer.builder().gis(gis).websites(websites).build().toFacility())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
