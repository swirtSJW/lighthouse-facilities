package gov.va.api.lighthouse.facilities.collector;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class VetCentersCollector {
  @NonNull final Collection<VastEntity> vastEntities;

  @NonNull final Map<String, String> websites;

  Collection<Facility> collect() {
    try {
      return vastEntities.stream()
          .filter(Objects::nonNull)
          .filter(vast -> vast.isVetCenter())
          .map(
              vast ->
                  VetCenterTransformer.builder().vast(vast).websites(websites).build().toFacility())
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new CollectorExceptions.VetCentersCollectorException(e);
    }
  }
}
