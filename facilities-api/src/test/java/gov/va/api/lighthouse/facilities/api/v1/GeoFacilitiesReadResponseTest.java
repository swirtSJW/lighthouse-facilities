package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class GeoFacilitiesReadResponseTest {
  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(GeoFacilityReadResponse.builder().build().isEmpty()).isTrue();
    assertThat(
            GeoFacilityReadResponse.builder()
                .geometry(GeoFacility.Geometry.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            GeoFacilityReadResponse.builder()
                .properties(GeoFacility.Properties.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(GeoFacilityReadResponse.builder().type(GeoFacility.Type.Feature).build().isEmpty())
        .isFalse();
    assertThat(
            GeoFacilityReadResponse.builder()
                .geometry(
                    GeoFacility.Geometry.builder().type(GeoFacility.GeometryType.Point).build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            GeoFacilityReadResponse.builder()
                .properties(
                    GeoFacility.Properties.builder()
                        .facilityType(Facility.FacilityType.va_health_facility)
                        .build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void of() {
    GeoFacility orig =
        GeoFacility.builder()
            .type(GeoFacility.Type.Feature)
            .geometry(GeoFacility.Geometry.builder().type(GeoFacility.GeometryType.Point).build())
            .properties(
                GeoFacility.Properties.builder()
                    .facilityType(Facility.FacilityType.va_health_facility)
                    .build())
            .build();
    GeoFacilityReadResponse copy = GeoFacilityReadResponse.of(orig);
    assertThat(copy.type()).usingRecursiveComparison().isEqualTo(orig.type());
    assertThat(copy.properties()).usingRecursiveComparison().isEqualTo(orig.properties());
    assertThat(copy.geometry()).usingRecursiveComparison().isEqualTo(orig.geometry());

    assertThatThrownBy(() -> GeoFacilityReadResponse.of(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("facility is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void satisfactionAndWaitTimesPopulated() {
    String expectedJson =
        getExpectedJson("v1/GeoFacilityReadResponse/satisfactionAndWaitTimesPopulated.json");
    GeoFacilityReadResponse readResponse =
        GeoFacilityReadResponse.builder()
            .properties(
                GeoFacility.Properties.builder()
                    .id("nca_s402")
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
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(readResponse))
        .isEqualToIgnoringWhitespace(expectedJson);
    expectedJson =
        getExpectedJson(
            "v1/GeoFacilityReadResponse/satisfactionAndWaitTimesPopulatedOnlyAddress1.json");
    readResponse =
        GeoFacilityReadResponse.builder()
            .properties(
                GeoFacility.Properties.builder()
                    .id("nca_s402")
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
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(readResponse))
        .isEqualToIgnoringWhitespace(expectedJson);
  }
}
