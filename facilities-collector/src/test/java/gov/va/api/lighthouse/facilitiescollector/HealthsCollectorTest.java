package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

public class HealthsCollectorTest {
  @Test
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void collect() {
    RestTemplate restTemplate = mock(RestTemplate.class);
    RestTemplate insecureRestTemplate = mock(RestTemplate.class);

    ResponseEntity<List<AccessToPwtEntry>> atpResponse = mock(ResponseEntity.class);
    when(atpResponse.getBody())
        .thenReturn(
            List.of(
                AccessToPwtEntry.builder()
                    .facilityId("666")
                    .apptTypeName("Specialty Care (Routine)")
                    .shepScore(new BigDecimal("0.9100000262260437"))
                    .sliceEndDate("2019-06-20T10:41:00")
                    .build()));

    when(restTemplate.exchange(
            startsWith("http://atp"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)))
        .thenReturn(atpResponse);
    ResponseEntity<String> arcGisResponse = mock(ResponseEntity.class);
    when(arcGisResponse.getBody())
        .thenReturn(
            JacksonConfig.createMapper()
                .writeValueAsString(
                    ArcGisHealths.builder()
                        .features(
                            List.of(
                                ArcGisHealths.Feature.builder()
                                    .geometry(
                                        ArcGisHealths.Geometry.builder()
                                            .latitude(new BigDecimal("14.544080000000065"))
                                            .longitude(new BigDecimal("120.99139000000002"))
                                            .build())
                                    .attributes(
                                        ArcGisHealths.Attributes.builder()
                                            .stationNum("666")
                                            .name("Manila VA Clinic")
                                            .featureCode("OOS")
                                            .cocClassificationId("5")
                                            .address1("NOX3 Seafront Compound")
                                            .address2("1501 Roxas Boulevard")
                                            .municipality("Pasay City")
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
                                            .mobile(0)
                                            .visn("21")
                                            .build())
                                    .build()))
                        .build()));

    when(insecureRestTemplate.exchange(
            startsWith("http://vaarcgis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(arcGisResponse);

    assertThat(
            HealthsCollector.builder()
                .atcBaseUrl("file:src/test/resources/")
                .atpBaseUrl("http://atp")
                .jdbcTemplate(mock(JdbcTemplate.class))
                .restTemplate(restTemplate)
                .insecureRestTemplate(insecureRestTemplate)
                .vaArcGisBaseUrl("http://vaarcgis")
                .websites(ImmutableMap.of())
                .build()
                .healths())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("vha_666")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.Attributes.builder()
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
                            .build())
                    .build()));
  }

  @Test
  @SneakyThrows
  public void mentalHealthContact_blankPhone() {
    Map<String, String> map = new HashMap<>();
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("StationNumber")).thenReturn("x");
    HealthsCollector.putMentalHealthContact(rs, map);
    assertThat(map).isEmpty();
  }

  @Test
  @SneakyThrows
  public void mentalHealthContact_blankStation() {
    Map<String, String> map = new HashMap<>();
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("StationNumber")).thenReturn(" ");
    HealthsCollector.putMentalHealthContact(rs, map);
    assertThat(map).isEmpty();
  }

  @Test
  @SneakyThrows
  public void mentalHealthContact_duplicate() {
    Map<String, String> map = new HashMap<>();
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("StationNumber")).thenReturn("x");
    when(rs.getString("MHPhone")).thenReturn("123");
    HealthsCollector.putMentalHealthContact(rs, map);
    when(rs.getString("StationNumber")).thenReturn("x");
    when(rs.getString("MHPhone")).thenReturn("456");
    HealthsCollector.putMentalHealthContact(rs, map);
    assertThat(map).containsEntry("vha_x", "456");
  }

  @Test
  @SneakyThrows
  public void mentalHealthContact_extension() {
    Map<String, String> map = new HashMap<>();
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("StationNumber")).thenReturn("x");
    when(rs.getString("MHPhone")).thenReturn("123");
    when(rs.getString("Extension")).thenReturn("9999");
    HealthsCollector.putMentalHealthContact(rs, map);
    assertThat(map).containsEntry("vha_x", "123 x 9999");
  }
}
