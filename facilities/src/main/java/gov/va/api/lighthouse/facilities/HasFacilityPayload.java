package gov.va.api.lighthouse.facilities;

/** A DTO projection of the FacilityEntity. */
public interface HasFacilityPayload {
  String cmsOperatingStatus();

  String cmsServices();

  String facility();
}
