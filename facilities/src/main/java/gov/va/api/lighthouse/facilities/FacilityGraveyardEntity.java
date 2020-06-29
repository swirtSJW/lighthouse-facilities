package gov.va.api.lighthouse.facilities;

import java.time.Instant;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "facility_graveyard", schema = "app")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FacilityGraveyardEntity {
  @EqualsAndHashCode.Include @EmbeddedId private FacilityEntity.Pk id;

  @Version private Integer version;

  @Lob
  @Column
  @Basic(fetch = FetchType.EAGER)
  private String facility;

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @Column(name = "cms_overlay")
  private String cmsOverlay;

  @Column(name = "missing_timestamp")
  private Long missingTimestamp;

  @Column(name = "last_updated")
  private Instant lastUpdated;
}
