package gov.va.api.lighthouse.facilities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
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
@Table(name = "drive_time_band", schema = "app")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriveTimeBandEntity {

  /**
   * A band is unique to a facility and a drive-time range (or band), e.g. 10-20 minutes. A given
   * facility can have multiple bands around it.
   */
  @EqualsAndHashCode.Include @EmbeddedId private Pk id;

  /** aka y1. */
  @Column(name = "min_latitude")
  private double minLatitude;

  /** aka x1. */
  @Column(name = "min_longitude")
  private double minLongitude;

  /** aka y2. */
  @Column(name = "max_latitude")
  private double maxLatitude;

  /** aka x2. */
  @Column(name = "max_longitude")
  private double maxLongitude;

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @Column
  private String band;

  @Version private Integer version;

  @Data
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(staticName = "of")
  @Embeddable
  public static class Pk implements Serializable {
    @Column(name = "station_number", nullable = false)
    private String stationNumber;

    @Column(name = "from_minutes", nullable = false)
    private int fromMinutes;

    @Column(name = "to_minutes", nullable = false)
    private int toMinutes;
  }
}
