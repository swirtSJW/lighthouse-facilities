package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import lombok.Builder;

@Builder
public class FacilityPair {
  Facility v0;
  gov.va.api.lighthouse.facilities.api.v1.Facility v1;
}
