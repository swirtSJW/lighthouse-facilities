package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TimeZoneMapTest {
  @Test
  void invalidLatitudeLongitude() {
    String invalidLatitudeZone =
        TimeZoneFinder.calculateTimeZonesWithMap(null, BigDecimal.valueOf(149.915261), "vba_041");
    String invalidLongitudeZone =
        TimeZoneFinder.calculateTimeZonesWithMap(BigDecimal.valueOf(36.737343), null, "vba_041");
    assertThat(invalidLatitudeZone).isNull();
    assertThat(invalidLongitudeZone).isNull();
  }

  @Test
  void timezoneContinentalUSA() {
    String anchorageZone =
        TimeZoneFinder.calculateTimeZonesWithMap(
            BigDecimal.valueOf(61.2181), BigDecimal.valueOf(-149.915261), "vba_041");
    String fresnoZone =
        TimeZoneFinder.calculateTimeZonesWithMap(
            BigDecimal.valueOf(36.737343), BigDecimal.valueOf(-119.771027), "vba_041");
    String saltLakeCityZone =
        TimeZoneFinder.calculateTimeZonesWithMap(
            BigDecimal.valueOf(40.724780), BigDecimal.valueOf(-111.896381), "vba_041");
    String newOrleansZone =
        TimeZoneFinder.calculateTimeZonesWithMap(
            BigDecimal.valueOf(29.953269), BigDecimal.valueOf(-90.093790), "vba_041");
    String bangorZone =
        TimeZoneFinder.calculateTimeZonesWithMap(
            BigDecimal.valueOf(44.801035), BigDecimal.valueOf(-68.781614), "vba_041");
    assertThat(anchorageZone).isEqualTo("America/Anchorage");
    assertThat(fresnoZone).isEqualTo("America/Los_Angeles");
    assertThat(saltLakeCityZone).isEqualTo("America/Denver");
    assertThat(newOrleansZone).isEqualTo("America/Chicago");
    assertThat(bangorZone).isEqualTo("America/New_York");
  }
}
