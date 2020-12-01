package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CemeteriesTransformerTest {
  @Test
  void toFacility() {
    assertThat(tx().toFacility())
        .isEqualTo(CemeteriesSamples.Facilities.create().cemeteriesFacilities().get(0));
  }

  @Test
  void transformerPrioritizesWebsiteFromCdw() {
    String cdw = "https://shanktopus.com/vha/facility";
    String csv = "https://shanktofake.com/nope";
    assertThat(tx().website(null)).isNull();
    assertThat(tx(csv).website(cdw)).isEqualTo(cdw);
  }

  private CemeteriesTransformer tx() {
    return tx(null);
  }

  private CemeteriesTransformer tx(String csvWebsite) {
    return CemeteriesTransformer.builder()
        .cdwFacility(CemeteriesSamples.Cdw.create().cdwCemeteries())
        .website(csvWebsite)
        .build();
  }

  @Test
  void websiteInCsvReturnsValueWhenCdwIsNull() {
    String url = "https://shanktopus.com/vha/facility";
    assertThat(tx(url).toFacility().attributes().website()).isEqualTo(url);
  }
}
