package gov.va.api.lighthouse.facilities;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

/** This is the response returned by the internal management API when reloading facilities data. */
@Builder
@Data
public class ReloadResponse {
  private int totalFacilities;
  private List<String> facilitiesCreated;
  private List<String> facilitiesUpdated;
  private List<String> facilitiesDeleted;
  private List<Problem> problems;
  private Timing timing;

  /**
   * Create an instance that is has thread safe collections that can be added to when processing
   * records simultaneously and has timing initialized to start now.
   */
  public static ReloadResponse start() {
    return ReloadResponse.builder()
        .timing(Timing.builder().start(Instant.now()).build())
        .facilitiesCreated(new CopyOnWriteArrayList<>())
        .facilitiesUpdated(new CopyOnWriteArrayList<>())
        .facilitiesDeleted(new CopyOnWriteArrayList<>())
        .problems(new CopyOnWriteArrayList<>())
        .build();
  }

  @Value
  @AllArgsConstructor(staticName = "of")
  public static class Problem {
    String facilityId;
    String description;
  }

  @Builder
  @Data
  public static class Timing {
    /** The time we started the reload process. */
    private Instant start;

    /**
     * The time after we started and completed the collection phase, but not yet started updating
     * the database.
     */
    private Instant completeCollection;

    /** The time completed all work (including the DB updates). */
    private Instant complete;

    /** The amount of time it took to perform the full reload cycle. */
    private Duration totalDuration;

    /** Set the 'complete' time to now and compute the 'totalDuration'. */
    public void markComplete() {
      complete = Instant.now();
      totalDuration = Duration.between(start, complete);
    }

    /** Set the 'completeCollection' time to now. */
    public void markCompleteCollection() {
      completeCollection = Instant.now();
    }
  }
}
