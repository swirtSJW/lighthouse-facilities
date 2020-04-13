package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.DriveTimeBandEntity.Pk;
import gov.va.api.lighthouse.facilities.DriveTimeBandManagementController.BandResult;
import gov.va.api.lighthouse.facilities.ExceptionsV0.NotFound;
import gov.va.api.lighthouse.facilities.api.pssg.PathEncoder;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Attributes;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Geometry;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DriveTimeBandManagementControllerTest {

  @Mock DriveTimeBandRepository repo;

  DriveTimeBandManagementController controller() {
    return DriveTimeBandManagementController.builder().repository(repo).build();
  }

  @Test
  void getAllBands() {
    when(repo.findAllIds())
        .thenReturn(
            List.of(Pk.fromName("a-10-20"), Pk.fromName("a-20-30"), Pk.fromName("b-10-20")));
    assertThat(controller().driveTimeBandIds())
        .containsExactlyInAnyOrder("a-10-20", "a-20-30", "b-10-20");
  }

  @Test
  void getBandByNameReturnsKnownBand() {
    var e = Entities.diamond("a-1-2", 100);
    when(repo.findById(e.id())).thenReturn(Optional.of(e));
    assertThat(controller().band("a-1-2")).isEqualTo(new BandResult(e));
  }

  @Test
  void getBandByNameThrowsExceptionForUnknownBand() {
    when(repo.findById(Pk.fromName("a-1-2"))).thenReturn(Optional.empty());
    assertThatExceptionOfType(NotFound.class).isThrownBy(() -> controller().band("a-1-2"));
  }

  @Test
  void updateBandCreatesNewRecord() {
    var existingA12 = Entities.diamond("a-1-2", 900); // exists
    var existingA23 = Entities.diamond("a-2-3", 800); // exists
    var a12 = Entities.diamond("a-1-2", 100); // update
    var a23 = Entities.diamond("a-2-3", 200); // update
    var a34 = Entities.diamond("a-3-4", 300); // create

    when(repo.findById(a12.id())).thenReturn(Optional.of(existingA12));
    when(repo.findById(a23.id())).thenReturn(Optional.of(existingA23));
    when(repo.findById(a34.id())).thenReturn(Optional.empty());

    controller()
        .update(
            List.of(
                Entities.diamondBand("a-1-2", 100),
                Entities.diamondBand("a-2-3", 200),
                Entities.diamondBand("a-3-4", 300)));

    verify(repo).save(a12);
    verify(repo).save(a23);
    verify(repo).save(a34);
  }

  static class Entities {
    static DriveTimeBandEntity diamond(String name, int offset) {
      return DriveTimeBandEntity.builder()
          .id(Pk.fromName(name))
          .maxLongitude(offset + 1)
          .maxLatitude(offset + 2)
          .minLongitude(offset - 1)
          .minLatitude(offset - 2)
          .band(PathEncoder.create().encodeToBase64(diamondBand(name, offset)))
          .build();
    }

    static PssgDriveTimeBand diamondBand(String name, int offset) {
      Pk pk = Pk.fromName(name);
      List<List<Double>> ring1 = PssgDriveTimeBand.newRing(4);
      // Diamond around offset,offset
      ring1.add(PssgDriveTimeBand.coord(offset, offset + 2));
      ring1.add(PssgDriveTimeBand.coord(offset + 1, offset));
      ring1.add(PssgDriveTimeBand.coord(offset, offset - 2));
      ring1.add(PssgDriveTimeBand.coord(offset - 1, offset));
      List<List<List<Double>>> rings = PssgDriveTimeBand.newListOfRings();
      rings.add(ring1);
      return PssgDriveTimeBand.builder()
          .attributes(
              Attributes.builder()
                  .stationNumber(pk.stationNumber())
                  .fromBreak(pk.fromMinutes())
                  .toBreak(pk.toMinutes())
                  .build())
          .geometry(Geometry.builder().rings(rings).build())
          .build();
    }
  }
}
