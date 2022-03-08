package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildTypedServiceLink;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/** Utility class for transforming DatamartFacility to version 0 facility object and back. */
@UtilityClass
public final class FacilityTransformerV0 extends BaseVersionedTransformer {
  /** Transform persisted DatamartFacility to version 0 facility. */
  static Facility toFacility(@NonNull DatamartFacility df) {
    return Facility.builder()
        .id(df.id())
        .type(toType(df.type()))
        .attributes(
            (df.attributes() != null)
                ? Facility.FacilityAttributes.builder()
                    .facilityType(toFacilityType(df.attributes().facilityType()))
                    .address(toFacilityAddresses(df.attributes().address()))
                    .hours(toFacilityHours(df.attributes().hours()))
                    .latitude(df.attributes().latitude())
                    .longitude(df.attributes().longitude())
                    .name(df.attributes().name())
                    .phone(toFacilityPhone(df.attributes().phone()))
                    .website(df.attributes().website())
                    .classification(df.attributes().classification())
                    .timeZone(df.attributes().timeZone())
                    .mobile(df.attributes().mobile())
                    .services(toFacilityServices(df.attributes().services()))
                    .activeStatus(toFacilityActiveStatus(df.attributes().activeStatus()))
                    .visn(df.attributes().visn())
                    .satisfaction(toFacilitySatisfaction(df.attributes().satisfaction()))
                    .waitTimes(toFacilityWaitTimes(df.attributes().waitTimes()))
                    .operatingStatus(toFacilityOperatingStatus(df.attributes().operatingStatus()))
                    .detailedServices(
                        DetailedServiceTransformerV0.toDetailedServices(
                            df.attributes().detailedServices()))
                    .operationalHoursSpecialInstructions(
                        df.attributes().operationalHoursSpecialInstructions())
                    .build()
                : null)
        .build();
  }

  /** Transform DatamartFacility active status to version 0 facility active status. */
  private static Facility.ActiveStatus toFacilityActiveStatus(
      DatamartFacility.ActiveStatus datamartFacilityActiveStatus) {
    return (datamartFacilityActiveStatus != null)
        ? containsValueOfName(Facility.ActiveStatus.values(), datamartFacilityActiveStatus.name())
            ? Facility.ActiveStatus.valueOf(datamartFacilityActiveStatus.name())
            : null
        : null;
  }

  /** Transform DatamartFacility address to version 0 facility address. */
  private static Facility.Address toFacilityAddress(
      DatamartFacility.Address datamartFacilityAddress) {
    return (datamartFacilityAddress != null)
        ? Facility.Address.builder()
            .address1(datamartFacilityAddress.address1())
            .address2(datamartFacilityAddress.address2())
            .address3(datamartFacilityAddress.address3())
            .city(datamartFacilityAddress.city())
            .state(datamartFacilityAddress.state())
            .zip(datamartFacilityAddress.zip())
            .build()
        : Facility.Address.builder().build();
  }

  /** Transform DatamartFacility addresses to version 0 facility addresses. */
  private static Facility.Addresses toFacilityAddresses(
      DatamartFacility.Addresses datamartFacilityAddresses) {
    return (datamartFacilityAddresses != null)
        ? Facility.Addresses.builder()
            .physical(toFacilityAddress(datamartFacilityAddresses.physical()))
            .mailing(toFacilityAddress(datamartFacilityAddresses.mailing()))
            .build()
        : Facility.Addresses.builder().build();
  }

  /** Transform DatamartFacility benefits service to version 0 facility benefits service. */
  private static Facility.BenefitsService toFacilityBenefitsTypedService(
      @NonNull
          DatamartFacility.TypedService<DatamartFacility.BenefitsService>
              datamartFacilityTypedBenefitsService) {
    return containsValueOfName(
            Facility.BenefitsService.values(),
            capitalize(datamartFacilityTypedBenefitsService.serviceId()))
        ? Facility.BenefitsService.fromString(
            capitalize(datamartFacilityTypedBenefitsService.serviceId()))
        : null;
  }

