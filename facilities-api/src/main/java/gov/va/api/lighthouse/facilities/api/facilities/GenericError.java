package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/* Can be used for AuthorizationErrors AND ParameterErrors. */
@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GenericError {
  @NotNull String message;
}
