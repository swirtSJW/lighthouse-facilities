package gov.va.api.lighthouse.facilitiescollector;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class CollectorHealthControllerTest {

  @Mock InsecureRestTemplateProvider insecureRestTemplateProvider;

  @Mock RestTemplate restTemplate;

  static void assertStatus(Health overallStatus, ExpectedStatus expected) {
    //noinspection unchecked
    Map<String, Status> serviceStatus =
        ((List<Health>) overallStatus.getDetails().get("downstreamServices"))
            .stream()
                .collect(Collectors.toMap(h -> h.getStatus().getDescription(), Health::getStatus));
    System.out.println(serviceStatus);
    assertThat(serviceStatus.get("Access to Care").getCode()).isEqualTo(expected.accesToCare());
    assertThat(serviceStatus.get("Access to PWT").getCode()).isEqualTo(expected.accessToPWT());
    assertThat(serviceStatus.get("Public ArcGIS").getCode()).isEqualTo(expected.publicArcGIS());
    assertThat(serviceStatus.get("State Cemeteries").getCode())
        .isEqualTo(expected.stateCemeteries());
    assertThat(serviceStatus.get("VA ArcGIS").getCode()).isEqualTo(expected.vaArcGis());
  }

  @Test
  @SneakyThrows
  public void allHealthyBackendServicesReturns200AndHealthWithUpStatuses() {
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(restTemplate);
    when(restTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://arcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://vaarcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    ResponseEntity<Health> response = controller().collectorHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertStatus(
        requireNonNull(response.getBody()),
        ExpectedStatus.builder()
            .accesToCare("UP")
            .accessToPWT("UP")
            .publicArcGIS("UP")
            .stateCemeteries("UP")
            .vaArcGis("UP")
            .build());
  }

  @Test
  @SneakyThrows
  public void cacheClearing() {
    controller().clearCacheScheduler();
  }

  public CollectorHealthController controller() {
    return new CollectorHealthController(
        insecureRestTemplateProvider,
        restTemplate,
        "http://arcgis",
        "http://atc",
        "http://atp",
        "http://statecems",
        "http://vaarcgis");
  }

  private ResponseEntity<String> notOk() {
    return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
  }

  private ResponseEntity<String> ok() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Test
  @SneakyThrows
  public void oneOrMoreUnhealthyBackendServicesReturns503AndHealthWithDownStatuses() {
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(restTemplate);
    when(restTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(notOk());
    when(restTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(notOk());
    when(restTemplate.exchange(
            startsWith("http://arcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://vaarcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    ResponseEntity<Health> response = controller().collectorHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertStatus(
        requireNonNull(response.getBody()),
        ExpectedStatus.builder()
            .accesToCare("DOWN")
            .accessToPWT("DOWN")
            .publicArcGIS("UP")
            .stateCemeteries("UP")
            .vaArcGis("UP")
            .build());
  }

  @Test
  @SneakyThrows
  public void servicesUnreachable() {
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(restTemplate);
    when(restTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new ResourceAccessException("I/O error on GET request"));
    when(restTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new ResourceAccessException("I/O error on GET request"));
    when(restTemplate.exchange(
            startsWith("http://arcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://vaarcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    ResponseEntity<Health> response = controller().collectorHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertStatus(
        requireNonNull(response.getBody()),
        ExpectedStatus.builder()
            .accesToCare("DOWN")
            .accessToPWT("DOWN")
            .publicArcGIS("UP")
            .stateCemeteries("UP")
            .vaArcGis("UP")
            .build());
  }

  @Value
  @Builder
  private static class ExpectedStatus {
    String accesToCare;
    String accessToPWT;
    String publicArcGIS;
    String stateCemeteries;
    String vaArcGis;
  }
}
