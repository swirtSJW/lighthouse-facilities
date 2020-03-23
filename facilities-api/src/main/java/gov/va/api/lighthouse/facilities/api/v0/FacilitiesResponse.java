package gov.va.api.lighthouse.facilities.api.v0;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class FacilitiesResponse {
  @Valid List<Facility> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull Metadata meta;

  @Value
  @Builder
  public static final class Distance {
    @NotNull String id;

    @NotNull BigDecimal distance;
  }

  @Value
  @Builder
  public static final class Metadata {
    @Valid @NotNull Pagination pagination;

    @Valid @NotNull List<Distance> distances;
  }
}