  /** Transform DatamartFacility health service to version 0 facility health service. */
  private static Facility.HealthService toFacilityHealthService(
      @NonNull DatamartFacility.HealthService datamartFacilityHealthService) {
    return DatamartFacility.HealthService.MentalHealth.equals(datamartFacilityHealthService)
        ? Facility.HealthService.MentalHealthCare
        : DatamartFacility.HealthService.Dental.equals(datamartFacilityHealthService)
            ? Facility.HealthService.DentalServices
            : containsValueOfName(
                    Facility.HealthService.values(), datamartFacilityHealthService.name())
                ? Facility.HealthService.fromString(datamartFacilityHealthService.name())
                : null;
  }

  /** Transform DatamartFacility health typed service to version 0 facility health service. */
  private static Facility.HealthService toFacilityHealthTypedService(
      @NonNull
          DatamartFacility.TypedService<DatamartFacility.HealthService>
              datamartFacilityTypedHealthService) {
    return DatamartFacility.HealthService.MentalHealth.equals(
            datamartFacilityTypedHealthService.serviceType())
        ? Facility.HealthService.MentalHealthCare
        : DatamartFacility.HealthService.Dental.equals(
                datamartFacilityTypedHealthService.serviceType())
            ? Facility.HealthService.DentalServices
            : containsValueOfName(
                    Facility.HealthService.values(),
                    capitalize(datamartFacilityTypedHealthService.serviceId()))
                ? Facility.HealthService.fromString(
                    capitalize(datamartFacilityTypedHealthService.serviceId()))
                : null;
  }

  /** Transform DatamartFacility hours to version 0 facility hours. */
  private static Facility.Hours toFacilityHours(DatamartFacility.Hours datamartFacilityHours) {
    return (datamartFacilityHours != null)
        ? Facility.Hours.builder()
            .monday(datamartFacilityHours.monday())
            .tuesday(datamartFacilityHours.tuesday())
            .wednesday(datamartFacilityHours.wednesday())
            .thursday(datamartFacilityHours.thursday())
            .friday(datamartFacilityHours.friday())
            .saturday(datamartFacilityHours.saturday())
            .sunday(datamartFacilityHours.sunday())
            .build()
        : Facility.Hours.builder().build();
  }

  /** Transform DatamartFacility operating status to version 0 facility operating status. */
  public static Facility.OperatingStatus toFacilityOperatingStatus(
      DatamartFacility.OperatingStatus datamartFacilityOperatingStatus) {
    return (datamartFacilityOperatingStatus != null)
        ? Facility.OperatingStatus.builder()
            .code(
                (datamartFacilityOperatingStatus.code() != null)
                    ? containsValueOfName(
                            Facility.OperatingStatusCode.values(),
                            datamartFacilityOperatingStatus.code().name())
                        ? Facility.OperatingStatusCode.valueOf(
                            datamartFacilityOperatingStatus.code().name())
                        : null
                    : null)
            .additionalInfo(datamartFacilityOperatingStatus.additionalInfo())
            .build()
        : null;
  }

  /** Transform DatamartFacility other typed service to version 0 facility other service. */
  private static Facility.OtherService toFacilityOtherTypedService(
      @NonNull
          DatamartFacility.TypedService<DatamartFacility.OtherService>
              datamartFacilityTypedOtherService) {
    return containsValueOfName(
            Facility.OtherService.values(),
            capitalize(datamartFacilityTypedOtherService.serviceId()))
        ? Facility.OtherService.valueOf(capitalize(datamartFacilityTypedOtherService.serviceId()))
        : null;
  }

