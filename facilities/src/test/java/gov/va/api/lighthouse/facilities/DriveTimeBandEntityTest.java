package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import gov.va.api.lighthouse.facilities.DriveTimeBandEntity.Pk;
import org.junit.jupiter.api.Test;

public class DriveTimeBandEntityTest {

  @Test
  void pkNameConversion() {
    Pk pk = Pk.of("ABC", 10, 20);
    assertThat(pk.name()).isEqualTo("ABC-10-20");
    assertThat(Pk.fromName("ABC-10-20")).isEqualTo(pk);
    assertThatIllegalArgumentException().isThrownBy(() -> Pk.fromName("nope"));
    assertThatIllegalArgumentException().isThrownBy(() -> Pk.fromName("nope-1"));
    assertThatIllegalArgumentException().isThrownBy(() -> Pk.fromName("nope-1-2-3"));
  }
}
