package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.collector.FacilitiesCollector.loadWebsites;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@Validated
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayController {
  public static final String CMS_OVERLAY_SERVICE_NAME_COVID_19 = "COVID-19 vaccines";

  private static final ObjectMapper MAPPER_V0 = FacilitiesJacksonConfigV0.createMapper();

  private static final String COVID_CSV_WEBSITES_RESOURCE_NAME = "COVID-19-Facility-URLs.csv";

  private final FacilityRepository facilityRepository;

  private final CmsOverlayRepository cmsOverlayRepository;

  @SneakyThrows
  List<DetailedService> findServicesToSave(
      CmsOverlayEntity cmsOverlayEntity, String id, CmsOverlay overlay) {
    List<DetailedService> currentDetailedServices =
        cmsOverlayEntity.cmsServices() == null
            ? Collections.emptyList()
            : List.of(MAPPER_V0.readValue(cmsOverlayEntity.cmsServices(), DetailedService[].class));
    List<DetailedService> finalDetailedServices = new ArrayList<>();
    List<String> overlayServiceNames = new ArrayList<>();
    if (overlay.detailedServices() != null) {
      overlayServiceNames =
          overlay.detailedServices().stream()
              .map(DetailedService::name)
              .collect(Collectors.toList());
    }
    for (DetailedService currentDetailedService : currentDetailedServices) {
      if (!overlayServiceNames.contains(currentDetailedService.name())) {
        finalDetailedServices.add(currentDetailedService);
      }
    }
    if (overlay.detailedServices() != null) {
      for (DetailedService newDetailedService : overlay.detailedServices()) {
        if (newDetailedService.active()) {
          finalDetailedServices.add(newDetailedService);
        }
      }
    }
    updateServiceUrlPaths(id, finalDetailedServices);
    finalDetailedServices.sort(Comparator.comparing(DetailedService::name));
    return finalDetailedServices;
  }

  private List<DetailedService> getActiveServicesFromOverlay(String id, CmsOverlay overlay) {
    List<DetailedService> activeServices = new ArrayList<>();
    if (overlay.detailedServices() != null) {
      for (DetailedService d : overlay.detailedServices()) {
        if (d.active()) {
          activeServices.add(d);
        }
      }
    }
    if (!activeServices.isEmpty()) {
      updateServiceUrlPaths(id, activeServices);
      activeServices.sort(Comparator.comparing(DetailedService::name));
    }
    return activeServices;
  }

  @GetMapping(
      value = {"/v0/facilities/{id}/cms-overlay", "/v1/facilities/{id}/cms-overlay"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<CmsOverlayResponse> getOverlay(@PathVariable("id") String id) {
    FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString(id);
    Optional<CmsOverlayEntity> existingOverlayEntity = cmsOverlayRepository.findById(pk);
    if (existingOverlayEntity.isPresent()) {
      CmsOverlayEntity cmsOverlayEntity = existingOverlayEntity.get();
      CmsOverlayResponse response =
          CmsOverlayResponse.builder()
              .overlay(
                  CmsOverlay.builder()
                      .operatingStatus(
                          cmsOverlayEntity.cmsOperatingStatus() == null
                              ? null
                              : MAPPER_V0.readValue(
                                  cmsOverlayEntity.cmsOperatingStatus(),
                                  Facility.OperatingStatus.class))
                      .detailedServices(
                          cmsOverlayEntity.cmsServices() == null
                              ? null
                              : List.of(
                                  MAPPER_V0.readValue(
                                      cmsOverlayEntity.cmsServices(), DetailedService[].class)))
                      .build())
              .build();
      return ResponseEntity.ok(response);
    }
    throw new ExceptionsUtils.NotFound(id);
  }

  @InitBinder
  void initDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  @PostMapping(
      value = {"/v0/facilities/{id}/cms-overlay", "/v1/facilities/{id}/cms-overlay"},
      produces = "application/json",
      consumes = "application/json")
  @SneakyThrows
  ResponseEntity<Void> saveOverlay(
      @PathVariable("id") String id, @Valid @RequestBody CmsOverlay overlay) {
    Optional<FacilityEntity> existingFacilityEntity =
        facilityRepository.findById(FacilityEntity.Pk.fromIdString(id));
    Optional<CmsOverlayEntity> existingCmsOverlayEntity =
        cmsOverlayRepository.findById(FacilityEntity.Pk.fromIdString(id));
    updateCmsOverlayData(existingCmsOverlayEntity, id, overlay);
    if (existingFacilityEntity.isEmpty()) {
      log.info("Received Unknown Facility ID ({}) for CMS Overlay", sanitize(id));
      return ResponseEntity.accepted().build();
    } else {
      updateFacilityData(existingFacilityEntity.get(), existingCmsOverlayEntity, id, overlay);
      return ResponseEntity.ok().build();
    }
  }

  @SneakyThrows
  void updateCmsOverlayData(
      Optional<CmsOverlayEntity> existingCmsOverlayEntity, String id, CmsOverlay overlay) {
    CmsOverlayEntity cmsOverlayEntity;
    if (existingCmsOverlayEntity.isEmpty()) {
      List<DetailedService> activeServices = getActiveServicesFromOverlay(id, overlay);
      cmsOverlayEntity =
          CmsOverlayEntity.builder()
              .id(FacilityEntity.Pk.fromIdString(id))
              .cmsOperatingStatus(MAPPER_V0.writeValueAsString(overlay.operatingStatus()))
              .cmsServices(
                  activeServices.isEmpty() ? null : MAPPER_V0.writeValueAsString(activeServices))
              .build();
    } else {
      cmsOverlayEntity = existingCmsOverlayEntity.get();
      if (overlay.operatingStatus() != null) {
        cmsOverlayEntity.cmsOperatingStatus(
            MAPPER_V0.writeValueAsString(overlay.operatingStatus()));
      }
      List<DetailedService> overlayServices = overlay.detailedServices();
      if (overlayServices != null) {
        List<DetailedService> toSaveDetailedServices =
            findServicesToSave(cmsOverlayEntity, id, overlay);
        cmsOverlayEntity.cmsServices(
            toSaveDetailedServices.isEmpty()
                ? null
                : MAPPER_V0.writeValueAsString(toSaveDetailedServices));
      }
    }
    cmsOverlayRepository.save(cmsOverlayEntity);
  }

  @SneakyThrows
  void updateFacilityData(
      FacilityEntity facilityEntity,
      Optional<CmsOverlayEntity> existingCmsOverlayEntity,
      String id,
      CmsOverlay overlay) {
    Facility facility = MAPPER_V0.readValue(facilityEntity.facility(), Facility.class);
    // Only save active services from the overlay if they exist
    List<DetailedService> toSaveDetailedServices;
    if (existingCmsOverlayEntity.isEmpty()) {
      toSaveDetailedServices = getActiveServicesFromOverlay(id, overlay);
    } else {
      toSaveDetailedServices = findServicesToSave(existingCmsOverlayEntity.get(), id, overlay);
    }
    if (facility != null) {
      Facility.OperatingStatus operatingStatus = overlay.operatingStatus();
      if (operatingStatus != null) {
        facility.attributes().operatingStatus(operatingStatus);
        if (operatingStatus.code() == Facility.OperatingStatusCode.CLOSED) {
          facility.attributes().activeStatus(Facility.ActiveStatus.T);
        } else {
          facility.attributes().activeStatus(Facility.ActiveStatus.A);
        }
      }
      if (overlay.detailedServices() != null) {
        facility
            .attributes()
            .detailedServices(toSaveDetailedServices.isEmpty() ? null : toSaveDetailedServices);
      }
    }
    facilityEntity.facility(MAPPER_V0.writeValueAsString(facility));
    if (!toSaveDetailedServices.isEmpty()) {
      Set<String> detailedServices = new HashSet<>();
      for (DetailedService service : toSaveDetailedServices) {
        if (service.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
          detailedServices.add("Covid19Vaccine");
        } else {
          detailedServices.add(service.name());
        }
      }
      facilityEntity.overlayServices(detailedServices);
    }
    facilityRepository.save(facilityEntity);
  }

  void updateServiceUrlPaths(String id, List<DetailedService> detailedServices) {
    for (DetailedService service : detailedServices) {
      if (service.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
        Map<String, String> websites = loadWebsites(COVID_CSV_WEBSITES_RESOURCE_NAME);
        service.path(websites.get(id));
      }
    }
  }
}
