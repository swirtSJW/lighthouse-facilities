package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "API invocation or processing error")
public final class ApiError {
  @NotEmpty List<ErrorMessage> errors;

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class ErrorMessage {
    @Schema(example = "Error title")
    String title;

    @Schema(example = "Detailed error message")
    String detail;

    @Schema(example = "103")
    String code;

    @Schema(example = "400")
    String status;
  }
}
