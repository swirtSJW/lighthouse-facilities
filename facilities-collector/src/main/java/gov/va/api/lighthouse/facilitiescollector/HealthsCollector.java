package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.commons.lang3.StringUtils.upperCase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Builder
final class HealthsCollector {
  @NonNull final String atcBaseUrl;

  @NonNull final String atpBaseUrl;

  @NonNull final JdbcTemplate jdbcTemplate;

  @NonNull final RestTemplate insecureRestTemplate;

  @NonNull final String vaArcGisBaseUrl;

  @NonNull final Map<String, String> websites;

  @SneakyThrows
  static void putMentalHealthContact(ResultSet resultSet, Map<String, String> map) {
    String stationNumber = resultSet.getString("StationNumber");
    if (isBlank(stationNumber)) {
      log.warn("Mental health contact missing station number");
      return;
    }
    String phone = trimToEmpty(resultSet.getString("MHPhone"));
    if (isBlank(phone)) {
      log.warn(
          "Mental health contact for station number {} missing phone", sanitize(stationNumber));
      return;
    }
    String ext = trimToEmpty(resultSet.getString("Extension"));
    if (ext.indexOf(".") >= 0) {
      ext = ext.substring(0, ext.indexOf("."));
    }
    if (isNotBlank(ext) && !equalsIgnoreCase(ext, "null") && !ext.matches("^[0]+$")) {
      phone = phone + " x " + ext;
    }
    String id = "vha_" + stationNumber;
    if (map.containsKey(id)) {
      log.warn("Duplicate mental health contact for station number {}", sanitize(stationNumber));
    }
    map.put(id, phone);
  }

  @SneakyThrows
  static void putStopCode(ResultSet resultSet, ListMultimap<String, StopCode> map) {
    String stationNumber = trimToNull(resultSet.getString("Sta6a"));
    if (stationNumber == null) {
      log.warn("Stop code missing station number");
      return;
    }
    String code = trimToNull(resultSet.getString("PrimaryStopCode"));
    if (code == null) {
      log.warn("Stop code for station number {} missing PrimaryStopCode", sanitize(stationNumber));
      return;
    }
    String name = trimToNull(resultSet.getString("PrimaryStopCodeName"));
    String wait = trimToNull(resultSet.getString("AvgWaitTimeNew"));
    BigDecimal waitNum = null;
    try {
      waitNum = wait == null ? null : new BigDecimal(wait);
    } catch (NumberFormatException nfe) {
      log.warn(
          "Stop code for station number {} has invalid AvgWaitTimeNew '{}'",
          sanitize(stationNumber),
          sanitize(wait));
      return;
    }
    map.put(
        upperCase("vha_" + stationNumber, Locale.US),
        StopCode.builder()
            .stationNum(stationNumber)
            .code(code)
            .name(name)
            .waitTimeNew(waitNum)
            .build());
  }

