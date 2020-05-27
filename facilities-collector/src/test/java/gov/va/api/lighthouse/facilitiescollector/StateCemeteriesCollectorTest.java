package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class StateCemeteriesCollectorTest {
  @Test
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void collect() {
    RestTemplate insecureRestTemplate = mock(RestTemplate.class);

    ResponseEntity<String> arcGisResponse = mock(ResponseEntity.class);
    when(arcGisResponse.getBody())
        .thenReturn(
            new XmlMapper()
                .writeValueAsString(
                    StateCemeteries.builder()
                        .cem(
                            List.of(
                                StateCemeteries.StateCemetery.builder()
                                    .id("1001")
                                    .stateCode("AL")
                                    .name(
                                        "Alabama State Veterans Memorial Cemetery At Spanish Fort")
                                    .url("http://www.va.state.al.us/spanishfort.aspx")
                                    .addressLine1("34904 State Highway 225")
                                    .addressLine2("Spanish Fort, AL 36577")
                                    .phone("251-625-1338")
                                    .fax("251-626-9204")
                                    .latitude("30.7346233")
                                    .longitude("-87.8985442")
                                    .build()))
                        .build()));

    when(insecureRestTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(arcGisResponse);

    assertThat(
            StateCemeteriesCollector.builder()
                .baseUrl("http://statecems")
                .insecureRestTemplate(insecureRestTemplate)
                .websites(ImmutableMap.of("nca_s1001", "DONTUSE"))
                .build()
                .collect())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("nca_s1001")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Alabama State Veterans Memorial Cemetery At Spanish Fort")
                            .facilityType(Facility.FacilityType.va_cemetery)
                            .classification("State Cemetery")
                            .website("http://www.va.state.al.us/spanishfort.aspx")
                            .latitude(new BigDecimal("30.7346233"))
                            .longitude(new BigDecimal("-87.8985442"))
                            .address(
                                Facility.Addresses.builder()
                                    .physical(
                                        Facility.Address.builder()
                                            .zip("36577")
                                            .city("Spanish Fort")
                                            .state("AL")
                                            .address1("34904 State Highway 225")
                                            .build())
                                    .build())
                            .phone(
                                Facility.Phone.builder()
                                    .fax("251-626-9204")
                                    .main("251-625-1338")
                                    .build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("Sunrise - Sunset")
                                    .tuesday("Sunrise - Sunset")
                                    .wednesday("Sunrise - Sunset")
                                    .thursday("Sunrise - Sunset")
                                    .friday("Sunrise - Sunset")
                                    .saturday("Sunrise - Sunset")
                                    .sunday("Sunrise - Sunset")
                                    .build())
                            .build())
                    .build()));
  }

  @Test(expected = CollectorExceptions.StateCemeteriesCollectorException.class)
  public void exception() {
    RestTemplate insecureRestTemplate = mock(RestTemplate.class);
    StateCemeteriesCollector.builder()
        .baseUrl("http://wrong")
        .insecureRestTemplate(insecureRestTemplate)
        .websites(ImmutableMap.of("nca_s1001", "DONTUSE"))
        .build()
        .collect();
  }
}
