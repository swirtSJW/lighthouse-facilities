package gov.va.api.lighthouse.facilities;

import static java.util.stream.Collectors.toList;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.facilities.ExceptionsV0.NotFound;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(
    value = {"/internal/management/bands"},
    produces = {"application/json"})
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Builder
@Slf4j
public class DriveTimeBandManagementController {

  private final DriveTimeBandRepository repository;

  /** Get a band based on it's internal name: {stationNumber}-{fromMinutes}-{toMinutes}. */
  @GetMapping("/{name}")
  public PssgDriveTimeBand band(@PathVariable("name") String name) {
    return repository
        .findById(DriveTimeBandEntity.Pk.fromName(name))
        .orElseThrow(() -> new NotFound(name))
        .asPssgDriveTimeBand();
  }

  private Rectangle2D boundsOf(PssgDriveTimeBand band) {
    var rect = new AtomicReference<Rectangle2D>();
    band.geometry().rings().stream()
        .flatMap(r -> r.stream())
        .forEach(
            coord -> {
              double x = coord.get(PssgDriveTimeBand.INDEX_LONGITUDE);
              double y = coord.get(PssgDriveTimeBand.INDEX_LATITUDE);
              if (rect.get() == null) {
                rect.set(new Rectangle2D.Double(x, y, x, y));
              } else {
                rect.get().add(x, y);
              }
            });
    /* If there now rings for some reason, bounds are a point. */
    if (rect.get() == null) {
      return new Rectangle2D.Double();
    }
    return rect.get();
  }

  /** Get all band internal names: {stationNumber}-{fromMinutes}-{toMinutes.} */
  @GetMapping
  public List<String> driveTimeBandIds() {
    return repository.findAllIds().stream().map(DriveTimeBandEntity.Pk::name).collect(toList());
  }

  /** Create or update a band. */
  @Loggable(arguments = false)
  @PostMapping(consumes = "application/json")
  public void update(@Valid @NotEmpty @Size(max = 250) @RequestBody List<PssgDriveTimeBand> bands) {
    log.info("Updating {} bands", bands.size());
    bands.stream().forEach(this::updateBand);
  }

  @SneakyThrows
  private void updateBand(PssgDriveTimeBand band) {
    var pk =
        DriveTimeBandEntity.Pk.of(
            band.attributes().stationNumber(),
            band.attributes().fromBreak(),
            band.attributes().toBreak());
    DriveTimeBandEntity entity = repository.findById(pk).orElse(null);
    if (entity == null) {
      entity = DriveTimeBandEntity.builder().id(pk).build();
    }
    var bounds = boundsOf(band);
    entity.minLongitude(bounds.getMinX());
    entity.minLatitude(bounds.getMinY());
    entity.maxLongitude(bounds.getMaxX());
    entity.maxLatitude(bounds.getMaxY());
    entity.band(JacksonConfig.createMapper().writeValueAsString(band));
    repository.save(entity);
  }
}
