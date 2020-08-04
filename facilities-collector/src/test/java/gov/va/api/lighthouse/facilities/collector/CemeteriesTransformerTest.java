package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CemeteriesTransformerTest {
  @Test
  public void toFacility() {
    assertThat(tx().toFacility())
        .isEqualTo(CemeteriesSamples.Facilities.create().cemeteriesFacilities().get(0));
  }

  @Test
  public void transformerPrioritizesWebsiteFromArcGis() {
    String arcgis = "https://shanktopus.com/vha/facility";
    String csv = "https://shanktofake.com/nope";
    assertThat(tx().website(null)).isNull();
    assertThat(tx(csv).website(arcgis)).isEqualTo(arcgis);
  }

  private CemeteriesTransformer tx() {
    return tx(null);
  }

  private CemeteriesTransformer tx(String csvWebsite) {
    return CemeteriesTransformer.builder()
        .arcgisFacility(CemeteriesSamples.ArcGis.create().arcgisCemeteries().features().get(0))
        .csvWebsite(csvWebsite)
        .build();
  }

  @Test
  public void websiteInCsvReturnsValueWhenArcGisIsNull() {
    String url = "https://shanktopus.com/vha/facility";
    assertThat(tx(url).toFacility().attributes().website()).isEqualTo(url);
  }
}
