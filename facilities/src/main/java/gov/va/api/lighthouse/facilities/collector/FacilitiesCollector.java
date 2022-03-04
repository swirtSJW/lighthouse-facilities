package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.lighthouse.facilities.collector.CsvLoader.loadWebsites;
import static java.util.stream.Collectors.toList;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Version agnostic facilities collector. */
@Slf4j
@Component
public class FacilitiesCollector {
  private static final String WEBSITES_CSV_RESOURCE_NAME = "websites.csv";

  private static final String CSC_STATIONS_RESOURCE_NAME = "csc_stations.txt";

  protected final InsecureRestTemplateProvider insecureRestTemplateProvider;

  protected final JdbcTemplate jdbcTemplate;

  protected final String atcBaseUrl;

  protected final String atpBaseUrl;

  protected final String cemeteriesBaseUrl;

  private final CmsOverlayCollector cmsOverlayCollector;

  /** Primary facilities collector constructor. */
  public FacilitiesCollector(
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Autowired JdbcTemplate jdbcTemplate,
      @Autowired CmsOverlayCollector cmsOverlayCollector,
      @Value("${access-to-care.url}") String atcBaseUrl,
      @Value("${access-to-pwt.url}") String atpBaseUrl,
      @Value("${cemeteries.url}") String cemeteriesBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.jdbcTemplate = jdbcTemplate;
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    this.atpBaseUrl = withTrailingSlash(atpBaseUrl);
    this.cemeteriesBaseUrl = withTrailingSlash(cemeteriesBaseUrl);
    this.cmsOverlayCollector = cmsOverlayCollector;
  }

