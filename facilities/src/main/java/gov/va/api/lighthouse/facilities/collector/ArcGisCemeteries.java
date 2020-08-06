package gov.va.api.lighthouse.facilities.collector;

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
final class ArcGisCemeteries {
  @Builder.Default List<Feature> features = new ArrayList<>();

  @JsonIgnoreProperties(ignoreUnknown = true)
  static final class ArcGisCemeteriesBuilder {}

  @Value
  @Builder
  static final class Attributes {
    @JsonProperty("SITE_ID")
    String siteId;

    @JsonProperty("FULL_NAME")
    String fullName;

    @JsonProperty("SITE_TYPE")
    String siteType;

    @JsonProperty("SITE_ADDRESS1")
    String siteAddress1;

    @JsonProperty("SITE_ADDRESS2")
    String siteAddress2;

    @JsonProperty("SITE_CITY")
    String siteCity;

    @JsonProperty("SITE_STATE")
    String siteState;

    @JsonProperty("SITE_ZIP")
    String siteZip;

    @JsonProperty("MAIL_ADDRESS1")
    String mailAddress1;

    @JsonProperty("MAIL_ADDRESS2")
    String mailAddress2;

    @JsonProperty("MAIL_CITY")
    String mailCity;

    @JsonProperty("MAIL_STATE")
    String mailState;

    @JsonProperty("MAIL_ZIP")
    String mailZip;

    @JsonProperty("PHONE")
    String phone;

    @JsonProperty("FAX")
    String fax;

    @JsonProperty("VISITATION_HOURS_WEEKDAY")
    String visitationHoursWeekday;

    @JsonProperty("VISITATION_HOURS_WEEKEND")
    String visitationHoursWeekend;

    @JsonProperty("Website_URL")
    String websiteUrl;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class AttributesBuilder {}

    /* Unused Fields:
     *   @JsonProperty("OBJECTID")
     *   String objectId;
     *   @JsonProperty("SHORT_NAME")
     *   String shortName;
     *   @JsonProperty("CEMETERY_I")
     *   String cemeteryI;
     *   @JsonProperty("SITE_SQFT")
     *   BigDecimal siteSqft;
     *   @JsonProperty("created_user")
     *   String createdUser;
     *   @JsonProperty("GlobalID")
     *   String globalId;
     *   @JsonProperty("created_date")
     *   Date createdDate;
     *   @JsonProperty("last_edited_user")
     *   String lastEditedUser;
     *   @JsonProperty("last_edited_date")
     *   Date lastEditedDate;
     *   @JsonProperty("COMMENT")
     *   String comment;
     *   @JsonProperty("ACTIVE")
     *   Integer active;
     *   @JsonProperty("DISTRICT")
     *   String district;
     *   @JsonProperty("POSITION_SRC")
     *   String positionSrc;
     *   @JsonProperty("GOVERNING_SITE_ID")
     *   Integer governingSiteId;
     *   @JsonProperty("OFFICE_HOURS_WEEKDAY")
     *   String officeHoursWeekday;
     *   @JsonProperty("OFFICE_HOURS_WEEKEND")
     *   String officeHoursWeekend;
     *   @JsonProperty("OFFICE_HOURS_COMMENT")
     *   String officeHoursComment;
     *   @JsonProperty("LATITUDE_DD")
     *   BigDecimal latitudeDd;
     *   @JsonProperty("LONGITUDE_DD")
     *   BigDecimal longitudeDd;
     *   @JsonProperty("VISITATION_HOURS_COMMENT")
     *   String visitation;
     *   @JsonProperty("SITE_STATUS")
     *   String siteStatus;
     *   @JsonProperty("SITE_OWNER")
     *   String siteOwner;
     *   @JsonProperty("SITE_COUNTRY")
     *   String siteCountry;
     *   @JsonProperty("MAIL_COUNTRY")
     *   String mailCountry;
     */
  }

  @Value
  @Builder
  static final class Feature {
    Attributes attributes;

    Geometry geometry;
  }

  @Value
  @Builder
  static final class Geometry {
    @JsonProperty("x")
    BigDecimal longitude;

    @JsonProperty("y")
    BigDecimal latitude;
  }
}
