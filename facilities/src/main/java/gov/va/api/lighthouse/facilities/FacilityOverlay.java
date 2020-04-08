package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Builder
@Value
@Slf4j
public class FacilityOverlay implements Function<HasFacilityPayload, Facility> {
  @NonNull ObjectMapper mapper;

  private static void applyCmsOverlay(Facility facility, CmsOverlay overlay) {
    if (overlay.operatingStatus() == null) {
      log.warn("CMS Overlay for facility {} is missing operating status", facility.id());
      return;
    }
    facility.attributes().operatingStatus(overlay.operatingStatus());
    if (overlay.operatingStatus().code() == OperatingStatusCode.CLOSED) {
      facility.attributes().activeStatus(ActiveStatus.T);
    } else {
      facility.attributes().activeStatus(ActiveStatus.A);
    }
  }

  private static OperatingStatus determineOperatingStatusFromActiveStatus(
      ActiveStatus activeStatus) {
    if (activeStatus == ActiveStatus.T) {
      return OperatingStatus.builder().code(OperatingStatusCode.CLOSED).build();
    }
    return OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build();
  }

  @Override
  @SneakyThrows
  public Facility apply(HasFacilityPayload entity) {
    Facility facility = mapper.readValue(entity.facility(), Facility.class);
    if (entity.cmsOverlay() != null) {
      applyCmsOverlay(facility, mapper.readValue(entity.cmsOverlay(), CmsOverlay.class));
    }
    if (facility.attributes().operatingStatus() == null) {
      facility
          .attributes()
          .operatingStatus(
              determineOperatingStatusFromActiveStatus(facility.attributes().activeStatus()));
    }
    return facility;
  }
}
