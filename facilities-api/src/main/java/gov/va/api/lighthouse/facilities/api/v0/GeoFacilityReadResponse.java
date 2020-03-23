package gov.va.api.lighthouse.facilities.api.v0;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeoFacilityReadResponse {
  @NotNull GeoFacility.Type type;

  @Valid @NotNull GeoFacility.Geometry geometry;

  @Valid @NotNull GeoFacility.Properties properties;
}
