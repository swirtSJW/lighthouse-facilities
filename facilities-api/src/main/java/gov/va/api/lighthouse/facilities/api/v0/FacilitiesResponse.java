package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FacilitiesResponse {
  @Valid List<Facility> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull Metadata meta;

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Distance {
    @NotNull String id;

    @NotNull BigDecimal distance;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Metadata {
    @Valid @NotNull Pagination pagination;

    @Valid @NotNull List<Distance> distances;
  }

  @Value
  @Builder
  @JsonInclude(JsonInclude.Include.ALWAYS)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class PageLinks {
    @NotNull String self;

    String first;

    String prev;

    String next;

    String last;
  }
}
