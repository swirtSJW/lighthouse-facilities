package gov.va.api.lighthouse.facilities.collector;

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

  static final Set<String> DENTISTRY = Set.of("180");

  String stationNumber;

  String code;

  String name;

  BigDecimal waitTimeNew;
}
