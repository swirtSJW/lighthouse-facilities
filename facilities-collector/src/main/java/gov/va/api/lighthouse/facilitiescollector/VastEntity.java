package gov.va.api.lighthouse.facilitiescollector;

import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

@Data
@Entity
@Builder
@Table(name = "vast", schema = "app")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VastEntity {
  @Id @EqualsAndHashCode.Include private Long vastId;

  @Column(name = "VCTR2")
  private Boolean vetCenter;

  @Column(name = "MVCTR")
  private Boolean mobileVetCenter;

  @Column(name = "LAT")
  private BigDecimal latitude;

  @Column(name = "LON")
  private BigDecimal longitude;

  @Column(name = "STA_NO")
  private String stationNumber;

  private String stationName;

  @Column(name = "S_ABBR")
  private String abbreviation;

  private String cocClassificationId;

  private String address1;

  private String address2;

  private String address3;

  private String city;

  @Column(name = "ST")
  private String state;

  private String zip;

  private String zip4;

  private String monday;

  private String tuesday;

  private String wednesday;

  private String thursday;

  private String friday;

  private String saturday;

  private String sunday;

  @Column(name = "STA_PHONE")
  private String staPhone;

  @Column(name = "STA_FAX")
  private String staFax;

  private String afterHoursPhone;

  private String patientAdvocatePhone;

  private String enrollmentCoordinatorPhone;

  private String pharmacyPhone;

  private String pod;

  private Boolean mobile;

  private String visn;

  @Column(name = "LastUpdated")
  private Instant lastUpdated;

  boolean isVetCenter() {
    return BooleanUtils.isTrue(vetCenter()) || BooleanUtils.isTrue(mobileVetCenter());
  }

  // Unused fields:
  // ExtractDate
  // GeoExtractDate
  // D_VISN
  // StationNo
  // CategoryID
  // CD
  // CNAME
  // CONT
  // District_VISN
  // EXTRDATE
  // FIPS
  // LOC_NAME
  // MARKET
  // NewVISN
  // OldVISN
  // OPERA_DATE
  // PAR_STA_NO
  // ParentStationCL
  // PROV
  // SCORE
  // SECTOR
  // SHAR
  // STA_NAME_Official
  // STATE_FIPS
  // SUBMARKET
  // SUSPENDED
  // UNIQUESTAT
  // URH
  // VA_SITE
  // OperationalHoursSpecialInstructions
  // CLC
  // District
  // DistrictName
  // DRRTP
  // HCC
  // MCS
  // MSCBOC
  // NewVAMC
  // OOS
  // OutPatientRating
  // PCCBOC
  // S_CLASS_NAME
  // VAH
  // SH_PAR_STA
}
