package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CovidServiceUpdaterTest {
  @Test
  public void noCovidServicesToUpdate() {
    DetailedService service1 =
        DetailedService.builder().name("Service1").path("http://www.service.one.va.gov").build();
    DetailedService service2 =
        DetailedService.builder().name("Service2").path("http://www.service.two.va.gov").build();
    DetailedService service3 =
        DetailedService.builder().name("Service3").path("http://www.service.three.va.gov").build();
    List<DetailedService> detailedServices = List.of(service1, service2, service3);
    assertThat(CovidServiceUpdater.updateServiceUrlPaths("vha_438GA", detailedServices).size())
        .isEqualTo(3);
    assertThat(service1.path()).isEqualTo("http://www.service.one.va.gov");
    assertThat(service2.path()).isEqualTo("http://www.service.two.va.gov");
    assertThat(service3.path()).isEqualTo("http://www.service.three.va.gov");
  }

  @Test
  public void updateCovidServicePaths() {
    DetailedService service1 =
        DetailedService.builder().name("Service1").path("http://www.service.one.va.gov").build();
    DetailedService service2 =
        DetailedService.builder().name("Service2").path("http://www.service.two.va.gov").build();
    DetailedService covidService =
        DetailedService.builder()
            .name(CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19)
            .path("http://path.to.update.gov")
            .build();
    DetailedService service3 =
        DetailedService.builder().name("Service3").path("http://www.service.three.va.gov").build();
    List<DetailedService> detailedServices = List.of(service1, service2, covidService, service3);
    // Update covid service url paths for id vha_438GA
    assertThat(CovidServiceUpdater.updateServiceUrlPaths("vha_438GA", detailedServices).size())
        .isEqualTo(4);
    assertThat(service1.path()).isEqualTo("http://www.service.one.va.gov");
    assertThat(service2.path()).isEqualTo("http://www.service.two.va.gov");
    assertThat(service3.path()).isEqualTo("http://www.service.three.va.gov");
    assertThat(covidService.path())
        .isEqualTo("https://www.va.gov/sioux-falls-health-care/programs/covid-19-vaccines/");
    // Update covid service url paths for id vha_463GA
    assertThat(CovidServiceUpdater.updateServiceUrlPaths("vha_463GA", detailedServices).size())
        .isEqualTo(4);
    assertThat(service1.path()).isEqualTo("http://www.service.one.va.gov");
    assertThat(service2.path()).isEqualTo("http://www.service.two.va.gov");
    assertThat(service3.path()).isEqualTo("http://www.service.three.va.gov");
    assertThat(covidService.path())
        .isEqualTo("https://www.va.gov/alaska-health-care/programs/covid-19-vaccines/");
    // Update covid service url paths for non-existent id
    assertThat(CovidServiceUpdater.updateServiceUrlPaths("non-existent", detailedServices).size())
        .isEqualTo(4);
    assertThat(service1.path()).isEqualTo("http://www.service.one.va.gov");
    assertThat(service2.path()).isEqualTo("http://www.service.two.va.gov");
    assertThat(service3.path()).isEqualTo("http://www.service.three.va.gov");
    assertThat(covidService.path()).isNull();
  }

  @Test
  public void updateEmptyList() {
    List<DetailedService> emptyList = new ArrayList<>();
    assertThat(CovidServiceUpdater.updateServiceUrlPaths("vha_438GA", emptyList)).isEmpty();
  }
}
