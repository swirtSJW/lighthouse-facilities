package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FacilitiesSearchResponse {
  List<Facility> data;

  PageLinks links;

  Metadata meta;

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Items {
    String id;

    BigDecimal distance;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Metadata {
    @NotNull List<Items> distances;

    @NotNull Pagination pagination;
  }
}
