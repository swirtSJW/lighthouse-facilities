package gov.va.api.lighthouse.facilities.api.telehealth;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "Data provided by telehealth team to Facilities.")
public class TelehealthBody {
  // todo fill in variables once confirmed
  @JsonProperty("stub")
  String stub;
}
