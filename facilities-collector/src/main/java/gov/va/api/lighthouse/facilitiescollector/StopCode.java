package gov.va.api.lighthouse.facilitiescollector;

import java.math.BigDecimal;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class StopCode {
  static final Set<String> NUTRITION = Set.of("123", "124");

  static final Set<String> PODIATRY = Set.of("411");

  String stationNum;

  String code;

  String name;

  BigDecimal waitTimeNew;
}
