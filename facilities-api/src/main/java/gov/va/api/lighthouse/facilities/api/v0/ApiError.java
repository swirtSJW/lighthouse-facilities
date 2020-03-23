package gov.va.api.lighthouse.facilities.api.v0;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class ApiError {
  @NotEmpty List<ErrorMessage> errors;

  @Value
  @Builder
  public static final class ErrorMessage {
    String title;

    String detail;

    String code;

    String status;
  }
}
