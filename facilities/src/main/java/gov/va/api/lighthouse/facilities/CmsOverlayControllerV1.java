package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

/** CMS Overlay Controller for version 1 facilities. */
@Slf4j
@Builder
@Validated
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayControllerV1 extends BaseCmsOverlayController {
  private static final ObjectMapper MAPPER_V1 = FacilitiesJacksonConfigV1.createMapper();

  private static final ObjectMapper DATAMART_MAPPER =
      DatamartFacilitiesJacksonConfig.createMapper();

  private final FacilityRepository facilityRepository;

  private final CmsOverlayRepository cmsOverlayRepository;

  @GetMapping(
      value = {"/v1/facilities/{id}/cms-overlay"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<CmsOverlayResponse> getOverlay(@PathVariable("id") String id) {
    FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString(id);
    Optional<CmsOverlayEntity> existingOverlayEntity = cmsOverlayRepository.findById(pk);
    if (!existingOverlayEntity.isPresent()) {
      throw new ExceptionsUtils.NotFound(id);
    }
    CmsOverlayEntity cmsOverlayEntity = existingOverlayEntity.get();
    CmsOverlayResponse response =
        CmsOverlayResponse.builder()
            .overlay(
                CmsOverlay.builder()
                    .operatingStatus(
                        CmsOverlayHelperV1.getOperatingStatus(
                            MAPPER_V1, cmsOverlayEntity.cmsOperatingStatus()))
                    .detailedServices(
                        CmsOverlayHelperV1.getDetailedServices(
                            MAPPER_V1, cmsOverlayEntity.cmsServices()))
                    .build())
            .build();
    return ResponseEntity.ok(response);
  }

  @InitBinder
  void initDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  @PostMapping(
      value = {"/v1/facilities/{id}/cms-overlay"},
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
      List<DetailedService> activeServices =
          getActiveServicesFromOverlay(id, overlay.detailedServices());
      cmsOverlayEntity =
          CmsOverlayEntity.builder()
              .id(FacilityEntity.Pk.fromIdString(id))
              .cmsOperatingStatus(
                  CmsOverlayHelperV1.serializeOperatingStatus(MAPPER_V1, overlay.operatingStatus()))
              .cmsServices(CmsOverlayHelperV1.serializeDetailedServices(MAPPER_V1, activeServices))
              .build();
    } else {
      cmsOverlayEntity = existingCmsOverlayEntity.get();
      if (overlay.operatingStatus() != null) {
        cmsOverlayEntity.cmsOperatingStatus(
            CmsOverlayHelperV1.serializeOperatingStatus(MAPPER_V1, overlay.operatingStatus()));
      }
      List<DetailedService> overlayServices = overlay.detailedServices();
      if (overlayServices != null) {
        List<DetailedService> toSaveDetailedServices =
            findServicesToSave(cmsOverlayEntity, id, overlay.detailedServices(), MAPPER_V1);
        cmsOverlayEntity.cmsServices(
            CmsOverlayHelperV1.serializeDetailedServices(MAPPER_V1, toSaveDetailedServices));
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
    Facility facility =
        FacilityTransformerV1.toFacility(
            DATAMART_MAPPER.readValue(facilityEntity.facility(), DatamartFacility.class));
    // Only save active services from the overlay if they exist
    List<DetailedService> toSaveDetailedServices;
    if (existingCmsOverlayEntity.isEmpty()) {
      toSaveDetailedServices = getActiveServicesFromOverlay(id, overlay.detailedServices());
    } else {
      toSaveDetailedServices =
          findServicesToSave(
              existingCmsOverlayEntity.get(), id, overlay.detailedServices(), MAPPER_V1);
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
      facilityEntity.facility(
          DATAMART_MAPPER.writeValueAsString(FacilityTransformerV1.toVersionAgnostic(facility)));
    }
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
}
