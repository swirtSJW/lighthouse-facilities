package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Address;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Addresses;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Services;
import gov.va.api.lighthouse.facilities.collectorapi.CollectorApi;
import java.math.BigDecimal;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class FacilityManagementControllerTest {
  @Autowired FacilityRepository facilityRepository;

  @Autowired FacilityIdRepository facilityIdRepository;

  CollectorApi collector = mock(CollectorApi.class);

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
                .build())
        .build();
  }

  private FacilityManagementController _controller() {
    return FacilityManagementController.builder()
        .collector(collector)
        .facilityRepository(facilityRepository)
        .facilityIdRepository(facilityIdRepository)
        .build();
  }

  private FacilityEntity _entity(Facility f1) {
    return FacilityManagementController.populate(
        FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString(f1.id())).build(), f1);
  }

  @Before
  public void _resetDatabase() {
    facilityRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  public void collect_createUpdate() {
    Facility f1 =
        _facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    Facility f2 = _facility("vha_f2", "NEAT", "32934", 5.6, 6.7, List.of(HealthService.UrgentCare));
    Facility f2Old =
        _facility("vha_f2", "NO", "666", 9.0, 9.1, List.of(HealthService.SpecialtyCare));
    facilityRepository.save(_entity(f2Old));
    when(collector.collectFacilities())
        .thenReturn(CollectorFacilitiesResponse.builder().facilities(List.of(f1, f2)).build());
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesCreated()).isEqualTo(List.of("vha_f1"));
    assertThat(response.facilitiesUpdated()).isEqualTo(List.of("vha_f2"));
    assertThat(facilityRepository.findAll()).isEqualTo(List.of(_entity(f1), _entity(f2)));
  }

  @Test
  @SneakyThrows
  public void collect_delete() {
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
    facilityRepository.save(_entity(f1Old));
    facilityRepository.save(_entity(f2Old));
    facilityRepository.save(_entity(f3Old));
    facilityRepository.save(_entity(f4Old));
    when(collector.collectFacilities())
        .thenReturn(CollectorFacilitiesResponse.builder().facilities(List.of(f1)).build());
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesUpdated()).isEqualTo(List.of("vha_f1"));
    assertThat(response.facilitiesDeleted()).isEqualTo(List.of("vha_f2", "vha_f3", "vha_f4"));
    assertThat(facilityRepository.findAll()).isEqualTo(List.of(_entity(f1)));
  }

  @Test
  public void servicesOf() {
    assertThat(
            FacilityManagementController.serviceTypesOf(
                Facility.builder().attributes(FacilityAttributes.builder().build()).build()))
        .isEmpty();
    assertThat(
            FacilityManagementController.serviceTypesOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder().services(Services.builder().build()).build())
                    .build()))
        .isEmpty();
    assertThat(
            FacilityManagementController.serviceTypesOf(
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
            FacilityManagementController.serviceTypesOf(
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
  public void stateOf() {
    // No address
    assertThat(
            FacilityManagementController.stateOf(
                Facility.builder().attributes(FacilityAttributes.builder().build()).build()))
        .isNull();
    // No physical or mailing
    assertThat(
            FacilityManagementController.stateOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder().address(Addresses.builder().build()).build())
                    .build()))
        .isNull();
    // No Physical zip
    assertThat(
            FacilityManagementController.stateOf(
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
            FacilityManagementController.stateOf(
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
  public void zipOf() {
    // No address
    assertThat(
            FacilityManagementController.zipOf(
                Facility.builder().attributes(FacilityAttributes.builder().build()).build()))
        .isNull();
    // No physical or mailing
    assertThat(
            FacilityManagementController.zipOf(
                Facility.builder()
                    .attributes(
                        FacilityAttributes.builder().address(Addresses.builder().build()).build())
                    .build()))
        .isNull();
    // No Physical zip
    assertThat(
            FacilityManagementController.zipOf(
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
            FacilityManagementController.zipOf(
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
    // Physical zip that is log
    assertThat(
            FacilityManagementController.zipOf(
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
