package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Locale;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class CemeteriesTransformer {
  @NonNull ArcGisCemeteries.Feature arcgisFacility;

  String csvWebsite;

  private Facility.FacilityAttributes attributes(ArcGisCemeteries.Attributes attributes) {
    return Facility.FacilityAttributes.builder()
        .name(attributes.fullName())
        .facilityType(Facility.FacilityType.va_cemetery)
        .classification(attributes.siteType())
        .latitude(arcgisFacility.geometry().latitude())
        .longitude(arcgisFacility.geometry().longitude())
        .website(website(arcgisFacility.attributes().websiteUrl()))
        .address(
            Facility.Addresses.builder()
                .physical(
                    Facility.Address.builder()
                        .address1(checkAngleBracketNull(attributes.siteAddress1()))
                        .address2(checkAngleBracketNull(attributes.siteAddress2()))
                        .city(attributes.siteCity())
                        .state(upperCase(attributes.siteState(), Locale.US))
                        .zip(attributes.siteZip())
                        .build())
                .mailing(
                    Facility.Address.builder()
                        .address1(checkAngleBracketNull(attributes.mailAddress1()))
                        .address2(checkAngleBracketNull(attributes.mailAddress2()))
                        .city(attributes.mailCity())
                        .state(upperCase(attributes.mailState(), Locale.US))
                        .zip(attributes.mailZip())
                        .build())
                .build())
        .phone(phone(attributes.phone(), attributes.fax()))
        .hours(
            Facility.Hours.builder()
                .monday(attributes.visitationHoursWeekday())
                .tuesday(attributes.visitationHoursWeekday())
                .wednesday(attributes.visitationHoursWeekday())
                .thursday(attributes.visitationHoursWeekday())
                .friday(attributes.visitationHoursWeekday())
                .saturday(attributes.visitationHoursWeekend())
                .sunday(attributes.visitationHoursWeekend())
                .build())
        .build();
  }

  private Facility.Phone phone(String attPhone, String attFax) {

    String main = phoneTrim(attPhone);
    String fax = phoneTrim(attFax);

    if (allBlank(main, fax)) {
      return null;
    } else {
      return Facility.Phone.builder().main(main).fax(fax).build();
    }
  }

  Facility toFacility() {
    return Facility.builder()
        .id("nca_" + arcgisFacility.attributes().siteId())
        .type(Facility.Type.va_facilities)
        .attributes(attributes(arcgisFacility.attributes()))
        .build();
  }

  String website(String arcgisWebsite) {
    /* ArcGIS returns a string NULL... We don't want to return that.*/
    return arcgisWebsite == null || arcgisWebsite.equalsIgnoreCase("NULL")
        ? csvWebsite
        : arcgisWebsite;
  }
}
