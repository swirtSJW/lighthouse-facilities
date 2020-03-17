package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public class GeoFacility {
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
  public static class Geometry {
    @NotNull GeometryType type;

    @Size(min = 2, max = 2)
    List<BigDecimal> coordinates;
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonPropertyOrder({
    "id",
    "name",
    "facilityType",
    "classification",
    "website",
    "address",
    "phone",
    "hours",
    "services",
    "satisfaction",
    "waitTimes",
    "mobile",
    "activeStatus",
    "visn"
  })
  public static class Properties {
    @NotNull String id;

    String name;

    @JsonProperty("facility_type")
    @NotNull
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
