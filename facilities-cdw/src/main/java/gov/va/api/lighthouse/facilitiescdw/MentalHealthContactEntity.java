package gov.va.api.lighthouse.facilitiescdw;

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

@Data
@Entity
@Builder
@Table(name = "VHA_Mental_Health_Contact_Info", schema = "App")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MentalHealthContactEntity {
  @Id
  @Column(name = "ID")
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "Region", nullable = true)
  private String region;

  @Column(name = "VISN", nullable = true)
  private String visn;

  @Column(name = "AdminParent", nullable = true)
  private String adminParent;

  @Column(name = "StationNumber", nullable = true)
  private String stationNumber;

  @Column(name = "MHClinicPhone", nullable = true)
  private String mhClinicPhone;

  @Column(name = "MHPhone", nullable = true)
  private String mhPhone;

  @Column(name = "Extension", nullable = true)
  private String extension;

  @Column(name = "OfficialStationName", nullable = true)
  private String officialStationName;

  @Column(name = "Email", nullable = true)
  private String email;

  @Column(name = "Status", nullable = true)
  private String status;

  @Column(name = "Modified", nullable = true)
  private String modified;

  @Column(name = "Created", nullable = true)
  private String created;

  @Column(name = "AddedToOutbox", nullable = true)
  private String addedToOutbox;
}
