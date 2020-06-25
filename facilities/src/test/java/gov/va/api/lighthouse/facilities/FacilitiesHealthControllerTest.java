package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FacilitiesHealthControllerTest {
  private final FacilityRepository repository = mock(FacilityRepository.class);

  @Test
  public void clearCache() {
    controller().clearCacheScheduler();
  }

  public FacilitiesHealthController controller() {
    return new FacilitiesHealthController(repository);
  }

  @Test
  public void healthy() {
    when(repository.findLastUpdated()).thenReturn(Instant.now());
    ResponseEntity<Health> actual = controller().collectionStatus();
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actual.getBody().getStatus()).isEqualTo(Status.UP);
  }

  @Test
  public void unhealthy() {
    when(repository.findLastUpdated()).thenReturn(Instant.parse("2020-01-20T02:20:00Z"));
    ResponseEntity<Health> actual = controller().collectionStatus();
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(actual.getBody().getStatus()).isEqualTo(Status.DOWN);
  }
}
