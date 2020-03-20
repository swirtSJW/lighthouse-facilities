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
final class AccessToPwtEntry {
  @JsonProperty("facilityID")
  String facilityId;

  @JsonProperty("ApptTypeName")
  String apptTypeName;

  @JsonProperty("SHEPScore")
  BigDecimal shepScore;

  String sliceEndDate;

  @JsonIgnoreProperties(ignoreUnknown = true)
  static final class AccessToPwtEntryBuilder {}

  // Unused fields:
  // address
  // city
  // distance
  // fax
  // latitude
  // Letter
  // longitude
  // name
  // phone
  // state
  // type
  // url
  // VISN
  // zip
}
