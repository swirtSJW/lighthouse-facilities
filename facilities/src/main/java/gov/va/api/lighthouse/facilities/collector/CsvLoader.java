package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.google.common.base.Stopwatch;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@UtilityClass
public class CsvLoader {

  /** Load websites given a resource name. */
  @SneakyThrows
  public static Map<String, String> loadWebsites(String resourceName) {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    try (InputStreamReader reader =
        new InputStreamReader(
            new ClassPathResource(resourceName).getInputStream(), StandardCharsets.UTF_8)) {
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
      Map<String, String> websites = Collections.unmodifiableMap(map);
      log.info(
          "Loading websites took {} millis for {} entries",
          totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
          websites.size());
      checkState(!websites.isEmpty(), "No website entries");
      return websites;
    }
  }
}
