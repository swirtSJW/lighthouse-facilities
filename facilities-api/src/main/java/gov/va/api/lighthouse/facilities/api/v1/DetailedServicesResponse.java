package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServicesMetadataSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServicesResponseSerializer;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = DetailedServicesResponseSerializer.class)
public class DetailedServicesResponse implements CanBeEmpty {
  List<@Valid DetailedService> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull DetailedServicesMetadata meta;

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return ObjectUtils.isEmpty(data())
        && (links() == null || links().isEmpty())
        && (meta() == null || meta().isEmpty());
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = DetailedServicesMetadataSerializer.class)
  public static final class DetailedServicesMetadata implements CanBeEmpty {
    @Valid @NotNull Pagination pagination;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return pagination() == null || pagination().isEmpty();
    }
  }
}