  /** Caregiver support facilities given a resource name. */
  @SneakyThrows
  public static ArrayList<String> loadCaregiverSupport(String resourceName) {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    ArrayList<String> cscFacilities = new ArrayList<>();
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(
                new ClassPathResource(resourceName).getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        cscFacilities.add("vha_" + line);
      }
    }
    log.info(
        "Loading caregiver support facilities took {} millis for {} entries",
        totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
        cscFacilities.size());
    checkState(!cscFacilities.isEmpty(), "No caregiver support entries");
    return cscFacilities;
  }

  @SneakyThrows
  static VastEntity toVastEntity(ResultSet rs) {
    return VastEntity.builder()
        .vetCenter(rs.getBoolean("VCTR2"))
        .mobileVetCenter(rs.getBoolean("MVCTR"))
        .latitude(rs.getBigDecimal("LAT"))
        .longitude(rs.getBigDecimal("LON"))
        .stationNumber(rs.getString("STA_NO"))
        .stationName(rs.getString("STATIONNAME"))
        .abbreviation(rs.getString("S_ABBR"))
        .cocClassificationId(rs.getString("COCCLASSIFICATIONID"))
        .address1(rs.getString("ADDRESS1"))
        .address2(rs.getString("ADDRESS2"))
        .address3(rs.getString("ADDRESS3"))
        .city(rs.getString("CITY"))
        .state(rs.getString("ST"))
        .zip(rs.getString("zip"))
        .zip4(rs.getString("ZIP4"))
        .monday(rs.getString("MONDAY"))
        .tuesday(rs.getString("TUESDAY"))
        .wednesday(rs.getString("WEDNESDAY"))
        .thursday(rs.getString("THURSDAY"))
        .friday(rs.getString("FRIDAY"))
        .saturday(rs.getString("SATURDAY"))
        .sunday(rs.getString("SUNDAY"))
        .staPhone(rs.getString("STA_PHONE"))
        .staFax(rs.getString("STA_FAX"))
        .afterHoursPhone(rs.getString("AFTERHOURSPHONE"))
        .patientAdvocatePhone(rs.getString("PATIENTADVOCATEPHONE"))
        .enrollmentCoordinatorPhone(rs.getString("ENROLLMENTCOORDINATORPHONE"))
        .pharmacyPhone(rs.getString("PHARMACYPHONE"))
        .pod(rs.getString("POD"))
        .mobile(rs.getBoolean("MOBILE"))
        .visn(rs.getString("VISN"))
        .lastUpdated(
            Optional.ofNullable(rs.getTimestamp("LASTUPDATED"))
                .map(t -> t.toInstant())
                .orElse(null))
        .operationalHoursSpecialInstructions(rs.getString("OPERATIONALHOURSSPECIALINSTRUCTIONS"))
        .build();
  }

  static String withTrailingSlash(@NonNull String url) {
    return url.endsWith("/") ? url : url + "/";
  }

  /** Collect datamart facilities. */
  @SneakyThrows
  public List<DatamartFacility> collectFacilities() {
    Map<String, String> websites;
    Collection<VastEntity> vastEntities;
    ArrayList<String> cscFacilities;
    try {
      websites = loadWebsites(WEBSITES_CSV_RESOURCE_NAME);
      vastEntities = loadVast();
      cscFacilities = loadCaregiverSupport(CSC_STATIONS_RESOURCE_NAME);
    } catch (Exception e) {
      throw new CollectorExceptions.CollectorException(e);
    }
    Collection<DatamartFacility> healths =
        HealthsCollector.builder()
            .atcBaseUrl(atcBaseUrl)
            .atpBaseUrl(atpBaseUrl)
            .cscFacilities(cscFacilities)
            .jdbcTemplate(jdbcTemplate)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .vastEntities(vastEntities)
            .websites(websites)
            .build()
            .collect();
    Collection<DatamartFacility> stateCems =
        StateCemeteriesCollector.builder()
            .baseUrl(cemeteriesBaseUrl)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .websites(websites)
            .build()
            .collect();
    Collection<DatamartFacility> vetCenters =
        VetCentersCollector.builder()
            .vastEntities(vastEntities)
            .websites(websites)
            .build()
            .collect();
    Collection<DatamartFacility> benefits =
        BenefitsCollector.builder().websites(websites).jdbcTemplate(jdbcTemplate).build().collect();
    Collection<DatamartFacility> cemeteries =
        CemeteriesCollector.builder()
            .baseUrl(cemeteriesBaseUrl)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .websites(websites)
            .jdbcTemplate(jdbcTemplate)
            .build()
            .collect();
    log.info(
        "Collected V0: Health {},  Benefits {},  Vet centers {}, "
            + "Non-national cemeteries {}, Cemeteries {}",
        healths.size(),
        benefits.size(),
        vetCenters.size(),
        stateCems.size(),
        cemeteries.size());
    List<DatamartFacility> datamartFacilities =
        Streams.stream(Iterables.concat(benefits, cemeteries, healths, stateCems, vetCenters))
            .sorted((left, right) -> left.id().compareToIgnoreCase(right.id()))
            .collect(toList());
    updateOperatingStatusFromCmsOverlay(datamartFacilities);
    return datamartFacilities;
  }

  private List<VastEntity> loadVast() {
    Stopwatch watch = Stopwatch.createStarted();
    List<VastEntity> entities =
        ImmutableList.copyOf(
            jdbcTemplate.query(
                "SELECT "
                    + "VCTR2,"
                    + "MVCTR,"
                    + "LAT,"
                    + "LON,"
                    + "STA_NO,"
                    + "STATIONNAME,"
                    + "S_ABBR,"
                    + "COCCLASSIFICATIONID,"
                    + "ADDRESS1,"
                    + "ADDRESS2,"
                    + "ADDRESS3,"
                    + "CITY,"
                    + "ST,"
                    + "ZIP,"
                    + "ZIP4,"
                    + "MONDAY,"
                    + "TUESDAY,"
                    + "WEDNESDAY,"
                    + "THURSDAY,"
                    + "FRIDAY,"
                    + "SATURDAY,"
                    + "SUNDAY,"
                    + "OPERATIONALHOURSSPECIALINSTRUCTIONS,"
                    + "STA_PHONE,"
                    + "STA_FAX,"
                    + "AFTERHOURSPHONE,"
                    + "PATIENTADVOCATEPHONE,"
                    + "ENROLLMENTCOORDINATORPHONE,"
                    + "PHARMACYPHONE,"
                    + "POD,"
                    + "MOBILE,"
                    + "VISN,"
                    + "LASTUPDATED"
                    + " FROM App.Vast",
                (rs, rowNum) -> toVastEntity(rs)));
    log.info(
        "Loading VAST took {} millis for {} entries",
        watch.stop().elapsed(TimeUnit.MILLISECONDS),
        entities.size());
    checkState(!entities.isEmpty(), "No App.Vast entries");
    return entities;
  }

  @SneakyThrows
  void updateOperatingStatusFromCmsOverlay(List<DatamartFacility> datamartFacilities) {
    HashMap<String, DatamartCmsOverlay> cmsOverlays;
    try {
      cmsOverlays = cmsOverlayCollector.loadAndUpdateCmsOverlays();
    } catch (Exception e) {
      throw new CollectorExceptions.CollectorException(e);
    }
    for (DatamartFacility datamartFacility : datamartFacilities) {
      if (cmsOverlays.containsKey(datamartFacility.id())) {
        DatamartCmsOverlay cmsOverlay = cmsOverlays.get(datamartFacility.id());
        datamartFacility.attributes().operatingStatus(cmsOverlay.operatingStatus());
        datamartFacility.attributes().detailedServices(cmsOverlay.detailedServices());
      } else {
        log.warn("No cms overlay for facility: {}", datamartFacility.id());
      }
    }
  }
}
