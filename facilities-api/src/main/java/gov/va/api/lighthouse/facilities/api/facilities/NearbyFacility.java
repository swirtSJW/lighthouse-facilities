package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NearbyFacility {
  @Valid @NotNull List<Nearby> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull Metadata meta;

  public enum Type {
    @JsonProperty("nearby_facility")
    NearbyFacility
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Attributes {
    @NotNull
    @JsonProperty("min_time")
    Integer minTime;

    @NotNull
    @JsonProperty("max_time")
    Integer maxTime;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Links {
    @NotNull String related;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Metadata {
    @NotNull @Valid Pagination pagination;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Nearby {
    @NotNull String id;

    @NotNull Type type;

    @Valid @NotNull Attributes attributes;

    @Valid @NotNull Relationships relationships;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Relationships {
    @Valid
    @NotNull
    @JsonProperty("va_facility")
    VaFacility vaFacility;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class VaFacility {
    @Valid @NotNull Links links;
  }
}
