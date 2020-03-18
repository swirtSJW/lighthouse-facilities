package gov.va.api.lighthouse.facilities;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NoOpResponse {
  String message;
}
