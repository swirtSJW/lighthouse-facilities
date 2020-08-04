package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

public class WebExceptionHandlerTest {
  @Test
  public void handleSnafu() {
    ErrorResponse response = new WebExceptionHandler().handleSnafu(new RuntimeException("oh noez"));
    assertThat(Instant.now().toEpochMilli() - response.timestamp()).isLessThan(2000);
    assertThat(response)
        .isEqualTo(
            ErrorResponse.builder()
                .timestamp(response.timestamp())
                .type("RuntimeException")
                .message("oh noez")
                .build());
  }
}
