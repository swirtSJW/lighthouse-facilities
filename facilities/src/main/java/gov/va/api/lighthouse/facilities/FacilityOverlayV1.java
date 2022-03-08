package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v1.FacilityTypedServiceUtil.getFacilityTypedServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatusCode;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Builder
@Value
@Slf4j
public class FacilityOverlayV1 implements BiFunction<HasFacilityPayload, String, Facility> {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  @Override
  @SneakyThrows
  public Facility apply(@NonNull HasFacilityPayload entity, @NonNull String linkerUrl) {
    Facility facility =
        FacilityTransformerV1.toFacility(
            DATAMART_MAPPER.readValue(entity.facility(), DatamartFacility.class));
    if (facility.attributes().operatingStatus() == null) {
      facility
          .attributes()
          .operatingStatus(
              determineOperatingStatusFromActiveStatus(facility.attributes().activeStatus()));
    }
    if (entity.overlayServices() != null) {
      applyCmsOverlayServices(facility, entity.overlayServices(), linkerUrl);
    } else {
      log.warn("CMS Overlay for facility {} is missing CMS Services", facility.id());
    }
    return facility;
  }

  private void applyCmsOverlayServices(
      @NonNull Facility facility, @NonNull Set<String> overlayServices, @NonNull String linkerUrl) {
    boolean needToSort = false;
    for (String overlayService : overlayServices) {
      if (Facility.HealthService.Covid19Vaccine.name().equalsIgnoreCase(overlayService)) {
        if (facility.attributes().services().health() != null) {
          facility
              .attributes()
              .services()
              .health()
              .addAll(
                  getFacilityTypedServices(
                      List.of(Facility.HealthService.Covid19Vaccine), linkerUrl, facility.id()));
        } else {
          facility
              .attributes()
              .services()
              .health(
                  getFacilityTypedServices(
                      List.of(Facility.HealthService.Covid19Vaccine), linkerUrl, facility.id()));
        }
        needToSort = true;
        break;
      }
    }
    // re-sort the health services list with the newly added field(s)
    if (needToSort && facility.attributes().services().health().size() > 1) {
      Collections.sort(
          facility.attributes().services().health(),
          (left, right) -> left.serviceId().compareToIgnoreCase(right.serviceId()));
    }
  }

  private OperatingStatus determineOperatingStatusFromActiveStatus(ActiveStatus activeStatus) {
    return OperatingStatus.builder()
        .code(
            activeStatus == ActiveStatus.T
                ? OperatingStatusCode.CLOSED
                : OperatingStatusCode.NORMAL)
        .build();
  }
}
