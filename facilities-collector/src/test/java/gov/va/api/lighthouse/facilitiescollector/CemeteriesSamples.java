package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CemeteriesSamples {
  @AllArgsConstructor(staticName = "create")
  public static class ArcGis {
    public ArcGisCemeteries arcgisCemeteries() {
      return ArcGisCemeteries.builder()
          .features(
              List.of(
                  ArcGisCemeteries.Feature.builder()
                      .attributes(attributes())
                      .geometry(
                          ArcGisCemeteries.Geometry.builder()
                              .latitude(new BigDecimal("-73.776232849999985"))
                              .longitude(new BigDecimal("42.651408840000045"))
                              .build())
                      .build()))
          .build();
    }

    private ArcGisCemeteries.Attributes attributes() {
      return ArcGisCemeteries.Attributes.builder()
          .fullName("Shanktopus Lot")
          .siteId("088")
          .siteType("Lot")
          .siteAddress1("8 Shanktopus Lane")
          .siteAddress2("Apartment 8")
          .siteCity("North")
          .siteState("Dakota")
          .siteZip("12208")
          .mailAddress1("8 Shanktopus Lane")
          .mailAddress2("Apartment 8")
          .mailCity("South")
          .mailState("Dakota")
          .mailZip("12208")
          .fax("123-456-7890")
          .phone("123-789-0456")
          .visitationHoursWeekday("Sunrise-Sunset")
          .visitationHoursWeekend("Sunrise-Sunset")
          .websiteUrl("NULL")
          .build();
    }
  }

  @AllArgsConstructor(staticName = "create")
  public static class Facilities {
    private Facility.Attributes attributes() {
      return Facility.Attributes.builder()
          .name("Shanktopus Lot")
          .facilityType(Facility.FacilityType.va_cemetery)
          .classification("Lot")
          .latitude(new BigDecimal("-73.776232849999985"))
          .longitude(new BigDecimal("42.651408840000045"))
          .address(
              Facility.Addresses.builder()
                  .physical(physicalAddress())
                  .mailing(mailingAddress())
                  .build())
          .phone(Facility.Phone.builder().main("123-789-0456").fax("123-456-7890").build())
          .hours(
              Facility.Hours.builder()
                  .monday("Sunrise-Sunset")
                  .tuesday("Sunrise-Sunset")
                  .wednesday("Sunrise-Sunset")
                  .thursday("Sunrise-Sunset")
                  .friday("Sunrise-Sunset")
                  .saturday("Sunrise-Sunset")
                  .sunday("Sunrise-Sunset")
                  .build())
          .build();
    }

    public List<Facility> cemeteriesFacilities() {
      return List.of(
          Facility.builder()
              .id("nca_088")
              .type(Facility.Type.va_facilities)
              .attributes(attributes())
              .build());
    }

    private Facility.Address mailingAddress() {
      return Facility.Address.builder()
          .address1("8 Shanktopus Lane")
          .address2("Apartment 8")
          .city("South")
          .state("Dakota")
          .zip("12208")
          .build();
    }

    private Facility.Address physicalAddress() {
      return Facility.Address.builder()
          .address1("8 Shanktopus Lane")
          .address2("Apartment 8")
          .city("North")
          .state("Dakota")
          .zip("12208")
          .build();
    }
  }
}
