package gov.va.api.lighthouse.facilities.api.collector;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;

/**
 * This specialized API response provides a list of facilities, but represents a very expensive
 * operation. Normal consumers will not need invoke "collection". The normal "get all" operation
 * provides the same data, in a much more responsive manner.
 */
@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CollectorFacilitiesResponse {
  @Builder.Default List<Facility> facilities = new ArrayList<>();
}
