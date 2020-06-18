package gov.va.api.lighthouse.facilitiescollector;

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

public class CemeteriesCollectorTest {
  @Test
  @SneakyThrows
  public void collect() {
    RestTemplate restTemplate = mock(RestTemplate.class);

    ResponseEntity<String> body =
        ResponseEntity.of(
            Optional.of(
                new String(
                    getClass().getResourceAsStream("/arcgis-cemeteries.json").readAllBytes())));

    when(restTemplate.exchange(
            startsWith("http://localhost:8080"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(body);

    assertThat(
            CemeteriesCollector.builder()
                .arcgisUrl("http://localhost:8080")
                .restTemplate(restTemplate)
                .websites(new HashMap<>())
                .build()
                .collect())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("nca_088")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Albany Rural")
                            .facilityType(Facility.FacilityType.va_cemetery)
                            .classification("Rural")
                            .latitude(new BigDecimal("42.703844900000036"))
                            .longitude(new BigDecimal("-73.72356499999995"))
                            .address(
                                Facility.Addresses.builder()
                                    .physical(
                                        Facility.Address.builder()
                                            .address1("Cemetery Avenue")
                                            .address2(null)
                                            .city("Albany")
                                            .state("NY")
                                            .zip("12204")
                                            .build())
                                    .mailing(
                                        Facility.Address.builder()
                                            .address1("200 Duell Road")
                                            .address2(null)
                                            .city("Schuylerville")
                                            .state("NY")
                                            .zip("12871-1721")
                                            .build())
                                    .build())
                            .phone(Facility.Phone.builder().fax("5184630787").main(null).build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("Sunrise - Sundown")
                                    .tuesday("Sunrise - Sundown")
                                    .wednesday("Sunrise - Sundown")
                                    .thursday("Sunrise - Sundown")
                                    .friday("Sunrise - Sundown")
                                    .saturday("Sunrise - Sundown")
                                    .sunday("Sunrise - Sundown")
                                    .build())
                            .build())
                    .build()));
  }

  @Test
  public void exception() {
    RestTemplate restTemplate = mock(RestTemplate.class);
    assertThrows(
        CollectorExceptions.CemeteriesCollectorException.class,
        () ->
            CemeteriesCollector.builder()
                .arcgisUrl("http://wrong:8080")
                .restTemplate(restTemplate)
                .websites(emptyMap())
                .build()
                .collect());
  }
}
