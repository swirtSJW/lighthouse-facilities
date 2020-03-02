package gov.va.api.lighthouse.facilitiescdw;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQuery;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NamedNativeQuery(
    name = "allStopCodes",
    query = "SELECT * FROM App.VHA_Stop_Code_Wait_Times_Paginated(1, 100)",
    resultClass = StopCodeEntity.class)
public class StopCodeEntity {
  @EmbeddedId private StopCodeRow row;

  @Data
  @Builder
  @Embeddable
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class StopCodeRow implements Serializable {
    @Column(name = "DIVISION_FCDMD", nullable = true)
    private String divisionFcdmd;

    @Column(name = "CocClassification", nullable = true)
    private String cocClassification;

    @Column(name = "Sta6a", nullable = true)
    private String sta6a;

    @Column(name = "PrimaryStopCode", nullable = true)
    private String primaryStopCode;

    @Column(name = "PrimaryStopCodeName", nullable = true)
    private String primaryStopCodeName;

    @Column(name = "NumberOfAppointmentsLinkedToConsult", nullable = true)
    private String numberOfAppointmentsLinkedToConsult;

    @Column(name = "NumberOfLocations", nullable = true)
    private String numberOfLocations;

    @Column(name = "AvgWaitTimeNew", nullable = true)
    private String avgWaitTimeNew;
  }
}
