package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class GeoFacilityTransformer {
  @NonNull private final Facility facility;

  private GeoFacility.Geometry geometry() {
    Facility.Attributes attr = facility.attributes();
    if (attr == null || (attr.longitude() == null && attr.latitude() == null)) {
      return null;
    }
    return GeoFacility.Geometry.builder()
        .type(GeoFacility.GeometryType.Point)
        .coordinates(List.of(attr.longitude(), attr.latitude()))
        .build();
  }

  private String id() {
    return facility.id();
  }

  private GeoFacility.Properties properties() {
    if (isBlank(id())) {
      return null;
    }
    Facility.Attributes attr = facility.attributes();
    return GeoFacility.Properties.builder()
        .id(id())
        .name(attr.name())
        .facilityType(attr.facilityType())
        .classification(attr.classification())
        .website(attr.website())
        .address(attr.address())
        .phone(attr.phone())
        .hours(attr.hours())
        .services(attr.services())
        .satisfaction(attr.satisfaction())
        .waitTimes(attr.waitTimes())
        .mobile(attr.mobile())
        .activeStatus(attr.activeStatus())
        .visn(attr.visn())
        .build();
  }

  GeoFacility toGeoFacility() {
    if (isBlank(id())) {
      return null;
    }
    return GeoFacility.builder()
        .type(GeoFacility.Type.Feature)
        .geometry(geometry())
        .properties(properties())
        .build();
  }
}