  /** Transform DatamartFacility patient wait times to version 0 facility patient wait times. */
  private static Facility.PatientWaitTime toFacilityPatientWaitTime(
      DatamartFacility.PatientWaitTime datamartPatientWaitTime) {
    return (datamartPatientWaitTime != null)
        ? Facility.PatientWaitTime.builder()
            .newPatientWaitTime(datamartPatientWaitTime.newPatientWaitTime())
            .establishedPatientWaitTime(datamartPatientWaitTime.establishedPatientWaitTime())
            .service(
                (datamartPatientWaitTime.service() != null)
                    ? toFacilityHealthService(datamartPatientWaitTime.service())
                    : null)
            .build()
        : Facility.PatientWaitTime.builder().build();
  }

  /** Transform DatamartFacility phone to version 0 facility phone. */
  private static Facility.Phone toFacilityPhone(DatamartFacility.Phone datamartFacilityPhone) {
    return (datamartFacilityPhone != null)
        ? Facility.Phone.builder()
            .fax(datamartFacilityPhone.fax())
            .main(datamartFacilityPhone.main())
            .afterHours(datamartFacilityPhone.afterHours())
            .enrollmentCoordinator(datamartFacilityPhone.enrollmentCoordinator())
            .mentalHealthClinic(datamartFacilityPhone.mentalHealthClinic())
            .patientAdvocate(datamartFacilityPhone.patientAdvocate())
            .pharmacy(datamartFacilityPhone.pharmacy())
            .build()
        : Facility.Phone.builder().build();
  }

  /** Transform DatamartFacility satisfaction to version 0 facility satisfaction. */
  private static Facility.Satisfaction toFacilitySatisfaction(
      DatamartFacility.Satisfaction datamartFacilitySatisfaction) {
    return (datamartFacilitySatisfaction != null)
        ? Facility.Satisfaction.builder()
            .health(
                (datamartFacilitySatisfaction.health() != null)
                    ? Facility.PatientSatisfaction.builder()
                        .primaryCareRoutine(
                            datamartFacilitySatisfaction.health().primaryCareRoutine())
                        .primaryCareUrgent(
                            datamartFacilitySatisfaction.health().primaryCareUrgent())
                        .specialtyCareRoutine(
                            datamartFacilitySatisfaction.health().specialtyCareRoutine())
                        .specialtyCareUrgent(
                            datamartFacilitySatisfaction.health().specialtyCareUrgent())
                        .build()
                    : null)
            .effectiveDate(datamartFacilitySatisfaction.effectiveDate())
            .build()
        : Facility.Satisfaction.builder().build();
  }

  /** Transform DatamartFacility services to version 0 facility services. */
  private static Facility.Services toFacilityServices(
      DatamartFacility.Services datamartFacilityServices) {
    return (datamartFacilityServices != null)
        ? Facility.Services.builder()
            .health(
                (datamartFacilityServices.health() != null)
                    ? datamartFacilityServices.health().parallelStream()
                        .filter(
                            ts ->
                                containsValueOfName(
                                        Facility.HealthService.values(), capitalize(ts.serviceId()))
                                    || checkHealthServiceNameChange(ts.serviceType()))
                        .map(ts -> toFacilityHealthTypedService(ts))
                        .collect(Collectors.toList())
                    : null)
            .benefits(
                (datamartFacilityServices.benefits() != null)
                    ? datamartFacilityServices.benefits().parallelStream()
                        .filter(
                            ts ->
                                containsValueOfName(
                                    Facility.BenefitsService.values(), capitalize(ts.serviceId())))
                        .map(ts -> toFacilityBenefitsTypedService(ts))
                        .collect(Collectors.toList())
                    : null)
            .other(
                (datamartFacilityServices.other() != null)
                    ? datamartFacilityServices.other().parallelStream()
                        .filter(
                            ts ->
                                containsValueOfName(
                                    Facility.OtherService.values(), capitalize(ts.serviceId())))
                        .map(ts -> toFacilityOtherTypedService(ts))
                        .collect(Collectors.toList())
                    : null)
            .lastUpdated(datamartFacilityServices.lastUpdated())
            .build()
        : Facility.Services.builder().build();
  }

