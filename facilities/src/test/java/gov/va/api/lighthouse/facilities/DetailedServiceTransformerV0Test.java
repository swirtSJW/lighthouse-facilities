package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DetailedServiceTransformerV0Test {
  @Test
  void datamartDetailedServiceWithEmptyAttributesRoundTrip() {
    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithEmptyAttributes();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV0.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  void datamartDetailedServiceWithNullAttributesRoundTrip() {
    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithNullAttributes();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV0.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  void nullReturn() {
    assertThat(DetailedServiceTransformerV0.toDetailedServiceEmailContacts(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServiceLocations(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServicePhoneNumbers(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServices(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceEmailContacts(null))
        .isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceLocations(null))
        .isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServicePhoneNumbers(null))
        .isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServiceAddress(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceAddress(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceEmailContact(null))
        .isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServiceEmailContact(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServiceHours(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceHours(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServiceLocation(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceLocation(null))
        .isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServiceAppointmentPhoneNumber(null)).isNull();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceAppointmentPhoneNumber(
                null))
        .isNull();
  }

  @Test
  public void roundTripTransformation() {
    List<DatamartDetailedService> datamartDetailedServices =
        DatamartDetailedServicesTestUtils.datamartDetailedServices(true);
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(
                DetailedServiceTransformerV0.toDetailedServices(datamartDetailedServices)))
        .containsAll(datamartDetailedServices);
  }

  @Test
  void toDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class, () -> DetailedServiceTransformerV0.toDetailedService(null));
  }

  @Test
  void toDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV0.toDetailedServices(new ArrayList<>())).isEmpty();
  }

  @Test
  void toDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV0.toDetailedServices(null)).isEqualTo(null);
  }

  @Test
  void toVersionAgnosticDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class,
        () -> DetailedServiceTransformerV0.toVersionAgnosticDetailedService(null));
  }

  @Test
  void toVersionAgnosticDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toVersionAgnosticDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(null))
        .isEqualTo(null);
  }
}
