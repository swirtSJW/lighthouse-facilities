package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableSet;
import gov.va.api.health.sentinel.ExpectedResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import java.time.Instant;
import java.util.Set;
import lombok.Data;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

public class HealthIT {
  @Test
  void collectorBackendHealth() {
    SystemDefinitions.Service svc = systemDefinition().collector();
    String path = "collector/health";
    Set<Instant> times = ImmutableSet.of(timeOf(svc, path), timeOf(svc, path), timeOf(svc, path));
    assertThat(times.size()).isLessThan(3);
  }

  @Test
  void reloadHealth() {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    String path = "collection/status";
    Set<Instant> times = ImmutableSet.of(timeOf(svc, path), timeOf(svc, path), timeOf(svc, path));
    // FIXME
    // assertThat(times.size()).isLessThan(3);
  }

  private Instant timeOf(@NonNull SystemDefinitions.Service svc, @NonNull String path) {
    Health health =
        ExpectedResponse.of(
                RestAssured.given()
                    .baseUri(svc.url())
                    .port(svc.port())
                    .relaxedHTTPSValidation()
                    .request(Method.GET, svc.urlWithApiPath() + path))
            .expectValid(Health.class);
    Instant time = health.details().time();
    assertThat(time).isNotNull();
    return time;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static final class Health {
    Details details;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static final class Details {
    Instant time;
  }
}
