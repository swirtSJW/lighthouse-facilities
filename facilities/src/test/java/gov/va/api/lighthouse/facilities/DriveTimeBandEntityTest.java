package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.DriveTimeBandEntity.Pk;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Attributes;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Geometry;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DriveTimeBandEntityTest {

  @SneakyThrows
  @Test
  void asPssgDriveTimeBand() {

    List<List<Double>> ring1 = PssgDriveTimeBand.newRing(2);
    ring1.add(PssgDriveTimeBand.coord(1, 2));
    ring1.add(PssgDriveTimeBand.coord(3, 4));
    List<List<List<Double>>> rings = PssgDriveTimeBand.newListOfRings();
    rings.add(ring1);
    PssgDriveTimeBand band =
        PssgDriveTimeBand.builder()
            .attributes(Attributes.builder().stationNumber("ABC").fromBreak(10).toBreak(20).build())
            .geometry(Geometry.builder().rings(rings).build())
            .build();
    var mapper = JacksonConfig.createMapper();
    var entity = DriveTimeBandEntity.builder().band(mapper.writeValueAsString(band)).build();
    var actual = entity.asPssgDriveTimeBand();
    assertThat(actual).isEqualTo(band);
  }

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
