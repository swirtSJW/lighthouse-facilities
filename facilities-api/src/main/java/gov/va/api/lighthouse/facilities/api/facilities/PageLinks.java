package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PageLinks {

  String related;

  String self;

  String first;

  String prev;

  String next;

  String last;
}
