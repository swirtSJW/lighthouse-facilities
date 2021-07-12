package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CemeteriesTransformerV0Test {
  @Test
  void toFacility() {
    assertThat(tx().toFacility())
        .isEqualTo(CemeteriesSamples.Facilities.create().cemeteriesFacilities().get(0));
  }

  @Test
  void transformerPrioritizesNameFromXml() {
    String cdwFacilityName = "Fort Richardson";
    String xmlFacilityName = "Fort Richardson National Cemetery";
    assertThat(tx().facilityName(null)).isNull();
    assertThat(tx(null, xmlFacilityName).facilityName(cdwFacilityName)).isEqualTo(xmlFacilityName);
  }

  @Test
  void transformerPrioritizesWebsiteFromXml() {
    String cdwWebsite = "https://axolotl.com/nope";
    String xmlWebsite = "https://axolotl.com/vha/facility";
    assertThat(tx().website(null)).isNull();
    assertThat(tx(xmlWebsite, null).website(cdwWebsite)).isEqualTo(xmlWebsite);
  }

  private CemeteriesTransformerV0 tx() {
    return tx(null, null);
  }

  private CemeteriesTransformerV0 tx(String xmlWebsite, String xmlFacilityName) {
    return CemeteriesTransformerV0.builder()
        .cdwFacility(CemeteriesSamples.Cdw.create().cdwCemeteries())
        .externalFacilityName(xmlFacilityName)
        .externalWebsite(xmlWebsite)
        .build();
  }

  @Test
  void websiteInCsvReturnsValueWhenCdwIsNull() {
    String url = "https://shanktopus.com/vha/facility";
    assertThat(tx(url, null).toFacility().attributes().website()).isEqualTo(url);
  }
}
