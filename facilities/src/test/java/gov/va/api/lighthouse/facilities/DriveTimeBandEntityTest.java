package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

public class DriveTimeBandEntityTest {
  @Test
  void pkNameConversion() {
    var pk = DriveTimeBandEntity.Pk.of("ABC", 10, 20);
    assertThat(pk.name()).isEqualTo("ABC-10-20");
    assertThat(DriveTimeBandEntity.Pk.fromName("ABC-10-20")).isEqualTo(pk);
    assertThatIllegalArgumentException().isThrownBy(() -> DriveTimeBandEntity.Pk.fromName("nope"));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> DriveTimeBandEntity.Pk.fromName("nope-1"));
    assertThatIllegalArgumentException()
        .isThrownBy(() -> DriveTimeBandEntity.Pk.fromName("nope-1-2-3"));
  }
}
