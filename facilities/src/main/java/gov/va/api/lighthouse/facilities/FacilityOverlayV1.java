package gov.va.api.lighthouse.facilities;

// @Builder
// @Value
// @Slf4j
// public class FacilityOverlayV1 implements Function<HasFacilityPayload, Facility> {
public class FacilityOverlayV1 {
  //  @NonNull ObjectMapper mapper;
  //
  //  private static void applyCmsOverlayOperatingStatus(
  //      Facility facility, OperatingStatus operatingStatus) {
  //    if (operatingStatus == null) {
  //      log.warn("CMS Overlay for facility {} is missing operating status", facility.id());
  //    } else {
  //      facility.attributes().operatingStatus(operatingStatus);
  //      if (operatingStatus.code() == OperatingStatusCode.CLOSED) {
  //        facility.attributes().activeStatus(ActiveStatus.T);
  //      } else {
  //        facility.attributes().activeStatus(ActiveStatus.A);
  //      }
  //    }
  //  }
  //
  //  private static void applyCmsOverlayServices(Facility facility, Set<String> overlayServices) {
  //    if (overlayServices == null) {
  //      log.warn("CMS Overlay for facility {} is missing CMS Services", facility.id());
  //    } else {
  //      boolean needToSort = false;
  //      for (String overlayService : overlayServices) {
  //        if ("Covid19Vaccine".equals(overlayService)) {
  //          if (facility.attributes().services().health() != null) {
  //
  // facility.attributes().services().health().add(Facility.HealthService.Covid19Vaccine);
  //          } else {
  //
  // facility.attributes().services().health(List.of(Facility.HealthService.Covid19Vaccine));
  //          }
  //          needToSort = true;
  //          break;
  //        }
  //      }
  //      // re-sort the health services list with the newly added field(s)
  //      if (needToSort && facility.attributes().services().health().size() > 1) {
  //        Collections.sort(
  //            facility.attributes().services().health(),
  //            (left, right) -> left.name().compareToIgnoreCase(right.name()));
  //      }
  //    }
  //  }
  //
  //  private static void applyDetailedServices(
  //      Facility facility, List<DetailedService> detailedServices) {
  //    if (detailedServices == null) {
  //      log.warn("CMS Overlay for facility {} is missing Detailed CMS Services", facility.id());
  //    } else {
  //      facility.attributes().detailedServices(detailedServices);
  //    }
  //  }
  //
  //  private static OperatingStatus determineOperatingStatusFromActiveStatus(
  //      ActiveStatus activeStatus) {
  //    if (activeStatus == ActiveStatus.T) {
  //      return OperatingStatus.builder().code(OperatingStatusCode.CLOSED).build();
  //    }
  //    return OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build();
  //  }
  //
  //  @Override
  //  @SneakyThrows
  //  public Facility apply(HasFacilityPayload entity) {
  //    //    Facility facility = mapper.readValue(entity.facilityV1(), Facility.class);
  //    // todo replace 'Facility facility = Facility.builder().build();' with line above (or
  // something
  //    // similar)
  //    Facility facility =
  //        Facility.builder().attributes(Facility.FacilityAttributes.builder().build()).build();
  //
  //    if (entity.cmsOperatingStatus() != null) {
  //      applyCmsOverlayOperatingStatus(
  //          facility, mapper.readValue(entity.cmsOperatingStatus(), OperatingStatus.class));
  //    }
  //    if (facility.attributes().operatingStatus() == null) {
  //      facility
  //          .attributes()
  //          .operatingStatus(
  //              determineOperatingStatusFromActiveStatus(facility.attributes().activeStatus()));
  //    }
  //    if (entity.overlayServices() != null) {
  //      applyCmsOverlayServices(facility, entity.overlayServices());
  //    }
  //    if (entity.cmsServices() != null) {
  //      applyDetailedServices(
  //          facility, List.of(mapper.readValue(entity.cmsServices(), DetailedService[].class)));
  //    }
  //    return facility;
  //  }
}
