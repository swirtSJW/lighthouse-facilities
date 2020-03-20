package gov.va.api.lighthouse.facilitiescollector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class AccessToCareEntry {
  @JsonProperty("facilityID")
  String facilityId;

  @JsonProperty("ApptTypeName")
  String apptTypeName;

  BigDecimal estWaitTime;

  BigDecimal newWaitTime;

  @JsonProperty("ED")
  Boolean emergencyCare;

  @JsonProperty("UC")
  Boolean urgentCare;

  String sliceEndDate;

  @JsonIgnoreProperties(ignoreUnknown = true)
  static final class AccessToCareEntryBuilder {}

  // Unused fields:
  // address
  // city
  // distance
  // fax
  // latitude
  // longitude
  // name
  // phone
  // SameDayMH
  // SameDayPC
  // state
  // TelehealthMH
  // TelehealthPC
  // type
  // url
  // VISN
  // WalkInMH
  // WalkInPC
  // zip
}
