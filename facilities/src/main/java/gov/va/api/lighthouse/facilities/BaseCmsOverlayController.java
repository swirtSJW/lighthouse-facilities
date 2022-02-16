package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public abstract class BaseCmsOverlayController {
  @SneakyThrows
  protected List<DatamartDetailedService> findServicesToSave(
      CmsOverlayEntity cmsOverlayEntity,
      String id,
      List<DatamartDetailedService> detailedServices,
      ObjectMapper mapper) {
    final List<DatamartDetailedService> ds =
        (detailedServices == null) ? Collections.emptyList() : detailedServices;
    List<DatamartDetailedService> currentDetailedServices =
        cmsOverlayEntity.cmsServices() == null
            ? Collections.emptyList()
            : List.of(
                mapper.readValue(cmsOverlayEntity.cmsServices(), DatamartDetailedService[].class));
    final List<String> overlayServiceIds =
        ds.stream().map(x -> x.serviceInfo().serviceId()).collect(Collectors.toList());
    final List<DatamartDetailedService> finalDetailedServices = new ArrayList<>();
    finalDetailedServices.addAll(
        currentDetailedServices.parallelStream()
            .filter(
                currentDetailedService ->
                    !overlayServiceIds.contains(currentDetailedService.serviceInfo().serviceId()))
            .collect(Collectors.toList()));
    finalDetailedServices.addAll(
        ds.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    updateServiceUrlPaths(id, finalDetailedServices);
    finalDetailedServices.sort(Comparator.comparing(x -> x.serviceInfo().serviceId()));
    return finalDetailedServices;
  }

  protected List<DatamartDetailedService> getActiveServicesFromOverlay(
      String id, List<DatamartDetailedService> detailedServices) {
    final List<DatamartDetailedService> activeServices = new ArrayList<>();
    if (detailedServices != null) {
      activeServices.addAll(
          detailedServices.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    }
    updateServiceUrlPaths(id, activeServices);
    activeServices.sort(Comparator.comparing(x -> x.serviceInfo().serviceId()));
    return activeServices;
  }

  protected abstract Optional<CmsOverlayEntity> getExistingOverlayEntity(FacilityEntity.Pk pk);

}
