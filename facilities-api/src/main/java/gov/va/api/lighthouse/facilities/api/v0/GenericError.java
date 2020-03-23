package gov.va.api.lighthouse.facilities.api.v0;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/* Can be used for AuthorizationErrors AND ParameterErrors. */
@Value
@Builder
public final class GenericError {
  @NotNull String message;
}