  /** Transform DatamartFacility facility type to version 0 facility type. */
  private static Facility.FacilityType toFacilityType(
      DatamartFacility.FacilityType datamartFacilityType) {
    return (datamartFacilityType != null)
        ? containsValueOfName(Facility.FacilityType.values(), datamartFacilityType.name())
            ? Facility.FacilityType.valueOf(datamartFacilityType.name())
            : null
        : null;
  }

  /** Transform DatamartFacility wait times to version 0 facility wait times. */
  private static Facility.WaitTimes toFacilityWaitTimes(
      DatamartFacility.WaitTimes datamartFacilityWaitTimes) {
    return (datamartFacilityWaitTimes != null)
        ? Facility.WaitTimes.builder()
            .health(
                (datamartFacilityWaitTimes.health() != null)
                    ? datamartFacilityWaitTimes.health().parallelStream()
                        .map(e -> toFacilityPatientWaitTime(e))
                        .collect(Collectors.toList())
                    : null)
            .effectiveDate(datamartFacilityWaitTimes.effectiveDate())
            .build()
        : Facility.WaitTimes.builder().build();
  }

  /** Transform version 0 facility type to DatamartFacility facility type. */
  private static Facility.Type toType(DatamartFacility.Type datamartType) {
    return (datamartType != null)
        ? containsValueOfName(Facility.Type.values(), datamartType.name())
            ? Facility.Type.valueOf(datamartType.name())
            : null
        : null;
  }

  /** Transform version 0 facility to DatamartFacility for persistence. */
  public static DatamartFacility toVersionAgnostic(@NonNull Facility f, @NonNull String linkerUrl) {
    return DatamartFacility.builder()
        .id(f.id())
        .type(toVersionAgnosticType(f.type()))
        .attributes(
            (f.attributes() != null)
                ? DatamartFacility.FacilityAttributes.builder()
                    .facilityType(toVersionAgnosticFacilityType(f.attributes().facilityType()))
                    .address(toVersionAgnosticFacilityAddresses(f.attributes().address()))
                    .hours(toVersionAgnosticFacilityHours(f.attributes().hours()))
                    .latitude(f.attributes().latitude())
                    .longitude(f.attributes().longitude())
                    .name(f.attributes().name())
                    .phone(toVersionAgnosticFacilityPhone(f.attributes().phone()))
                    .website(f.attributes().website())
                    .classification(f.attributes().classification())
                    .timeZone(f.attributes().timeZone())
                    .mobile(f.attributes().mobile())
                    .services(
                        toVersionAgnosticFacilityServices(
                            f.attributes().services(), linkerUrl, f.id()))
                    .activeStatus(
                        toVersionAgnosticFacilityActiveStatus(f.attributes().activeStatus()))
                    .visn(f.attributes().visn())
                    .satisfaction(
                        toVersionAgnosticFacilitySatisfaction(f.attributes().satisfaction()))
                    .waitTimes(toVersionAgnosticFacilityWaitTimes(f.attributes().waitTimes()))
                    .operatingStatus(
                        toVersionAgnosticFacilityOperatingStatus(f.attributes().operatingStatus()))
                    .detailedServices(
                        DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(
                            f.attributes().detailedServices()))
                    .operationalHoursSpecialInstructions(
                        f.attributes().operationalHoursSpecialInstructions())
                    .build()
                : null)
        .build();
  }

  /** Transform version 0 facility active status to DatamartFacility active status. */
  private static DatamartFacility.ActiveStatus toVersionAgnosticFacilityActiveStatus(
      Facility.ActiveStatus facilityActiveStatus) {
    return (facilityActiveStatus != null)
        ? containsValueOfName(DatamartFacility.ActiveStatus.values(), facilityActiveStatus.name())
            ? DatamartFacility.ActiveStatus.valueOf(facilityActiveStatus.name())
            : null
        : null;
  }

