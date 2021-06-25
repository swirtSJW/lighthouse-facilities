package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class SearchByZipGeoJsonTest {
  @SneakyThrows
  private void assertReadable(String json) {
    gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse f =
        createMapper()
            .readValue(
                getClass().getResourceAsStream(json),
                gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse.class);
    assertThat(f).isEqualTo(sample());
  }

  private gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime patientWaitTime(
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService service,
      Double newPat,
      Double oldPat) {
    gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime.PatientWaitTimeBuilder
        waitTime = gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime.builder();
    if (service != null) {
      waitTime.service(service);
    }
    if (newPat != null) {
      waitTime.newPatientWaitTime(BigDecimal.valueOf(newPat));
    }
    if (oldPat != null) {
      waitTime.establishedPatientWaitTime(BigDecimal.valueOf(oldPat));
    }
    return waitTime.build();
  }

  private gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse sample() {
    return gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.GeoFacility.builder()
                    .type(gov.va.api.lighthouse.facilities.api.v0.GeoFacility.Type.Feature)
                    .geometry(
                        gov.va.api.lighthouse.facilities.api.v0.GeoFacility.Geometry.builder()
                            .type(
                                gov.va.api.lighthouse.facilities.api.v0.GeoFacility.GeometryType
                                    .Point)
                            .coordinates(
                                List.of(
                                    BigDecimal.valueOf(-80.73907113),
                                    BigDecimal.valueOf(28.2552385700001)))
                            .build())
                    .properties(
                        GeoFacility.Properties.builder()
                            .id("vha_675GA")
                            .name("Viera VA Clinic")
                            .facilityType(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityType
                                    .va_health_facility)
                            .classification("Health Care Center (HCC)")
                            .website("https://www.orlando.va.gov/locations/Viera.asp")
                            .timeZone("America/New_York")
                            .address(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.Addresses.builder()
                                    .mailing(
                                        gov.va.api.lighthouse.facilities.api.v0.Facility.Address
                                            .builder()
                                            .build())
                                    .physical(
                                        gov.va.api.lighthouse.facilities.api.v0.Facility.Address
                                            .builder()
                                            .zip("32940-8007")
                                            .city("Viera")
                                            .state("FL")
                                            .address1("2900 Veterans Way")
                                            .build())
                                    .build())
                            .phone(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.Phone.builder()
                                    .fax("321-637-3515")
                                    .main("321-637-3788")
                                    .pharmacy("877-646-4550")
                                    .afterHours("877-741-3400")
                                    .patientAdvocate("407-631-1187")
                                    .mentalHealthClinic("321-637-3788")
                                    .enrollmentCoordinator("321-637-3527")
                                    .build())
                            .hours(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.Hours.builder()
                                    .mon("730AM-430PM")
                                    .tues("730AM-430PM")
                                    .wed("730AM-430PM")
                                    .thurs("730AM-430PM")
                                    .fri("730AM-430PM")
                                    .sat("Closed")
                                    .sun("Closed")
                                    .build())
                            .services(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.Services.builder()
                                    .other(new ArrayList<>())
                                    .health(
                                        List.of(
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.PrimaryCare,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.MentalHealthCare,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.Audiology,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.Cardiology,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.Dermatology,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.Gastroenterology,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.Ophthalmology,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.Optometry,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.Orthopedics,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.Urology,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.SpecialtyCare,
                                            gov.va.api.lighthouse.facilities.api.v0.Facility
                                                .HealthService.DentalServices))
                                    .lastUpdated(LocalDate.parse("2020-03-02"))
                                    .build())
                            .satisfaction(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.Satisfaction
                                    .builder()
                                    .health(
                                        gov.va.api.lighthouse.facilities.api.v0.Facility
                                            .PatientSatisfaction.builder()
                                            .primaryCareUrgent(BigDecimal.valueOf(0.74))
                                            .primaryCareRoutine(BigDecimal.valueOf(0.83))
                                            .build())
                                    .effectiveDate(LocalDate.parse("2019-06-20"))
                                    .build())
                            .waitTimes(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.WaitTimes.builder()
                                    .health(
                                        List.of(
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.Urology,
                                                32.047619,
                                                9.879032),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.Audiology,
                                                1.706967,
                                                2.126855),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.Optometry,
                                                76.396226,
                                                7.900787),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.Cardiology,
                                                18.657142,
                                                6.4),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.Dermatology,
                                                0.616666,
                                                0.555555),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.Orthopedics,
                                                24.682539,
                                                4.995024),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.PrimaryCare,
                                                26.405405,
                                                1.545372),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.Ophthalmology,
                                                47.571428,
                                                3.258992),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.SpecialtyCare,
                                                20.963572,
                                                5.775406),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.Gastroenterology,
                                                22.151515,
                                                4.943661),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v0.Facility
                                                    .HealthService.MentalHealthCare,
                                                7.592814,
                                                3.97159)))
                                    .effectiveDate(LocalDate.parse("2020-03-02"))
                                    .build())
                            .mobile(false)
                            .activeStatus(Facility.ActiveStatus.A)
                            .visn("8")
                            .build())
                    .build()))
        .build();
  }

  @Test
  void unmarshallSample() {
    assertReadable("/search-zip-geojson.json");
  }
}
