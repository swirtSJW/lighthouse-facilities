package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ValidationV0Test {
  private static final Validator VALIDATOR =
      Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void all() {
    String path = "/all.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @SneakyThrows
  private <T> void assertValid(String path, Class<T> clazz) {
    T response = createMapper().readValue(getClass().getResourceAsStream(path), clazz);
    Set<ConstraintViolation<T>> violations = VALIDATOR.validate(response);
    assertThat(violations).isEmpty();
  }

  @Test
  void nearby() {
    String path = "/nearby.json";
    assertValid(path, NearbyResponse.class);
  }

  @Test
  @SneakyThrows
  void readBenefits() {
    String path = "/read-benefits.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readBenefitsGeoJson() {
    String path = "/read-benefits-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readCemetery() {
    String path = "/read-cemetery.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readCemeteryGeoJson() {
    String path = "/read-cemetery-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  void readHealth() {
    String path = "/read-health.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  void readHealthGeoJson() {
    String path = "/read-health-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readStateCemetery() {
    String path = "/read-state-cemetery.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readStateCemeteryGeoJson() {
    String path = "/read-state-cemetery-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  void readVetCenter() {
    String path = "/read-vet-center.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  void readVetCenterGeoJson() {
    String path = "/read-vet-center-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  void searchByBbox() {
    String path = "/search-bbox.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByBboxGeoJson() {
    String path = "/search-bbox-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @Test
  void searchByIds() {
    String path = "/search-ids.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByIdsGeoJson() {
    String path = "/search-ids-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @Test
  void searchByLatLong() {
    String path = "/search-lat-long.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongGeoJson() {
    String path = "/search-lat-long-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @Test
  void searchByState() {
    String path = "/search-state.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByStateGeoJson() {
    String path = "/search-state-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @Test
  void searchByZip() {
    String path = "/search-zip.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByZipGeoJson() {
    String path = "/search-zip-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }
}
