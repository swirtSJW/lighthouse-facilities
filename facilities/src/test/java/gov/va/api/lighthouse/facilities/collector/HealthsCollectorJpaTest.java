package gov.va.api.lighthouse.facilities.collector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class HealthsCollectorJpaTest {
  @Autowired JdbcTemplate jdbcTemplate;

  private void _initDatabase() {
    jdbcTemplate.execute(
        "CREATE TABLE App.VHA_Mental_Health_Contact_Info ("
            + "StationNumber VARCHAR,"
            + "MHPhone VARCHAR,"
            + "Extension FLOAT"
            + ")");
    jdbcTemplate.execute(
        "CREATE TABLE App.VSSC_ClinicalServices ("
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
            "INSERT INTO App.VSSC_ClinicalServices ("
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
  void collect() {
    _initDatabase();
    _saveMentalHealthContact("666", "867-5309", 5555D);
    _saveStopCode("666", "123", "", "10");
    _saveStopCode("666", "180", "", "20");
    _saveStopCode("666", "411", "", "30");
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
            .operationalHoursSpecialInstructions(
                "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m. |")
            .staPhone("632-550-3888 x")
            .staFax("632-310-5962 x")
            .afterHoursPhone(null)
            .patientAdvocatePhone("632-550-3888 x3716")
            .enrollmentCoordinatorPhone("632-550-3888 x3780")
            .pharmacyPhone("632-550-3888 x5029")
            .pod("A")
            .mobile(false)
            .visn("21")
            .build();
    assertThat(
            HealthsCollector.builder()
                .atcBaseUrl("http://atc/")
                .atpBaseUrl("http://atp/")
                .cscFacilities(new ArrayList<>())
                .jdbcTemplate(jdbcTemplate)
                .insecureRestTemplate(insecureRestTemplate)
                .vastEntities(List.of(entity))
                .websites(emptyMap())
                .build()
                .collect())
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
                            .timeZone("Asia/Manila")
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
                                    .afterHours(null)
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
                            .operationalHoursSpecialInstructions(
                                "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m. |")
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
                            .build())
                    .build()));
  }

  @Test
  @SneakyThrows
  void collectV1() {
    _initDatabase();
    _saveMentalHealthContact("666", "867-5309", 5555D);
    _saveStopCode("666", "123", "", "10");
    _saveStopCode("666", "180", "", "20");
    _saveStopCode("666", "411", "", "30");
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
            .operationalHoursSpecialInstructions(
                "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m. |")
            .staPhone("632-550-3888 x")
            .staFax("632-310-5962 x")
            .afterHoursPhone(null)
            .patientAdvocatePhone("632-550-3888 x3716")
            .enrollmentCoordinatorPhone("632-550-3888 x3780")
            .pharmacyPhone("632-550-3888 x5029")
            .pod("A")
            .mobile(false)
            .visn("21")
            .build();
    assertThat(
            HealthsCollector.builder()
                .atcBaseUrl("http://atc/")
                .atpBaseUrl("http://atp/")
                .cscFacilities(new ArrayList<>())
                .jdbcTemplate(jdbcTemplate)
                .insecureRestTemplate(insecureRestTemplate)
                .vastEntities(List.of(entity))
                .websites(emptyMap())
                .build()
                .collectV1())
        .isEqualTo(
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.builder()
                    .id("vha_666")
                    .type(gov.va.api.lighthouse.facilities.api.v1.Facility.Type.va_facilities)
                    .attributes(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityAttributes
                            .builder()
                            .name("Manila VA Clinic")
                            .facilityType(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType
                                    .va_health_facility)
                            .classification("Other Outpatient Services (OOS)")
                            .latitude(new BigDecimal("14.544080000000065"))
                            .longitude(new BigDecimal("120.99139000000002"))
                            .timeZone("Asia/Manila")
                            .address(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses.builder()
                                    .physical(
                                        gov.va.api.lighthouse.facilities.api.v1.Facility.Address
                                            .builder()
                                            .zip("01302")
                                            .city("Pasay City")
                                            .state("PH")
                                            .address1("1501 Roxas Boulevard")
                                            .address2("NOX3 Seafront Compound")
                                            .build())
                                    .build())
                            .phone(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Phone.builder()
                                    .fax("632-310-5962")
                                    .main("632-550-3888")
                                    .pharmacy("632-550-3888 x5029")
                                    .afterHours(null)
                                    .patientAdvocate("632-550-3888 x3716")
                                    .mentalHealthClinic("867-5309 x 5555")
                                    .enrollmentCoordinator("632-550-3888 x3780")
                                    .build())
                            .hours(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Hours.builder()
                                    .monday("730AM-430PM")
                                    .tuesday("730AM-430PM")
                                    .wednesday("730AM-430PM")
                                    .thursday("730AM-430PM")
                                    .friday("730AM-430PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .operationalHoursSpecialInstructions(
                                "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m. |")
                            .services(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Services.builder()
                                    .health(
                                        List.of(
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Audiology,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.DentalServices,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.EmergencyCare,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Nutrition,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Podiatry,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.UrgentCare))
                                    .lastUpdated(LocalDate.parse("2020-03-02"))
                                    .build())
                            .satisfaction(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Satisfaction
                                    .builder()
                                    .health(
                                        gov.va.api.lighthouse.facilities.api.v1.Facility
                                            .PatientSatisfaction.builder()
                                            .specialtyCareRoutine(
                                                new BigDecimal("0.9100000262260437"))
                                            .build())
                                    .effectiveDate(LocalDate.parse("2019-06-20"))
                                    .build())
                            .waitTimes(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.WaitTimes.builder()
                                    .health(
                                        List.of(
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .PatientWaitTime.builder()
                                                .service(
                                                    gov.va.api.lighthouse.facilities.api.v1.Facility
                                                        .HealthService.Audiology)
                                                .newPatientWaitTime(new BigDecimal("128.378378"))
                                                .establishedPatientWaitTime(
                                                    new BigDecimal("28.857142"))
                                                .build()))
                                    .effectiveDate(LocalDate.parse("2020-03-02"))
                                    .build())
                            .mobile(false)
                            .activeStatus(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.ActiveStatus.A)
                            .visn("21")
                            .build())
                    .build()));
  }
}