  /** Transform version 0 facility address to DatamartFacility address. */
  private static DatamartFacility.Address toVersionAgnosticFacilityAddress(
      Facility.Address facilityAddress) {
    return (facilityAddress != null)
        ? DatamartFacility.Address.builder()
            .address1(facilityAddress.address1())
            .address2(facilityAddress.address2())
            .address3(facilityAddress.address3())
            .city(facilityAddress.city())
            .state(facilityAddress.state())
            .zip(facilityAddress.zip())
            .build()
        : DatamartFacility.Address.builder().build();
  }

  /** Transform version 0 facility addresses to DatamartFacility addresses. */
  private static DatamartFacility.Addresses toVersionAgnosticFacilityAddresses(
      Facility.Addresses facilityAddresses) {
    return (facilityAddresses != null)
        ? DatamartFacility.Addresses.builder()
            .physical(toVersionAgnosticFacilityAddress(facilityAddresses.physical()))
            .mailing(toVersionAgnosticFacilityAddress(facilityAddresses.mailing()))
            .build()
        : DatamartFacility.Addresses.builder().build();
  }

  /** Transform version 0 facility benefits service to DatamartFacility benefits typed service. */
  private static DatamartFacility.TypedService<DatamartFacility.BenefitsService>
      toVersionAgnosticFacilityBenefitsTypedService(
          @NonNull Facility.BenefitsService facilityBenefitsService,
          @NonNull String linkUrl,
          @NonNull String facilityId) {
    return containsValueOfName(
            DatamartFacility.BenefitsService.values(), facilityBenefitsService.name())
        ? new DatamartFacility.TypedService<DatamartFacility.BenefitsService>(
            DatamartFacility.BenefitsService.fromString(facilityBenefitsService.name()),
            facilityBenefitsService.name(),
            buildTypedServiceLink(
                linkUrl, facilityId, uncapitalize(facilityBenefitsService.name())))
        : null;
  }

  /** Transform version 0 facility health service to DatamartFacility health service. */
  private static DatamartFacility.HealthService toVersionAgnosticFacilityHealthService(
      @NonNull Facility.HealthService facilityHealthService) {
    return Facility.HealthService.MentalHealthCare.equals(facilityHealthService)
        ? DatamartFacility.HealthService.MentalHealth
        : Facility.HealthService.DentalServices.equals(facilityHealthService)
            ? DatamartFacility.HealthService.Dental
            : containsValueOfName(
                    DatamartFacility.HealthService.values(), facilityHealthService.name())
                ? DatamartFacility.HealthService.fromString(facilityHealthService.name())
                : null;
  }

  /** Transform version 0 facility health service to DatamartFacility health typed service. */
  private static DatamartFacility.TypedService<DatamartFacility.HealthService>
      toVersionAgnosticFacilityHealthTypedService(
          @NonNull Facility.HealthService facilityHealthService,
          @NonNull String linkUrl,
          @NonNull String facilityId) {
    return Facility.HealthService.MentalHealthCare.equals(facilityHealthService)
        ? new DatamartFacility.TypedService<DatamartFacility.HealthService>(
            DatamartFacility.HealthService.MentalHealth,
            DatamartFacility.HealthService.MentalHealth.name(),
            buildTypedServiceLink(
                linkUrl,
                facilityId,
                uncapitalize(DatamartFacility.HealthService.MentalHealth.name())))
        : Facility.HealthService.DentalServices.equals(facilityHealthService)
            ? new DatamartFacility.TypedService<DatamartFacility.HealthService>(
                DatamartFacility.HealthService.Dental,
                DatamartFacility.HealthService.Dental.name(),
                buildTypedServiceLink(
                    linkUrl,
                    facilityId,
                    uncapitalize(DatamartFacility.HealthService.Dental.name())))
            : containsValueOfName(
                    DatamartFacility.HealthService.values(), facilityHealthService.name())
                ? new DatamartFacility.TypedService<DatamartFacility.HealthService>(
                    DatamartFacility.HealthService.fromString(facilityHealthService.name()),
                    facilityHealthService.name(),
                    buildTypedServiceLink(
                        linkUrl, facilityId, uncapitalize(facilityHealthService.name())))
                : null;
  }

