package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.CmsOverlayEntity;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV0;
import gov.va.api.lighthouse.facilities.FacilityEntity;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CmsOverlayCollectorTest {
  @Mock CmsOverlayRepository mockCmsOverlayRepository;

  @Test
  @SneakyThrows
  void loadCovidOverlay() {
    DetailedService covidService =
        DetailedService.builder()
            .name(CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19)
            .active(true)
            .path("replace_this_path")
            .build();
    CmsOverlay overlay =
        CmsOverlay.builder()
            .operatingStatus(
                Facility.OperatingStatus.builder()
                    .code(Facility.OperatingStatusCode.NORMAL)
                    .build())
            .detailedServices(List.of(covidService))
            .build();
    var pk = FacilityEntity.Pk.fromIdString("vha_558GA");
    CmsOverlayEntity overlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                FacilitiesJacksonConfigV0.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                FacilitiesJacksonConfigV0.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    List<CmsOverlayEntity> mockOverlays = new ArrayList<CmsOverlayEntity>();
    IntStream.range(1, 5000)
        .forEachOrdered(
            n -> {
              CmsOverlayEntity entity =
                  CmsOverlayEntity.builder()
                      .id(FacilityEntity.Pk.fromIdString("vha_" + Integer.toString(n)))
                      .cmsOperatingStatus(overlayEntity.cmsOperatingStatus())
                      .cmsServices(overlayEntity.cmsServices())
                      .build();
              mockOverlays.add(entity);
            });
    mockOverlays.add(overlayEntity);
    InsecureRestTemplateProvider mockInsecureRestTemplateProvider =
        mock(InsecureRestTemplateProvider.class);
    JdbcTemplate mockTemplate = mock(JdbcTemplate.class);
    when(mockCmsOverlayRepository.findAll()).thenReturn(mockOverlays); // List.of(overlayEntity));
    CmsOverlayCollector cmsOverlayCollector = new CmsOverlayCollector(mockCmsOverlayRepository);
    HashMap<String, CmsOverlay> cmsOverlays = cmsOverlayCollector.loadAndUpdateCmsOverlays();
    // Verify loaded CMS overlay
    assertThat(cmsOverlays.isEmpty()).isFalse();
    DetailedService updatedCovidService =
        DetailedService.builder()
            .name(CMS_OVERLAY_SERVICE_NAME_COVID_19)
            // .path("https://www.va.gov/durham-health-care/programs/covid-19-vaccines/")
            .path("replace_this_path")
            .build();
    CmsOverlay updatedOverlay =
        CmsOverlay.builder()
            .operatingStatus(overlay.operatingStatus())
            .detailedServices(List.of(updatedCovidService))
            .build();
    assertThat(cmsOverlays.get(pk.toIdString()))
        .usingRecursiveComparison()
        .isEqualTo(updatedOverlay);
  }

  @Test
  @SneakyThrows
  void verifyContainsCovidService() {
    assertThat(
            new CmsOverlayCollector(mockCmsOverlayRepository)
                .containsCovidService(
                    List.of(
                        DetailedService.builder().name(CMS_OVERLAY_SERVICE_NAME_COVID_19).build(),
                        DetailedService.builder().name("Cardiology").build(),
                        DetailedService.builder().name("Dermatology").build())))
        .isTrue();
  }

  @Test
  void verifyDoesNotContainCovidService() {
    assertThat(
            new CmsOverlayCollector(mockCmsOverlayRepository)
                .containsCovidService(
                    List.of(
                        DetailedService.builder().name("Optometry").build(),
                        DetailedService.builder().name("Cardiology").build(),
                        DetailedService.builder().name("Dermatology").build())))
        .isFalse();
    assertThat(new CmsOverlayCollector(mockCmsOverlayRepository).containsCovidService(null))
        .isFalse();
  }
}
