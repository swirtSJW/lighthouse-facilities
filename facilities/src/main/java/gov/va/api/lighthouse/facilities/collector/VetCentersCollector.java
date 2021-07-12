package gov.va.api.lighthouse.facilities.collector;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class VetCentersCollector {
  @NonNull final Collection<VastEntity> vastEntities;

  @NonNull final Map<String, String> websites;

  Collection<gov.va.api.lighthouse.facilities.api.v0.Facility> collect() {
    try {
      return vastEntities.stream()
          .filter(Objects::nonNull)
          .filter(VastEntity::isVetCenter)
          .map(
              vast ->
                  VetCenterTransformerV0.builder()
                      .vast(vast)
                      .websites(websites)
                      .build()
                      .toFacility())
          .filter(Objects::nonNull)
          .collect(toList());
    } catch (Exception e) {
      throw new CollectorExceptions.VetCentersCollectorException(e);
    }
  }

  Collection<gov.va.api.lighthouse.facilities.api.v1.Facility> collectV1() {
    try {
      return vastEntities.stream()
          .filter(Objects::nonNull)
          .filter(VastEntity::isVetCenter)
          .map(
              vast ->
                  VetCenterTransformerV1.builder()
                      .vast(vast)
                      .websites(websites)
                      .build()
                      .toFacility())
          .filter(Objects::nonNull)
          .collect(toList());
    } catch (Exception e) {
      throw new CollectorExceptions.VetCentersCollectorException(e);
    }
  }
}
