package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class NearbyResponse {
  @Valid @NotNull List<Nearby> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull NearbyMetadata meta;

  public enum Type {
    @JsonProperty("nearby_facility")
    NearbyFacility
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class NearbyAttributes {
    @NotNull
    @Schema(example = "10")
    @JsonProperty("min_time")
    Integer minTime;

    @NotNull
    @Schema(example = "20")
    @JsonProperty("max_time")
    Integer maxTime;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Links {
    @Schema(example = "/services/va_facilities/v0/facilities/vha_688")
    @NotNull
    String related;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class NearbyMetadata {
    @Valid @NotNull Pagination pagination;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @Schema(description = "JSON API-compliant object describing a nearby VA facility")
  public static final class Nearby {
    @Schema(example = "vha_688")
    @NotNull
    String id;

    @Schema(example = "nearby_facility")
    @NotNull
    Type type;

    @Valid @NotNull NearbyAttributes attributes;

    @Valid @NotNull Relationships relationships;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Relationships {
    @Valid
    @NotNull
    @JsonProperty("va_facility")
    VaFacility vaFacility;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class VaFacility {
    @Valid @NotNull Links links;
  }
}
