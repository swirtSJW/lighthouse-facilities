package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class NearbyResponse {
  @Valid @NotNull List<Nearby> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull Metadata meta;

  public enum Type {
    @JsonProperty("nearby_facility")
    NearbyFacility
  }

  @Value
  @Builder
  public static final class Attributes {
    @NotNull
    @JsonProperty("min_time")
    Integer minTime;

    @NotNull
    @JsonProperty("max_time")
    Integer maxTime;
  }

  @Value
  @Builder
  public static final class Links {
    @NotNull String related;
  }

  @Value
  @Builder
  public static final class Metadata {
    @Valid @NotNull Pagination pagination;
  }

  @Value
  @Builder
  public static final class Nearby {
    @NotNull String id;

    @NotNull Type type;

    @Valid @NotNull Attributes attributes;

    @Valid @NotNull Relationships relationships;
  }

  @Value
  @Builder
  public static final class Relationships {
    @Valid
    @NotNull
    @JsonProperty("va_facility")
    VaFacility vaFacility;
  }

  @Value
  @Builder
  public static final class VaFacility {
    @Valid @NotNull Links links;
  }
}
