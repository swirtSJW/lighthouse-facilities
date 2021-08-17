package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV0;
import gov.va.api.lighthouse.facilities.api.FacilityPair;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FacilitiesCollector {
  private static final String WEBSITES_CSV_RESOURCE_NAME = "websites.csv";

  private static final String CSC_STATIONS_RESOURCE_NAME = "csc_stations.txt";

  private final InsecureRestTemplateProvider insecureRestTemplateProvider;

  private final JdbcTemplate jdbcTemplate;

  private final CmsOverlayRepository cmsOverlayRepository;

  private final String atcBaseUrl;

  private final String atpBaseUrl;

  private final String cemeteriesBaseUrl;

  /** Autowired constructor. */
  public FacilitiesCollector(
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Autowired JdbcTemplate jdbcTemplate,
      @Autowired CmsOverlayRepository cmsOverlayRepository,
      @Value("${access-to-care.url}") String atcBaseUrl,
      @Value("${access-to-pwt.url}") String atpBaseUrl,
      @Value("${cemeteries.url}") String cemeteriesBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.jdbcTemplate = jdbcTemplate;
    this.cmsOverlayRepository = cmsOverlayRepository;
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    this.atpBaseUrl = withTrailingSlash(atpBaseUrl);
    this.cemeteriesBaseUrl = withTrailingSlash(cemeteriesBaseUrl);
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

  /** Collect facilities. */
  @SneakyThrows
  public List<FacilityPair> collectFacilities() {
    Map<String, String> websites;
    Collection<VastEntity> vastEntities;
    ArrayList<String> cscFacilities;
    HashMap<String, CmsOverlay> cmsOverlays;
    try {
      websites = loadWebsites(WEBSITES_CSV_RESOURCE_NAME);
      vastEntities = loadVast();
      cscFacilities = loadCaregiverSupport(CSC_STATIONS_RESOURCE_NAME);
      cmsOverlays = loadCmsOverlays();
    } catch (Exception e) {
      throw new CollectorExceptions.CollectorException(e);
    }
    Collection<gov.va.api.lighthouse.facilities.api.v0.Facility> healthsV0 =
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
    Collection<gov.va.api.lighthouse.facilities.api.v1.Facility> healthsV1 =
        HealthsCollector.builder()
            .atcBaseUrl(atcBaseUrl)
            .atpBaseUrl(atpBaseUrl)
            .cscFacilities(cscFacilities)
            .jdbcTemplate(jdbcTemplate)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .vastEntities(vastEntities)
            .websites(websites)
            .build()
            .collectV1();
    Collection<gov.va.api.lighthouse.facilities.api.v0.Facility> stateCemsV0 =
        StateCemeteriesCollector.builder()
            .baseUrl(cemeteriesBaseUrl)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .websites(websites)
            .build()
            .collect();
    Collection<gov.va.api.lighthouse.facilities.api.v1.Facility> stateCemsV1 =
        StateCemeteriesCollector.builder()
            .baseUrl(cemeteriesBaseUrl)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .websites(websites)
            .build()
            .collectV1();
    Collection<gov.va.api.lighthouse.facilities.api.v0.Facility> vetCentersV0 =
        VetCentersCollector.builder()
            .vastEntities(vastEntities)
            .websites(websites)
            .build()
            .collect();
    Collection<gov.va.api.lighthouse.facilities.api.v1.Facility> vetCentersV1 =
        VetCentersCollector.builder()
            .vastEntities(vastEntities)
            .websites(websites)
            .build()
            .collectV1();
    Collection<gov.va.api.lighthouse.facilities.api.v0.Facility> benefitsV0 =
        BenefitsCollector.builder().websites(websites).jdbcTemplate(jdbcTemplate).build().collect();
    Collection<gov.va.api.lighthouse.facilities.api.v1.Facility> benefitsV1 =
        BenefitsCollector.builder()
            .websites(websites)
            .jdbcTemplate(jdbcTemplate)
            .build()
            .collectV1();
    Collection<gov.va.api.lighthouse.facilities.api.v0.Facility> cemeteriesV0 =
        CemeteriesCollector.builder()
            .baseUrl(cemeteriesBaseUrl)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .websites(websites)
            .jdbcTemplate(jdbcTemplate)
            .build()
            .collect();
    Collection<gov.va.api.lighthouse.facilities.api.v1.Facility> cemeteriesV1 =
        CemeteriesCollector.builder()
            .baseUrl(cemeteriesBaseUrl)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .websites(websites)
            .jdbcTemplate(jdbcTemplate)
            .build()
            .collectV1();
    log.info(
        "Collected V0: Health {},  Benefits {},  Vet centers {}, "
            + "Non-national cemeteries {}, Cemeteries {}",
        healthsV0.size(),
        benefitsV0.size(),
        vetCentersV0.size(),
        stateCemsV0.size(),
        cemeteriesV0.size());
    log.info(
        "Collected V1: Health {},  Benefits {},  Vet centers {}, "
            + "Non-national cemeteries {}, Cemeteries {}",
        healthsV1.size(),
        benefitsV1.size(),
        vetCentersV1.size(),
        stateCemsV1.size(),
        cemeteriesV1.size());
    List<FacilityPair> facilityPairs = new ArrayList<>();
    List<gov.va.api.lighthouse.facilities.api.v0.Facility> facilitiesV0 =
        Streams.stream(
                Iterables.concat(benefitsV0, cemeteriesV0, healthsV0, stateCemsV0, vetCentersV0))
            .sorted((left, right) -> left.id().compareToIgnoreCase(right.id()))
            .collect(toList());
    // todo: This only needs to be done for v1 in the future. Necessary changes coming in future
    // work
    for (gov.va.api.lighthouse.facilities.api.v0.Facility facility : facilitiesV0) {
      if (cmsOverlays.containsKey(facility.id())) {
        CmsOverlay cmsOverlay = cmsOverlays.get(facility.id());
        facility.attributes().operatingStatus(cmsOverlay.operatingStatus());
        facility.attributes().detailedServices(cmsOverlay.detailedServices());
      } else {
        log.warn("No cms overlay for facility: {}", facility.id());
      }
    }
    List<gov.va.api.lighthouse.facilities.api.v1.Facility> facilitiesV1 =
        Streams.stream(
                Iterables.concat(benefitsV1, cemeteriesV1, healthsV1, stateCemsV1, vetCentersV1))
            .sorted((left, right) -> left.id().compareToIgnoreCase(right.id()))
            .collect(toList());
    for (int i = 0; i < facilitiesV0.size(); i++) {
      facilityPairs.add(
          FacilityPair.builder().v0(facilitiesV0.get(i)).v1(facilitiesV1.get(i)).build());
    }
    return facilityPairs;
  }

  private HashMap<String, CmsOverlay> loadCmsOverlays() {
    HashMap<String, CmsOverlay> returnMap = new HashMap<>();
    Streams.stream(cmsOverlayRepository.findAll())
        .parallel()
        .forEach(
            cmsOverlayEntity -> {
              try {
                returnMap.put(
                    cmsOverlayEntity.id().toIdString(),
                    CmsOverlay.builder()
                        .operatingStatus(
                            cmsOverlayEntity.cmsOperatingStatus() != null
                                ? FacilitiesJacksonConfigV0.createMapper()
                                    .readValue(
                                        cmsOverlayEntity.cmsOperatingStatus(),
                                        Facility.OperatingStatus.class)
                                : null)
                        .detailedServices(
                            cmsOverlayEntity.cmsServices() != null
                                ? List.of(
                                    FacilitiesJacksonConfigV0.createMapper()
                                        .readValue(
                                            cmsOverlayEntity.cmsServices(),
                                            DetailedService[].class))
                                : null)
                        .build());
              } catch (Exception e) {
                log.error("Failed to load cms overlay data. {}", e.getMessage());
              }
            });
    return returnMap;
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
}
