package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

public class CollectContollerTest {
  @Test
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void verifyResponse() {
    RestTemplate restTemplate = mock(RestTemplate.class);

    when(restTemplate.exchange(
            contains("VHA_VetCenters"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(ArcGisVetCenters.builder().build()))));

    when(restTemplate.exchange(
            contains("VBA_Facilities"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(ArcGisBenefits.builder().build()))));

    when(restTemplate.exchange(
            startsWith("http://atp"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(List.of(AccessToPwtEntry.builder().facilityId("x").build()))));

    when(restTemplate.exchange(
            startsWith("http://vaarcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ArcGisHealths.class)))
        .thenReturn(ResponseEntity.of(Optional.of(ArcGisHealths.builder().build())));

    assertThat(
            new CollectController(
                    mock(JdbcTemplate.class),
                    restTemplate,
                    "http://arcgis",
                    "file:src/test/resources",
                    "http://atp",
                    "file:src/test/resources",
                    "http://vaarcgis")
                .collectFacilities())
        .isExactlyInstanceOf(CollectorFacilitiesResponse.class);
  }
}
