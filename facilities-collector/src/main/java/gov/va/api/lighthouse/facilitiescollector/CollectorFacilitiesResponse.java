package gov.va.api.lighthouse.facilitiescollector;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CollectorFacilitiesResponse {
  @Builder.Default List<Facility> facilities = new ArrayList<>();
}
