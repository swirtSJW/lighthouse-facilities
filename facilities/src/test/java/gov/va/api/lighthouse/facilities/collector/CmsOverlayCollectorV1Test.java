package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.CmsOverlayEntity;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV1;
import gov.va.api.lighthouse.facilities.FacilityEntity;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CmsOverlayCollectorV1Test {
  @Mock CmsOverlayRepository mockCmsOverlayRepository;

  @Test
  public void exceptions() {
    CmsOverlayEntity mockEntity = mock(CmsOverlayEntity.class);
    when(mockEntity.id()).thenThrow(new NullPointerException("oh noes"));
    when(mockCmsOverlayRepository.findAll()).thenReturn(List.of(mockEntity));
    CmsOverlayCollectorV1 collectorV1 = new CmsOverlayCollectorV1(mockCmsOverlayRepository);
    assertThat(collectorV1.loadAndUpdateCmsOverlays()).isEqualTo(Collections.emptyMap());
  }

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
                FacilitiesJacksonConfigV1.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                FacilitiesJacksonConfigV1.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    InsecureRestTemplateProvider mockInsecureRestTemplateProvider =
        mock(InsecureRestTemplateProvider.class);
    JdbcTemplate mockTemplate = mock(JdbcTemplate.class);
    when(mockCmsOverlayRepository.findAll()).thenReturn(List.of(overlayEntity));
    CmsOverlayCollectorV1 cmsOverlayCollector = new CmsOverlayCollectorV1(mockCmsOverlayRepository);
    HashMap<String, CmsOverlay> cmsOverlays = cmsOverlayCollector.loadAndUpdateCmsOverlays();
    // Verify loaded CMS overlay
    assertThat(cmsOverlays.isEmpty()).isFalse();
    DetailedService updatedCovidService =
        DetailedService.builder()
            .name(CMS_OVERLAY_SERVICE_NAME_COVID_19)
            .path("https://www.va.gov/durham-health-care/programs/covid-19-vaccines/")
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
  public void overlayWithNoDetailedServices() {
    var id = "vha_123GA";
    CmsOverlayEntity mockEntity = mock(CmsOverlayEntity.class);
    when(mockEntity.id()).thenReturn(FacilityEntity.Pk.fromIdString(id));
    when(mockEntity.cmsOperatingStatus())
        .thenReturn(
            FacilitiesJacksonConfigV1.createMapper()
                .writeValueAsString(
                    Facility.OperatingStatus.builder()
                        .code(Facility.OperatingStatusCode.NORMAL)
                        .build()));
    when(mockEntity.cmsServices()).thenReturn(null);
    when(mockCmsOverlayRepository.findAll()).thenReturn(List.of(mockEntity));
    HashMap<String, CmsOverlay> expectedOverlays = new HashMap<>();
    expectedOverlays.put(
        id,
        CmsOverlay.builder()
            .operatingStatus(
                Facility.OperatingStatus.builder()
                    .code(Facility.OperatingStatusCode.NORMAL)
                    .build())
            .build());
    CmsOverlayCollectorV1 collectorV1 = new CmsOverlayCollectorV1(mockCmsOverlayRepository);
    assertThat(collectorV1.loadAndUpdateCmsOverlays())
        .usingRecursiveComparison()
        .isEqualTo(expectedOverlays);
  }

  @Test
  @SneakyThrows
  void verifyContainsCovidService() {
    assertThat(
            new CmsOverlayCollectorV1(mockCmsOverlayRepository)
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
            new CmsOverlayCollectorV1(mockCmsOverlayRepository)
                .containsCovidService(
                    List.of(
                        DetailedService.builder().name("Optometry").build(),
                        DetailedService.builder().name("Cardiology").build(),
                        DetailedService.builder().name("Dermatology").build())))
        .isFalse();
    assertThat(new CmsOverlayCollectorV1(mockCmsOverlayRepository).containsCovidService(null))
        .isFalse();
  }
}
