package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class FacilityReadResponse {
  @Valid
  @NotNull
  @JsonProperty("data")
  Facility facility;
}
