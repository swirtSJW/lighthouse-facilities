package gov.va.api.lighthouse.facilities.collector;

import static java.util.stream.Collectors.toList;

import gov.va.api.lighthouse.facilities.DatamartFacility;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class VetCentersCollector {
  @NonNull final Collection<VastEntity> vastEntities;

  @NonNull final Map<String, String> websites;

  Collection<DatamartFacility> collect() {
    try {
      return vastEntities.stream()
          .filter(Objects::nonNull)
          .filter(VastEntity::isVetCenter)
          .map(
              vast ->
                  VetCenterTransformer.builder()
                      .vast(vast)
                      .websites(websites)
                      .build()
                      .toDatamartFacility())
          .filter(Objects::nonNull)
          .collect(toList());
    } catch (Exception e) {
      throw new CollectorExceptions.VetCentersCollectorException(e);
    }
  }
}
