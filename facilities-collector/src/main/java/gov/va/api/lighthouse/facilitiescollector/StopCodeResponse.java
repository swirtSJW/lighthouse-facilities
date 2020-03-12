package gov.va.api.lighthouse.facilitiescollector;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public final class StopCodeResponse {
  @Builder.Default List<StopCode> stopCodes = new ArrayList<>();

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class StopCode {
    String divisionFcdmd;

    String cocClassification;

    String sta6a;

    String primaryStopCode;

    String primaryStopCodeName;

    String numberOfAppointmentsLinkedToConsult;

    String numberOfLocations;

    String avgWaitTimeNew;
  }
}
