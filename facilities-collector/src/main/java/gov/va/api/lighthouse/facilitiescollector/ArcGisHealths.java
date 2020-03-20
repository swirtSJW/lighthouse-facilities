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
final class ArcGisHealths {
  @Builder.Default List<Feature> features = new ArrayList<>();

  @JsonIgnoreProperties(ignoreUnknown = true)
  static final class ArcGisHealthsBuilder {}

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class Attributes {
    @JsonProperty("Sta_No")
    String stationNum;

    @JsonProperty("NAME")
    String name;

    @JsonProperty("FEATURECODE")
    String featureCode;

    @JsonProperty("CocClassificationID")
    String cocClassificationId;

    @JsonProperty("Address1")
    String address1;

    @JsonProperty("Address2")
    String address2;

    @JsonProperty("Address3")
    String address3;

    @JsonProperty("MUNICIPALITY")
    String municipality;

    @JsonProperty("STATE")
    String state;

    @JsonProperty("zip")
    String zip;

    @JsonProperty("Zip4")
    String zip4;

    @JsonProperty("Monday")
    String monday;

    @JsonProperty("Tuesday")
    String tuesday;

    @JsonProperty("Wednesday")
    String wednesday;

    @JsonProperty("Thursday")
    String thursday;

    @JsonProperty("Friday")
    String friday;

    @JsonProperty("Saturday")
    String saturday;

    @JsonProperty("Sunday")
    String sunday;

    @JsonProperty("Sta_Phone")
    String staPhone;

    @JsonProperty("Sta_Fax")
    String staFax;

    @JsonProperty("afterhoursphone")
    String afterHoursPhone;

    @JsonProperty("patientadvocatephone")
    String patientAdvocatePhone;

    @JsonProperty("enrollmentcoordinatorphone")
    String enrollmentCoordinatorPhone;

    @JsonProperty("pharmacyphone")
    String pharmacyPhone;

    @JsonProperty("Pod")
    String pod;

    @JsonProperty("Mobile")
    Integer mobile;

    @JsonProperty("Visn")
    String visn;

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
  // globalIdFieldName
  // geometryType
  // spatialReference(wkid, latestWkid)
  // fields(name, type, alias, length)
  // Unused attributes:
  // CAPTUREMETH
  // CategoryID
  // Cd
  // Clc
  // Cname
  // Cont
  // created_date
  // created_user
  // DESCRIPT
  // District
  // districtname
  // district_visn
  // drrtp
  // D_Visn
  // extractdate
  // ExtrDate
  // FACAREA
  // FACILITYID
  // FACILITYID_Source
  // FIPS
  // FULLADDR
  // geoextractdate
  // Hcc
  // LASTEDITOR
  // LASTUPDATE
  // LOCATIONTYPE
  // Loc_Name
  // Market
  // Mcs
  // Mscboc
  // Mvctr
  // NewVamc
  // NewVisn
  // OBJECTID
  // OldVisn
  // Oos
  // operationalhoursspecialinstruct
  // Opera_Date
  // OutpatientRating
  // OWNER
  // OWNTYPE
  // parentstationcl
  // Par_sta_no
  // Pccboc
  // Prov
  // Score
  // Sector
  // Shar
  // Sh_Par_Sta
  // State_Fips
  // Submarket
  // SUBTYPEFIELD
  // Suspended
  // S_Abbr
  // S_Class_name
  // UniqueStat
  // Urh
  // Vah
  // VastID
  // Va_Site
  // Vctr2
}
