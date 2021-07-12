package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
final class GraveyardResponse {
  @Builder.Default List<Item> facilities = new ArrayList<>();

  @Value
  @Builder
  static final class Item {
    private gov.va.api.lighthouse.facilities.api.v0.Facility facility;

    //    private gov.va.api.lighthouse.facilities.api.v1.Facility facilityV1;

    private CmsOverlay cmsOverlay;

    private Set<String> overlayServices;

    private Instant missing;

    private Instant lastUpdated;
  }
}
