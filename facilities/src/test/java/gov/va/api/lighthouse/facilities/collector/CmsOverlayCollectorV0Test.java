package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.CmsOverlayEntity;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV0;
import gov.va.api.lighthouse.facilities.FacilityEntity;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.Collections;
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
public class CmsOverlayCollectorV0Test {
  @Mock CmsOverlayRepository mockCmsOverlayRepository;

  @Test
  public void exceptions() {
    CmsOverlayEntity mockEntity = mock(CmsOverlayEntity.class);
    when(mockEntity.id()).thenReturn(FacilityEntity.Pk.fromIdString("vha_123"));
    when(mockEntity.cmsServices()).thenThrow(new NullPointerException("oh noes"));
    when(mockCmsOverlayRepository.findAll()).thenReturn(List.of(mockEntity));
    CmsOverlayCollectorV0 collectorV0 = new CmsOverlayCollectorV0(mockCmsOverlayRepository);
    assertThat(collectorV0.loadAndUpdateCmsOverlays()).isEqualTo(Collections.emptyMap());
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
    CmsOverlayCollectorV0 cmsOverlayCollector = new CmsOverlayCollectorV0(mockCmsOverlayRepository);
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
  public void overlayWithNoDetailedServices() {
    var id = "vha_123GA";
    CmsOverlayEntity mockEntity = mock(CmsOverlayEntity.class);
    when(mockEntity.id()).thenReturn(FacilityEntity.Pk.fromIdString(id));
    when(mockEntity.cmsOperatingStatus())
        .thenReturn(
            FacilitiesJacksonConfigV0.createMapper()
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
    CmsOverlayCollectorV0 collectorV0 = new CmsOverlayCollectorV0(mockCmsOverlayRepository);
    assertThat(collectorV0.loadAndUpdateCmsOverlays())
        .usingRecursiveComparison()
        .isEqualTo(expectedOverlays);
  }

  @Test
  @SneakyThrows
  void verifyContainsCovidService() {
    assertThat(
            new CmsOverlayCollectorV0(mockCmsOverlayRepository)
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
            new CmsOverlayCollectorV0(mockCmsOverlayRepository)
                .containsCovidService(
                    List.of(
                        DetailedService.builder().name("Optometry").build(),
                        DetailedService.builder().name("Cardiology").build(),
                        DetailedService.builder().name("Dermatology").build())))
        .isFalse();
    assertThat(new CmsOverlayCollectorV0(mockCmsOverlayRepository).containsCovidService(null))
        .isFalse();
  }
}