  /** Transform version 0 facility hours to DatamartFacility hours. */
  private static DatamartFacility.Hours toVersionAgnosticFacilityHours(
      Facility.Hours facilityHours) {
    return (facilityHours != null)
        ? DatamartFacility.Hours.builder()
            .monday(facilityHours.monday())
            .tuesday(facilityHours.tuesday())
            .wednesday(facilityHours.wednesday())
            .thursday(facilityHours.thursday())
            .friday(facilityHours.friday())
            .saturday(facilityHours.saturday())
            .sunday(facilityHours.sunday())
            .build()
        : DatamartFacility.Hours.builder().build();
  }

  /** Transform version 0 facility operating status to DatamartFacility operating status. */
  public static DatamartFacility.OperatingStatus toVersionAgnosticFacilityOperatingStatus(
      Facility.OperatingStatus facilityOperatingStatus) {
    return (facilityOperatingStatus != null)
        ? DatamartFacility.OperatingStatus.builder()
            .code(
                (facilityOperatingStatus.code() != null)
                    ? containsValueOfName(
                            DatamartFacility.OperatingStatusCode.values(),
                            facilityOperatingStatus.code().name())
                        ? DatamartFacility.OperatingStatusCode.valueOf(
                            facilityOperatingStatus.code().name())
                        : null
                    : null)
            .additionalInfo(facilityOperatingStatus.additionalInfo())
            .build()
        : null;
  }

  /** Transform version 0 facility other service to DatamartFacility other typed service. */
  private static DatamartFacility.TypedService<DatamartFacility.OtherService>
      toVersionAgnosticFacilityOtherTypedService(
          @NonNull Facility.OtherService facilityOtherService,
          @NonNull String linkUrl,
          @NonNull String facilityId) {
    return containsValueOfName(DatamartFacility.OtherService.values(), facilityOtherService.name())
        ? new DatamartFacility.TypedService<DatamartFacility.OtherService>(
            DatamartFacility.OtherService.valueOf(facilityOtherService.name()),
            facilityOtherService.name(),
            buildTypedServiceLink(linkUrl, facilityId, uncapitalize(facilityOtherService.name())))
        : null;
  }

  /** Transform version 0 facility patient wait times to DatamartFacility patient wait times. */
  private static DatamartFacility.PatientWaitTime toVersionAgnosticFacilityPatientWaitTime(
      Facility.PatientWaitTime facilityPatientWaitTime) {
    return (facilityPatientWaitTime != null)
        ? DatamartFacility.PatientWaitTime.builder()
            .newPatientWaitTime(facilityPatientWaitTime.newPatientWaitTime())
            .establishedPatientWaitTime(facilityPatientWaitTime.establishedPatientWaitTime())
            .service(
                (facilityPatientWaitTime.service() != null)
                    ? toVersionAgnosticFacilityHealthService(facilityPatientWaitTime.service())
                    : null)
            .build()
        : DatamartFacility.PatientWaitTime.builder().build();
  }

  /** Transform version 0 facility phone to DatamartFacility phone. */
  private static DatamartFacility.Phone toVersionAgnosticFacilityPhone(
      Facility.Phone facilityPhone) {
    return (facilityPhone != null)
        ? DatamartFacility.Phone.builder()
            .fax(facilityPhone.fax())
            .main(facilityPhone.main())
            .afterHours(facilityPhone.afterHours())
            .enrollmentCoordinator(facilityPhone.enrollmentCoordinator())
            .mentalHealthClinic(facilityPhone.mentalHealthClinic())
            .patientAdvocate(facilityPhone.patientAdvocate())
            .pharmacy(facilityPhone.pharmacy())
            .build()
        : DatamartFacility.Phone.builder().build();
  }

