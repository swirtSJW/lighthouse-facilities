package gov.va.api.lighthouse.facilities.collector;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CdwCemetery {
  String siteId;

  String fullName;

  String siteType;

  String siteAddress1;

  String siteAddress2;

  String siteCity;

  String siteState;

  String siteZip;

  String mailAddress1;

  String mailAddress2;

  String mailCity;

  String mailState;

  String mailZip;

  String phone;

  String fax;

  String visitationHoursWeekday;

  String visitationHoursWeekend;

  String websiteUrl;

  BigDecimal longitude;

  BigDecimal latitude;
}
