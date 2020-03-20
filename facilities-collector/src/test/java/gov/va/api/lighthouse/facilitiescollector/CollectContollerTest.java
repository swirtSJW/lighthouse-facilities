package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

public class CollectContollerTest {
  @Test
  @SuppressWarnings("unchecked")
  public void verifyResponse() {
    RestTemplate restTemplate = mock(RestTemplate.class);

    ResponseEntity<List<AccessToPwtEntry>> atpResponse = mock(ResponseEntity.class);
    when(atpResponse.getBody())
        .thenReturn(List.of(AccessToPwtEntry.builder().facilityId("x").build()));
    when(restTemplate.exchange(
            startsWith("http://atp"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)))
        .thenReturn(atpResponse);

    ResponseEntity<ArcGisHealths> arcGisResponse = mock(ResponseEntity.class);
    when(arcGisResponse.getBody()).thenReturn(ArcGisHealths.builder().build());
    when(restTemplate.exchange(
            startsWith("http://vaarcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ArcGisHealths.class)))
        .thenReturn(arcGisResponse);

    assertThat(
            new CollectController(
                    mock(JdbcTemplate.class),
                    restTemplate,
                    "file:src/test/resources",
                    "http://atp",
                    "file:src/test/resources",
                    "http://vaarcgis")
                .collectFacilities())
        .isExactlyInstanceOf(CollectorFacilitiesResponse.class);
  }
}