  /** Transform version 0 facility satisfaction to DatamartFacility satisfaction. */
  private static DatamartFacility.Satisfaction toVersionAgnosticFacilitySatisfaction(
      Facility.Satisfaction facilitySatisfaction) {
    return (facilitySatisfaction != null)
        ? DatamartFacility.Satisfaction.builder()
            .health(
                (facilitySatisfaction.health() != null)
                    ? DatamartFacility.PatientSatisfaction.builder()
                        .primaryCareRoutine(facilitySatisfaction.health().primaryCareRoutine())
                        .primaryCareUrgent(facilitySatisfaction.health().primaryCareUrgent())
                        .specialtyCareRoutine(facilitySatisfaction.health().specialtyCareRoutine())
                        .specialtyCareUrgent(facilitySatisfaction.health().specialtyCareUrgent())
                        .build()
                    : null)
            .effectiveDate(facilitySatisfaction.effectiveDate())
            .build()
        : DatamartFacility.Satisfaction.builder().build();
  }

  /** Transform version 0 facility services to DatamartFacility services. */
  private static DatamartFacility.Services toVersionAgnosticFacilityServices(
      Facility.Services facilityServices, @NonNull String linkUrl, @NonNull String facilityId) {
    return (facilityServices != null)
        ? DatamartFacility.Services.builder()
            .health(
                (facilityServices.health() != null)
                    ? facilityServices.health().parallelStream()
                        .filter(
                            e ->
                                containsValueOfName(
                                        DatamartFacility.HealthService.values(), e.name())
                                    || checkHealthServiceNameChange(e))
                        .map(
                            e ->
                                toVersionAgnosticFacilityHealthTypedService(e, linkUrl, facilityId))
                        .collect(Collectors.toList())
                    : null)
            .benefits(
                (facilityServices.benefits() != null)
                    ? facilityServices.benefits().parallelStream()
                        .filter(
                            e ->
                                containsValueOfName(
                                    DatamartFacility.BenefitsService.values(), e.name()))
                        .map(
                            e ->
                                toVersionAgnosticFacilityBenefitsTypedService(
                                    e, linkUrl, facilityId))
                        .collect(Collectors.toList())
                    : null)
            .other(
                (facilityServices.other() != null)
                    ? facilityServices.other().parallelStream()
                        .filter(
                            e ->
                                containsValueOfName(
                                    DatamartFacility.OtherService.values(), e.name()))
                        .map(
                            e -> toVersionAgnosticFacilityOtherTypedService(e, linkUrl, facilityId))
                        .collect(Collectors.toList())
                    : null)
            .link(buildServicesLink(linkUrl, facilityId))
            .lastUpdated(facilityServices.lastUpdated())
            .build()
        : DatamartFacility.Services.builder().build();
  }

  /** Transform version 0 facility type to DatamartFacility facility type. */
  private static DatamartFacility.FacilityType toVersionAgnosticFacilityType(
      Facility.FacilityType facilityType) {
    return (facilityType != null)
        ? containsValueOfName(DatamartFacility.FacilityType.values(), facilityType.name())
            ? DatamartFacility.FacilityType.valueOf(facilityType.name())
            : null
        : null;
  }

  /** Transform version 0 facility wait times to DatamartFacility wait times. */
  private static DatamartFacility.WaitTimes toVersionAgnosticFacilityWaitTimes(
      Facility.WaitTimes facilityWaitTimes) {
    return (facilityWaitTimes != null)
        ? DatamartFacility.WaitTimes.builder()
            .health(
                (facilityWaitTimes.health() != null)
                    ? facilityWaitTimes.health().parallelStream()
                        .map(e -> toVersionAgnosticFacilityPatientWaitTime(e))
                        .collect(Collectors.toList())
                    : null)
            .effectiveDate(facilityWaitTimes.effectiveDate())
            .build()
        : DatamartFacility.WaitTimes.builder().build();
  }

  /** Transform DatamartFacility type to version 0 facility type. */
  private static DatamartFacility.Type toVersionAgnosticType(Facility.Type type) {
    return (type != null)
        ? containsValueOfName(DatamartFacility.Type.values(), type.name())
            ? DatamartFacility.Type.valueOf(type.name())
            : null
        : null;
  }
}
