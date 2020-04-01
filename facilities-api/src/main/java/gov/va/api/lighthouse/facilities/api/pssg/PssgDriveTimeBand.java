package gov.va.api.lighthouse.facilities.api.pssg;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PssgDriveTimeBand {
  Attributes attributes;

  Geometry geometry;

  /** Create a coordinate list where index 0 is longitude, and index 1 is latitude. */
  public static List<Double> coord(double longitudeOrX, double latitudeOrY) {
    return List.of(longitudeOrX, latitudeOrY);
  }

  /** Create a new, empty list of rings, each ring will have coordinate data. */
  public static List<List<List<Double>>> newListOfRings() {
    return new ArrayList<>();
  }

  /** Create a new, empty list of ring coordinate data. */
  public static List<List<Double>> newRing(int size) {
    return new ArrayList<>(size);
  }

  /**
   * The PSSG response actually contains more fields, but we don't use them and have not been
   * mapped.
   */
  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Attributes {
    @JsonProperty("Sta_No")
    String stationNumber;

    @JsonProperty("FromBreak")
    int fromBreak;

    @JsonProperty("ToBreak")
    int toBreak;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Geometry {
    /**
     * Oh lawdy.
     *
     * <pre>
     *   List 1 = List of rings. There should at least one ring.
     *   List 2 = A ring, which is a list of coordinates.
     *            There will be many coordinates to describe the polygon.
     *   List 3 = A two item list of longitude, then latitude.
     * </pre>
     *
     * yo dawg. i herd you like lists so we put a list in yo list so you list lists while you list
     * lists.
     */
    @Builder.Default List<List<List<Double>>> rings = new ArrayList<>();
  }
}
