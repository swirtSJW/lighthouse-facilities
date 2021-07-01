package gov.va.api.lighthouse.facilities.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(nullable = true)
public final class WaitTimes {
  @Schema(nullable = true)
  @Valid
  List<PatientWaitTime> health;

  @Schema(example = "2018-01-01", nullable = true)
  @JsonProperty("effective_date")
  LocalDate effectiveDate;
}
