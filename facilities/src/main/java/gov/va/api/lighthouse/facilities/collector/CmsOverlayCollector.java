package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.CmsOverlayEntity;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV0;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayCollector {
  private static final ObjectMapper MAPPER_V0 = FacilitiesJacksonConfigV0.createMapper();

  private final CmsOverlayRepository cmsOverlayRepository;

  /** Method for determining whether Covid service is contained within detailed services. */
  public static boolean containsCovidService(List<DetailedService> detailedServices) {
    return (detailedServices != null)
        ? !detailedServices.parallelStream()
            .filter(f -> f.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19))
            .collect(toList())
            .isEmpty()
        : false;
  }

  /** Load and return map of CMS overlays for each facility id. */
  public HashMap<String, CmsOverlay> loadAndUpdateCmsOverlays() {
    HashMap<String, CmsOverlay> returnMap = new HashMap<>();
    Streams.stream(cmsOverlayRepository.findAll())
        .parallel()
        .forEach(
            cmsOverlayEntity -> {
              try {
                returnMap.put(
                    cmsOverlayEntity.id().toIdString(),
                    CmsOverlay.builder()
                        .operatingStatus(
                            cmsOverlayEntity.cmsOperatingStatus() != null
                                ? FacilitiesJacksonConfigV0.createMapper()
                                    .readValue(
                                        cmsOverlayEntity.cmsOperatingStatus(),
                                        Facility.OperatingStatus.class)
                                : null)
                        .detailedServices(
                            cmsOverlayEntity.cmsServices() != null
                                ? updateServiceUrlPaths(
                                    cmsOverlayEntity.id().toIdString(),
                                    List.of(
                                        FacilitiesJacksonConfigV0.createMapper()
                                            .readValue(
                                                cmsOverlayEntity.cmsServices(),
                                                DetailedService[].class)))
                                : null)
                        .build());
                // Save updates made to overlay with Covid services
                final Facility.OperatingStatus operatingStatus =
                    returnMap.get(cmsOverlayEntity.id().toIdString()).operatingStatus();
                final List<DetailedService> detailedServices =
                    returnMap.get(cmsOverlayEntity.id().toIdString()).detailedServices();
                if (containsCovidService(detailedServices)) {
                  cmsOverlayRepository.save(
                      CmsOverlayEntity.builder()
                          .id(cmsOverlayEntity.id())
                          .cmsOperatingStatus(
                              operatingStatus != null
                                  ? MAPPER_V0.writeValueAsString(operatingStatus)
                                  : null)
                          .cmsServices(MAPPER_V0.writeValueAsString(detailedServices))
                          .build());
                  log.info(
                      "CMS overlay updated for {} facility",
                      sanitize(cmsOverlayEntity.id().toIdString()));
                }
              } catch (Exception e) {
                log.error("Failed to load and/or update cms overlay data. {}", e.getMessage());
              }
            });
    return returnMap;
  }
}
