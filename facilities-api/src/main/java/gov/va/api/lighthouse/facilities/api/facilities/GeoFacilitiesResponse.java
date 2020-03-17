package gov.va.api.lighthouse.facilities.api.facilities;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GeoFacilitiesResponse {
  Type type;

  List<GeoFacility> features;

  public enum Type {
    FeatureCollection
  }
}
