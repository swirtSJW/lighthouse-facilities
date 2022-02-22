package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilitiesControllerV1Test {
  FacilityRepository fr = mock(FacilityRepository.class);

  @Test
  @SneakyThrows
  void all() {
    FacilitySamples samples = FacilitySamples.defaultSamples();
    when(fr.findAll(FacilityRepository.FacilitySpecificationHelper.builder().build()))
        .thenReturn(
            new ArrayList<>(
                List.of(
                    samples.facilityEntity("vha_691GB"),
                    samples.facilityEntity("vha_740GA"),
                    samples.facilityEntity("vha_757"))));
    assertThat(
            controller()
                .jsonFacilities(
                    null, null, null, null, null, null, null, null, null, null, null, 1, 3))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(
                    List.of(
                        samples.facilityV1("vha_691GB"),
                        samples.facilityV1("vha_740GA"),
                        samples.facilityV1("vha_757")))
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v1/facilities?page=1&per_page=3")
                        .first("http://foo/bp/v1/facilities?page=1&per_page=3")
                        .prev(null)
                        .next(null)
                        .last("http://foo/bp/v1/facilities?page=1&per_page=3")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(3)
                                .totalPages(1)
                                .totalEntries(3)
                                .build())
                        .build())
                .build());
  }

  @Test
  void allCsv() {
    FacilitySamples samples = FacilitySamples.defaultSamples();
    when(fr.findAllProjectedBy())
        .thenReturn(
            List.of(
                samples.facilityEntity("vha_691GB"),
                samples.facilityEntity("vha_740GA"),
                samples.facilityEntity("vha_757")));
    String actual = controller().allCsv();
    List<String> actualLines = Splitter.onPattern("\\r?\\n").omitEmptyStrings().splitToList(actual);
    assertThat(actualLines.size()).isEqualTo(4);
    assertThat(actualLines.get(0)).isEqualTo(Joiner.on(",").join(CsvTransformerV0.HEADERS));
    assertThat(actualLines.get(1))
        .isEqualTo(
            "vha_691GB,Santa Barbara VA Clinic,691GB,34.4423637,-119.77646693,va_health_facility,"
                + "Primary Care CBOC,https://www.losangeles.va.gov/locations/directions-SB.asp,false,"
                + "22,4440 Calle Real,,,Santa Barbara,CA,93110-1002,,,,,,,805-683-1491,805-683-3631,"
                + "310-268-4449,800-952-4852,877-252-4866,818-895-9564,818-891-7711 x35894,800AM-430PM,"
                + "800AM-430PM,800AM-430PM,800AM-430PM,800AM-430PM,Closed,Closed,NORMAL,\"all day, every day\"");
    assertThat(actualLines.get(2))
        .isEqualTo(
            "vha_740GA,Harlingen VA Clinic-Treasure Hills,740GA,26.1745479800001,-97.6667188,va_health_facility,"
                + "Multi-Specialty CBOC,https://www.texasvalley.va.gov/locations/Harlingen_OPC.asp,false,"
                + "17,2106 Treasure Hills Boulevard,,,Harlingen,TX,78550-8736,,,,,,,956-366-4500,956-366-4595,"
                + "956-366-4526,877-752-0650,888-686-6350,956-366-4500 x67810,956-291-9791,800AM-430PM,"
                + "800AM-430PM,800AM-430PM,800AM-430PM,800AM-430PM,Closed,Closed,NORMAL,");
    assertThat(actualLines.get(3))
        .isEqualTo(
            "vha_757,Chalmers P. Wylie Veterans Outpatient Clinic,757,39.9813738,-82.9118322899999,va_health_facility,"
                + "Health Care Center (HCC),https://www.columbus.va.gov/locations/directions.asp,false,"
                + "10,\"420 North James, Road\",,,Columbus,OH,43219-1834,,,,,,,614-257-5200,614-257-5460,"
                + "614-257-5631,614-257-5230,614-257-5512,614-257-5290,614-257-5298,730AM-600PM,"
                + "730AM-600PM,730AM-600PM,730AM-600PM,730AM-600PM,800AM-400PM,800AM-400PM,NORMAL,");
  }

  private FacilitiesControllerV1 controller() {
    return FacilitiesControllerV1.builder()
        .facilityRepository(fr)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  @SneakyThrows
  void exceptions() {
    Method facilityMethod =
        FacilitiesControllerV1.class.getDeclaredMethod("facility", HasFacilityPayload.class);
    facilityMethod.setAccessible(true);
    HasFacilityPayload nullPayload = null;
    assertThatThrownBy(() -> facilityMethod.invoke(null, nullPayload))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException(
                "Cannot invoke \"gov.va.api.lighthouse.facilities.HasFacilityPayload.facility()\" because \"entity\" is null"));
    when(fr.findAll(FacilityRepository.FacilitySpecificationHelper.builder().build()))
        .thenThrow(new NullPointerException("oh noes"));
    assertThrows(
        NullPointerException.class,
        () ->
            controller()
                .jsonFacilities(
                    null, null, null, null, null, null, null, null, null, null, null, 1, 2));
    when(fr.findAllProjectedBy()).thenThrow(new NullPointerException("oh noes"));
    assertThrows(NullPointerException.class, () -> controller().allCsv());
  }

  @Test
  void facilityIdsByType() {
    when(fr.findAllIds())
        .thenReturn(
            List.of(
                FacilitySamples.defaultSamples().facilityEntity("vha_691GB").id(),
                FacilitySamples.defaultSamples().facilityEntity("vha_740GA").id(),
                FacilitySamples.defaultSamples().facilityEntity("vha_757").id()));
    assertThat(controller().facilityIdsByType("benefits").data()).isEmpty();
    assertThat(controller().facilityIdsByType("health").data())
        .usingRecursiveComparison()
        .isEqualTo(List.of("vha_691GB", "vha_740GA", "vha_757"));
  }

  @Test
  @SneakyThrows
  void invalidParameters() {
    Method jsonFacilities =
        FacilitiesControllerV1.class.getDeclaredMethod(
            "jsonFacilities",
            List.class,
            String.class,
            String.class,
            String.class,
            BigDecimal.class,
            BigDecimal.class,
            BigDecimal.class,
            String.class,
            List.class,
            Boolean.class,
            String.class,
            int.class,
            int.class);
    jsonFacilities.setAccessible(true);
    // Bounding Box is empty list
    assertThatThrownBy(
            () ->
                jsonFacilities.invoke(
                    controller(),
                    new ArrayList<BigDecimal>(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    1,
                    10))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new ExceptionsUtils.InvalidParameter("bbox", "[]"));
    // Facility Type is invalid
    assertThatThrownBy(
            () ->
                jsonFacilities.invoke(
                    controller(),
                    null,
                    null,
                    null,
                    "no_such_type",
                    BigDecimal.valueOf(0.0),
                    BigDecimal.valueOf(0.0),
                    null,
                    "fake_ids",
                    new ArrayList<String>(),
                    Boolean.FALSE,
                    null,
                    1,
                    10))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new ExceptionsUtils.InvalidParameter("type", "no_such_type"));
    assertThatThrownBy(
            () ->
                jsonFacilities.invoke(
                    controller(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of("these", "are", "fake", "services"),
                    Boolean.FALSE,
                    null,
                    1,
                    10))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new ExceptionsUtils.InvalidParameter("services", "these"));
    // Radius is less than zero
    assertThatThrownBy(
            () ->
                jsonFacilities.invoke(
                    controller(),
                    null,
                    null,
                    null,
                    null,
                    BigDecimal.valueOf(0.0),
                    BigDecimal.valueOf(0.0),
                    BigDecimal.valueOf(-2),
                    null,
                    new ArrayList<String>(),
                    Boolean.FALSE,
                    null,
                    1,
                    10))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new ExceptionsUtils.InvalidParameter("radius", "-2"));
  }

  @Test
  void jsonFacilitiesByBoundingBox() {
    when(fr.findAll(
            FacilityRepository.FacilitySpecificationHelper.builder()
                .boundingBox(
                    FacilityRepository.BoundingBoxSpecification.builder()
                        .minLongitude(BigDecimal.valueOf(-97.65).min(BigDecimal.valueOf(-97.67)))
                        .maxLongitude(BigDecimal.valueOf(-97.65).max(BigDecimal.valueOf(-97.67)))
                        .minLatitude(BigDecimal.valueOf(26.16).min(BigDecimal.valueOf(26.18)))
                        .maxLatitude(BigDecimal.valueOf(26.16).max(BigDecimal.valueOf(26.18)))
                        .build())
                .facilityType(
                    FacilityRepository.FacilityTypeSpecification.builder()
                        .facilityType(FacilityEntity.Type.vha)
                        .build())
                .services(
                    FacilityRepository.ServicesSpecification.builder()
                        .services(
                            ImmutableSet.copyOf(
                                List.of(
                                    Facility.HealthService.Cardiology,
                                    Facility.HealthService.Audiology,
                                    Facility.HealthService.Urology)))
                        .build())
                .mobile(
                    FacilityRepository.MobileSpecification.builder().mobile(Boolean.FALSE).build())
                .zip(FacilityRepository.ZipSpecification.builder().zip("32934").build())
                .state(FacilityRepository.StateSpecification.builder().state("FL").build())
                .visn(FacilityRepository.VisnSpecification.builder().visn("test_visn").build())
                .ids(
                    FacilityRepository.TypeServicesIdsSpecification.builder()
                        .ids(List.of(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "740GA")))
                        .build())
                .build()))
        .thenReturn(
            new ArrayList<>(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"))));
    assertThat(
            controller()
                .jsonFacilities(
                    List.of(
                        BigDecimal.valueOf(-97.65),
                        BigDecimal.valueOf(26.16),
                        BigDecimal.valueOf(-97.67),
                        BigDecimal.valueOf(26.18)),
                    "FL",
                    "32934",
                    "health",
                    null,
                    null,
                    null,
                    "vha_740GA",
                    List.of("cardiology", "audiology", "urology"),
                    Boolean.FALSE,
                    "test_visn",
                    1,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facilityV1("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?bbox%5B%5D=-97.65&bbox%5B%5D=26.16&bbox%5B%5D=-97.67&bbox%5B%5D=26.18&ids=vha_740GA&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&state=FL&type=health&visn=test_visn&zip=32934&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?bbox%5B%5D=-97.65&bbox%5B%5D=26.16&bbox%5B%5D=-97.67&bbox%5B%5D=26.18&ids=vha_740GA&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&state=FL&type=health&visn=test_visn&zip=32934&page=1&per_page=1")
                        .prev(null)
                        .next(null)
                        .last(
                            "http://foo/bp/v1/facilities?bbox%5B%5D=-97.65&bbox%5B%5D=26.16&bbox%5B%5D=-97.67&bbox%5B%5D=26.18&ids=vha_740GA&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&state=FL&type=health&visn=test_visn&zip=32934&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  void jsonFacilitiesByIds() {
    when(fr.findAll(
            FacilityRepository.FacilitySpecificationHelper.builder()
                .ids(
                    FacilityRepository.TypeServicesIdsSpecification.builder()
                        .ids(
                            List.of(
                                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB"),
                                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "740GA"),
                                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "757")))
                        .build())
                .build()))
        .thenReturn(
            new ArrayList<>(
                List.of(
                    FacilitySamples.defaultSamples().facilityEntity("vha_740GA"),
                    FacilitySamples.defaultSamples().facilityEntity("vha_691GB"),
                    FacilitySamples.defaultSamples().facilityEntity("vha_757"))));
    assertThat(
            controller()
                .jsonFacilities(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "x,vha_691GB,,x,,vha_740GA,vha_757",
                    null,
                    null,
                    null,
                    2,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facilityV1("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=2&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=1&per_page=1")
                        .prev(
                            "http://foo/bp/v1/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=1&per_page=1")
                        .next(
                            "http://foo/bp/v1/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=3&per_page=1")
                        .last(
                            "http://foo/bp/v1/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=3&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(2)
                                .entriesPerPage(1)
                                .totalPages(3)
                                .totalEntries(3)
                                .build())
                        .build())
                .build());
  }

  @Test
  void jsonFacilitiesByIds_perPageZero() {
    when(fr.findAll(
            FacilityRepository.FacilitySpecificationHelper.builder()
                .ids(
                    FacilityRepository.TypeServicesIdsSpecification.builder()
                        .ids(
                            List.of(
                                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB"),
                                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "740GA"),
                                FacilityEntity.Pk.of(FacilityEntity.Type.vha, "757")))
                        .build())
                .build()))
        .thenReturn(
            new ArrayList<>(
                List.of(
                    FacilitySamples.defaultSamples().facilityEntity("vha_740GA"),
                    FacilitySamples.defaultSamples().facilityEntity("vha_691GB"),
                    FacilitySamples.defaultSamples().facilityEntity("vha_757"))));
    assertThat(
            controller()
                .jsonFacilities(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "x,vha_691GB,,x,,vha_740GA,vha_757",
                    null,
                    null,
                    null,
                    2,
                    0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=2&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(2)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(3)
                                .build())
                        .build())
                .build());
  }

  @Test
  void jsonFacilitiesByLatLong() {
    when(fr.findAll(
            FacilityRepository.FacilitySpecificationHelper.builder()
                .ids(
                    FacilityRepository.TypeServicesIdsSpecification.builder()
                        .ids(List.of(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "740GA")))
                        .build())
                .facilityType(
                    FacilityRepository.FacilityTypeSpecification.builder()
                        .facilityType(FacilityEntity.Type.vha)
                        .build())
                .services(
                    FacilityRepository.ServicesSpecification.builder()
                        .services(
                            ImmutableSet.copyOf(
                                List.of(
                                    Facility.HealthService.Cardiology,
                                    Facility.HealthService.Audiology,
                                    Facility.HealthService.Urology)))
                        .build())
                .mobile(
                    FacilityRepository.MobileSpecification.builder().mobile(Boolean.FALSE).build())
                .build()))
        .thenReturn(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_740GA")));
    // Query for facilities without constraining to a specified radius
    assertThat(
            controller()
                .jsonFacilities(
                    null,
                    null,
                    null,
                    "health",
                    BigDecimal.valueOf(26.1745479800001),
                    BigDecimal.valueOf(-97.6667188),
                    null,
                    "vha_740GA",
                    List.of("cardiology", "audiology", "urology"),
                    Boolean.FALSE,
                    null,
                    1,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facilityV1("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=26.1745479800001&long=-97.6667188&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=26.1745479800001&long=-97.6667188&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .prev(null)
                        .next(null)
                        .last(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=26.1745479800001&long=-97.6667188&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .distances(
                            List.of(
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_740GA")
                                    .distance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN))
                                    .build()))
                        .build())
                .build());
    // Given that each degree of latitude is approximately 69 miles, query for facilities within a
    // 75 mile radius of (27.1745479800001, -97.6667188), which is north of VA Health Care Center in
    // Harlingen, TX: (26.1745479800001, -97.6667188). Confirm that one facility is found in current
    // test scenario.
    assertThat(
            controller()
                .jsonFacilities(
                    null,
                    null,
                    null,
                    "health",
                    BigDecimal.valueOf(27.1745479800001),
                    BigDecimal.valueOf(-97.6667188),
                    BigDecimal.valueOf(75),
                    "vha_740GA",
                    List.of("cardiology", "audiology", "urology"),
                    Boolean.FALSE,
                    null,
                    1,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facilityV1("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=27.1745479800001&long=-97.6667188&mobile=false&radius=75&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=27.1745479800001&long=-97.6667188&mobile=false&radius=75&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .prev(null)
                        .next(null)
                        .last(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=27.1745479800001&long=-97.6667188&mobile=false&radius=75&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .distances(
                            List.of(
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_740GA")
                                    .distance(
                                        BigDecimal.valueOf(69.09)
                                            .setScale(2, RoundingMode.HALF_EVEN))
                                    .build()))
                        .build())
                .build());
    // Query for facilities within 50 miles of (27.1745479800001, -97.6667188). Confirm no
    // facilities are found in current test scenario.
    assertThat(
            controller()
                .jsonFacilities(
                    null,
                    null,
                    null,
                    "health",
                    BigDecimal.valueOf(27.1745479800001),
                    BigDecimal.valueOf(-97.6667188),
                    BigDecimal.valueOf(50),
                    "vha_740GA",
                    List.of("cardiology", "audiology", "urology"),
                    Boolean.FALSE,
                    null,
                    1,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=27.1745479800001&long=-97.6667188&mobile=false&radius=50&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=27.1745479800001&long=-97.6667188&mobile=false&radius=50&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .prev(null)
                        .next(null)
                        .last(
                            "http://foo/bp/v1/facilities?ids=vha_740GA&lat=27.1745479800001&long=-97.6667188&mobile=false&radius=50&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(0)
                                .build())
                        .distances(emptyList())
                        .build())
                .build());
  }

  @Test
  void jsonFacilitiesByLatLongWithBoundingBox() {
    // When lat/long and bbox are both included, no distance sorting should be performed
    // and the normal result should be returned
    when(fr.findAll(
            FacilityRepository.FacilitySpecificationHelper.builder()
                .boundingBox(
                    FacilityRepository.BoundingBoxSpecification.builder()
                        .minLongitude(BigDecimal.valueOf(-97.65).min(BigDecimal.valueOf(-97.67)))
                        .maxLongitude(BigDecimal.valueOf(-97.65).max(BigDecimal.valueOf(-97.67)))
                        .minLatitude(BigDecimal.valueOf(26.16).min(BigDecimal.valueOf(26.18)))
                        .maxLatitude(BigDecimal.valueOf(26.16).max(BigDecimal.valueOf(26.18)))
                        .build())
                .ids(
                    FacilityRepository.TypeServicesIdsSpecification.builder()
                        .ids(List.of(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "740GA")))
                        .build())
                .facilityType(
                    FacilityRepository.FacilityTypeSpecification.builder()
                        .facilityType(FacilityEntity.Type.vha)
                        .build())
                .services(
                    FacilityRepository.ServicesSpecification.builder()
                        .services(
                            ImmutableSet.copyOf(
                                List.of(
                                    Facility.HealthService.Cardiology,
                                    Facility.HealthService.Audiology,
                                    Facility.HealthService.Urology)))
                        .build())
                .mobile(
                    FacilityRepository.MobileSpecification.builder().mobile(Boolean.FALSE).build())
                .build()))
        .thenReturn(
            new ArrayList<>(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"))));
    assertThat(
            controller()
                .jsonFacilities(
                    List.of(
                        BigDecimal.valueOf(-97.65),
                        BigDecimal.valueOf(26.16),
                        BigDecimal.valueOf(-97.67),
                        BigDecimal.valueOf(26.18)),
                    null,
                    null,
                    "health",
                    BigDecimal.valueOf(26.1745479800001),
                    BigDecimal.valueOf(-97.6667188),
                    null,
                    "vha_740GA",
                    List.of("cardiology", "audiology", "urology"),
                    Boolean.FALSE,
                    null,
                    1,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facilityV1("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?bbox%5B%5D=-97.65&bbox%5B%5D=26.16&bbox%5B%5D=-97.67&bbox%5B%5D=26.18&ids=vha_740GA&lat=26.1745479800001&long=-97.6667188&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?bbox%5B%5D=-97.65&bbox%5B%5D=26.16&bbox%5B%5D=-97.67&bbox%5B%5D=26.18&ids=vha_740GA&lat=26.1745479800001&long=-97.6667188&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .prev(null)
                        .next(null)
                        .last(
                            "http://foo/bp/v1/facilities?bbox%5B%5D=-97.65&bbox%5B%5D=26.16&bbox%5B%5D=-97.67&bbox%5B%5D=26.18&ids=vha_740GA&lat=26.1745479800001&long=-97.6667188&mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .distances(null)
                        .build())
                .build());
  }

  @Test
  void jsonFacilitiesByState() {
    when(fr.findAll(
            FacilityRepository.FacilitySpecificationHelper.builder()
                .state(FacilityRepository.StateSpecification.builder().state("FL").build())
                .facilityType(
                    FacilityRepository.FacilityTypeSpecification.builder()
                        .facilityType(FacilityEntity.Type.vha)
                        .build())
                .services(
                    FacilityRepository.ServicesSpecification.builder()
                        .services(
                            ImmutableSet.copyOf(
                                List.of(
                                    Facility.HealthService.Cardiology,
                                    Facility.HealthService.Audiology,
                                    Facility.HealthService.Urology)))
                        .build())
                .mobile(
                    FacilityRepository.MobileSpecification.builder().mobile(Boolean.FALSE).build())
                .build()))
        .thenReturn(
            new ArrayList<>(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"))));
    assertThat(
            controller()
                .jsonFacilities(
                    null,
                    "FL",
                    null,
                    "health",
                    null,
                    null,
                    null,
                    null,
                    List.of("cardiology", "audiology", "urology"),
                    Boolean.FALSE,
                    null,
                    1,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facilityV1("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&state=FL&type=health&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&state=FL&type=health&page=1&per_page=1")
                        .prev(null)
                        .next(null)
                        .last(
                            "http://foo/bp/v1/facilities?mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&state=FL&type=health&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  void jsonFacilitiesByVisn() {
    when(fr.findAll(
            FacilityRepository.FacilitySpecificationHelper.builder()
                .visn(FacilityRepository.VisnSpecification.builder().visn("test_visn").build())
                .build()))
        .thenReturn(
            new ArrayList<>(List.of(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"))));
    assertThat(
            controller()
                .jsonFacilities(
                    null, null, null, null, null, null, null, null, null, null, "test_visn", 1, 1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facilityV1("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v1/facilities?visn=test_visn&page=1&per_page=1")
                        .first("http://foo/bp/v1/facilities?visn=test_visn&page=1&per_page=1")
                        .prev(null)
                        .next(null)
                        .last("http://foo/bp/v1/facilities?visn=test_visn&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  void jsonFacilitiesByZip() {
    when(fr.findAll(
            FacilityRepository.FacilitySpecificationHelper.builder()
                .zip(FacilityRepository.ZipSpecification.builder().zip("32934").build())
                .mobile(
                    FacilityRepository.MobileSpecification.builder().mobile(Boolean.FALSE).build())
                .services(
                    FacilityRepository.ServicesSpecification.builder()
                        .services(
                            ImmutableSet.copyOf(
                                List.of(
                                    Facility.HealthService.Cardiology,
                                    Facility.HealthService.Audiology,
                                    Facility.HealthService.Urology)))
                        .build())
                .facilityType(
                    FacilityRepository.FacilityTypeSpecification.builder()
                        .facilityType(FacilityEntity.Type.vha)
                        .build())
                .build()))
        .thenReturn(
            new ArrayList<>(
                List.of((FacilitySamples.defaultSamples().facilityEntity("vha_740GA")))));
    assertThat(
            controller()
                .jsonFacilities(
                    null,
                    null,
                    "32934",
                    "health",
                    null,
                    null,
                    null,
                    null,
                    List.of("cardiology", "audiology", "urology"),
                    Boolean.FALSE,
                    null,
                    1,
                    1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facilityV1("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v1/facilities?mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&zip=32934&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v1/facilities?mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&zip=32934&page=1&per_page=1")
                        .prev(null)
                        .next(null)
                        .last(
                            "http://foo/bp/v1/facilities?mobile=false&services%5B%5D=cardiology&services%5B%5D=audiology&services%5B%5D=urology&type=health&zip=32934&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  @SneakyThrows
  void latLongMissingValue() {
    Method jsonFacilities =
        FacilitiesControllerV1.class.getDeclaredMethod(
            "jsonFacilities",
            List.class,
            String.class,
            String.class,
            String.class,
            BigDecimal.class,
            BigDecimal.class,
            BigDecimal.class,
            String.class,
            List.class,
            Boolean.class,
            String.class,
            int.class,
            int.class);
    jsonFacilities.setAccessible(true);
    assertThatThrownBy(
            () ->
                jsonFacilities.invoke(
                    controller(),
                    null,
                    null,
                    null,
                    null,
                    BigDecimal.valueOf(0.0),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    1,
                    10))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new ExceptionsUtils.ParameterInvalidWithoutOthers("latitude", List.of("longitude")));
    assertThatThrownBy(
            () ->
                jsonFacilities.invoke(
                    controller(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    BigDecimal.valueOf(0.0),
                    null,
                    null,
                    null,
                    null,
                    null,
                    1,
                    10))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new ExceptionsUtils.ParameterInvalidWithoutOthers("longitude", List.of("latitude")));
    assertThatThrownBy(
            () ->
                jsonFacilities.invoke(
                    controller(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    BigDecimal.valueOf(0.0),
                    null,
                    null,
                    null,
                    null,
                    1,
                    10))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new ExceptionsUtils.ParameterInvalidWithoutOthers(
                "radius", List.of("latitude, longitude")));
  }

  @Test
  void readJson() {
    Facility facility = FacilitySamples.defaultSamples().facilityV1("vha_691GB");
    FacilityEntity entity = FacilitySamples.defaultSamples().facilityEntity("vha_691GB");
    when(fr.findById(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "691GB")))
        .thenReturn(Optional.of(entity));
    assertThat(controller().readJson("vha_691GB"))
        .isEqualTo(FacilityReadResponse.builder().facility(facility).build());
  }

  @Test
  void readJson_malformed() {
    assertThrows(ExceptionsUtils.NotFound.class, () -> controller().readJson("xxx"));
  }

  @Test
  void readJson_notFound() {
    assertThrows(ExceptionsUtils.NotFound.class, () -> controller().readJson("vha_691GB"));
  }
}
