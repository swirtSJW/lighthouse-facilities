package gov.va.api.lighthouse.facilities.api;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilityPair {
  Facility v0;
  gov.va.api.lighthouse.facilities.api.v1.Facility v1;
}
