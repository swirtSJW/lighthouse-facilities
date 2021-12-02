package gov.va.api.lighthouse.facilities;

import java.util.Set;

/** A DTO projection of the FacilityEntity. */
public interface HasFacilityPayload {
  String cmsOperatingStatus();

  String cmsServices();

  /**
   * In order to be API version agnostic, facility data is persisted as a JSON string representing a
   * DatamartFacility object. When retrieved from the facility entity, the JSON string is then
   * transformed into the appropriate versioned facility object.
   */
  String facility();

  Set<String> overlayServices();
}
