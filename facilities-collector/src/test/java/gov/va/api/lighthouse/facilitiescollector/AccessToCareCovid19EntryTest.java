package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import lombok.SneakyThrows;
import org.junit.Test;

public class AccessToCareCovid19EntryTest {
  @Test
  public void badFacility() {
    AccessToCareCovid19Entry expected =
        AccessToCareCovid19Entry.builder().confirmedCases(66).deaths(6).build();
    assertThat(serializeCovidAtcEntry(buildCovidAtcEntryString("null", "\"66\"", "\"6\"")))
        .isEqualTo(expected);
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"666 The Sixth Circle\"", "\"66\"", "\"6\"")))
        .isEqualTo(expected);
  }

  private String buildCovidAtcEntryString(String facility, String confirmed, String deaths) {
    return buildCovidAtcEntryString(facility, confirmed, deaths, null);
  }

  private String buildCovidAtcEntryString(
      String facility, String confirmed, String deaths, String extra) {
    StringBuilder covid = new StringBuilder();
    covid.append("{");
    covid.append("\"Facility Name\": ").append(facility).append(",");
    covid.append("\"VA Confirmed\": ").append(confirmed).append(",");
    covid.append("\"VA Deaths\": ").append(deaths);
    if (extra != null) {
      covid.append(",").append(extra);
    }
    covid.append("}");
    return covid.toString();
  }

  @Test
  public void confirmedCases() {
    AccessToCareCovid19Entry expected =
        AccessToCareCovid19Entry.builder().stationId("666").deaths(6).build();

    // Null
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "null", "\"6\"")))
        .isEqualTo(expected);
    // String
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "\"Sixty-Six\"", "\"6\"")))
        .isEqualTo(expected);
    // Double
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "66.6", "\"6\"")))
        .isEqualTo(expected);
    // Long
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "2147483648", "\"6\"")))
        .isEqualTo(expected);
  }

  @Test
  public void deaths() {
    AccessToCareCovid19Entry expected =
        AccessToCareCovid19Entry.builder().stationId("666").confirmedCases(66).build();

    // Null
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "\"66\"", "null")))
        .isEqualTo(expected);
    // String
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "\"66\"", "\"Six\"")))
        .isEqualTo(expected);
    // Double
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "\"66\"", "6.66")))
        .isEqualTo(expected);
    // Long
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "\"66\"", "2147483648")))
        .isEqualTo(expected);
  }

  @Test
  public void extraFieldsHaveNoEffect() {
    // Good Response
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString(
                    "\"(666) The Sixth Circle\"",
                    "\"66\"",
                    "\"6\"",
                    "\"Location\": \"The Sixth Circle\"")))
        .isEqualTo(
            AccessToCareCovid19Entry.builder()
                .stationId("666")
                .confirmedCases(66)
                .deaths(6)
                .build());
    // Bad Response
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString(
                    "\"666 The Sixth Circle\"",
                    "\"66\"",
                    "\"6\"",
                    "\"Location\": \"The Sixth Circle\"")))
        .isEqualTo(AccessToCareCovid19Entry.builder().confirmedCases(66).deaths(6).build());
  }

  @SneakyThrows
  private AccessToCareCovid19Entry serializeCovidAtcEntry(String json) {
    return JacksonConfig.createMapper().readValue(json, AccessToCareCovid19Entry.class);
  }

  @Test
  public void validResponse() {
    // Number String
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "\"66\"", "\"6\"")))
        .isEqualTo(
            AccessToCareCovid19Entry.builder()
                .stationId("666")
                .confirmedCases(66)
                .deaths(6)
                .build());
    // Integers
    assertThat(
            serializeCovidAtcEntry(
                buildCovidAtcEntryString("\"(666) The Sixth Circle\"", "66", "6")))
        .isEqualTo(
            AccessToCareCovid19Entry.builder()
                .stationId("666")
                .confirmedCases(66)
                .deaths(6)
                .build());
  }
}
