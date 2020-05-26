package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@DataJpaTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class HealthsCollectorTest {
  @Autowired JdbcTemplate jdbcTemplate;

  @SneakyThrows
  @SuppressWarnings("unused")
  public static ResultSet stopCodeWaitTimesPaginated(Connection conn, int page, int count) {
    return conn.prepareStatement("SELECT * FROM APP.VHA_Stop_Code_Wait_Times").executeQuery();
  }

  private void _initDatabase() {
    jdbcTemplate.execute(
        "CREATE TABLE App.VHA_Mental_Health_Contact_Info ("
            + "StationNumber VARCHAR,"
            + "MHPhone VARCHAR,"
            + "Extension FLOAT"
            + ")");
    jdbcTemplate.execute(
        "CREATE ALIAS App.VHA_Stop_Code_Wait_Times_Paginated FOR"
            + " \"gov.va.api.lighthouse.facilitiescollector.HealthsCollectorTest.stopCodeWaitTimesPaginated\"");
    jdbcTemplate.execute(
        "CREATE TABLE App.VHA_Stop_Code_Wait_Times ("
            + "Sta6a VARCHAR,"
            + "PrimaryStopCode VARCHAR,"
            + "PrimaryStopCodeName VARCHAR,"
            + "AvgWaitTimeNew VARCHAR"
            + ")");
  }

  private void _saveMentalHealthContact(String stationNum, String phone, Double extension) {
    jdbcTemplate.execute(
        String.format(
            "INSERT INTO App.VHA_Mental_Health_Contact_Info ("
                + "StationNumber,"
                + "MHPhone,"
                + "Extension"
                + ") VALUES ("
                + "'%s',"
                + "'%s',"
                + "%s"
                + ")",
            stationNum, phone, extension));
  }

  private void _saveStopCode(String stationNum, String code, String name, String wait) {
    jdbcTemplate.execute(
        String.format(
            "INSERT INTO App.VHA_Stop_Code_Wait_Times ("
                + "Sta6a,"
                + "PrimaryStopCode,"
                + "PrimaryStopCodeName,"
                + "AvgWaitTimeNew"
                + ") VALUES ("
                + "'%s',"
                + "'%s',"
                + "'%s',"
                + "'%s'"
                + ")",
            stationNum, code, name, wait));
  }

  @Test
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void collect() {
    _initDatabase();
    _saveMentalHealthContact("666", "867-5309", 5555D);
    _saveStopCode("666", "123", "", "10");
    _saveStopCode("666", "124", "", "20");
    _saveStopCode("666", "180", "", "30");
    _saveStopCode("666", "411", "", "40");

    RestTemplate insecureRestTemplate = mock(RestTemplate.class);
    when(insecureRestTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            List.of(
                                AccessToCareEntry.builder()
                                    .facilityId("666")
                                    .apptTypeName("Audiology")
                                    .estWaitTime(new BigDecimal("28.857142"))
                                    .newWaitTime(new BigDecimal("128.378378"))
                                    .emergencyCare(true)
                                    .urgentCare(true)
                                    .sliceEndDate("2020-03-02T00:00:00")
                                    .build())))));

    when(insecureRestTemplate.exchange(
            startsWith("http://covid"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            List.of(
                                ImmutableMap.of(
                                    "Facility Latitude",
                                    "42.95122016",
                                    "Facility Longitude",
                                    "-78.81368285",
                                    "VA Confirmed",
                                    "56",
                                    "VA Deaths",
                                    "4",
                                    "Facility Name",
                                    "(666) UPSTATE NEW YORK HCS"))))));

    when(insecureRestTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            List.of(
                                AccessToPwtEntry.builder()
                                    .facilityId("666")
                                    .apptTypeName("Specialty Care (Routine)")
                                    .shepScore(new BigDecimal("0.9100000262260437"))
                                    .sliceEndDate("2019-06-20T10:41:00")
                                    .build())))));

    VastEntity entity =
        VastEntity.builder()
            .latitude(new BigDecimal("14.544080000000065"))
            .longitude(new BigDecimal("120.99139000000002"))
            .stationNumber("666")
            .stationName("Manila VA Clinic")
            .abbreviation("OOS")
            .cocClassificationId("5")
            .address1("NOX3 Seafront Compound")
            .address2("1501 Roxas Boulevard")
            .city("Pasay City")
            .state("PH")
            .zip("01302")
            .zip4("0000")
            .monday("730AM-430PM")
            .tuesday("730AM-430PM")
            .wednesday("730AM-430PM")
            .thursday("730AM-430PM")
            .friday("730AM-430PM")
            .saturday("-")
            .sunday("-")
            .staPhone("632-550-3888 x")
            .staFax("632-310-5962 x")
            .afterHoursPhone("000-000-0000 x")
            .patientAdvocatePhone("632-550-3888 x3716")
            .enrollmentCoordinatorPhone("632-550-3888 x3780")
            .pharmacyPhone("632-550-3888 x5029")
            .pod("A")
            .mobile(false)
            .visn("21")
            .build();

    /* Going straight to the collector doesn't add the trailing slash and
     * causes issues with the mock responses. Therefore, these trailing slashes
     * are necessary.
     */
    assertThat(
            HealthsCollector.builder()
                .atcBaseUrl("http://atc/")
                .atcCovidBaseUrl("http://covid/")
                .atpBaseUrl("http://atp/")
                .jdbcTemplate(jdbcTemplate)
                .insecureRestTemplate(insecureRestTemplate)
                .vastEntities(List.of(entity))
                .websites(ImmutableMap.of())
                .build()
                .healths())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("vha_666")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Manila VA Clinic")
                            .facilityType(Facility.FacilityType.va_health_facility)
                            .classification("Other Outpatient Services (OOS)")
                            .latitude(new BigDecimal("14.544080000000065"))
                            .longitude(new BigDecimal("120.99139000000002"))
                            .address(
                                Facility.Addresses.builder()
                                    .physical(
                                        Facility.Address.builder()
                                            .zip("01302")
                                            .city("Pasay City")
                                            .state("PH")
                                            .address1("1501 Roxas Boulevard")
                                            .address2("NOX3 Seafront Compound")
                                            .build())
                                    .build())
                            .phone(
                                Facility.Phone.builder()
                                    .fax("632-310-5962")
                                    .main("632-550-3888")
                                    .pharmacy("632-550-3888 x5029")
                                    .afterHours("000-000-0000")
                                    .patientAdvocate("632-550-3888 x3716")
                                    .mentalHealthClinic("867-5309 x 5555")
                                    .enrollmentCoordinator("632-550-3888 x3780")
                                    .build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("730AM-430PM")
                                    .tuesday("730AM-430PM")
                                    .wednesday("730AM-430PM")
                                    .thursday("730AM-430PM")
                                    .friday("730AM-430PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .services(
                                Facility.Services.builder()
                                    .health(
                                        List.of(
                                            Facility.HealthService.Audiology,
                                            Facility.HealthService.DentalServices,
                                            Facility.HealthService.EmergencyCare,
                                            Facility.HealthService.Nutrition,
                                            Facility.HealthService.Podiatry,
                                            Facility.HealthService.UrgentCare))
                                    .lastUpdated(LocalDate.parse("2020-03-02"))
                                    .build())
                            .satisfaction(
                                Facility.Satisfaction.builder()
                                    .health(
                                        Facility.PatientSatisfaction.builder()
                                            .specialtyCareRoutine(
                                                new BigDecimal("0.9100000262260437"))
                                            .build())
                                    .effectiveDate(LocalDate.parse("2019-06-20"))
                                    .build())
                            .waitTimes(
                                Facility.WaitTimes.builder()
                                    .health(
                                        List.of(
                                            Facility.PatientWaitTime.builder()
                                                .service(Facility.HealthService.Audiology)
                                                .newPatientWaitTime(new BigDecimal("128.378378"))
                                                .establishedPatientWaitTime(
                                                    new BigDecimal("28.857142"))
                                                .build()))
                                    .effectiveDate(LocalDate.parse("2020-03-02"))
                                    .build())
                            .mobile(false)
                            .activeStatus(Facility.ActiveStatus.A)
                            .visn("21")
                            .covid19(
                                Facility.Covid19.builder().confirmedCases(56).deaths(4).build())
                            .build())
                    .build()));
  }

  @Test
  @SneakyThrows
  public void mentalHealthContact_blankPhone() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("StationNumber")).thenReturn("x");
    Map<String, String> map = new HashMap<>();
    HealthsCollector.putMentalHealthContact(rs, map);
    assertThat(map).isEmpty();
  }

  @Test
  @SneakyThrows
  public void mentalHealthContact_blankStation() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("StationNumber")).thenReturn(" ");
    Map<String, String> map = new HashMap<>();
    HealthsCollector.putMentalHealthContact(rs, map);
    assertThat(map).isEmpty();
  }

  @Test
  @SneakyThrows
  public void mentalHealthContact_duplicate() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("StationNumber")).thenReturn("x");
    when(rs.getString("MHPhone")).thenReturn("123");
    Map<String, String> map = new HashMap<>();
    HealthsCollector.putMentalHealthContact(rs, map);
    when(rs.getString("StationNumber")).thenReturn("x");
    when(rs.getString("MHPhone")).thenReturn("456");
    HealthsCollector.putMentalHealthContact(rs, map);
    assertThat(map).containsEntry("vha_x", "456");
  }

  @Test
  @SneakyThrows
  public void mentalHealthContact_extension() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("StationNumber")).thenReturn("x");
    when(rs.getString("MHPhone")).thenReturn("123");
    when(rs.getString("Extension")).thenReturn("9999");
    Map<String, String> map = new HashMap<>();
    HealthsCollector.putMentalHealthContact(rs, map);
    assertThat(map).containsEntry("vha_x", "123 x 9999");
  }

  @Test
  @SneakyThrows
  public void stopCode() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("Sta6a")).thenReturn("x");
    when(rs.getString("PrimaryStopCode")).thenReturn("000");
    when(rs.getString("PrimaryStopCodeName")).thenReturn("foo");
    when(rs.getString("AvgWaitTimeNew")).thenReturn("1.23");
    ListMultimap<String, StopCode> map = ArrayListMultimap.create();
    HealthsCollector.putStopCode(rs, map);
    assertThat(map.get("VHA_X"))
        .isEqualTo(
            List.of(
                StopCode.builder()
                    .stationNumber("x")
                    .code("000")
                    .name("foo")
                    .waitTimeNew(new BigDecimal("1.23"))
                    .build()));
  }

  @Test
  @SneakyThrows
  public void stopCode_blankCode() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("Sta6a")).thenReturn("x");
    when(rs.getString("PrimaryStopCode")).thenReturn(" ");
    ListMultimap<String, StopCode> map = ArrayListMultimap.create();
    HealthsCollector.putStopCode(rs, map);
    assertThat(map.asMap()).isEmpty();
  }

  @Test
  @SneakyThrows
  public void stopCode_blankStation() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("Sta6a")).thenReturn(" ");
    ListMultimap<String, StopCode> map = ArrayListMultimap.create();
    HealthsCollector.putStopCode(rs, map);
    assertThat(map.asMap()).isEmpty();
  }

  @Test
  @SneakyThrows
  public void stopCode_malformedWaitTime() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("Sta6a")).thenReturn("x");
    when(rs.getString("PrimaryStopCode")).thenReturn("000");
    when(rs.getString("PrimaryStopCodeName")).thenReturn("foo");
    when(rs.getString("AvgWaitTimeNew")).thenReturn("nope");
    ListMultimap<String, StopCode> map = ArrayListMultimap.create();
    HealthsCollector.putStopCode(rs, map);
    assertThat(map.asMap()).isEmpty();
  }

  @Test
  @SneakyThrows
  public void stopCode_noWaitTime() {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("Sta6a")).thenReturn("x");
    when(rs.getString("PrimaryStopCode")).thenReturn("000");
    when(rs.getString("PrimaryStopCodeName")).thenReturn("foo");
    ListMultimap<String, StopCode> map = ArrayListMultimap.create();
    HealthsCollector.putStopCode(rs, map);
    assertThat(map.get("VHA_X"))
        .isEqualTo(List.of(StopCode.builder().stationNumber("x").code("000").name("foo").build()));
  }
}
