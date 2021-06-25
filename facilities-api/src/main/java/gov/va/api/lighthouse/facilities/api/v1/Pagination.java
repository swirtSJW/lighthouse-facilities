package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class Pagination {
  @NotNull
  @Schema(example = "1")
  @JsonProperty("current_page")
  Integer currentPage;

  @NotNull
  @Schema(example = "10")
  @JsonProperty("per_page")
  Integer entriesPerPage;

  @NotNull
  @Schema(example = "217")
  @JsonProperty("total_pages")
  Integer totalPages;

  @NotNull
  @Schema(example = "2162")
  @JsonProperty("total_entries")
  Integer totalEntries;
}