  Collection<Facility> healths() {
    ListMultimap<String, AccessToCareEntry> accessToCareEntries = loadAccessToCare();
    ListMultimap<String, AccessToPwtEntry> accessToPwtEntries = loadAccessToPwt();
    Map<String, String> mentalHealthPhoneNumbers = loadMentalHealthPhoneNumbers();
    ListMultimap<String, StopCode> stopCodesMap = loadStopCodes();
    return loadArcGis().features().stream()
        .filter(Objects::nonNull)
        .map(
            gis ->
                HealthTransformer.builder()
                    .gis(gis)
                    .accessToCare(accessToCareEntries)
                    .accessToPwt(accessToPwtEntries)
                    .mentalHealthPhoneNumbers(mentalHealthPhoneNumbers)
                    .stopCodesMap(stopCodesMap)
                    .websites(websites)
                    .build()
                    .toFacility())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @SneakyThrows
  private ListMultimap<String, AccessToCareEntry> loadAccessToCare() {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    String url =
        UriComponentsBuilder.fromHttpUrl(atcBaseUrl + "atcapis/v1.1/patientwaittimes")
            .build()
            .toUriString();
    String response =
        insecureRestTemplate
            .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
            .getBody();
    List<AccessToCareEntry> entries =
        JacksonConfig.createMapper()
            .readValue(response, new TypeReference<List<AccessToCareEntry>>() {});
    ListMultimap<String, AccessToCareEntry> map = ArrayListMultimap.create();
    for (int i = 0; i < entries.size(); i++) {
      AccessToCareEntry entry = entries.get(i);
      if (entry.facilityId() == null) {
        log.warn("AccessToCare entry has null facility ID");
        continue;
      }
      map.put(upperCase("vha_" + entry.facilityId(), Locale.US), entry);
    }
    log.info(
        "Loading AccessToCare took {} millis", totalWatch.stop().elapsed(TimeUnit.MILLISECONDS));
    return ImmutableListMultimap.copyOf(map);
  }

  @SneakyThrows
  private ListMultimap<String, AccessToPwtEntry> loadAccessToPwt() {
    Stopwatch watch = Stopwatch.createStarted();
    String url =
        UriComponentsBuilder.fromHttpUrl(atpBaseUrl + "Shep/getRawData")
            .queryParam("location", "*")
            .build()
            .toUriString();
    String response =
        insecureRestTemplate
            .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
            .getBody();
    List<AccessToPwtEntry> entries =
        JacksonConfig.createMapper()
            .readValue(response, new TypeReference<List<AccessToPwtEntry>>() {});
    ListMultimap<String, AccessToPwtEntry> map = ArrayListMultimap.create();
    for (AccessToPwtEntry entry : entries) {
      if (entry.facilityId() == null) {
        log.warn("AccessToPwt entry has null facility ID");
        continue;
      }
      map.put(upperCase("vha_" + entry.facilityId(), Locale.US), entry);
    }
    log.info("Loading AccessToPWT took {} millis", watch.stop().elapsed(TimeUnit.MILLISECONDS));
    return ImmutableListMultimap.copyOf(map);
  }

  @SneakyThrows
  private ArcGisHealths loadArcGis() {
    Stopwatch watch = Stopwatch.createStarted();
    String url =
        UriComponentsBuilder.fromHttpUrl(
                vaArcGisBaseUrl
                    + "server/rest/services/VA/FacilitySitePoint_VHA/FeatureServer/0/query")
            .queryParam("f", "json")
            .queryParam("inSR", "4326")
            .queryParam("outSR", "4326")
            .queryParam("orderByFields", "Sta_No")
            .queryParam("outFields", "*")
            .queryParam("resultOffset", "0")
            .queryParam("returnCountOnly", "false")
            .queryParam("returnDistinctValues", "false")
            .queryParam("returnGeometry", "true")
            .queryParam("where", "s_abbr!='VTCR' AND s_abbr!='MVCTR'")
            .build()
            .toUriString();
    String response =
        insecureRestTemplate
            .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
            .getBody();
    ArcGisHealths result = JacksonConfig.createMapper().readValue(response, ArcGisHealths.class);
    log.info("Loading VA ArcGIS took {} millis", watch.stop().elapsed(TimeUnit.MILLISECONDS));
    return result;
  }

  private Map<String, String> loadMentalHealthPhoneNumbers() {
    Stopwatch watch = Stopwatch.createStarted();
    Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    jdbcTemplate.query(
        "SELECT StationNumber, MHPhone, Extension FROM App.VHA_Mental_Health_Contact_Info",
        (RowCallbackHandler) (rs) -> putMentalHealthContact(rs, map));
    log.info(
        "Loading mental health contact took {} millis",
        watch.stop().elapsed(TimeUnit.MILLISECONDS));
    return Collections.unmodifiableMap(map);
  }

  private ListMultimap<String, StopCode> loadStopCodes() {
    Stopwatch watch = Stopwatch.createStarted();
    ListMultimap<String, StopCode> map = ArrayListMultimap.create();
    jdbcTemplate.query(
        "SELECT Sta6a, PrimaryStopCode, PrimaryStopCodeName, AvgWaitTimeNew"
            + " FROM App.VHA_Stop_Code_Wait_Times_Paginated(1, 999999)",
        (RowCallbackHandler) (rs) -> putStopCode(rs, map));
    log.info("Loading stop codes took {} millis", watch.stop().elapsed(TimeUnit.MILLISECONDS));
    return ImmutableListMultimap.copyOf(map);
  }
}
