package gov.va.api.lighthouse.facilities.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilityPair {
  gov.va.api.lighthouse.facilities.api.v0.Facility v0;
  gov.va.api.lighthouse.facilities.api.v1.Facility v1;
}
