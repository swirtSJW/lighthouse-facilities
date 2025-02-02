package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DetailedServiceTransformerV1 {
  /** Transform DatamartDetailedService to version 1 DetailedService. */
  public static DetailedService toDetailedService(@NonNull DatamartDetailedService dds) {
    return DetailedService.builder()
        .name(dds.name())
        .active(dds.active())
        .changed(dds.changed())
        .descriptionFacility(dds.descriptionFacility())
        .appointmentLeadIn(dds.appointmentLeadIn())
        .onlineSchedulingAvailable(dds.onlineSchedulingAvailable())
        .path(dds.path())
        .phoneNumbers(toDetailedServicePhoneNumbers(dds.phoneNumbers()))
        .referralRequired(dds.referralRequired())
        .serviceLocations(toDetailedServiceLocations(dds.serviceLocations()))
        .walkInsAccepted(dds.walkInsAccepted())
        .build();
  }

  /**
   * Transform a list of DatamartDetailedService.DetailedServiceEmailContact to a list of version 1
   * DetailedService.DetailedServiceEmailContact.
   */
  public static List<DetailedService.DetailedServiceEmailContact> toDetailedServiceEmailContacts(
      List<DatamartDetailedService.DetailedServiceEmailContact>
          datamartDetailedServiceEmailContacts) {
    return (datamartDetailedServiceEmailContacts == null)
        ? null
        : !datamartDetailedServiceEmailContacts.isEmpty()
            ? datamartDetailedServiceEmailContacts.stream()
                .map(DetailedServiceTransformerV1::transformDetailedServiceEmailContact)
                .collect(Collectors.toList())
            : new ArrayList<>();
  }

  /**
   * Transform a list of DatamartDetailedService.DetailedServiceLocation to a list of version 1
   * DetailedService.DetailedServiceLocation.
   */
  public static List<DetailedService.DetailedServiceLocation> toDetailedServiceLocations(
      List<DatamartDetailedService.DetailedServiceLocation> datamartDetailedServiceLocations) {
    return (datamartDetailedServiceLocations == null)
        ? null
        : !datamartDetailedServiceLocations.isEmpty()
            ? datamartDetailedServiceLocations.stream()
                .map(DetailedServiceTransformerV1::transformDetailedServiceLocation)
                .collect(Collectors.toList())
            : new ArrayList<>();
  }

  /**
   * Transform a list of DatamartDetailedService.AppointmentPhoneNumber to a list of version 1
   * DetailedService.AppointmentPhoneNumber.
   */
  public static List<DetailedService.AppointmentPhoneNumber> toDetailedServicePhoneNumbers(
      List<DatamartDetailedService.AppointmentPhoneNumber> datamartDetailedServicePhoneNumbers) {
    return (datamartDetailedServicePhoneNumbers == null)
        ? null
        : !datamartDetailedServicePhoneNumbers.isEmpty()
            ? datamartDetailedServicePhoneNumbers.stream()
                .map(DetailedServiceTransformerV1::transfromDetailedServiceAppointmentPhoneNumber)
                .collect(Collectors.toList())
            : new ArrayList<>();
  }

  /** Transform a list of DatamartDetailedService> to a list of version 1 DetailedService. */
  public static List<DetailedService> toDetailedServices(
      @Valid List<DatamartDetailedService> detailedServices) {
    return (detailedServices == null)
        ? null
        : !detailedServices.isEmpty()
            ? detailedServices.stream()
                .map(DetailedServiceTransformerV1::toDetailedService)
                .collect(Collectors.toList())
            : new ArrayList<>();
  }

  /** Transform version 1 DetailedService to version agnostic DatamartDetailedService. */
  public static DatamartDetailedService toVersionAgnosticDetailedService(
      @NonNull DetailedService dds) {
    return DatamartDetailedService.builder()
        .name(dds.name())
        .active(dds.active())
        .changed(dds.changed())
        .descriptionFacility(dds.descriptionFacility())
        .appointmentLeadIn(dds.appointmentLeadIn())
        .onlineSchedulingAvailable(dds.onlineSchedulingAvailable())
        .path(dds.path())
        .phoneNumbers(toVersionAgnosticDetailedServicePhoneNumbers(dds.phoneNumbers()))
        .referralRequired(dds.referralRequired())
        .serviceLocations(toVersionAgnosticDetailedServiceLocations(dds.serviceLocations()))
        .walkInsAccepted(dds.walkInsAccepted())
        .build();
  }

  /**
   * Transform a list of version 1 DatamartDetailedService.DetailedServiceEmailContact to a list of
   * version agnostic DatamartDetailedService.DetailedServiceEmailContact.
   */
  public static List<DatamartDetailedService.DetailedServiceEmailContact>
      toVersionAgnosticDetailedServiceEmailContacts(
          List<DetailedService.DetailedServiceEmailContact> detailedServiceEmailContacts) {
    return (detailedServiceEmailContacts == null)
        ? null
        : !detailedServiceEmailContacts.isEmpty()
            ? detailedServiceEmailContacts.stream()
                .map(DetailedServiceTransformerV1::transformDetailedServiceEmailContact)
                .collect(Collectors.toList())
            : new ArrayList<>();
  }

  /**
   * Transform a list of version 1 DetailedService.DetailedServiceLocation to a list of version
   * agnostic DatamartDetailedService.DetailedServiceLocation.
   */
  public static List<DatamartDetailedService.DetailedServiceLocation>
      toVersionAgnosticDetailedServiceLocations(
          List<DetailedService.DetailedServiceLocation> detailedServiceLocations) {
    return (detailedServiceLocations == null)
        ? null
        : !detailedServiceLocations.isEmpty()
            ? detailedServiceLocations.stream()
                .map(DetailedServiceTransformerV1::transformDetailedServiceLocation)
                .collect(Collectors.toList())
            : new ArrayList<>();
  }

  /**
   * Transform a list of version 1 DetailedService.AppointmentPhoneNumber to a list of version
   * agnostic DatamartDetailedService.AppointmentPhoneNumber
   */
  public static List<DatamartDetailedService.AppointmentPhoneNumber>
      toVersionAgnosticDetailedServicePhoneNumbers(
          List<DetailedService.AppointmentPhoneNumber> detailedServicePhoneNumbers) {
    return (detailedServicePhoneNumbers == null)
        ? null
        : !detailedServicePhoneNumbers.isEmpty()
            ? detailedServicePhoneNumbers.stream()
                .map(DetailedServiceTransformerV1::transfromDetailedServiceAppointmentPhoneNumber)
                .collect(Collectors.toList())
            : new ArrayList<>();
  }

  /**
   * Transform a list of version 1 DetailedService to a list of version agnostic
   * DatamartDetailedService.
   */
  public static List<DatamartDetailedService> toVersionAgnosticDetailedServices(
      @Valid List<DetailedService> detailedServices) {
    return (detailedServices == null)
        ? null
        : !detailedServices.isEmpty()
            ? detailedServices.stream()
                .map(DetailedServiceTransformerV1::toVersionAgnosticDetailedService)
                .collect(Collectors.toList())
            : new ArrayList<>();
  }

  /**
   * Transform DatamartDetailedService.DetailedServiceAddress to version 1
   * DetailedService.DetailedServiceAddress
   */
  public static DetailedService.DetailedServiceAddress transformDetailedServiceAddress(
      DatamartDetailedService.DetailedServiceAddress dda) {
    return (dda != null)
        ? DetailedService.DetailedServiceAddress.builder()
            .address1(dda.address1())
            .address2(dda.address2())
            .state(dda.state())
            .buildingNameNumber(dda.buildingNameNumber())
            .clinicName(dda.clinicName())
            .countryCode(dda.countryCode())
            .city(dda.city())
            .zipCode(dda.zipCode())
            .wingFloorOrRoomNumber(dda.wingFloorOrRoomNumber())
            .build()
        : null;
  }

  /**
   * Transform version 1 DetailedService.DetailedServiceAddress to version agnostic
   * DatamartDetailedService.DetailedServiceAddress
   */
  public static DatamartDetailedService.DetailedServiceAddress transformDetailedServiceAddress(
      DetailedService.DetailedServiceAddress da) {
    return (da != null)
        ? DatamartDetailedService.DetailedServiceAddress.builder()
            .address1(da.address1())
            .address2(da.address2())
            .state(da.state())
            .buildingNameNumber(da.buildingNameNumber())
            .clinicName(da.clinicName())
            .countryCode(da.countryCode())
            .city(da.city())
            .zipCode(da.zipCode())
            .wingFloorOrRoomNumber(da.wingFloorOrRoomNumber())
            .build()
        : null;
  }

  /**
   * Transform DatamartDetailedService.DetailedServiceEmailContact to version 1
   * DetailedService.DetailedServiceEmailContact
   */
  public static DetailedService.DetailedServiceEmailContact transformDetailedServiceEmailContact(
      DatamartDetailedService.DetailedServiceEmailContact dde) {
    return (dde != null)
        ? DetailedService.DetailedServiceEmailContact.builder()
            .emailAddress(dde.emailAddress())
            .emailLabel(dde.emailLabel())
            .build()
        : null;
  }

  /**
   * Transform version 1 DetailedService.DetailedServiceEmailContact to version agnostic
   * DatamartDetailedService.DetailedServiceEmailContact
   */
  public static DatamartDetailedService.DetailedServiceEmailContact
      transformDetailedServiceEmailContact(DetailedService.DetailedServiceEmailContact de) {
    return (de != null)
        ? DatamartDetailedService.DetailedServiceEmailContact.builder()
            .emailAddress(de.emailAddress())
            .emailLabel(de.emailLabel())
            .build()
        : null;
  }

  /**
   * Transform DatamartDetailedService.DetailedServiceHours to version 1
   * DetailedService.DetailedServiceHours
   */
  public static DetailedService.DetailedServiceHours transformDetailedServiceHours(
      DatamartDetailedService.DetailedServiceHours ddh) {
    return (ddh != null)
        ? DetailedService.DetailedServiceHours.builder()
            .monday(ddh.monday())
            .tuesday(ddh.tuesday())
            .wednesday(ddh.wednesday())
            .thursday(ddh.thursday())
            .friday(ddh.friday())
            .saturday(ddh.saturday())
            .sunday(ddh.sunday())
            .build()
        : null;
  }

  /**
   * Transform version 1 DetailedService.DetailedServiceHours to version agnostic
   * DatamartDetailedService.DetailedServiceHours
   */
  public static DatamartDetailedService.DetailedServiceHours transformDetailedServiceHours(
      DetailedService.DetailedServiceHours dh) {
    return (dh != null)
        ? DatamartDetailedService.DetailedServiceHours.builder()
            .monday(dh.monday())
            .tuesday(dh.tuesday())
            .wednesday(dh.wednesday())
            .thursday(dh.thursday())
            .friday(dh.friday())
            .saturday(dh.saturday())
            .sunday(dh.sunday())
            .build()
        : null;
  }

  /**
   * Transform DatamartDetailedService.DetailedServiceEmailContact to version 0
   * DetailedService.DetailedServiceEmailContact
   */
  public static DetailedService.DetailedServiceLocation transformDetailedServiceLocation(
      DatamartDetailedService.DetailedServiceLocation ddl) {
    return (ddl != null)
        ? DetailedService.DetailedServiceLocation.builder()
            .additionalHoursInfo(ddl.additionalHoursInfo())
            .emailContacts(toDetailedServiceEmailContacts(ddl.emailContacts()))
            .facilityServiceHours(transformDetailedServiceHours(ddl.facilityServiceHours()))
            .appointmentPhoneNumbers(toDetailedServicePhoneNumbers(ddl.appointmentPhoneNumbers()))
            .serviceLocationAddress(transformDetailedServiceAddress(ddl.serviceLocationAddress()))
            .build()
        : null;
  }

  /**
   * Transform version 1 DetailedService.DetailedServiceEmailContact to version agnostic
   * DatamartDetailedService.DetailedServiceEmailContact
   */
  public static DatamartDetailedService.DetailedServiceLocation transformDetailedServiceLocation(
      DetailedService.DetailedServiceLocation dl) {
    return (dl != null)
        ? DatamartDetailedService.DetailedServiceLocation.builder()
            .additionalHoursInfo(dl.additionalHoursInfo())
            .emailContacts(toVersionAgnosticDetailedServiceEmailContacts(dl.emailContacts()))
            .facilityServiceHours(transformDetailedServiceHours(dl.facilityServiceHours()))
            .appointmentPhoneNumbers(
                toVersionAgnosticDetailedServicePhoneNumbers(dl.appointmentPhoneNumbers()))
            .serviceLocationAddress(transformDetailedServiceAddress(dl.serviceLocationAddress()))
            .build()
        : null;
  }

  /**
   * Transform DatamartDetailedService.AppointmentPhoneNumber to version 1
   * DetailedService.AppointmentPhoneNumber
   */
  public static DetailedService.AppointmentPhoneNumber
      transfromDetailedServiceAppointmentPhoneNumber(
          DatamartDetailedService.AppointmentPhoneNumber dda) {
    return (dda != null)
        ? DetailedService.AppointmentPhoneNumber.builder()
            .extension(dda.extension())
            .label(dda.label())
            .number(dda.number())
            .type(dda.type())
            .build()
        : null;
  }

  /**
   * Transform version 1 DetailedService.AppointmentPhoneNumber to version agnostic
   * DatamartDetailedService.AppointmentPhoneNumber
   */
  public static DatamartDetailedService.AppointmentPhoneNumber
      transfromDetailedServiceAppointmentPhoneNumber(DetailedService.AppointmentPhoneNumber da) {
    return (da != null)
        ? DatamartDetailedService.AppointmentPhoneNumber.builder()
            .extension(da.extension())
            .label(da.label())
            .number(da.number())
            .type(da.type())
            .build()
        : null;
  }
}
