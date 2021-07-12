package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FacilityUtils {
  /** Unitless distance approximation based on geometric distance formula. For sorting only. */
  static double distance(@NonNull FacilityEntity entity, double lng, double lat) {
    double lngDiff = entity.longitude() - lng;
    double latDiff = entity.latitude() - lat;
    return Math.sqrt(lngDiff * lngDiff + latDiff * latDiff);
  }

  static List<FacilityEntity.Pk> entityIds(String ids) {
    if (ids == null) {
      return emptyList();
    }
    return Splitter.on(",")
        .splitToStream(ids)
        .map(id -> trimToNull(id))
        .filter(Objects::nonNull)
        .distinct()
        .map(id -> FacilityEntity.Pk.optionalFromIdString(id).orElse(null))
        .filter(Objects::nonNull)
        .collect(toList());
  }

  /** Distance in miles using Haversine algorithm. */
  static double haversine(@NonNull FacilityEntity entity, double lng, double lat) {
    double lon1 = Math.toRadians(entity.longitude());
    double lat1 = Math.toRadians(entity.latitude());
    double lon2 = Math.toRadians(lng);
    double lat2 = Math.toRadians(lat);
    double lonDiff = lon2 - lon1;
    double latDiff = lat2 - lat1;
    double x = Math.sin(latDiff / 2);
    double y = Math.sin(lonDiff / 2);
    double coeff = Math.cos(lat1) * Math.cos(lat2);
    // 3958.8 is Earth radius in miles
    return 3958.8 * 2 * Math.asin(Math.sqrt(x * x + coeff * y * y));
  }

  @SneakyThrows
  static void writeNonNull(JsonGenerator jgen, String fieldName, Object value) {
    if (value != null) {
      jgen.writeObjectField(fieldName, value);
    }
  }
}
