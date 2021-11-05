package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.InternalFacilitiesController.*;
import static gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityType.va_health_facility;
import static gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityType.vet_center;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.FacilityPair;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Address;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Addresses;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Services;
import gov.va.api.lighthouse.facilities.api.v0.ReloadResponse;
import gov.va.api.lighthouse.facilities.collector.FacilitiesCollector;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InternalFacilitiesControllerTest {
  @Autowired FacilityRepository facilityRepository;

  @Autowired CmsOverlayRepository overlayRepository;

  @Autowired FacilityGraveyardRepository graveyardRepository;

  FacilitiesCollector collector = mock(FacilitiesCollector.class);

  private static Facility _facility(
      String id,
      String state,
      String zip,
      double latitude,
      double longitude,
      List<HealthService> health) {
    return Facility.builder()
        .id(id)
        .attributes(
            FacilityAttributes.builder()
                .address(
                    Addresses.builder()
                        .physical(Address.builder().state(state).zip(zip).build())
                        .build())
                .latitude(BigDecimal.valueOf(latitude))
                .longitude(BigDecimal.valueOf(longitude))
                .services(Services.builder().health(health).build())
                .mobile(false)
                .facilityType(Facility.FacilityType.va_cemetery)
                .build())
        .build();
  }

  private static gov.va.api.lighthouse.facilities.api.v1.Facility _facilityV1(
      String id,
      String state,
      String zip,
      double latitude,
      double longitude,
      List<gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService> health) {
    return gov.va.api.lighthouse.facilities.api.v1.Facility.builder()
        .id(id)
        .attributes(
            gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityAttributes.builder()
                .address(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses.builder()
                        .physical(
                            gov.va.api.lighthouse.facilities.api.v1.Facility.Address.builder()
                                .state(state)
                                .zip(zip)
                                .build())
                        .build())
                .latitude(BigDecimal.valueOf(latitude))
                .longitude(BigDecimal.valueOf(longitude))
                .services(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.Services.builder()
                        .health(health)
                        .build())
                .mobile(false)
                .facilityType(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType.va_cemetery)
                .build())
        .build();
  }

  private static CmsOverlay _overlay() {
    return CmsOverlay.builder()
        .operatingStatus(_overlay_operating_status())
        .detailedServices(_overlay_detailed_services())
        .build();
  }

  private static List<DetailedService> _overlay_detailed_services() {
    return List.of(
        DetailedService.builder()
            .active(true)
            .name("Covid19Vaccine")
            .descriptionFacility(null)
            .appointmentLeadIn("Your VA health care team will contact you if you...more text")
            .onlineSchedulingAvailable("True")
            .phoneNumbers(
                List.of(
                    DetailedService.AppointmentPhoneNumber.builder()
                        .extension("123")
                        .label("Main phone")
                        .number("555-555-1212")
                        .type("tel")
                        .build()))
            .referralRequired("True")
            .walkInsAccepted("False")
            .serviceLocations(
                List.of(
                    DetailedService.DetailedServiceLocation.builder()
                        .serviceLocationAddress(
                            DetailedService.DetailedServiceAddress.builder()
                                .buildingNameNumber("Baxter Building")
                                .clinicName("Baxter Clinic")
                                .wingFloorOrRoomNumber("Wing East")
                                .address1("122 Main St.")
                                .address2(null)
                                .city("Rochester")
                                .state("NY")
                                .zipCode("14623-1345")
                                .countryCode("US")
                                .build())
                        .appointmentPhoneNumbers(
                            List.of(
                                DetailedService.AppointmentPhoneNumber.builder()
                                    .extension("567")
                                    .label("Alt phone")
                                    .number("556-565-1119")
                                    .type("tel")
                                    .build()))
                        .emailContacts(
                            List.of(
                                DetailedService.DetailedServiceEmailContact.builder()
                                    .emailAddress("georgea@va.gov")
                                    .emailLabel("George Anderson")
                                    .build()))
                        .facilityServiceHours(
                            DetailedService.DetailedServiceHours.builder()
                                .monday("8:30AM-7:00PM")
                                .tuesday("8:30AM-7:00PM")
                                .wednesday("8:30AM-7:00PM")
                                .thursday("8:30AM-7:00PM")
                                .friday("8:30AM-7:00PM")
                                .saturday("8:30AM-7:00PM")
                                .sunday("CLOSED")
                                .build())
                        .additionalHoursInfo("Please call for an appointment outside...")
                        .build()))
            .build());
  }

  private static Facility.OperatingStatus _overlay_operating_status() {
    return Facility.OperatingStatus.builder()
        .code(Facility.OperatingStatusCode.LIMITED)
        .additionalInfo("Limited")
        .build();
  }

  private InternalFacilitiesController _controller() {
    return InternalFacilitiesController.builder()
        .collector(collector)
        .facilityRepository(facilityRepository)
        .cmsOverlayRepository(overlayRepository)
        .graveyardRepository(graveyardRepository)
        .build();
  }

  private FacilityEntity _facilityEntity(
      Facility fac, gov.va.api.lighthouse.facilities.api.v1.Facility facV1) {
    return _facilityEntity(fac, facV1, null);
  }

  @SneakyThrows
  private FacilityEntity _facilityEntity(
      Facility fac, gov.va.api.lighthouse.facilities.api.v1.Facility facV1, CmsOverlay overlay) {
    String operatingStatusString = null;
    Set<String> cmsServicesNames = new HashSet<>();
    String cmsServicesString = null;
    if (overlay != null) {
      operatingStatusString =
          overlay.operatingStatus() == null
              ? null
              : JacksonConfig.createMapper().writeValueAsString(overlay.operatingStatus());
      cmsServicesString =
          overlay.detailedServices() == null
              ? null
              : JacksonConfig.createMapper().writeValueAsString(overlay.detailedServices());
      if (overlay.detailedServices() != null) {
        for (DetailedService service : overlay.detailedServices()) {
          if (service.active()) {
            cmsServicesNames.add(service.name());
          }
        }
      }
    }
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(fac.id()))
            .cmsOperatingStatus(operatingStatusString)
            .overlayServices(cmsServicesNames)
            .cmsServices(cmsServicesString)
            .lastUpdated(Instant.now())
            .build(),
        FacilityPair.builder().v0(fac).v1(facV1).build());
  }

  @SneakyThrows
  private FacilityGraveyardEntity _graveyardEntityWithOverlay(
      Facility fac, gov.va.api.lighthouse.facilities.api.v1.Facility facV1, CmsOverlay overlay) {
    String operatingStatusString = null;
    Set<String> cmsServicesNames = new HashSet<>();
    String cmsServicesString = null;
    if (overlay != null) {
      operatingStatusString =
          overlay.operatingStatus() == null
              ? null
              : JacksonConfig.createMapper().writeValueAsString(overlay.operatingStatus());
      if (overlay.detailedServices() != null) {
        for (DetailedService service : overlay.detailedServices()) {
          if (service.active()) {
            cmsServicesNames.add(service.name());
          }
        }
      }
      cmsServicesString =
          overlay.detailedServices() == null
              ? null
              : JacksonConfig.createMapper().writeValueAsString(overlay.detailedServices());
    }
    return FacilityGraveyardEntity.builder()
        .id(FacilityEntity.Pk.fromIdString(fac.id()))
        .facility(FacilitiesJacksonConfigV0.createMapper().writeValueAsString(fac))
        .cmsOperatingStatus(operatingStatusString)
        .graveyardOverlayServices(cmsServicesNames)
        .cmsServices(cmsServicesString)
        .missingTimestamp(LocalDateTime.now().minusDays(4).toInstant(ZoneOffset.UTC).toEpochMilli())
        .lastUpdated(Instant.now())
        .build();
  }

  @SneakyThrows
  private CmsOverlayEntity _overlayEntity(CmsOverlay overlay, String id) {
    return CmsOverlayEntity.builder()
        .id(FacilityEntity.Pk.fromIdString(id))
        .cmsOperatingStatus(
            overlay.operatingStatus() == null
                ? null
                : JacksonConfig.createMapper().writeValueAsString(overlay.operatingStatus()))
        .cmsServices(
            overlay.detailedServices() == null || overlay.detailedServices().isEmpty()
                ? null
                : JacksonConfig.createMapper().writeValueAsString(overlay.detailedServices()))
        .build();
  }

  @Test
  @SneakyThrows
  void collect_createUpdate() {
    Facility f1 =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    Facility f2 = _facility("vha_f2", "NEAT", "32934", 5.6, 6.7, List.of(HealthService.UrgentCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2V1 =
        _facilityV1(
            "vha_f2",
            "NEAT",
            "32934",
            5.6,
            6.7,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare));
    List<FacilityPair> facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f1).v1(f1V1).build());
    facilityPairs.add(FacilityPair.builder().v0(f2).v1(f2V1).build());
    Facility f2Old =
        _facility("vha_f2", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2OldV1 =
        _facilityV1(
            "vha_f2",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    facilityRepository.save(_facilityEntity(f2Old, f2OldV1));
    when(collector.collectFacilities()).thenReturn(facilityPairs);
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesCreated()).isEqualTo(List.of("vha_f1"));
    assertThat(response.facilitiesUpdated()).isEqualTo(List.of("vha_f2"));
    RecursiveComparisonConfiguration comparisonConfig =
        RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("version", "lastUpdated")
            .build();
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(comparisonConfig)
        .containsExactlyInAnyOrder(_facilityEntity(f1, f1V1), _facilityEntity(f2, f2V1));
  }

  @Test
  @SneakyThrows
  void collect_fromTheGraveyard() {
    Facility f1 =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    Facility f1Old =
        _facility("vha_f1", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1OldV1 =
        _facilityV1(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    List<FacilityPair> facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f1).v1(f1V1).build());
    FacilityGraveyardEntity entity = _graveyardEntityWithOverlay(f1Old, f1OldV1, _overlay());
    graveyardRepository.save(entity);
    when(collector.collectFacilities()).thenReturn(facilityPairs);
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesRevived()).isEqualTo(List.of("vha_f1"));
    assertThat(graveyardRepository.findAll()).isEmpty();
    FacilityEntity result = Iterables.getOnlyElement(facilityRepository.findAll());
    assertThat(result.id()).isEqualTo(entity.id());
    assertThat(result.facility())
        .isEqualTo(FacilitiesJacksonConfigV0.createMapper().writeValueAsString(f1));
    assertThat(result.cmsOperatingStatus()).isEqualTo(entity.cmsOperatingStatus());
    assertThat(result.overlayServices()).isEqualTo(entity.graveyardOverlayServices());
    assertThat(result.cmsServices()).isEqualTo(entity.cmsServices());
    assertThat(result.missingTimestamp()).isNull();
    assertThat(result.lastUpdated()).isEqualTo(response.timing().completeCollection());
  }

  @Test
  @SneakyThrows
  void collect_invalidLatLong() {
    Facility f1 =
        _facility("vha_f1", "FL", "999", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    f1.attributes().latitude(null);
    f1.attributes().longitude(null);
    f1V1.attributes().latitude(null);
    f1V1.attributes().longitude(null);
    List<FacilityPair> facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f1).v1(f1V1).build());
    when(collector.collectFacilities()).thenReturn(facilityPairs);
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.problems())
        .isEqualTo(List.of(ReloadResponse.Problem.of("vha_f1", "Missing coordinates")));
  }

  @Test
  @SneakyThrows
  void collect_missing() {
    Facility f1 =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    Facility f1Old =
        _facility("vha_f1", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    Facility f2Old =
        _facility("vha_f2", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    Facility f3Old =
        _facility("vha_f3", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    Facility f4Old =
        _facility("vha_f4", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1OldV1 =
        _facilityV1(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2OldV1 =
        _facilityV1(
            "vha_f2",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f3OldV1 =
        _facilityV1(
            "vha_f3",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f4OldV1 =
        _facilityV1(
            "vha_f4",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    List<FacilityPair> facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f1).v1(f1V1).build());
    facilityRepository.save(_facilityEntity(f1Old, f1OldV1));
    facilityRepository.save(_facilityEntity(f2Old, f2OldV1));
    facilityRepository.save(_facilityEntity(f3Old, f3OldV1));
    facilityRepository.save(_facilityEntity(f4Old, f4OldV1));
    when(collector.collectFacilities()).thenReturn(facilityPairs);
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesUpdated()).isEqualTo(List.of("vha_f1"));
    assertThat(response.facilitiesMissing()).isEqualTo(List.of("vha_f2", "vha_f3", "vha_f4"));
    List<FacilityEntity> findAll = ImmutableList.copyOf(facilityRepository.findAll());
    assertThat(findAll).hasSize(4);
    assertThat(findAll.get(0).missingTimestamp()).isNull();
    assertThat(findAll.get(0).services()).isEqualTo(Set.of("MentalHealthCare"));
    assertThat(findAll.get(1).missingTimestamp()).isNotNull();
    assertThat(findAll.get(2).missingTimestamp()).isNotNull();
    assertThat(findAll.get(3).missingTimestamp()).isNotNull();
  }

  @Test
  @SneakyThrows
  void collect_missingComesBack() {
    Facility f1 =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    Facility f1Old =
        _facility("vha_f1", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1OldV1 =
        _facilityV1(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    List<FacilityPair> facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f1).v1(f1V1).build());
    facilityRepository.save(
        _facilityEntity(f1Old, f1OldV1).missingTimestamp(Instant.now().toEpochMilli()));
    when(collector.collectFacilities()).thenReturn(facilityPairs);
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesUpdated()).isEqualTo(List.of("vha_f1"));
    FacilityEntity result = Iterables.getOnlyElement(facilityRepository.findAll());
    assertThat(result.missingTimestamp()).isNull();
    assertThat(result.services()).isEqualTo(Set.of("MentalHealthCare"));
  }

  @Test
  @SneakyThrows
  void collect_missingTimestampPreserved() {
    Facility f1Old =
        _facility("vha_f1", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1OldV1 =
        _facilityV1(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    long early = Instant.now().minusSeconds(60).toEpochMilli();
    facilityRepository.save(_facilityEntity(f1Old, f1OldV1).missingTimestamp(early));
    when(collector.collectFacilities()).thenReturn(emptyList());
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesMissing()).isEqualTo(List.of("vha_f1"));
    FacilityEntity result = Iterables.getOnlyElement(facilityRepository.findAll());
    assertThat(result.missingTimestamp()).isEqualTo(early);
  }

  @Test
  @SneakyThrows
  void collect_noStateOrZip() {
    Facility f1 =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    f1.attributes().address().physical().state(null);
    f1.attributes().address().physical().zip(null);
    f1.attributes().latitude(BigDecimal.valueOf(91.4));
    f1.attributes().longitude(BigDecimal.valueOf(181.4));
    f1V1.attributes().address().physical().state(null);
    f1V1.attributes().address().physical().zip(null);
    f1V1.attributes().latitude(BigDecimal.valueOf(91.4));
    f1V1.attributes().longitude(BigDecimal.valueOf(181.4));
    List<FacilityPair> facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f1).v1(f1V1).build());
    when(collector.collectFacilities()).thenReturn(facilityPairs);
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesCreated()).isEqualTo(List.of("vha_f1"));
    assertThat(response.problems())
        .isEqualTo(
            List.of(
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid physical address zip"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address state"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address city"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address street information"),
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid mailing address zip"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address state"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address city"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address street information"),
                ReloadResponse.Problem.of("vha_f1", "Missing main phone number"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Monday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Tuesday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Wednesday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Thursday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Friday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Saturday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Sunday"),
                ReloadResponse.Problem.of("vha_f1", "Missing classification"),
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid location latitude"),
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid location longitude")));
  }

  @Test
  @SneakyThrows
  void collect_noVISN() {
    Facility f1 = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    f1.attributes().facilityType(va_health_facility);
    f1V1.attributes()
        .facilityType(
            gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType.va_health_facility);
    List<FacilityPair> facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f1).v1(f1V1).build());
    when(collector.collectFacilities()).thenReturn(facilityPairs);
    ReloadResponse responseHealth = _controller().reload().getBody();
    assertThat(responseHealth.facilitiesCreated()).isEqualTo(List.of("vha_f1"));
    assertThat(responseHealth.problems())
        .contains(ReloadResponse.Problem.of("vha_f1", "Missing VISN"));
    Facility f2 = _facility("vc_f1", "FL", "32934", 91.4, 181.4, List.of());
    gov.va.api.lighthouse.facilities.api.v1.Facility f2V1 =
        _facilityV1("vc_f1", "FL", "32934", 91.4, 181.4, List.of());
    f2.attributes().facilityType(vet_center);
    f2V1.attributes()
        .facilityType(gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType.vet_center);
    facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f2).v1(f2V1).build());
    when(collector.collectFacilities()).thenReturn(facilityPairs);
    ReloadResponse responseVetCenter = _controller().reload().getBody();
    assertThat(responseVetCenter.facilitiesCreated()).isEqualTo(List.of("vc_f1"));
    assertThat(responseVetCenter.problems())
        .contains(ReloadResponse.Problem.of("vc_f1", "Missing VISN"));
  }

  @Test
  @SneakyThrows
  void collect_toTheGraveyard() {
    Facility f1Old =
        _facility("vha_f1", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1OldV1 =
        _facilityV1(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    long threeDaysAgo = LocalDateTime.now().minusDays(3).toInstant(ZoneOffset.UTC).toEpochMilli();
    FacilityEntity entity =
        _facilityEntity(f1Old, f1OldV1, _overlay()).missingTimestamp(threeDaysAgo);
    facilityRepository.save(entity);
    when(collector.collectFacilities()).thenReturn(emptyList());
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesRemoved()).isEqualTo(List.of("vha_f1"));
    assertThat(facilityRepository.findAllIds()).isEmpty();
    FacilityGraveyardEntity result = Iterables.getOnlyElement(graveyardRepository.findAll());
    assertThat(result.id()).isEqualTo(entity.id());
    assertThat(result.facility()).isEqualTo(entity.facility());
    assertThat(result.cmsOperatingStatus()).isEqualTo(entity.cmsOperatingStatus());
    assertThat(result.graveyardOverlayServices()).isEqualTo(entity.overlayServices());
    assertThat(result.cmsServices()).isEqualTo(entity.cmsServices());
    assertThat(result.missingTimestamp()).isEqualTo(threeDaysAgo);
    assertThat(result.lastUpdated()).isEqualTo(response.timing().completeCollection());
  }

  @Test
  void deleteFacilityByIdWithOverlay() {
    Facility f =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    facilityRepository.save(_facilityEntity(f, f1V1, _overlay()));
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    ResponseEntity<String> response = _controller().deleteFacilityById("vha_f1");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(facilityRepository.findAll()).isEmpty();
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo((List.of(_overlayEntity(_overlay(), "vha_f1"))));
  }

  @Test
  void deleteFacilityByIdWithoutOverlay() {
    Facility f =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    facilityRepository.save(_facilityEntity(f, f1V1));
    ResponseEntity<String> response = _controller().deleteFacilityById("vha_f1");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(facilityRepository.findAll()).isEmpty();
  }

  @Test
  void deleteFacilityOverlayById() {
    Facility f =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    ResponseEntity<Void> response = null;
    facilityRepository.save(_facilityEntity(f, f1V1, _overlay()));
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "operating_status");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    RecursiveComparisonConfiguration facilityEntityCompConfig =
        RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("version", "lastUpdated")
            .build();
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(
            _facilityEntity(
                f,
                f1V1,
                CmsOverlay.builder().detailedServices(_overlay_detailed_services()).build()));
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                _overlayEntity(
                    CmsOverlay.builder().detailedServices(_overlay_detailed_services()).build(),
                    "vha_f1")));
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "detailed_services");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(
            _facilityEntity(
                f,
                f1V1,
                CmsOverlay.builder().operatingStatus(_overlay_operating_status()).build()));
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                _overlayEntity(
                    CmsOverlay.builder().operatingStatus(_overlay_operating_status()).build(),
                    "vha_f1")));
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", null);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(_facilityEntity(f, f1V1, CmsOverlay.builder().build()));
    assertThat(overlayRepository.findAll()).isEmpty();
  }

  @Test
  void deleteFacilityOverlayMissingNode() {
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "operating_status").getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "operating_status").getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "detailed_services").getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "detailed_services").getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void deleteFacilityOverlayNotFound() {
    assertThat(_controller().deleteCmsOverlayById("vha_f1", null).getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "operating_status").getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "detailed_services").getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void deleteFacilityOverlayUnrecognizedNode() {
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    assertThatThrownBy(() -> _controller().deleteCmsOverlayById("vha_f1", "foo"))
        .isInstanceOf(ExceptionsUtils.NotFound.class)
        .hasMessage("The record identified by foo could not be found");
  }

  @Test
  void deleteFromGraveyard_error() {
    FacilityGraveyardRepository repo = mock(FacilityGraveyardRepository.class);
    doThrow(new RuntimeException("oh noez")).when(repo).delete(any(FacilityGraveyardEntity.class));
    InternalFacilitiesController controller =
        InternalFacilitiesController.builder().graveyardRepository(repo).build();
    ReloadResponse response = ReloadResponse.start();
    assertThrows(
        RuntimeException.class,
        () ->
            controller.deleteFromGraveyard(
                response,
                FacilityGraveyardEntity.builder()
                    .id(FacilityEntity.Pk.fromIdString("vha_f1"))
                    .build()));
    assertThat(response.problems())
        .isEqualTo(
            List.of(
                ReloadResponse.Problem.of(
                    "vha_f1", "Failed to delete facility from graveyard: oh noez")));
  }

  @Test
  void deleteNonExistingFacilityByIdReturnsAccepted() {
    assertThat(_controller().deleteFacilityById("vha_f1").getStatusCodeValue()).isEqualTo(202);
  }

  @Test
  @SneakyThrows
  void duplicateFacility_invalidDuplicate() {
    Facility f1 =
        _facility("vha_f1", "FL", "32934", 100.00, 100.00, List.of(HealthService.MentalHealthCare));
    Facility f2 =
        _facility("vha_f2", "FL", "32934", 50.00, 50.00, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            100.00,
            100.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2V1 =
        _facilityV1(
            "vha_f2",
            "FL",
            "South",
            50.00,
            50.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    Facility f1Duplicate =
        _facility(
            "vha_f1dup", "FL", "32934", 102.00, 102.00, List.of(HealthService.MentalHealthCare));
    Facility f2Duplicate =
        _facility(
            "vha_f2dup", "FL", "32934", 52.00, 52.00, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1DuplicateV1 =
        _facilityV1(
            "vha_f1dup",
            "FL",
            "South",
            102.00,
            102.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2DuplicateV2 =
        _facilityV1(
            "vha_f2dup",
            "FL",
            "South",
            52.00,
            52.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    ReloadResponse response = ReloadResponse.start();
    facilityRepository.save(_facilityEntity(f1, f1V1));
    facilityRepository.save(_facilityEntity(f2, f2V1));
    assertThat(facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f1"))).isNotNull();
    assertThat(facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f2"))).isNotNull();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f1dup")).build(),
            FacilityPair.builder().v0(f1Duplicate).v1(f1DuplicateV1).build());
    assertThat(
            response.problems().stream()
                .filter(
                    f ->
                        f.facilityId().equalsIgnoreCase("vha_f1dup")
                            && f.description().equalsIgnoreCase("Duplicate Facilities"))
                .toList())
        .isEmpty();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f2dup")).build(),
            FacilityPair.builder().v0(f2Duplicate).v1(f2DuplicateV2).build());
    assertThat(
            response.problems().stream()
                .filter(
                    f ->
                        f.facilityId().equalsIgnoreCase("vha_f2dup")
                            && f.description().equalsIgnoreCase("Duplicate Facilities"))
                .toList())
        .isEmpty();
  }

  @Test
  @SneakyThrows
  void duplicateFacility_validDuplicate() {
    Facility f1 =
        _facility("vha_f1", "FL", "32934", 100.00, 100.00, List.of(HealthService.MentalHealthCare));
    Facility f2 =
        _facility("vha_f2", "FL", "32934", 50.00, 50.00, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            100.00,
            100.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2V1 =
        _facilityV1(
            "vha_f2",
            "FL",
            "South",
            50.00,
            50.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    Facility f1Duplicate =
        _facility(
            "vha_f1dup",
            "FL",
            "32934",
            100.0002,
            100.0002,
            List.of(HealthService.MentalHealthCare));
    Facility f2Duplicate =
        _facility(
            "vha_f2dup", "FL", "32934", 50.0002, 50.0002, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1DuplicateV1 =
        _facilityV1(
            "vha_f1dup",
            "FL",
            "South",
            100.0002,
            100.0002,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2DuplicateV2 =
        _facilityV1(
            "vha_f2dup",
            "FL",
            "South",
            50.0002,
            50.0002,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    facilityRepository.save(_facilityEntity(f1, f1V1));
    facilityRepository.save(_facilityEntity(f2, f2V1));
    assertThat(facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f1"))).isNotNull();
    assertThat(facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f2"))).isNotNull();
    ReloadResponse response = ReloadResponse.start();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f1dup")).build(),
            FacilityPair.builder().v0(f1Duplicate).v1(f1DuplicateV1).build());
    assertThat(
            response.problems().stream()
                .filter(
                    f ->
                        f.facilityId().equalsIgnoreCase("vha_f1dup")
                            && f.description().equalsIgnoreCase("Duplicate Facilities"))
                .toList())
        .isNotEmpty();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f2dup")).build(),
            FacilityPair.builder().v0(f2Duplicate).v1(f2DuplicateV2).build());
    assertThat(
            response.problems().stream()
                .filter(
                    f ->
                        f.facilityId().equalsIgnoreCase("vha_f2dup")
                            && f.description().equalsIgnoreCase("Duplicate Facilities"))
                .toList())
        .isNotEmpty();
  }

  @Test
  @SneakyThrows
  void graveyardAll() {
    Facility f1 = _facility("vha_f1", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility fV1 =
        _facilityV1(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare));
    CmsOverlay overlay = _overlay();
    FacilityGraveyardEntity entity = _graveyardEntityWithOverlay(f1, fV1, overlay);
    graveyardRepository.save(entity);
    // Since we don't save active status to the database, make it false
    for (DetailedService d : overlay.detailedServices()) {
      d.active(false);
    }
    assertThat(_controller().graveyardAll())
        .isEqualTo(
            GraveyardResponse.builder()
                .facilities(
                    List.of(
                        GraveyardResponse.Item.builder()
                            .facility(
                                JacksonConfig.createMapper()
                                    .readValue(entity.facility(), Facility.class))
                            .cmsOverlay(overlay)
                            .overlayServices(entity.graveyardOverlayServices())
                            .missing(Instant.ofEpochMilli(entity.missingTimestamp()))
                            .lastUpdated(entity.lastUpdated())
                            .build()))
                .build());
  }

  @Test
  void servicesOf() {
    assertThat(
            InternalFacilitiesController.serviceTypesOf(
                Facility.builder().attributes(FacilityAttributes.builder().build()).build()))
        .isEmpty();
    assertThat(
            InternalFacilitiesController.serviceTypesOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder().services(Services.builder().build()).build())
                    .build()))
        .isEmpty();
    assertThat(
            InternalFacilitiesController.serviceTypesOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .services(
                                Services.builder()
                                    .health(List.of())
                                    .benefits(List.of())
                                    .other(List.of())
                                    .build())
                            .build())
                    .build()))
        .isEmpty();
    assertThat(
            InternalFacilitiesController.serviceTypesOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .services(
                                Services.builder()
                                    .health(
                                        List.of(
                                            HealthService.PrimaryCare,
                                            HealthService.MentalHealthCare))
                                    .benefits(
                                        List.of(
                                            BenefitsService.ApplyingForBenefits,
                                            BenefitsService.BurialClaimAssistance))
                                    .other(List.of(OtherService.OnlineScheduling))
                                    .build())
                            .build())
                    .build()))
        .containsExactlyInAnyOrder(
            HealthService.PrimaryCare,
            HealthService.MentalHealthCare,
            BenefitsService.ApplyingForBenefits,
            BenefitsService.BurialClaimAssistance,
            OtherService.OnlineScheduling);
  }

  @Test
  void stateOf() {
    // No address
    assertThat(
            InternalFacilitiesController.stateOf(
                Facility.builder().attributes(FacilityAttributes.builder().build()).build()))
        .isNull();
    // No physical or mailing
    assertThat(
            InternalFacilitiesController.stateOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder().address(Addresses.builder().build()).build())
                    .build()))
        .isNull();
    // No Physical zip
    assertThat(
            InternalFacilitiesController.stateOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder().physical(Address.builder().build()).build())
                            .build())
                    .build()))
        .isNull();
    // Physical zip
    assertThat(
            InternalFacilitiesController.stateOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().state("FL").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("FL");
  }

  @Test
  void updateAndSave_error() {
    FacilityRepository repo = mock(FacilityRepository.class);
    when(repo.save(any(FacilityEntity.class))).thenThrow(new RuntimeException("oh noez"));
    InternalFacilitiesController controller =
        InternalFacilitiesController.builder().facilityRepository(repo).build();
    Facility f1 =
        _facility("vha_f1", "CO", "5319", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    f1.attributes().address().mailing(Address.builder().zip("12345-56").build());
    f1V1.attributes()
        .address()
        .mailing(
            gov.va.api.lighthouse.facilities.api.v1.Facility.Address.builder()
                .zip("12345-56")
                .build());
    ReloadResponse response = ReloadResponse.start();
    assertThrows(
        RuntimeException.class,
        () ->
            controller.updateAndSave(
                response,
                FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f1")).build(),
                FacilityPair.builder().v0(f1).v1(f1V1).build()));
    assertThat(response.problems())
        .isEqualTo(
            List.of(
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid physical address zip"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address city"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address street information"),
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid mailing address zip"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address state"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address city"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address street information"),
                ReloadResponse.Problem.of("vha_f1", "Missing main phone number"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Monday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Tuesday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Wednesday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Thursday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Friday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Saturday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Sunday"),
                ReloadResponse.Problem.of("vha_f1", "Missing classification"),
                ReloadResponse.Problem.of("vha_f1", "Failed to save record: oh noez")));
  }

  @Test
  void updateAndSave_replaceInstructions() {
    Facility f1 =
        _facility("vha_f1", "FL", "32934", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    Facility f2 = _facility("vha_f2", "FL", "32934", 5.6, 6.7, List.of(HealthService.UrgentCare));
    Facility f3 = _facility("vha_f3", "FL", "32934", 5.6, 6.7, List.of(HealthService.UrgentCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2V1 =
        _facilityV1(
            "vha_f2",
            "NEAT",
            "32934",
            5.6,
            6.7,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f3V1 =
        _facilityV1(
            "vha_f3",
            "FL",
            "32934",
            5.6,
            6.7,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare));
    f1.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_1);
    f2.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_2);
    f3.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_3);
    f1V1.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_1);
    f2V1.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_2);
    f3V1.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_3);
    ReloadResponse response = ReloadResponse.start();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f1")).build(),
            FacilityPair.builder().v0(f1).v1(f1V1).build());
    assertThat(f1.attributes().operationalHoursSpecialInstructions())
        .isEqualTo(SPECIAL_INSTRUCTION_UPDATED_1);
    assertThat(
            facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f1")).get().facility())
        .contains(SPECIAL_INSTRUCTION_UPDATED_1);
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f2")).build(),
            FacilityPair.builder().v0(f2).v1(f2V1).build());
    assertThat(f2.attributes().operationalHoursSpecialInstructions())
        .isEqualTo(SPECIAL_INSTRUCTION_UPDATED_2);
    assertThat(
            facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f2")).get().facility())
        .contains(SPECIAL_INSTRUCTION_UPDATED_2);
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f3")).build(),
            FacilityPair.builder().v0(f3).v1(f3V1).build());
    assertThat(f3.attributes().operationalHoursSpecialInstructions())
        .isEqualTo(SPECIAL_INSTRUCTION_UPDATED_3);
    assertThat(
            facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f3")).get().facility())
        .contains(SPECIAL_INSTRUCTION_UPDATED_3);
  }

  @Test
  @SneakyThrows
  void upload() {
    Facility f1 =
        _facility("vha_f91", "FU", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    Facility f2 =
        _facility("vha_f92", "NEAT", "32934", 5.6, 6.7, List.of(HealthService.UrgentCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealthCare));
    gov.va.api.lighthouse.facilities.api.v1.Facility f2V1 =
        _facilityV1(
            "vha_f2",
            "NEAT",
            "32934",
            5.6,
            6.7,
            List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare));
    List<FacilityPair> facilityPairs = new ArrayList<>();
    facilityPairs.add(FacilityPair.builder().v0(f1).v1(f1V1).build());
    facilityPairs.add(FacilityPair.builder().v0(f2).v1(f2V1).build());
    _controller().upload(facilityPairs);
    RecursiveComparisonConfiguration comparisonConfig =
        RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("version", "lastUpdated")
            .build();
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(comparisonConfig)
        .containsExactlyInAnyOrder(_facilityEntity(f1, f1V1), _facilityEntity(f2, f2V1));
  }

  @Test
  void zipOf() {
    // No address
    assertThat(
            InternalFacilitiesController.zipOf(
                Facility.builder().attributes(FacilityAttributes.builder().build()).build()))
        .isNull();
    // No physical or mailing
    assertThat(
            InternalFacilitiesController.zipOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder().address(Addresses.builder().build()).build())
                    .build()))
        .isNull();
    // No Physical zip
    assertThat(
            InternalFacilitiesController.zipOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder().physical(Address.builder().build()).build())
                            .build())
                    .build()))
        .isNull();
    // Physical zip
    assertThat(
            InternalFacilitiesController.zipOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().zip("12345").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("12345");
    // Physical zip that is long
    assertThat(
            InternalFacilitiesController.zipOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().zip("12345-9876").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("12345");
  }
}
