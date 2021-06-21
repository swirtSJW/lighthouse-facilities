package gov.va.api.lighthouse.facilities.collector;

import java.math.BigDecimal;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import us.dustinj.timezonemap.TimeZone;
import us.dustinj.timezonemap.TimeZoneMap;

/**
 * The TimeZoneMap library works by: 1. Generating an 'area' from the SW to NE to calculate on, via
 * lat/long points (TimeZoneMap) 2. Figuring out the time zone from a lat/long point within the map
 * (TimeZone) 3. Translating this point to Olsen time (getZoneId) Alternatively the EVERYWHERE map
 * can be used, though it does take longer to initiate.
 */
@Slf4j
@UtilityClass
public class TimeZoneFinder {
  private static final TimeZoneMap WORLD_MAP = TimeZoneMap.forEverywhere();

  /** Calculate and load timezones given longitude and latitude passing in a map. */
  @SneakyThrows
  public static String calculateTimeZonesWithMap(
      BigDecimal latitude, BigDecimal longitude, String facilityId) {

    String timeZone = null;
    TimeZone timeZoneOverlap = null;

    if (longitude != null && latitude != null) {

      timeZoneOverlap =
          WORLD_MAP.getOverlappingTimeZone(latitude.doubleValue(), longitude.doubleValue());

      if (timeZoneOverlap != null) {
        timeZone = timeZoneOverlap.getZoneId();
      } else {
        log.warn("Time zone calculation failed, unable to calculate mapping for {}.", facilityId);
      }
    } else {
      log.warn(
          "Time zone calculation failed, latitude [{}] and/or longitude [{}] is null for {}",
          latitude,
          longitude,
          facilityId);
    }
    return timeZone;
  }
}
