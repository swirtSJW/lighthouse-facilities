package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.FacilityEntity.Type;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Address;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Addresses;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Attributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Services;
import gov.va.api.lighthouse.facilities.collectorapi.CollectorApi;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InternalManagementControllerTest {

  @Mock FacilityRepository facilityRepository;
  @Mock CollectorApi collector;

  private static Facility facility(
      String id,
      String state,
      String zip,
      double latitude,
      double longitude,
      List<HealthService> health) {
    return Facility.builder()
        .id(id)
        .attributes(
            Attributes.builder()
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

  @Test
  @SneakyThrows
  void collect() {
    Facility f1 =
        facility("vha_f1", "FL", "South", 1.2, 3.4, List.of(HealthService.MentalHealthCare));
    FacilityEntity f1eExpected =
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.of(Type.vha, "f1"))
            .state("FL")
            .zip("South")
            .latitude(1.2)
            .longitude(3.4)
            .services(Set.of(HealthService.MentalHealthCare.toString()))
            .facility(FacilitiesJacksonConfig.createMapper().writeValueAsString(f1))
            .build();
    Facility f2 = facility("vha_f2", "NEAT", "32934", 5.6, 6.7, List.of(HealthService.UrgentCare));
    FacilityEntity f2eExisting =
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.of(Type.vha, "f2"))
            .state("NO")
            .zip("666")
            .latitude(9.0)
            .longitude(9.1)
            .services(Set.of(HealthService.SpecialtyCare.toString()))
            .facility("{}")
            .build();
    FacilityEntity f2eExpected =
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.of(Type.vha, "f2"))
            .state("NEAT")
            .zip("32934")
            .latitude(5.6)
            .longitude(6.7)
            .services(Set.of(HealthService.UrgentCare.toString()))
            .facility(FacilitiesJacksonConfig.createMapper().writeValueAsString(f2))
            .build();
    when(facilityRepository.findById(f1eExpected.id())).thenReturn(Optional.empty());
    when(facilityRepository.findById(f2eExpected.id())).thenReturn(Optional.of(f2eExisting));
    when(collector.collectFacilities())
        .thenReturn(CollectorFacilitiesResponse.builder().facilities(List.of(f1, f2)).build());
    controller().reload();
    verify(facilityRepository).save(f2eExpected);
    verify(facilityRepository).save(f2eExpected);
  }

  private InternalManagementController controller() {
    return InternalManagementController.builder()
        .collector(collector)
        .facilityRepository(facilityRepository)
        .build();
  }

  @Test
  void servicesOf() {
    assertThat(
            InternalManagementController.serviceTypesOf(
                Facility.builder().attributes(Attributes.builder().build()).build()))
        .isEmpty();
    assertThat(
            InternalManagementController.serviceTypesOf(
                Facility.builder()
                    .attributes(Attributes.builder().services(Services.builder().build()).build())
                    .build()))
        .isEmpty();
    assertThat(
            InternalManagementController.serviceTypesOf(
                Facility.builder()
                    .attributes(
                        Attributes.builder()
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
            InternalManagementController.serviceTypesOf(
                Facility.builder()
                    .attributes(
                        Attributes.builder()
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
            InternalManagementController.stateOf(
                Facility.builder().attributes(Attributes.builder().build()).build()))
        .isNull();
    // No physical or mailing
    assertThat(
            InternalManagementController.stateOf(
                Facility.builder()
                    .attributes(Attributes.builder().address(Addresses.builder().build()).build())
                    .build()))
        .isNull();
    // No Physical zip
    assertThat(
            InternalManagementController.stateOf(
                Facility.builder()
                    .attributes(
                        Attributes.builder()
                            .address(
                                Addresses.builder().physical(Address.builder().build()).build())
                            .build())
                    .build()))
        .isNull();
    // Physical zip
    assertThat(
            InternalManagementController.stateOf(
                Facility.builder()
                    .attributes(
                        Attributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().state("FL").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("FL");
  }

  @Test
  void zipOf() {
    // No address
    assertThat(
            InternalManagementController.zipOf(
                Facility.builder().attributes(Attributes.builder().build()).build()))
        .isNull();
    // No physical or mailing
    assertThat(
            InternalManagementController.zipOf(
                Facility.builder()
                    .attributes(Attributes.builder().address(Addresses.builder().build()).build())
                    .build()))
        .isNull();
    // No Physical zip
    assertThat(
            InternalManagementController.zipOf(
                Facility.builder()
                    .attributes(
                        Attributes.builder()
                            .address(
                                Addresses.builder().physical(Address.builder().build()).build())
                            .build())
                    .build()))
        .isNull();
    // Physical zip
    assertThat(
            InternalManagementController.zipOf(
                Facility.builder()
                    .attributes(
                        Attributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().zip("12345").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("12345");
    // Physical zip that is log
    assertThat(
            InternalManagementController.zipOf(
                Facility.builder()
                    .attributes(
                        Attributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().zip("12345-9876").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("12345");
  }
}
