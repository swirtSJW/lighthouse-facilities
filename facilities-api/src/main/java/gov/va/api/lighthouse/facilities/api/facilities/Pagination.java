package gov.va.api.lighthouse.facilities.api.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Pagination {
  @JsonProperty("current_page")
  @NotNull
  Integer currentPage;

  @JsonProperty("per_page")
  @NotNull
  Integer entriesPerPage;

  @JsonProperty("total_entries")
  @NotNull
  Integer totalEntries;

  @JsonProperty("total_pages")
  @NotNull
  Integer totalPages;
}
