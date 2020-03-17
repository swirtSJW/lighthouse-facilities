package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NearbyFacility {

  @NotNull List<Nearby> data;

  PageLinks links;

  Metadata meta;

  public enum Type {
    @JsonProperty("nearby_facility")
    NearbyFacility
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Attributes {
    @JsonProperty("min_time")
    @NotNull
    Integer minTime;

    @JsonProperty("max_time")
    @NotNull
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
    @NotNull Pagination pagination;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Nearby {
    @NotNull String id;

    @NotNull Type type;

    @NotNull Attributes attributes;

    Relationships relationships;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Relationships {
    @JsonProperty("va_facility")
    @NotNull
    VaFacility vaFacility;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class VaFacility {
    @NotNull Links links;
  }
}
