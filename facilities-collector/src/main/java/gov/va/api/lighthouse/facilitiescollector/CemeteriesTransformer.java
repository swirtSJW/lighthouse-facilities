package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import javax.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public class CemeteriesTransformer {
  @NotNull ArcGisCemeteries.Feature arcgisFacility;

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
                        .address1(attributes.siteAddress1())
                        .address2(attributes.siteAddress2())
                        .city(attributes.siteCity())
                        .state(attributes.siteState())
                        .zip(attributes.siteZip())
                        .build())
                .mailing(
                    Facility.Address.builder()
                        .address1(attributes.mailAddress1())
                        .address2(attributes.mailAddress2())
                        .city(attributes.mailCity())
                        .state(attributes.mailState())
                        .zip(attributes.mailZip())
                        .build())
                .build())
        .phone(Facility.Phone.builder().main(attributes.phone()).fax(attributes.fax()).build())
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
