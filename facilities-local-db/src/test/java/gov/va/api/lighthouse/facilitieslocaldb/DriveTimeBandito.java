package gov.va.api.lighthouse.facilitieslocaldb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Attributes;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Geometry;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKBReader;

/**
 * This tool is uses to synthesize PSSG drive time band responses based on SQL data captured by the
 * existing vets-api implementation and stored in Postgres. It will parse a SQL database dump and
 * produce pages of PSSG responses.
 */
@Value
@Builder
public class DriveTimeBandito {
  Path sqlExport;

  private static void log(String msg) {
    System.out.println(msg);
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      throw new RuntimeException(
          DriveTimeBandito.class.getSimpleName() + " <drivetime-band-export.sql>");
    }
    DriveTimeBandito.builder().sqlExport(Path.of(args[0])).build().stealDriveTimeBands();
  }

  private List<List<List<Double>>> ringsOf(String polygon) {
    return new PolygonBabyGon().parseRings(polygon);
  }

  @SneakyThrows
  public void stealDriveTimeBands() {
    log("Reading " + sqlExport.toAbsolutePath().getFileName());

    try (FileBunder bundler =
        FileBunder.builder().outputDir(new File("./pssg")).itemsPerPage(50).build()) {
      var bandLine = Pattern.compile("^[0-9]+.*$");
      Files.lines(sqlExport)
          .filter(l -> bandLine.matcher(l).matches())
          .map(Entry::fromTsv)
          .map(this::toPssgDriveTimeBand)
          .forEach(bundler);
    }
  }

  private PssgDriveTimeBand toPssgDriveTimeBand(Entry entry) {
    return PssgDriveTimeBand.builder()
        .attributes(
            Attributes.builder()
                .stationNumber(entry.stationNumber())
                .fromBreak(entry.min())
                .toBreak(entry.max())
                .build())
        .geometry(Geometry.builder().rings(ringsOf(entry.polygon())).build())
        .build();
  }

  /** The Entry represents a database row, exacted from SQL statements. */
  @Value
  @Builder
  private static class Entry {
    String polygon;

    String stationNumber;

    int min;

    int max;

    /**
     * Extract from SQL TSV line. We do not need, nor extract, all information.
     *
     * <pre>
     *  FIELD               USE   EXAMPLE
     *  ------------------- ----- ---------------------------
     *  id                  YES   814
     *  name                NO    402GB : 50 - 60
     *  unit                NO    minutes
     *  polygon             YES   010300002...Lots..of..data
     *  vha_facility_id     YES   402GB
     *  created_at          NO    2019-11-01 08:57:24.701199
     *  updated_at          NO    2020-03-05 09:55:10.981709
     *  min                 YES   50
     *  max                 YES   60
     *  vssc_extract_date   YES   2020-01-25 03:00:01
     * </pre>
     */
    static Entry fromTsv(String tsv) {
      var parts = Splitter.on("\t").trimResults().splitToList(tsv);
      return Entry.builder()
          .polygon(parts.get(3))
          .stationNumber(parts.get(4))
          .min(Integer.parseInt(parts.get(7)))
          .max(Integer.parseInt(parts.get(8)))
          .build();
    }

    String label() {
      return stationNumber + "-" + min + "-" + max;
    }
  }

  /**
   * The bundler will group a quantity of PSSG drive time bands in to files, creating multiple pages
   * of responses. Files are created in a specified output directory with naming convention:
   * pssg-drive-time-bands-${page}.json
   *
   * <p>You must `close()` the bundler to complete the last page.
   */
  private static class FileBunder implements Consumer<PssgDriveTimeBand>, AutoCloseable {
    private final File outputDir;

    private final int itemsPerPage;

    private final ObjectMapper mapper = new ObjectMapper();

    private final List<PssgDriveTimeBand> bands = new ArrayList<>();

    private int pageCount = 0;

    @Builder
    public FileBunder(File outputDir, int itemsPerPage) {
      this.outputDir = outputDir;
      this.itemsPerPage = itemsPerPage;
    }

    @Override
    public void accept(PssgDriveTimeBand band) {
      bands.add(band);
      if (bands.size() >= itemsPerPage) {
        close();
      }
    }

    @Override
    @SneakyThrows
    public void close() {
      outputDir.mkdirs();
      File output = new File(outputDir, "pssg-drive-time-bands-" + pageCount + ".json");
      log("Saving " + output.getAbsolutePath());
      mapper.writeValue(output, bands);
      bands.clear();
      pageCount++;
    }
  }

  /**
   * Provides parsing logic transition from encoded GeoJSON geometry to the list of lists of
   * coordinate lists.
   */
  private static class PolygonBabyGon {
    static double truncateTo(double unroundedNumber, int decimalPlaces) {
      int truncatedNumberInt = (int) (unroundedNumber * Math.pow(10, decimalPlaces));
      return (double) (truncatedNumberInt / Math.pow(10, decimalPlaces));
    }

    /**
     * The Geometry will be converted into rings per the GeoJSON specification.
     * https://tools.ietf.org/html/rfc7946#section-3.1.6
     */
    @SneakyThrows
    List<List<List<Double>>> parseRings(String base64Geometry) {
      GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);
      WKBReader wkbReader = new WKBReader(geoFactory);
      Polygon polygon = (Polygon) wkbReader.read(WKBReader.hexToBytes(base64Geometry));
      var rings = PssgDriveTimeBand.newListOfRings();
      rings.add(toRing(polygon.getExteriorRing().getCoordinates()));
      for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
        rings.add(toRing(polygon.getInteriorRingN(i).getCoordinates()));
      }
      return rings;
    }

    private List<List<Double>> toRing(Coordinate[] coordinates) {
      var ring = PssgDriveTimeBand.newRing(coordinates.length);
      for (Coordinate c : coordinates) {
        ring.add(PssgDriveTimeBand.coord(truncateTo(c.x, 5), truncateTo(c.y, 5)));
      }
      return ring;
    }
  }
}
