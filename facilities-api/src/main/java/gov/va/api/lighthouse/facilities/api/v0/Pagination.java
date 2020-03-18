package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Pagination {
  @NotNull
  @JsonProperty("current_page")
  Integer currentPage;

  @NotNull
  @JsonProperty("per_page")
  Integer entriesPerPage;

  @NotNull
  @JsonProperty("total_pages")
  Integer totalPages;

  @NotNull
  @JsonProperty("total_entries")
  Integer totalEntries;
}
