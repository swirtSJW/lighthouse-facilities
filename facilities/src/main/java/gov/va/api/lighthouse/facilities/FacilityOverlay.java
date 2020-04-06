package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import java.util.function.Function;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;

@Builder
@Value
public class FacilityOverlay implements Function<FacilityEntity, Facility> {

  ObjectMapper mapper;

  @Override
  @SneakyThrows
  public Facility apply(FacilityEntity entity) {
    Facility facility = mapper.readValue(entity.facility(), Facility.class);
    // TODO apply overlay here
    if (facility.attributes().operatingStatus() == null) {
      facility
          .attributes()
          .operatingStatus(
              determineOperatingStatusFromActiveStatus(facility.attributes().activeStatus()));
    }
    return facility;
  }

  private OperatingStatus determineOperatingStatusFromActiveStatus(ActiveStatus activeStatus) {
    if (activeStatus == ActiveStatus.T) {
      return OperatingStatus.builder().code(OperatingStatusCode.CLOSED).build();
    }
    return OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build();
  }
}
