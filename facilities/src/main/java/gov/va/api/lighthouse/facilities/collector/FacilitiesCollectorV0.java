package gov.va.api.lighthouse.facilities.collector;

import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.FacilityTransformerV0;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import java.util.HashMap;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** V0 Facilities Collector. */
@Slf4j
@Component
public class FacilitiesCollectorV0 extends FacilitiesCollector {
  private final CmsOverlayCollectorV0 cmsOverlayCollector;

  /** Autowired constructor. */
  public FacilitiesCollectorV0(
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Autowired JdbcTemplate jdbcTemplate,
      @Autowired CmsOverlayCollectorV0 cmsOverlayCollector,
      @Value("${access-to-care.url}") String atcBaseUrl,
      @Value("${access-to-pwt.url}") String atpBaseUrl,
      @Value("${cemeteries.url}") String cemeteriesBaseUrl) {
    super(insecureRestTemplateProvider, jdbcTemplate, atcBaseUrl, atpBaseUrl, cemeteriesBaseUrl);
    this.cmsOverlayCollector = cmsOverlayCollector;
  }

  @SneakyThrows
  protected void updateOperatingStatusFromCmsOverlay(List<DatamartFacility> datamartFacilities) {
    HashMap<String, CmsOverlay> cmsOverlays;
    try {
      cmsOverlays = cmsOverlayCollector.loadAndUpdateCmsOverlays();
    } catch (Exception e) {
      throw new CollectorExceptions.CollectorException(e);
    }
    for (DatamartFacility datamartFacility : datamartFacilities) {
      if (cmsOverlays.containsKey(datamartFacility.id())) {
        CmsOverlay cmsOverlay = cmsOverlays.get(datamartFacility.id());
        datamartFacility
            .attributes()
            .operatingStatus(
                FacilityTransformerV0.toVersionAgnosticFacilityOperatingStatus(
                    cmsOverlay.operatingStatus()));
        datamartFacility.attributes().detailedServices(cmsOverlay.detailedServices());
      } else {
        log.warn("No cms overlay for facility: {}", datamartFacility.id());
      }
    }
  }
}
