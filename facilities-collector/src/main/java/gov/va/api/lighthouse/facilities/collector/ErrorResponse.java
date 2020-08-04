package gov.va.api.lighthouse.facilities.collector;

import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/** The error response is the payload returned to the caller should a failure occur. */
@Value
@Builder
public final class ErrorResponse {
  long timestamp;

  String type;

  String message;

  /** Create a new error response based on the given exception. */
  public static ErrorResponse of(@NonNull Exception e) {
    return ErrorResponse.builder()
        .timestamp(Instant.now().toEpochMilli())
        .type(e.getClass().getSimpleName())
        .message(e.getMessage())
        .build();
  }
}
