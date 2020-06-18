package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.lighthouse.facilitiescollector.Transformers.isBlank;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessToCareCovid19Entry {
  String stationId;

  Integer confirmedCases;

  Integer deaths;

  /**
   * Lombok doesnt play well with the JsonAnySetter annotation, so a custom builder needs to exist
   * that deals with this.
   */
  public static class AccessToCareCovid19EntryBuilder {
    /** Takes a facility of format "(station-id) facility-name" and pulls out the station_id. */
    private String determineStationIdFromFacilityString(String facility) {
      if (facility == null) {
        return null;
      }
      try {
        return facility.substring(facility.indexOf("(") + 1, facility.indexOf(")"));
      } catch (IndexOutOfBoundsException e) {
        log.info("Failed to determine stationId from string value {}", facility);
        return null;
      }
    }

    /**
     * Converts a value from a string to an integer. If the string is an invalid integer, returns a
     * value of null.
     */
    private Integer parseInteger(String value) {
      if (value == null) {
        return null;
      }
      try {
        return Integer.valueOf(value);
      } catch (NumberFormatException e) {
        log.info("Failed to parse string value {} as Integer.", value);
        return null;
      }
    }

    /** Uses Jacksons JsonAnySetter annotation to map any unknown values to a map. */
    @JsonAnySetter
    public AccessToCareCovid19EntryBuilder saveUnknown(String key, String value) {
      if (containsIgnoreCase(key, "latitude") || containsIgnoreCase(key, "longitude")) {
        // Ignore Longitude and Latitude Nonsense
        return this;
      } else if (containsIgnoreCase(key, "confirmed")) {
        return confirmedCases(parseInteger(value));
      } else if (containsIgnoreCase(key, "deaths")) {
        return deaths(parseInteger(value));
      } else if (isBlank(stationId)) {
        // If we already know the stationId, we don't want to overwrite it.
        return stationId(determineStationIdFromFacilityString(value));
      }
      return this;
    }
  }
}
