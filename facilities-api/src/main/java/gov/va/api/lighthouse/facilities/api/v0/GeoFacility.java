package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeoFacility {
  @NotNull Type type;

  @Valid @NotNull Geometry geometry;

  @Valid @NotNull Properties properties;

  public enum GeometryType {
    Point
  }

  public enum Type {
    Feature
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Geometry {
    @NotNull GeometryType type;

    @Size(min = 2, max = 2)
    List<BigDecimal> coordinates;
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Properties {
    @NotNull String id;

    String name;

    @NotNull
    @JsonProperty("facility_type")
    Facility.FacilityType facilityType;

    String classification;

    String website;

    @Valid Facility.Addresses address;

    @Valid Facility.Phone phone;

    @Valid Facility.Hours hours;

    @Valid Facility.Services services;

    @Valid Facility.Satisfaction satisfaction;

    @Valid
    @JsonProperty("wait_times")
    Facility.WaitTimes waitTimes;

    Boolean mobile;

    @JsonProperty("active_status")
    Facility.ActiveStatus activeStatus;

    String visn;
  }
}
