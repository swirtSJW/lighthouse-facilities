package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.collectorapi.CollectorRestClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CollectorRestClientTest {
  @Test
  void collectFacilities() {
    RestTemplate restTemplate = mock(RestTemplate.class);
    CollectorFacilitiesResponse response =
        CollectorFacilitiesResponse.builder()
            .facilities(List.of(Facility.builder().id("x").build()))
            .build();
    when(restTemplate.exchange(
            eq("http://foo/collect/facilities"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CollectorFacilitiesResponse.class)))
        .thenReturn(ResponseEntity.ok(response));

    CollectorRestClient client = new CollectorRestClient(restTemplate, "http://foo/");
    assertThat(client.collectFacilities()).isEqualTo(response);
  }
}
