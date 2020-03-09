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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "VHA_Mental_Health_Contact_Info", schema = "App")
public class MentalHealthContactEntity {
  @Id
  @Column(name = "ID")
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "Region", nullable = true)
  private Double region;

  @Column(name = "VISN", nullable = true)
  private String visn;

  @Column(name = "AdminParent", nullable = true)
  private Double adminParent;

  @Column(name = "StationNumber", nullable = true)
  private String stationNumber;

  @Column(name = "MHClinicPhone", nullable = true)
  private Double mhClinicPhone;

  @Column(name = "MHPhone", nullable = true)
  private String mhPhone;

  @Column(name = "Extension", nullable = true)
  private Double extension;

  @Column(name = "OfficialStationName", nullable = true)
  private String officialStationName;

  @Column(name = "POC Email", nullable = true)
  private String pocEmail;

  @Column(name = "Status", nullable = true)
  private String status;

  @Column(name = "Modified", nullable = true)
  private String modified;

  @Column(name = "Created", nullable = true)
  private String created;

  @Column(name = "AddedToOutbox", nullable = true)
  private String addedToOutbox;
}
