package gov.va.api.lighthouse.facilities.api.v0;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CmsOverlayResponse {
  @Builder.Default CmsOverlay overlay = null;
}
