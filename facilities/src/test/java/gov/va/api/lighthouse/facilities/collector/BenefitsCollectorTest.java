package gov.va.api.lighthouse.facilities.collector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BenefitsCollectorTest {
  @Test
  @SneakyThrows
  void collect() {
    RestTemplate restTemplate = mock(RestTemplate.class);

    ResponseEntity<String> body =
        ResponseEntity.of(
            Optional.of(
                new String(
                    getClass().getResourceAsStream("/arcgis-benefits.json").readAllBytes())));

    when(restTemplate.exchange(
            startsWith("http://localhost:8080"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(body);

    assertThat(
            BenefitsCollector.builder()
                .arcgisUrl("http://localhost:8080")
                .restTemplate(restTemplate)
                .websites(new HashMap<>())
                .build()
                .collect())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("vba_306e")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("New York Regional Office at Albany VAMC")
                            .facilityType(Facility.FacilityType.va_benefits_facility)
                            .classification("Outbased")
                            .latitude(new BigDecimal("42.651408840000045"))
                            .longitude(new BigDecimal("-73.77623284999999"))
                            .address(
                                Facility.Addresses.builder()
                                    .physical(
                                        Facility.Address.builder()
                                            .address1("113 Holland Avenue")
                                            .city("Albany")
                                            .state("NY")
                                            .zip("12208")
                                            .build())
                                    .build())
                            .phone(
                                Facility.Phone.builder()
                                    .fax("518-626-5695")
                                    .main("518-626-5524")
                                    .build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("7:30AM-4:00PM")
                                    .tuesday("7:30AM-4:00PM")
                                    .wednesday("7:30AM-4:00PM")
                                    .thursday("7:30AM-4:00PM")
                                    .friday("7:30AM-4:00PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .services(
                                Facility.Services.builder()
                                    .benefits(
                                        List.of(
                                            Facility.BenefitsService.ApplyingForBenefits,
                                            Facility.BenefitsService.BurialClaimAssistance,
                                            Facility.BenefitsService.DisabilityClaimAssistance,
                                            Facility.BenefitsService
                                                .eBenefitsRegistrationAssistance,
                                            Facility.BenefitsService.FamilyMemberClaimAssistance,
                                            Facility.BenefitsService
                                                .UpdatingDirectDepositInformation))
                                    .build())
                            .build())
                    .build()));
  }

  @Test
  void exception() {
    RestTemplate restTemplate = mock(RestTemplate.class);
    assertThrows(
        CollectorExceptions.BenefitsCollectorException.class,
        () ->
            BenefitsCollector.builder()
                .arcgisUrl("http://wrong:8080")
                .restTemplate(restTemplate)
                .websites(emptyMap())
                .build()
                .collect());
  }
}
