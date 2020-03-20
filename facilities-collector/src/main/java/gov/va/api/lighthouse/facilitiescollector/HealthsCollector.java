package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.commons.lang3.StringUtils.upperCase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
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

  @NonNull final RestTemplate restTemplate;

  @NonNull final String vaArcGisBaseUrl;

  @NonNull final Map<String, String> websites;

  @SneakyThrows
  private static Set<String> loadDentalServiceFacilityIds() {
    try (InputStreamReader reader =
        new InputStreamReader(
            new ClassPathResource("dental-services.txt").getInputStream(), "UTF-8")) {
      List<String> list =
          Splitter.onPattern("\\s")
              .splitToStream(CharStreams.toString(reader))
              .map(id -> trimToNull(id))
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
      Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
      set.addAll(list);
      return Collections.unmodifiableSet(set);
    }
  }

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
    if (isNotBlank(ext) && !equalsIgnoreCase(ext, "null") && !ext.matches("^[0]+$")) {
      phone = phone + " x " + ext;
    }

    String id = "vha_" + stationNumber;
    if (map.containsKey(id)) {
      log.warn("Duplicate mental health contact for station number {}", sanitize(stationNumber));
    }

    map.put(id, phone);
  }

  Collection<Facility> healths() {
    ListMultimap<String, AccessToCareEntry> accessToCareEntries = loadAccessToCare();
    ListMultimap<String, AccessToPwtEntry> accessToPwtEntries = loadAccessToPwt();
    Set<String> dentalServiceFacilityIds = loadDentalServiceFacilityIds();
    Map<String, String> mentalHealthPhoneNumbers = loadMentalHealthPhoneNumbers();
    return loadArcGis().features().stream()
        .filter(Objects::nonNull)
        .map(
            gis ->
                HealthTransformer.builder()
                    .gis(gis)
                    .accessToCare(accessToCareEntries)
                    .accessToPwt(accessToPwtEntries)
                    .dentalServiceFacilityIds(dentalServiceFacilityIds)
                    .mentalHealthPhoneNumbers(mentalHealthPhoneNumbers)
                    .websites(websites)
                    .build()
                    .toFacility())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @SneakyThrows
  private ListMultimap<String, AccessToCareEntry> loadAccessToCare() {
    List<AccessToCareEntry> entries =
        JacksonConfig.createMapper()
            .readValue(
                new URL(atcBaseUrl + "atcapis/v1.1/patientwaittimes"),
                new TypeReference<List<AccessToCareEntry>>() {});

    ListMultimap<String, AccessToCareEntry> map = ArrayListMultimap.create();
    for (AccessToCareEntry entry : entries) {
      if (entry.facilityId() == null) {
        log.warn("AccessToCare entry has null facility ID");
        continue;
      }
      map.put(upperCase("vha_" + entry.facilityId(), Locale.US), entry);
    }
    return ImmutableListMultimap.copyOf(map);
  }

  @SneakyThrows
  private ListMultimap<String, AccessToPwtEntry> loadAccessToPwt() {
    String url =
        UriComponentsBuilder.fromHttpUrl(atpBaseUrl + "Shep/getRawData")
            .queryParam("location", "*")
            .build()
            .toUriString();

    List<AccessToPwtEntry> entries =
        restTemplate
            .exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<AccessToPwtEntry>>() {})
            .getBody();

    ListMultimap<String, AccessToPwtEntry> map = ArrayListMultimap.create();
    for (AccessToPwtEntry entry : Optional.ofNullable(entries).orElse(emptyList())) {
      if (entry.facilityId() == null) {
        log.warn("AccessToPwt entry has null facility ID");
        continue;
      }
      map.put(upperCase("vha_" + entry.facilityId(), Locale.US), entry);
    }
    return ImmutableListMultimap.copyOf(map);
  }

  @SneakyThrows
  private ArcGisHealths loadArcGis() {
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
    return restTemplate
        .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), ArcGisHealths.class)
        .getBody();
  }

  @SneakyThrows
  private Map<String, String> loadMentalHealthPhoneNumbers() {
    Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    jdbcTemplate.query(
        "SELECT StationNumber, MHPhone, Extension FROM App.VHA_Mental_Health_Contact_Info",
        (RowCallbackHandler) (rs) -> putMentalHealthContact(rs, map));
    return Collections.unmodifiableMap(map);
  }
}
