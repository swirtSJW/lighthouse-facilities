package gov.va.api.lighthouse.facilities.api.cms;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CmsOverlayResponse {
  @Builder.Default CmsOverlay overlay = null;
}
