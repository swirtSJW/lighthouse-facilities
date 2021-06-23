package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class PageLinks {
  @Schema(description = "/services/va_facilities/v0/facilities?id=vha_688", nullable = true)
  String related;

  @NotNull String self;

  @Schema(nullable = true)
  String first;

  @Schema(nullable = true)
  String prev;

  @Schema(nullable = true)
  String next;

  @Schema(nullable = true)
  String last;
}
