package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.lighthouse.facilitiescollector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.hoursToClosed;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class VetCenterTransformer {
  @NonNull private final ArcGisVetCenters.Feature gis;

  @NonNull private final Map<String, String> websites;

  private Facility.Addresses address() {
    ArcGisVetCenters.Attributes attr = gis.attributes();
    // address1 is repeat of station name
    if (attr == null
        || allBlank(attr.zip(), attr.city(), attr.state(), attr.address2(), attr.address3())) {
      return null;
    }
    return Facility.Addresses.builder()
        .physical(
            Facility.Address.builder()
                .zip(attr.zip())
                .city(attr.city())
                .state(upperCase(attr.state(), Locale.US))
                .address1(attr.address2())
                .address2(attr.address3())
                .build())
        .build();
  }

  private Facility.FacilityAttributes attributes() {
    if (allBlank(name(), website(), latitude(), longitude(), address(), phone(), hours())) {
      return null;
    }
    return Facility.FacilityAttributes.builder()
        .name(name())
        .facilityType(Facility.FacilityType.vet_center)
        .website(website())
        .latitude(latitude())
        .longitude(longitude())
        .address(address())
        .phone(phone())
        .hours(hours())
        .build();
  }

  private Facility.Hours hours() {
    ArcGisVetCenters.Attributes attr = gis.attributes();
    if (attr == null) {
      return null;
    }
    String mon = hoursToClosed(attr.monday());
    String tue = hoursToClosed(attr.tuesday());
    String wed = hoursToClosed(attr.wednesday());
    String thu = hoursToClosed(attr.thursday());
    String fri = hoursToClosed(attr.friday());
    String sat = hoursToClosed(attr.saturday());
    String sun = hoursToClosed(attr.sunday());
    if (allBlank(mon, tue, wed, thu, fri, sat, sun)) {
      return null;
    }
    return Facility.Hours.builder()
        .monday(mon)
        .tuesday(tue)
        .wednesday(wed)
        .thursday(thu)
        .friday(fri)
        .saturday(sat)
        .sunday(sun)
        .build();
  }

  private String id() {
    if (gis.attributes() == null || allBlank(gis.attributes().stationNo())) {
      return null;
    }
    return "vc_" + gis.attributes().stationNo();
  }

  private BigDecimal latitude() {
    return gis.geometry() == null ? null : gis.geometry().latitude();
  }

  private BigDecimal longitude() {
    return gis.geometry() == null ? null : gis.geometry().longitude();
  }

  private String name() {
    return gis.attributes() == null ? null : gis.attributes().stationName();
  }

  private Facility.Phone phone() {
    if (gis.attributes() == null) {
      return null;
    }
    String main = phoneTrim(gis.attributes().staPhone());
    if (allBlank(main)) {
      return null;
    }
    return Facility.Phone.builder().main(main).build();
  }

  Facility toFacility() {
    if (allBlank(id())) {
      return null;
    }
    return Facility.builder()
        .id(id())
        .type(Facility.Type.va_facilities)
        .attributes(attributes())
        .build();
  }

  String website() {
    return allBlank(id()) ? null : websites.get(id());
  }
}
