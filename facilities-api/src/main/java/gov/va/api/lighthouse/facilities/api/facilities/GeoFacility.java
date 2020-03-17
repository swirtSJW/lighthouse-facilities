package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GeoFacility {
  Type type;

  Geometry geometry;

  Properties properties;

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
    GeometryType type;

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
    String id;

    String name;

    @JsonProperty("facility_type")
    Facility.FacilityType facilityType;

    String classification;

    String website;

    Facility.Addresses address;

    Facility.Phone phone;

    Facility.Hours hours;

    Facility.Services services;

    Facility.Satisfaction satisfaction;

    @JsonProperty("wait_times")
    Facility.WaitTimes waitTimes;

    Boolean mobile;

    @JsonProperty("active_status")
    Facility.ActiveStatus activeStatus;

    String visn;
  }
}
