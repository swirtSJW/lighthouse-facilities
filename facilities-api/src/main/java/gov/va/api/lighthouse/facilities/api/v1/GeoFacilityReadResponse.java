package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.GeoFacilityReadResponseSerializer;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = GeoFacilityReadResponseSerializer.class)
public final class GeoFacilityReadResponse implements CanBeEmpty {
  @NotNull GeoFacility.Type type;

  @Valid @NotNull GeoFacility.Geometry geometry;

  @Valid @NotNull GeoFacility.Properties properties;

  /** Create GeoFacilityReadResponse with the same type, geometry, and properties. */
  public static GeoFacilityReadResponse of(@NonNull GeoFacility facility) {
    return GeoFacilityReadResponse.builder()
        .type(facility.type())
        .geometry(facility.geometry())
        .properties(facility.properties())
        .build();
  }

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return ObjectUtils.isEmpty(type())
        && (geometry() == null || geometry().isEmpty())
        && (properties() == null || properties().isEmpty());
  }
}
