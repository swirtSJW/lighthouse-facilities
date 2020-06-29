package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
final class GraveyardResponse {
  @Builder.Default List<Item> facilities = new ArrayList<>();

  @Value
  @Builder
  static final class Item {
    private Facility facility;

    private CmsOverlay cmsOverlay;

    private Instant missing;

    private Instant lastUpdated;
  }
}
