package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DetailedServiceTransformerV1Test {
  @Test
  void datamartDetailedServiceWithEmptyAttributesRoundTrip() {
    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithEmptyAttributes();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV1.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  void datamartDetailedServiceWithNullAttributesRoundTrip() {
    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithNullAttributes();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV1.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  void nullReturn() {
    assertThat(DetailedServiceTransformerV1.toDetailedServiceEmailContacts(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServiceLocations(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServicePhoneNumbers(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServices(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceEmailContacts(null))
        .isNull();
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceLocations(null))
        .isNull();
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServicePhoneNumbers(null))
        .isNull();
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServiceAddress(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceAddress(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceEmailContact(null))
        .isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServiceEmailContact(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServiceHours(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceHours(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServiceLocation(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceLocation(null))
        .isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServiceAppointmentPhoneNumber(null)).isNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceAppointmentPhoneNumber(
                null))
        .isNull();
  }

  @Test
  public void roundTripTransformation() {
    List<DatamartDetailedService> datamartDetailedServices =
        DatamartDetailedServicesTestUtils.datamartDetailedServices(true);
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(
                DetailedServiceTransformerV1.toDetailedServices(datamartDetailedServices)))
        .containsAll(datamartDetailedServices);
  }

  @Test
  void toDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class, () -> DetailedServiceTransformerV1.toDetailedService(null));
  }

  @Test
  void toDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV1.toDetailedServices(new ArrayList<>())).isEmpty();
  }

  @Test
  void toDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV1.toDetailedServices(null)).isEqualTo(null);
  }

  @Test
  void toVersionAgnosticDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class,
        () -> DetailedServiceTransformerV1.toVersionAgnosticDetailedService(null));
  }

  @Test
  void toVersionAgnosticDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toVersionAgnosticDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(null))
        .isEqualTo(null);
  }
}
