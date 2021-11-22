package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.CmsOverlayEntity;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV0;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayCollector {
  private final CmsOverlayRepository cmsOverlayRepository;

  /** Method for determining whether Covid service is contained within detailed services. */
  public static boolean containsCovidService(List<DetailedService> detailedServices) {
    return detailedServices != null
        && detailedServices.parallelStream()
            .anyMatch(f -> f.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19));
  }

  private static <K, V>
      Collector<AbstractMap.SimpleEntry<K, V>, ?, HashMap<K, V>> convertOverlayToMap() {
    return Collectors.toMap(
        AbstractMap.SimpleEntry::getKey,
        AbstractMap.SimpleEntry::getValue,
        (prev, next) -> next,
        HashMap::new);
  }

  /** Load and return map of CMS overlays for each facility id. */
  public HashMap<String, CmsOverlay> loadAndUpdateCmsOverlays() {
    final ObjectMapper mapper = FacilitiesJacksonConfigV0.createMapper();
    HashMap<String, CmsOverlay> overlays = Streams.stream(cmsOverlayRepository.findAll())
        .parallel()
        .map(
            cmsOverlayEntity -> {
              CmsOverlay overlay =
                  CmsOverlay.builder()
                      .operatingStatus(
                          cmsOverlayEntity.cmsOperatingStatus() != null
                              ? FacilitiesJacksonConfigV0.quietlyMap(
                                  mapper,
                                  cmsOverlayEntity.cmsOperatingStatus(),
                                  Facility.OperatingStatus.class)
                              : null)
                      .detailedServices(
                          cmsOverlayEntity.cmsServices() != null
                              ? updateServiceUrlPaths(
                                  cmsOverlayEntity.id().toIdString(),
                                  List.of(
                                      FacilitiesJacksonConfigV0.quietlyMap(
                                          mapper,
                                          cmsOverlayEntity.cmsServices(),
                                          DetailedService[].class)))
                              : null)
                      .build();
              // Save updates made to overlay with Covid services
              final Facility.OperatingStatus operatingStatus = overlay.operatingStatus();
              final List<DetailedService> detailedServices = overlay.detailedServices();
              if (containsCovidService(detailedServices)) {
                cmsOverlayRepository.save(
                    CmsOverlayEntity.builder()
                        .id(cmsOverlayEntity.id())
                        .cmsOperatingStatus(
                            operatingStatus != null
                                ? FacilitiesJacksonConfigV0.quietlyWriteValueAsString(
                                    mapper, operatingStatus)
                                : null)
                        .cmsServices(
                            FacilitiesJacksonConfigV0.quietlyWriteValueAsString(
                                mapper, detailedServices))
                        .build());
                log.info(
                    "CMS overlay updated for {} facility",
                    sanitize(cmsOverlayEntity.id().toIdString()));
              }
              return new AbstractMap.SimpleEntry<>(cmsOverlayEntity.id().toIdString(), overlay);
            })
        .collect(convertOverlayToMap());
    log.info("Loaded {} overlays from {} db entities", overlays.size(), cmsOverlayRepository.count());
    return overlays;
  }
}
