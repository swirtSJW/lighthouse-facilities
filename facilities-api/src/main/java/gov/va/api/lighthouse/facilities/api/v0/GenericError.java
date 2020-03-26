package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/* Can be used for AuthorizationErrors AND ParameterErrors. */
@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(
    description =
        "A general error json response for things like Authorization Errors or "
            + "Bad Requests (ex. invalid parameter values).")
public final class GenericError {
  @Schema(example = "Detailed Error Message")
  @NotNull
  String message;
}
