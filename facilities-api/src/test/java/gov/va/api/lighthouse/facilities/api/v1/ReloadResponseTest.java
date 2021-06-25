package gov.va.api.lighthouse.facilities.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ReloadResponseTest {
  @Test
  @SneakyThrows
  void reloadMarkCompleted() {
    final long MILLIS = 250;
    Instant start = Instant.now();
    ReloadResponse response =
        ReloadResponse.builder()
            .timing(ReloadResponse.Timing.builder().start(start).build())
            .facilitiesUpdated(List.of("Update facility"))
            .facilitiesRevived(List.of("Revived facility"))
            .facilitiesCreated(List.of("Created facility"))
            .facilitiesMissing(List.of("Missing facility"))
            .facilitiesRemoved(List.of("Removed facility"))
            .problems(List.of(ReloadResponse.Problem.of("vha_689", "Problem statement")))
            .build();
    Thread.sleep(MILLIS);
    response.timing().markComplete();
    Instant finish = Instant.now();
    assertThat(response.timing.complete).isBetween(start.plusMillis(MILLIS), finish);
    assertThat(response.timing.totalDuration)
        .isBetween(Duration.ofMillis(MILLIS), Duration.ofMillis(MILLIS + 50));
    start = Instant.now();
    Thread.sleep(MILLIS);
    response.timing.markCompleteCollection();
    finish = Instant.now();
    assertThat(response.timing.completeCollection).isBetween(start.plusMillis(MILLIS), finish);
  }

  @Test
  void startReloadResponse() {
    Instant start = Instant.now();
    ReloadResponse response = ReloadResponse.start();
    assertThat(response.timing.start).isBetween(start, start.plusMillis(50));
    assertThat(response.facilitiesUpdated).isEqualTo(List.of());
    assertThat(response.facilitiesRevived).isEqualTo(List.of());
    assertThat(response.facilitiesCreated).isEqualTo(List.of());
    assertThat(response.facilitiesMissing).isEqualTo(List.of());
    assertThat(response.facilitiesRemoved).isEqualTo(List.of());
    assertThat(response.problems).isEqualTo(List.of());
  }
}
