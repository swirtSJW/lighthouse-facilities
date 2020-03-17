package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CollectContollerTest {
  @Test
  public void verifyResponse() {
    assertThat(new CollectController().collectFacilities())
        .isExactlyInstanceOf(CollectorFacilitiesResponse.class);
  }
}
