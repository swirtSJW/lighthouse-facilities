package gov.va.api.lighthouse.facilities.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(
    description =
        "Expected wait times for new and established patients for a given health care service",
    nullable = true)
public final class PatientWaitTime {
  @NotNull HealthService service;

  @Schema(
      example = "10",
      description =
          "Average number of days a Veteran who hasn't been to this location has to wait "
              + "for a non-urgent appointment.",
      nullable = true)
  @JsonProperty("new")
  BigDecimal newPatientWaitTime;

  @Schema(
      example = "5",
      description =
          "Average number of days a patient who has already been to this location has to wait "
              + "for a non-urgent appointment.",
      nullable = true)
  @JsonProperty("established")
  BigDecimal establishedPatientWaitTime;
}
