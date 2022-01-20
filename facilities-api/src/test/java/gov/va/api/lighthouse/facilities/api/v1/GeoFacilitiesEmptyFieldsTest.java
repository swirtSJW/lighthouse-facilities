package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class GeoFacilitiesEmptyFieldsTest {

  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyFacility = getExpectedJson("v1/GeoFacility/geoFacilityWithNullFields.json");
    GeoFacility emptyFacility =
        GeoFacility.builder().type(null).geometry(null).properties(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    // Response with empty fields
    jsonEmptyFacility = getExpectedJson("v1/GeoFacility/geoFacilityWithTypeOnly.json");
    emptyFacility =
        GeoFacility.builder()
            .type(GeoFacility.Type.Feature)
            .geometry(null)
            .properties(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    jsonEmptyFacility = getExpectedJson("v1/GeoFacility/geoFacilityWithEmptyGeometry.json");
    emptyFacility =
        GeoFacility.builder()
            .type(GeoFacility.Type.Feature)
            .geometry(GeoFacility.Geometry.builder().build())
            .properties(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    jsonEmptyFacility = getExpectedJson("v1/GeoFacility/geoFacilityWithEmptyProperties.json");
    emptyFacility =
        GeoFacility.builder()
            .type(GeoFacility.Type.Feature)
            .geometry(GeoFacility.Geometry.builder().build())
            .properties(GeoFacility.Properties.builder().build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
  }

  @Test
  @SneakyThrows
  void emptyGeometry() {
    assertThat(GeoFacility.Geometry.builder().build().isEmpty()).isTrue();
    assertThat(GeoFacility.Geometry.builder().coordinates(emptyList()).build().isEmpty()).isTrue();
    assertThat(
            GeoFacility.Geometry.builder().type(GeoFacility.GeometryType.Point).build().isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Geometry.builder().coordinates(List.of(BigDecimal.ZERO)).build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyProperties() {
    assertThat(GeoFacility.Properties.builder().build().isEmpty()).isTrue();
    assertThat(
            GeoFacility.Properties.builder()
                .address(Facility.Addresses.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            GeoFacility.Properties.builder()
                .hours(Facility.Hours.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            GeoFacility.Properties.builder()
                .satisfaction(Facility.Satisfaction.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            GeoFacility.Properties.builder()
                .services(Facility.Services.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            GeoFacility.Properties.builder()
                .waitTimes(Facility.WaitTimes.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(GeoFacility.Properties.builder().detailedServices(emptyList()).build().isEmpty())
        .isTrue();
    assertThat(
            GeoFacility.Properties.builder()
                .operationalHoursSpecialInstructions(emptyList())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            GeoFacility.Properties.builder()
                .phone(Facility.Phone.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Blank values
    String blank = "   ";
    assertThat(GeoFacility.Properties.builder().id(blank).build().isEmpty()).isTrue();
    assertThat(GeoFacility.Properties.builder().name(blank).build().isEmpty()).isTrue();
    assertThat(GeoFacility.Properties.builder().classification(blank).build().isEmpty()).isTrue();
    assertThat(GeoFacility.Properties.builder().website(blank).build().isEmpty()).isTrue();
    assertThat(GeoFacility.Properties.builder().visn(blank).build().isEmpty()).isTrue();
    assertThat(GeoFacility.Properties.builder().timeZone(blank).build().isEmpty()).isTrue();
    // Non-blank values
    String nonBlank = "test";
    assertThat(GeoFacility.Properties.builder().id(nonBlank).build().isEmpty()).isFalse();
    assertThat(GeoFacility.Properties.builder().name(nonBlank).build().isEmpty()).isFalse();
    assertThat(GeoFacility.Properties.builder().classification(nonBlank).build().isEmpty())
        .isFalse();
    assertThat(GeoFacility.Properties.builder().website(nonBlank).build().isEmpty()).isFalse();
    assertThat(GeoFacility.Properties.builder().visn(nonBlank).build().isEmpty()).isFalse();
    assertThat(GeoFacility.Properties.builder().timeZone(nonBlank).build().isEmpty()).isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .facilityType(Facility.FacilityType.va_health_facility)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .phone(Facility.Phone.builder().main("202-555-1212").build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .detailedServices(List.of(DetailedService.builder().name("test").build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .waitTimes(
                    Facility.WaitTimes.builder()
                        .health(
                            List.of(
                                Facility.PatientWaitTime.builder()
                                    .service(Facility.HealthService.Cardiology)
                                    .build()))
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .services(
                    Facility.Services.builder()
                        .benefits(List.of(Facility.BenefitsService.Pensions))
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .satisfaction(
                    Facility.Satisfaction.builder()
                        .health(
                            Facility.PatientSatisfaction.builder()
                                .primaryCareRoutine(BigDecimal.ZERO)
                                .build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .activeStatus(Facility.ActiveStatus.A)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .hours(Facility.Hours.builder().monday("9AM-5PM").build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .address(
                    Facility.Addresses.builder()
                        .mailing(
                            Facility.Address.builder()
                                .address1("50 Irving Street, Northwest")
                                .build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(GeoFacility.Properties.builder().mobile(Boolean.FALSE).build().isEmpty()).isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .operatingStatus(
                    Facility.OperatingStatus.builder().additionalInfo("additional info").build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.Properties.builder()
                .operationalHoursSpecialInstructions(List.of("special instructions"))
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    assertThat(GeoFacility.builder().build().isEmpty()).isTrue();
    assertThat(
            GeoFacility.builder()
                .properties(GeoFacility.Properties.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            GeoFacility.builder()
                .geometry(GeoFacility.Geometry.builder().build())
                .build()
                .isEmpty())
        .isTrue();

    assertThat(GeoFacility.builder().type(GeoFacility.Type.Feature).build().isEmpty()).isFalse();
    assertThat(
            GeoFacility.builder()
                .geometry(
                    GeoFacility.Geometry.builder().type(GeoFacility.GeometryType.Point).build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacility.builder()
                .properties(GeoFacility.Properties.builder().name("test").build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void satisfactionAndWaitTimesPopulated() {
    String expectedJson = getExpectedJson("v1/GeoFacility/satisfactionAndWaitTimesPopulated.json");
    GeoFacility geoFacility =
        GeoFacility.builder()
            .properties(
                GeoFacility.Properties.builder()
                    .id("vha_402")
                    .address(
                        Facility.Addresses.builder()
                            .mailing(
                                Facility.Address.builder()
                                    .address1("50 Irving Street, Northwest")
                                    .address2("Bldg 2")
                                    .address3("Suite 7")
                                    .city("Washington")
                                    .state("DC")
                                    .zip("20422-0001")
                                    .build())
                            .build())
                    .satisfaction(
                        Facility.Satisfaction.builder()
                            .health(
                                Facility.PatientSatisfaction.builder()
                                    .primaryCareUrgent(BigDecimal.TEN)
                                    .build())
                            .effectiveDate(LocalDate.parse("2022-01-13"))
                            .build())
                    .waitTimes(
                        Facility.WaitTimes.builder()
                            .health(
                                List.of(
                                    Facility.PatientWaitTime.builder()
                                        .newPatientWaitTime(BigDecimal.ONE)
                                        .build()))
                            .effectiveDate(LocalDate.parse("2022-01-13"))
                            .build())
                    .build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(geoFacility))
        .isEqualToIgnoringWhitespace(expectedJson);
    expectedJson =
        getExpectedJson("v1/GeoFacility/satisfactionAndWaitTimesPopulatedOnlyAddress1.json");
    geoFacility =
        GeoFacility.builder()
            .properties(
                GeoFacility.Properties.builder()
                    .id("vha_402")
                    .address(
                        Facility.Addresses.builder()
                            .mailing(
                                Facility.Address.builder()
                                    .address1("50 Irving Street, Northwest")
                                    .build())
                            .build())
                    .satisfaction(
                        Facility.Satisfaction.builder()
                            .health(
                                Facility.PatientSatisfaction.builder()
                                    .primaryCareUrgent(BigDecimal.TEN)
                                    .build())
                            .effectiveDate(LocalDate.parse("2022-01-13"))
                            .build())
                    .waitTimes(
                        Facility.WaitTimes.builder()
                            .health(
                                List.of(
                                    Facility.PatientWaitTime.builder()
                                        .newPatientWaitTime(BigDecimal.ONE)
                                        .build()))
                            .effectiveDate(LocalDate.parse("2022-01-13"))
                            .build())
                    .build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(geoFacility))
        .isEqualToIgnoringWhitespace(expectedJson);
  }
}
