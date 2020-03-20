package gov.va.api.lighthouse.facilitiescollector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ArcGisVetCenters {
  @Builder.Default List<Feature> features = new ArrayList<>();

  @JsonIgnoreProperties(ignoreUnknown = true)
  static final class ArcGisVetCentersBuilder {}

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class Attributes {
    @JsonProperty("stationno")
    String stationNo;

    @JsonProperty("stationname")
    String stationName;

    String address2;

    String address3;

    String city;

    @JsonProperty("st")
    String state;

    String zip;

    String monday;

    String tuesday;

    String wednesday;

    String thursday;

    String friday;

    String saturday;

    String sunday;

    @JsonProperty("sta_phone")
    String staPhone;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class AttributesBuilder {}
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class Feature {
    Attributes attributes;

    Geometry geometry;
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class Geometry {
    @JsonProperty("x")
    BigDecimal longitude;

    @JsonProperty("y")
    BigDecimal latitude;
  }

  // Unused fields:
  // objectIdFieldName
  // uniqueIdField (name, isSystemMaintained)
  // globalIdFieldName
  // geometryType
  // spatialReference (wkid, latestWkid)
  // fields(name, type, alias, sqlType, length, domain, defaultValue)

  // Unused attributes:
  // address1
  // extractdate
  // lat
  // lon
  // OBJECTID
  // s_abbr
  // vastid
}
