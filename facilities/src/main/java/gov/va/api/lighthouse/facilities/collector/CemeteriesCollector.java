package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Builder
@Slf4j
final class CemeteriesCollector {

  @NonNull private final Map<String, String> websites;

  private final JdbcTemplate jdbcTemplate;

  /** Convert the results into a CdwCemetery Object. */
  @SneakyThrows
  static CdwCemetery toCdwCemetery(ResultSet rs) {
    return CdwCemetery.builder()
        .siteId(rs.getString("SITE_ID"))
        .fullName(rs.getString("FULL_NAME"))
        .siteType(rs.getString("SITE_TYPE"))
        .siteAddress1(rs.getString("SITE_ADDRESS1"))
        .siteAddress2(rs.getString("SITE_ADDRESS2"))
        .siteCity(rs.getString("SITE_CITY"))
        .siteState(rs.getString("SITE_STATE"))
        .siteZip(rs.getString("SITE_ZIP"))
        .mailAddress1(rs.getString("MAIL_ADDRESS1"))
        .mailAddress2(rs.getString("MAIL_ADDRESS2"))
        .mailCity(rs.getString("MAIL_CITY"))
        .mailState(rs.getString("MAIL_STATE"))
        .mailZip(rs.getString("MAIL_ZIP"))
        .phone(rs.getString("PHONE"))
        .fax(rs.getString("FAX"))
        .visitationHoursWeekday(rs.getString("VISITATION_HOURS_WEEKDAY"))
        .visitationHoursWeekend(rs.getString("VISITATION_HOURS_WEEKEND"))
        .latitude(rs.getBigDecimal("LATITUDE_DD"))
        .longitude(rs.getBigDecimal("LONGITUDE_DD"))
        .websiteUrl(rs.getString("Website_URL"))
        .build();
  }

  /** Collects and transforms all national cemeteries into a list of facilities. */
  public Collection<Facility> collect() {
    try {
      return queryCdwCemeteries().stream()
          .filter(c -> !equalsIgnoreCase(c.siteType(), "office"))
          .map(
              facility ->
                  CemeteriesTransformer.builder()
                      .cdwFacility(facility)
                      .csvWebsite(websites.get("nca_" + facility.siteId()))
                      .build()
                      .toFacility())
          .collect(toList());
    } catch (Exception e) {
      throw new CollectorExceptions.CemeteriesCollectorException(e);
    }
  }

  /** Requests CDW cemetery in a List. */
  @SneakyThrows
  private List<CdwCemetery> queryCdwCemeteries() {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    List<CdwCemetery> cdwCemeteries =
        ImmutableList.copyOf(
            jdbcTemplate.query(
                "SELECT "
                    + "SITE_ID,"
                    + "FULL_NAME,"
                    + "SITE_TYPE,"
                    + "SITE_ADDRESS1,"
                    + "SITE_ADDRESS2,"
                    + "SITE_CITY,"
                    + "SITE_STATE,"
                    + "SITE_ZIP,"
                    + "MAIL_ADDRESS1,"
                    + "MAIL_ADDRESS2,"
                    + "MAIL_CITY,"
                    + "MAIL_STATE,"
                    + "MAIL_ZIP,"
                    + "PHONE,"
                    + "FAX,"
                    + "VISITATION_HOURS_WEEKDAY,"
                    + "VISITATION_HOURS_WEEKEND,"
                    + "LATITUDE_DD,"
                    + "LONGITUDE_DD,"
                    + "Website_URL"
                    + " FROM App.FacilityLocator_NCA",
                (rs, rowNum) -> toCdwCemetery(rs)));
    log.info(
        "Loading cemeteries took {} millis for {} entries",
        totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
        cdwCemeteries.size());
    checkState(!cdwCemeteries.isEmpty(), "No App.FacilityLocator_NCA entries");
    return cdwCemeteries;
  }
}
