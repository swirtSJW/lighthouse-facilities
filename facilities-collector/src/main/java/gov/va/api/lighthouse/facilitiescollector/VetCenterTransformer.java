package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.lighthouse.facilitiescollector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.hoursToClosed;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Locale;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class VetCenterTransformer {
  @NonNull private final VastEntity vast;

  @NonNull private final Map<String, String> websites;

  private Facility.Addresses address() {
    // address1 is repeat of station name
    if (allBlank(vast.zip(), vast.city(), vast.state(), vast.address2(), vast.address3())) {
      return null;
    }
    return Facility.Addresses.builder()
        .physical(
            Facility.Address.builder()
                .zip(vast.zip())
                .city(vast.city())
                .state(upperCase(vast.state(), Locale.US))
                .address1(vast.address2())
                .address2(vast.address3())
                .build())
        .build();
  }

  private Facility.FacilityAttributes attributes() {
    if (allBlank(
        vast.stationName(),
        website(),
        vast.latitude(),
        vast.longitude(),
        address(),
        phone(),
        hours())) {
      return null;
    }
    return Facility.FacilityAttributes.builder()
        .name(vast.stationName())
        .facilityType(Facility.FacilityType.vet_center)
        .website(website())
        .latitude(vast.latitude())
        .longitude(vast.longitude())
        .address(address())
        .phone(phone())
        .hours(hours())
        .build();
  }

  private Facility.Hours hours() {
    String mon = hoursToClosed(vast.monday());
    String tue = hoursToClosed(vast.tuesday());
    String wed = hoursToClosed(vast.wednesday());
    String thu = hoursToClosed(vast.thursday());
    String fri = hoursToClosed(vast.friday());
    String sat = hoursToClosed(vast.saturday());
    String sun = hoursToClosed(vast.sunday());
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
    if (allBlank(vast.stationNumber())) {
      return null;
    }
    return "vc_" + vast.stationNumber();
  }

  private Facility.Phone phone() {
    String main = phoneTrim(vast.staPhone());
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
