package gov.va.api.lighthouse.facilitiescollector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class HealthTransformerTest {
  private void assertClassification(String classificationId, String featureCode, String expected) {
    assertThat(
            HealthTransformer.builder()
                .vast(
                    VastEntity.builder()
                        .cocClassificationId(classificationId)
                        .abbreviation(featureCode)
                        .build())
                .accessToCare(ArrayListMultimap.create())
                .accessToCareCovid19(emptyMap())
                .accessToPwt(ArrayListMultimap.create())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .build()
                .classification())
        .isEqualTo(expected);
  }

  @Test
  public void classification() {
    assertThat(
            HealthTransformer.builder()
                .vast(new VastEntity())
                .accessToCare(ArrayListMultimap.create())
                .accessToCareCovid19(emptyMap())
                .accessToPwt(ArrayListMultimap.create())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .build()
                .classification())
        .isNull();
    assertClassification(null, null, null);
    assertClassification("1", null, "VA Medical Center (VAMC)");
    assertClassification("2", null, "Health Care Center (HCC)");
    assertClassification("3", null, "Multi-Specialty CBOC");
    assertClassification("4", null, "Primary Care CBOC");
    assertClassification("5", null, "Other Outpatient Services (OOS)");
    assertClassification("7", null, "Residential Care Site (MH RRTP/DRRTP) (Stand-Alone)");
    assertClassification("8", null, "Extended Care Site (Community Living Center) (Stand-Alone)");
    assertClassification("x", null, "x");
    assertClassification(null, "f", "f");
  }

  @Test
  public void covid19() {
    assertThat(
            getCovid(
                AccessToCareCovid19Entry.builder()
                    .stationId("666")
                    .confirmedCases(120)
                    .deaths(40)
                    .build()))
        .isEqualTo(Facility.Covid19.builder().confirmedCases(120).deaths(40).build());
    assertThat(
            getCovid(
                AccessToCareCovid19Entry.builder().stationId("666").confirmedCases(120).build()))
        .isEqualTo(Facility.Covid19.builder().confirmedCases(120).build());
    assertThat(getCovid(AccessToCareCovid19Entry.builder().stationId("666").deaths(40).build()))
        .isEqualTo(Facility.Covid19.builder().deaths(40).build());
    assertThat(getCovid(AccessToCareCovid19Entry.builder().stationId("666").build())).isNull();
  }

  @Test
  public void empty() {
    assertThat(
            HealthTransformer.builder()
                .vast(new VastEntity())
                .accessToCare(ArrayListMultimap.create())
                .accessToCareCovid19(emptyMap())
                .accessToPwt(ArrayListMultimap.create())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .build()
                .toFacility())
        .isNull();
    ArrayListMultimap<String, AccessToCareEntry> atc = ArrayListMultimap.create();
    atc.put("VHA_X", AccessToCareEntry.builder().build());
    ArrayListMultimap<String, AccessToPwtEntry> atp = ArrayListMultimap.create();
    atp.put("VHA_X", AccessToPwtEntry.builder().build());
    ArrayListMultimap<String, StopCode> sc = ArrayListMultimap.create();
    sc.put("VHA_X", StopCode.builder().build());
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().stationNumber("x").build())
                .accessToCare(atc)
                .accessToCareCovid19(emptyMap())
                .accessToPwt(atp)
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(sc)
                .websites(emptyMap())
                .build()
                .toFacility())
        .isEqualTo(Facility.builder().id("vha_x").type(Facility.Type.va_facilities).build());
  }

  private Facility.Covid19 getCovid(AccessToCareCovid19Entry entry) {
    Map<String, AccessToCareCovid19Entry> covidAtc = ImmutableMap.of("VHA_666", entry);
    return HealthTransformer.builder()
        .vast(VastEntity.builder().stationNumber("666").build())
        .accessToCare(ArrayListMultimap.create())
        .accessToCareCovid19(covidAtc)
        .accessToPwt(ArrayListMultimap.create())
        .mentalHealthPhoneNumbers(emptyMap())
        .stopCodesMap(ArrayListMultimap.create())
        .websites(emptyMap())
        .build()
        .covid19();
  }
}
