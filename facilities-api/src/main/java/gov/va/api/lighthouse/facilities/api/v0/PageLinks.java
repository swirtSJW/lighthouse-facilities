package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class PageLinks {
  @Schema(description = "/services/va_facilities/v0/facilities?id=vha_688")
  String related;

  @NotNull String self;

  String first;

  String prev;

  String next;

  String last;
}
