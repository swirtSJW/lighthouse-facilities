package gov.va.api.lighthouse.facilitiescollector;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mapping to collect all facility information and output it in application/json format for
 * easy parsing.
 */
@Validated
@RestController
@SuppressWarnings("WeakerAccess")
@RequestMapping(value = "/collect", produces = "application/json")
public class CollectController {
  private final String stateCemeteriesUrl;

  public CollectController(@Value("${state-cemeteries.url}") String stateCemeteriesUrl) {
    this.stateCemeteriesUrl =
        stateCemeteriesUrl.endsWith("/") ? stateCemeteriesUrl : stateCemeteriesUrl + "/";
  }

  @SneakyThrows
  private static Map<String, String> loadWebsites() {
    try (InputStreamReader reader =
        new InputStreamReader(new ClassPathResource("websites.csv").getInputStream(), "UTF-8")) {
      Iterable<CSVRecord> rows = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
      Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      for (CSVRecord row : rows) {
        String id = trimToNull(row.get("id"));
        String url = trimToNull(row.get("url"));
        checkState(id != null, "Website %s missing ID", url);
        checkState(url != null, "Website %s missing url", id);
        checkState(!map.containsKey(id), "Website %s duplicate", id);
        map.put(id, url);
      }
      return Collections.unmodifiableMap(map);
    }
  }

  /** Request Mapping for the /collect endpoint. */
  @SneakyThrows
  @GetMapping(value = "/facilities")
  public CollectorFacilitiesResponse collectFacilities() {
    Map<String, String> websites = loadWebsites();
    Collection<Facility> cems =
        StateCemeteriesCollector.builder()
            .stateCemeteriesUrl(stateCemeteriesUrl)
            .websites(websites)
            .build()
            .stateCemeteries();
    return CollectorFacilitiesResponse.builder()
        .facilities(
            cems.stream()
                .sorted((left, right) -> left.id().compareToIgnoreCase(right.id()))
                .collect(Collectors.toList()))
        .build();
  }
}
