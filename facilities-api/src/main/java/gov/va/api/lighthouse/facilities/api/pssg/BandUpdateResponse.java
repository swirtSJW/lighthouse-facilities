package gov.va.api.lighthouse.facilities.api.pssg;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Attributes;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Geometry;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class BandUpdateResponse {
  private List<String> bandsCreated;

  private List<String> bandsUpdated;
}
