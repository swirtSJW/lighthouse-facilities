package gov.va.api.lighthouse.facilities.api.v0;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class PageLinks {
  String related;

  @NotNull String self;

  String first;

  String prev;

  String next;

  String last;
}
