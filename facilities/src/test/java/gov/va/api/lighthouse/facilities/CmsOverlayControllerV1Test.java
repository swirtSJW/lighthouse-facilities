package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.ExceptionsUtils.NotFound;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CmsOverlayControllerV1Test {
  @Mock FacilityRepository mockFacilityRepository;

  @Mock CmsOverlayRepository mockCmsOverlayRepository;

  private DatamartCmsOverlay activeOverlay() {
    return overlay(
        List.of(HealthService.Cardiology, HealthService.Covid19Vaccine, HealthService.Urology),
        true);
  }

  @Test
  @SneakyThrows
  void applyAtcWaitTimeToCmsServicesException() {
    DatamartCmsOverlay overlay = activeOverlay();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    assertThatThrownBy(() -> controller().getOverlay("vha_402"))
        .isInstanceOf(NotFound.class)
        .hasMessage("The record identified by vha_402 could not be found");
  }

  CmsOverlayControllerV1 controller() {
    return CmsOverlayControllerV1.builder()
        .facilityRepository(mockFacilityRepository)
        .cmsOverlayRepository(mockCmsOverlayRepository)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  public void exceptions() {
    var id = "vha_041";
    var pk = FacilityEntity.Pk.fromIdString(id);
    var page = 1;
    var perPage = 10;
    when(mockCmsOverlayRepository.findById(pk)).thenThrow(new NullPointerException("oh noes"));
    assertThatThrownBy(() -> controller().getExistingOverlayEntity(pk))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("oh noes");
    assertThatThrownBy(() -> controller().getDetailedServices(id, page, perPage))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("oh noes");
    when(mockFacilityRepository.findById(pk)).thenThrow(new NullPointerException("oh noes"));
    assertThatThrownBy(() -> controller().saveOverlay(id, activeOverlay()))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("oh noes");
  }

  private DatamartDetailedService getDatamartDetailedService(
      @NonNull HealthService healthService, boolean isActive) {
    return DatamartDetailedService.builder()
        .serviceInfo(
            DatamartDetailedService.ServiceInfo.builder()
                .serviceId(uncapitalize(healthService.name()))
                .name(healthService.name())
                .serviceType(getDatamartServiceType(healthService))
                .build())
        .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
        .active(isActive)
        .changed(null)
        .descriptionFacility(null)
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .onlineSchedulingAvailable("True")
        .path("replaceable path here")
        .phoneNumbers(
            List.of(
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .extension("123")
                    .label("Main phone")
                    .number("555-555-1212")
                    .type("tel")
                    .build()))
        .referralRequired("True")
        .walkInsAccepted("False")
        .serviceLocations(
            List.of(
                DatamartDetailedService.DetailedServiceLocation.builder()
                    .serviceLocationAddress(
                        DatamartDetailedService.DetailedServiceAddress.builder()
                            .buildingNameNumber("Baxter Building")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .address1("122 Main St.")
                            .address2(null)
                            .city("Rochester")
                            .state("NY")
                            .zipCode("14623-1345")
                            .countryCode("US")
                            .build())
                    .appointmentPhoneNumbers(
                        List.of(
                            DatamartDetailedService.AppointmentPhoneNumber.builder()
                                .extension("567")
                                .label("Alt phone")
                                .number("556-565-1119")
                                .type("tel")
                                .build()))
                    .emailContacts(
                        List.of(
                            DatamartDetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("georgea@va.gov")
                                .emailLabel("George Anderson")
                                .build()))
                    .facilityServiceHours(
                        DatamartDetailedService.DetailedServiceHours.builder()
                            .monday("8:30AM-7:00PM")
                            .tuesday("8:30AM-7:00PM")
                            .wednesday("8:30AM-7:00PM")
                            .thursday("8:30AM-7:00PM")
                            .friday("8:30AM-7:00PM")
                            .saturday("8:30AM-7:00PM")
                            .sunday("CLOSED")
                            .build())
                    .additionalHoursInfo("Please call for an appointment outside...")
                    .build()))
        .build();
  }

  private List<DatamartDetailedService> getDatamartDetailedServices(
      @NonNull List<HealthService> healthServices, boolean isActive) {
    return healthServices.stream()
        .map(hs -> getDatamartDetailedService(hs, isActive))
        .collect(Collectors.toList());
  }

  private DatamartDetailedService.ServiceType getDatamartServiceType(
      @NonNull ServiceType serviceType) {
    return Arrays.stream(DatamartFacility.HealthService.values())
            .anyMatch(hs -> hs.name().equals(serviceType.name()))
        ? DatamartDetailedService.ServiceType.Health
        : Arrays.stream(DatamartFacility.BenefitsService.values())
                .anyMatch(bs -> bs.name().equals(serviceType.name()))
            ? DatamartDetailedService.ServiceType.Benefits
            : Arrays.stream(DatamartFacility.OtherService.values())
                    .anyMatch(os -> os.name().equals(serviceType.name()))
                ? DatamartDetailedService.ServiceType.Other
                : // Default to health service type
                DatamartDetailedService.ServiceType.Health;
  }

  @Test
  @SneakyThrows
  public void getDetailedService() {
    DatamartCmsOverlay overlay = activeOverlay();
    var facilityId = "vha_402";
    var pk = FacilityEntity.Pk.fromIdString(facilityId);
    var serviceId = uncapitalize(HealthService.Covid19Vaccine.name());
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    String facility =
        "{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"name\":\"Greenville VA Clinic\",\"facility_type\":\"va_health_facility\",\"classification\":\"Multi-Specialty CBOC\",\"website\":\"https://www.va.gov/durham-health-care/locations/greenville-va-clinic/\",\"lat\":35.61679734,\"long\":-77.39924707,\"time_zone\":\"America/New_York\",\"address\":{\"mailing\":{},\"physical\":{\"zip\":\"27834-2885\",\"city\":\"Greenville\",\"state\":\"NC\",\"address_1\":\"401 Moye Boulevard\",\"address_2\":null,\"address_3\":null}},\"phone\":{\"fax\":\"252-830-1106\",\"main\":\"252-830-2149\",\"pharmacy\":\"888-878-6890\",\"after_hours\":\"919-286-0411\",\"patient_advocate\":\"919-286-0411 x176906\",\"mental_health_clinic\":\"252-830-2149 x 3220\",\"enrollment_coordinator\":\"919-286-0411 x176993\"},\"hours\":{\"monday\":\"800AM-430PM\",\"tuesday\":\"800AM-430PM\",\"wednesday\":\"800AM-430PM\",\"thursday\":\"800AM-430PM\",\"friday\":\"800AM-430PM\",\"saturday\":\"Closed\",\"sunday\":\"Closed\"},\"operational_hours_special_instructions\":null,\"services\":{\"other\":[],\"health\":[\"audiology\",\"cardiology\",\"dental\",\"dermatology\",\"emergencyCare\",\"gastroenterology\",\"gynecology\",\"mentalHealth\",\"nutrition\",\"ophthalmology\",\"optometry\",\"orthopedics\",\"podiatry\",\"primaryCare\",\"specialtyCare\",\"urology\",\"womensHealth\"],\"last_updated\":\"2020-03-09\"},\"satisfaction\":{\"health\":{\"primary_care_urgent\":0.7699999809265137,\"primary_care_routine\":0.8600000143051147},\"effective_date\":\"2019-06-20\"},\"wait_times\":{\"health\":[{\"service\":\"audiology\",\"new\":27.349514,\"established\":13.296296},{\"service\":\"cardiology\",\"new\":14.583333,\"established\":8.076923},{\"service\":\"dermatology\",\"new\":1.90909,\"established\":8.54878},{\"service\":\"gastroenterology\",\"new\":38.0,\"established\":0.0},{\"service\":\"gynecology\",\"new\":19.666666,\"established\":4.794871},{\"service\":\"mentalHealth\",\"new\":39.84375,\"established\":7.601423},{\"service\":\"ophthalmology\",\"new\":65.25,\"established\":13.947368},{\"service\":\"optometry\",\"new\":84.217948,\"established\":11.974619},{\"service\":\"orthopedics\",\"new\":30.222222,\"established\":6.40625},{\"service\":\"primaryCare\",\"new\":28.175438,\"established\":4.359409},{\"service\":\"specialtyCare\",\"new\":34.946478,\"established\":12.140114},{\"service\":\"urology\",\"new\":13.868421,\"established\":2.016129},{\"service\":\"womensHealth\",\"new\":19.666666,\"established\":4.794871}],\"effective_date\":\"2020-03-09\"},\"mobile\":false,\"active_status\":\"A\",\"operating_status\":null,\"detailed_services\":null,\"visn\":\"6\"}}";
    FacilityEntity facilityEntity = FacilityEntity.builder().facility(facility).build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(facilityEntity));
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    assertThat(controller().getDetailedService(facilityId, serviceId))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServiceResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedService(
                            getDatamartDetailedService(HealthService.Covid19Vaccine, false)))
                    .build()));
  }

  @Test
  @SneakyThrows
  void getDetailedServiceWithNoAtcWaitTime() {
    DatamartCmsOverlay overlay = activeOverlay();
    var facilityId = "vha_402";
    var pk = FacilityEntity.Pk.fromIdString(facilityId);
    var page = 1;
    var perPage = 1;
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    String facility =
        "{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"name\":\"Athens Community Based Outpatient Clinic\",\"facility_type\":\"va_benefits_facility\",\"classification\":\"Outbased\",\"website\":null,\"lat\":39.33105822,\"long\":-82.12304104,\"time_zone\":\"America/New_York\",\"address\":{\"mailing\":{},\"physical\":{\"zip\":\"45701\",\"city\":\"Athens\",\"state\":\"OH\",\"address_1\":\"510 West Union Street\",\"address_2\":\"\",\"address_3\":null}},\"phone\":{\"fax\":\"740-594-2804\",\"main\":\"740-593-7314\"},\"hours\":{\"monday\":\"8:00AM-4:30PM\",\"tuesday\":\"8:00AM-4:30PM\",\"wednesday\":\"8:00AM-4:30PM\",\"thursday\":\"8:00AM-4:30PM\",\"friday\":\"8:00AM-4:30PM\",\"saturday\":\"Closed\",\"sunday\":\"Closed\"},\"operational_hours_special_instructions\":null,\"services\":{\"benefits\":[]},\"satisfaction\":{},\"wait_times\":{},\"mobile\":null,\"active_status\":null,\"operating_status\":null,\"detailed_services\":null,\"visn\":null}}";
    FacilityEntity facilityEntity = FacilityEntity.builder().facility(facility).build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(facilityEntity));
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    assertThat(
            controller()
                .getDetailedService(facilityId, uncapitalize(HealthService.Covid19Vaccine.name())))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServiceResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedService(
                            getDatamartDetailedService(HealthService.Covid19Vaccine, false)))
                    .build()));
  }

  @Test
  @SneakyThrows
  public void getDetailedServices() {
    DatamartCmsOverlay overlay = activeOverlay();
    var facilityId = "vha_402";
    var pk = FacilityEntity.Pk.fromIdString(facilityId);
    var page = 1;
    var perPage = 1;
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    String facility =
        "{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"name\":\"Greenville VA Clinic\",\"facility_type\":\"va_health_facility\",\"classification\":\"Multi-Specialty CBOC\",\"website\":\"https://www.va.gov/durham-health-care/locations/greenville-va-clinic/\",\"lat\":35.61679734,\"long\":-77.39924707,\"time_zone\":\"America/New_York\",\"address\":{\"mailing\":{},\"physical\":{\"zip\":\"27834-2885\",\"city\":\"Greenville\",\"state\":\"NC\",\"address_1\":\"401 Moye Boulevard\",\"address_2\":null,\"address_3\":null}},\"phone\":{\"fax\":\"252-830-1106\",\"main\":\"252-830-2149\",\"pharmacy\":\"888-878-6890\",\"after_hours\":\"919-286-0411\",\"patient_advocate\":\"919-286-0411 x176906\",\"mental_health_clinic\":\"252-830-2149 x 3220\",\"enrollment_coordinator\":\"919-286-0411 x176993\"},\"hours\":{\"monday\":\"800AM-430PM\",\"tuesday\":\"800AM-430PM\",\"wednesday\":\"800AM-430PM\",\"thursday\":\"800AM-430PM\",\"friday\":\"800AM-430PM\",\"saturday\":\"Closed\",\"sunday\":\"Closed\"},\"operational_hours_special_instructions\":null,\"services\":{\"other\":[],\"health\":[\"audiology\",\"cardiology\",\"dental\",\"dermatology\",\"emergencyCare\",\"gastroenterology\",\"gynecology\",\"mentalHealth\",\"nutrition\",\"ophthalmology\",\"optometry\",\"orthopedics\",\"podiatry\",\"primaryCare\",\"specialtyCare\",\"urology\",\"womensHealth\"],\"last_updated\":\"2020-03-09\"},\"satisfaction\":{\"health\":{\"primary_care_urgent\":0.7699999809265137,\"primary_care_routine\":0.8600000143051147},\"effective_date\":\"2019-06-20\"},\"wait_times\":{\"health\":[{\"service\":\"audiology\",\"new\":27.349514,\"established\":13.296296},{\"service\":\"cardiology\",\"new\":14.583333,\"established\":8.076923},{\"service\":\"dermatology\",\"new\":1.90909,\"established\":8.54878},{\"service\":\"gastroenterology\",\"new\":38.0,\"established\":0.0},{\"service\":\"gynecology\",\"new\":19.666666,\"established\":4.794871},{\"service\":\"mentalHealth\",\"new\":39.84375,\"established\":7.601423},{\"service\":\"ophthalmology\",\"new\":65.25,\"established\":13.947368},{\"service\":\"optometry\",\"new\":84.217948,\"established\":11.974619},{\"service\":\"orthopedics\",\"new\":30.222222,\"established\":6.40625},{\"service\":\"primaryCare\",\"new\":28.175438,\"established\":4.359409},{\"service\":\"specialtyCare\",\"new\":34.946478,\"established\":12.140114},{\"service\":\"urology\",\"new\":13.868421,\"established\":2.016129},{\"service\":\"womensHealth\",\"new\":19.666666,\"established\":4.794871}],\"effective_date\":\"2020-03-09\"},\"mobile\":false,\"active_status\":\"A\",\"operating_status\":null,\"detailed_services\":null,\"visn\":\"6\"}}";
    FacilityEntity facilityEntity = FacilityEntity.builder().facility(facility).build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(facilityEntity));
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    assertThat(controller().getDetailedServices(facilityId, page, perPage))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServicesResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedServices(
                            getDatamartDetailedServices(List.of(HealthService.Cardiology), false)
                                .stream()
                                .map(
                                    dds ->
                                        dds.waitTime(
                                            DatamartDetailedService.PatientWaitTime.builder()
                                                .newPatientWaitTime(BigDecimal.valueOf(14.583333))
                                                .establishedPatientWaitTime(
                                                    BigDecimal.valueOf(8.076923))
                                                .effectiveDate(LocalDate.parse("2020-03-09"))
                                                .build()))
                                .collect(Collectors.toList())))
                    .links(
                        PageLinks.builder()
                            .self("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .first("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .prev(null)
                            .next("http://foo/bp/v1/facilities/vha_402/services?page=2&per_page=1")
                            .last("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .build())
                    .meta(
                        DetailedServicesResponse.DetailedServicesMetadata.builder()
                            .pagination(
                                Pagination.builder()
                                    .currentPage(1)
                                    .entriesPerPage(1)
                                    .totalPages(3)
                                    .totalEntries(3)
                                    .build())
                            .build())
                    .build()));
    // Obtain second page of detailed services - covid-19 detailed service
    page = 2;
    assertThat(controller().getDetailedServices(facilityId, page, perPage))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServicesResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedServices(
                            getDatamartDetailedServices(
                                List.of(HealthService.Covid19Vaccine), false)))
                    .links(
                        PageLinks.builder()
                            .self("http://foo/bp/v1/facilities/vha_402/services?page=2&per_page=1")
                            .first("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .prev("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .next("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .last("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .build())
                    .meta(
                        DetailedServicesResponse.DetailedServicesMetadata.builder()
                            .pagination(
                                Pagination.builder()
                                    .currentPage(2)
                                    .entriesPerPage(1)
                                    .totalPages(3)
                                    .totalEntries(3)
                                    .build())
                            .build())
                    .build()));
    // Obtain third and final page of detailed services - urology detailed service
    page = 3;
    assertThat(controller().getDetailedServices(facilityId, page, perPage))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServicesResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedServices(
                            getDatamartDetailedServices(List.of(HealthService.Urology), false)
                                .stream()
                                .map(
                                    dds ->
                                        dds.waitTime(
                                            DatamartDetailedService.PatientWaitTime.builder()
                                                .newPatientWaitTime(BigDecimal.valueOf(13.868421))
                                                .establishedPatientWaitTime(
                                                    BigDecimal.valueOf(2.016129))
                                                .effectiveDate(LocalDate.parse("2020-03-09"))
                                                .build()))
                                .collect(Collectors.toList())))
                    .links(
                        PageLinks.builder()
                            .self("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .first("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .prev("http://foo/bp/v1/facilities/vha_402/services?page=2&per_page=1")
                            .next(null)
                            .last("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .build())
                    .meta(
                        DetailedServicesResponse.DetailedServicesMetadata.builder()
                            .pagination(
                                Pagination.builder()
                                    .currentPage(3)
                                    .entriesPerPage(1)
                                    .totalPages(3)
                                    .totalEntries(3)
                                    .build())
                            .build())
                    .build()));
  }

  @Test
  @SneakyThrows
  void getExistingOverlay() {
    DatamartCmsOverlay overlay = activeOverlay();

    Optional<DatamartDetailedService> cardioOpt =
        overlay.detailedServices().stream()
            .filter(
                dds ->
                    dds.serviceInfo.serviceId().equalsIgnoreCase(HealthService.Cardiology.name()))
            .findFirst();
    DatamartDetailedService cardioService = cardioOpt.get();
    cardioService.waitTime(
        DatamartDetailedService.PatientWaitTime.builder()
            .newPatientWaitTime(BigDecimal.valueOf(14.583333))
            .establishedPatientWaitTime(BigDecimal.valueOf(8.076923))
            .effectiveDate(LocalDate.parse("2020-03-09"))
            .build());

    Optional<DatamartDetailedService> urologyOpt =
        overlay.detailedServices().stream()
            .filter(
                dds -> dds.serviceInfo.serviceId().equalsIgnoreCase(HealthService.Urology.name()))
            .findFirst();
    DatamartDetailedService urologyService = urologyOpt.get();
    urologyService.waitTime(
        DatamartDetailedService.PatientWaitTime.builder()
            .newPatientWaitTime(BigDecimal.valueOf(13.868421))
            .establishedPatientWaitTime(BigDecimal.valueOf(2.016129))
            .effectiveDate(LocalDate.parse("2020-03-09"))
            .build());

    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    String facility =
        "{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"name\":\"Greenville VA Clinic\",\"facility_type\":\"va_health_facility\",\"classification\":\"Multi-Specialty CBOC\",\"website\":\"https://www.va.gov/durham-health-care/locations/greenville-va-clinic/\",\"lat\":35.61679734,\"long\":-77.39924707,\"time_zone\":\"America/New_York\",\"address\":{\"mailing\":{},\"physical\":{\"zip\":\"27834-2885\",\"city\":\"Greenville\",\"state\":\"NC\",\"address_1\":\"401 Moye Boulevard\",\"address_2\":null,\"address_3\":null}},\"phone\":{\"fax\":\"252-830-1106\",\"main\":\"252-830-2149\",\"pharmacy\":\"888-878-6890\",\"after_hours\":\"919-286-0411\",\"patient_advocate\":\"919-286-0411 x176906\",\"mental_health_clinic\":\"252-830-2149 x 3220\",\"enrollment_coordinator\":\"919-286-0411 x176993\"},\"hours\":{\"monday\":\"800AM-430PM\",\"tuesday\":\"800AM-430PM\",\"wednesday\":\"800AM-430PM\",\"thursday\":\"800AM-430PM\",\"friday\":\"800AM-430PM\",\"saturday\":\"Closed\",\"sunday\":\"Closed\"},\"operational_hours_special_instructions\":null,\"services\":{\"other\":[],\"health\":[\"audiology\",\"cardiology\",\"dental\",\"dermatology\",\"emergencyCare\",\"gastroenterology\",\"gynecology\",\"mentalHealth\",\"nutrition\",\"ophthalmology\",\"optometry\",\"orthopedics\",\"podiatry\",\"primaryCare\",\"specialtyCare\",\"urology\",\"womensHealth\"],\"last_updated\":\"2020-03-09\"},\"satisfaction\":{\"health\":{\"primary_care_urgent\":0.7699999809265137,\"primary_care_routine\":0.8600000143051147},\"effective_date\":\"2019-06-20\"},\"wait_times\":{\"health\":[{\"service\":\"audiology\",\"new\":27.349514,\"established\":13.296296},{\"service\":\"cardiology\",\"new\":14.583333,\"established\":8.076923},{\"service\":\"dermatology\",\"new\":1.90909,\"established\":8.54878},{\"service\":\"gastroenterology\",\"new\":38.0,\"established\":0.0},{\"service\":\"gynecology\",\"new\":19.666666,\"established\":4.794871},{\"service\":\"mentalHealth\",\"new\":39.84375,\"established\":7.601423},{\"service\":\"ophthalmology\",\"new\":65.25,\"established\":13.947368},{\"service\":\"optometry\",\"new\":84.217948,\"established\":11.974619},{\"service\":\"orthopedics\",\"new\":30.222222,\"established\":6.40625},{\"service\":\"primaryCare\",\"new\":28.175438,\"established\":4.359409},{\"service\":\"specialtyCare\",\"new\":34.946478,\"established\":12.140114},{\"service\":\"urology\",\"new\":13.868421,\"established\":2.016129},{\"service\":\"womensHealth\",\"new\":19.666666,\"established\":4.794871}],\"effective_date\":\"2020-03-09\"},\"mobile\":false,\"active_status\":\"A\",\"operating_status\":null,\"detailed_services\":null,\"visn\":\"6\"}}";
    FacilityEntity facilityEntity = FacilityEntity.builder().facility(facility).build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(facilityEntity));
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    // active will ALWAYS be false when retrieving from the database, the fact the overlay
    // exists means that active was true at the time of insertion
    for (DatamartDetailedService d : overlay.detailedServices()) {
      d.active(false);
    }
    ResponseEntity<CmsOverlayResponse> response = controller().getOverlay("vha_402");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(CmsOverlayTransformerV1.toVersionAgnostic(response.getBody().overlay()))
        .isEqualTo(overlay);
  }

  @Test
  void getNonExistingOverlay() {
    assertThatThrownBy(() -> controller().getOverlay("vha_041"))
        .isInstanceOf(ExceptionsUtils.NotFound.class)
        .hasMessage("The record identified by vha_041 could not be found");
  }

  @Test
  void getOverlayDetailedServicesException() {
    assertThatThrownBy(() -> controller().getDetailedServices("vha_402", 1, 10))
        .isInstanceOf(NotFound.class)
        .hasMessage("The record identified by vha_402 could not be found");
  }

  private DatamartCmsOverlay overlay(
      @NonNull List<HealthService> healthServices, boolean isActive) {
    return DatamartCmsOverlay.builder()
        .operatingStatus(
            DatamartFacility.OperatingStatus.builder()
                .code(DatamartFacility.OperatingStatusCode.NOTICE)
                .additionalInfo("i need attention")
                .build())
        .detailedServices(getDatamartDetailedServices(healthServices, isActive))
        .build();
  }

  @Test
  @SneakyThrows
  void updateIsAcceptedForKnownStationUsingServiceId() {
    Facility f =
        Facility.builder()
            .id("vha_402")
            .attributes(Facility.FacilityAttributes.builder().website("va.gov").build())
            .build();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    FacilityEntity entity =
        FacilityEntity.builder()
            .id(pk)
            .facility(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(FacilityTransformerV1.toVersionAgnostic(f)))
            .build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(entity));
    DatamartCmsOverlay overlay = activeOverlay();
    ResponseEntity<Void> response = controller().saveOverlay("vha_402", overlay);
    Set<String> detailedServices = new HashSet<>();
    for (DatamartDetailedService service : overlay.detailedServices()) {
      if (service.active()) {
        detailedServices.add(capitalize(service.serviceInfo().serviceId()));
      }
    }
    // Test contained DetailedService is one of HealthService, BenefitsService, or OtherService
    assertThat(
            detailedServices.parallelStream()
                .filter(
                    ds ->
                        Arrays.stream(HealthService.values()).anyMatch(hs -> hs.name().equals(ds))
                            || Arrays.stream(BenefitsService.values())
                                .anyMatch(bs -> bs.name().equals(ds))
                            || Arrays.stream(OtherService.values())
                                .anyMatch(os -> os.name().equals(ds)))
                .collect(Collectors.toList()))
        .usingRecursiveComparison()
        .isEqualTo(detailedServices);
    entity.cmsOperatingStatus(
        DatamartFacilitiesJacksonConfig.createMapper()
            .writeValueAsString(overlay.operatingStatus()));
    entity.overlayServices(detailedServices);
    verify(mockFacilityRepository).save(entity);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @SneakyThrows
  void updateIsAcceptedForKnownStationUsingServiceName() {
    Facility f =
        Facility.builder()
            .id("vha_402")
            .attributes(Facility.FacilityAttributes.builder().website("va.gov").build())
            .build();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    FacilityEntity entity =
        FacilityEntity.builder()
            .id(pk)
            .facility(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(FacilityTransformerV1.toVersionAgnostic(f)))
            .build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(entity));
    DatamartCmsOverlay overlay = activeOverlay();
    ResponseEntity<Void> response = controller().saveOverlay("vha_402", overlay);
    Set<String> detailedServices = new HashSet<>();
    for (DatamartDetailedService service : overlay.detailedServices()) {
      if (service.active()) {
        if (CMS_OVERLAY_SERVICE_NAME_COVID_19.equals(service.serviceInfo().name())) {
          detailedServices.add(HealthService.Covid19Vaccine.name());
        } else {
          detailedServices.add(service.serviceInfo().name());
        }
      }
    }
    // Test contained DetailedService is one of HealthService, BenefitsService, or OtherService
    assertThat(
            detailedServices.parallelStream()
                .filter(
                    ds ->
                        Arrays.stream(HealthService.values()).anyMatch(hs -> hs.name().equals(ds))
                            || Arrays.stream(BenefitsService.values())
                                .anyMatch(bs -> bs.name().equals(ds))
                            || Arrays.stream(OtherService.values())
                                .anyMatch(os -> os.name().equals(ds)))
                .collect(Collectors.toList()))
        .usingRecursiveComparison()
        .isEqualTo(detailedServices);
    entity.cmsOperatingStatus(
        DatamartFacilitiesJacksonConfig.createMapper()
            .writeValueAsString(overlay.operatingStatus()));
    entity.overlayServices(detailedServices);
    verify(mockFacilityRepository).save(entity);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void updateIsSkippedForUnknownStation() {
    var pk = FacilityEntity.Pk.fromIdString("vha_666");
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.empty());
    ResponseEntity<Void> response = controller().saveOverlay("vha_666", activeOverlay());
    verifyNoMoreInteractions(mockFacilityRepository);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  @SneakyThrows
  void updateWithExistingOverlay() {
    DatamartCmsOverlay overlay = activeOverlay();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    List<DatamartDetailedService> additionalServices =
        List.of(
            DatamartDetailedService.builder()
                .serviceInfo(
                    DatamartDetailedService.ServiceInfo.builder()
                        .serviceId(
                            uncapitalize(DatamartFacility.HealthService.CaregiverSupport.name()))
                        .name("additional service1")
                        .serviceType(DatamartDetailedService.ServiceType.Health)
                        .build())
                .waitTime(DatamartDetailedService.PatientWaitTime.builder().build())
                .active(true)
                .build(),
            DatamartDetailedService.builder()
                .serviceInfo(
                    DatamartDetailedService.ServiceInfo.builder()
                        .serviceId(
                            uncapitalize(DatamartFacility.HealthService.Ophthalmology.name()))
                        .name("additional service2")
                        .serviceType(DatamartDetailedService.ServiceType.Health)
                        .build())
                .waitTime(
                    DatamartDetailedService.PatientWaitTime.builder()
                        .newPatientWaitTime(BigDecimal.valueOf(65.25))
                        .establishedPatientWaitTime(BigDecimal.valueOf(13.947368))
                        .effectiveDate(LocalDate.parse("2020-03-09"))
                        .build())
                .active(true)
                .build());
    overlay.detailedServices(additionalServices);
    ResponseEntity<Void> resp = controller().saveOverlay("vha_402", overlay);
    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    DatamartCmsOverlay updatedCovidPathOverlay = activeOverlay();
    List<DatamartDetailedService> datamartDetailedServices =
        updatedCovidPathOverlay.detailedServices();
    updateServiceUrlPaths("vha_402", datamartDetailedServices);
    updatedCovidPathOverlay.detailedServices(datamartDetailedServices);

    Optional<DatamartDetailedService> cardioOpt =
        updatedCovidPathOverlay.detailedServices().stream()
            .filter(
                dds ->
                    dds.serviceInfo.serviceId().equalsIgnoreCase(HealthService.Cardiology.name()))
            .findFirst();
    DatamartDetailedService cardioService = cardioOpt.get();
    cardioService.waitTime(
        DatamartDetailedService.PatientWaitTime.builder()
            .newPatientWaitTime(BigDecimal.valueOf(14.583333))
            .establishedPatientWaitTime(BigDecimal.valueOf(8.076923))
            .effectiveDate(LocalDate.parse("2020-03-09"))
            .build());

    Optional<DatamartDetailedService> urologyOpt =
        updatedCovidPathOverlay.detailedServices().stream()
            .filter(
                dds -> dds.serviceInfo.serviceId().equalsIgnoreCase(HealthService.Urology.name()))
            .findFirst();
    DatamartDetailedService urologyService = urologyOpt.get();
    urologyService.waitTime(
        DatamartDetailedService.PatientWaitTime.builder()
            .newPatientWaitTime(BigDecimal.valueOf(13.868421))
            .establishedPatientWaitTime(BigDecimal.valueOf(2.016129))
            .effectiveDate(LocalDate.parse("2020-03-09"))
            .build());
    List<DatamartDetailedService> combinedServices =
        Streams.stream(
                Iterables.concat(updatedCovidPathOverlay.detailedServices(), additionalServices))
            .toList();
    // active will ALWAYS be false when retrieving from the database, the fact the overlay
    // exists means that active was true at the time of insertion
    for (DatamartDetailedService d : combinedServices) {
      d.active(false);
    }
    String facility =
        "{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"name\":\"Greenville VA Clinic\",\"facility_type\":\"va_health_facility\",\"classification\":\"Multi-Specialty CBOC\",\"website\":\"https://www.va.gov/durham-health-care/locations/greenville-va-clinic/\",\"lat\":35.61679734,\"long\":-77.39924707,\"time_zone\":\"America/New_York\",\"address\":{\"mailing\":{},\"physical\":{\"zip\":\"27834-2885\",\"city\":\"Greenville\",\"state\":\"NC\",\"address_1\":\"401 Moye Boulevard\",\"address_2\":null,\"address_3\":null}},\"phone\":{\"fax\":\"252-830-1106\",\"main\":\"252-830-2149\",\"pharmacy\":\"888-878-6890\",\"after_hours\":\"919-286-0411\",\"patient_advocate\":\"919-286-0411 x176906\",\"mental_health_clinic\":\"252-830-2149 x 3220\",\"enrollment_coordinator\":\"919-286-0411 x176993\"},\"hours\":{\"monday\":\"800AM-430PM\",\"tuesday\":\"800AM-430PM\",\"wednesday\":\"800AM-430PM\",\"thursday\":\"800AM-430PM\",\"friday\":\"800AM-430PM\",\"saturday\":\"Closed\",\"sunday\":\"Closed\"},\"operational_hours_special_instructions\":null,\"services\":{\"other\":[],\"health\":[\"audiology\",\"cardiology\",\"dental\",\"dermatology\",\"emergencyCare\",\"gastroenterology\",\"gynecology\",\"mentalHealth\",\"nutrition\",\"ophthalmology\",\"optometry\",\"orthopedics\",\"podiatry\",\"primaryCare\",\"specialtyCare\",\"urology\",\"womensHealth\"],\"last_updated\":\"2020-03-09\"},\"satisfaction\":{\"health\":{\"primary_care_urgent\":0.7699999809265137,\"primary_care_routine\":0.8600000143051147},\"effective_date\":\"2019-06-20\"},\"wait_times\":{\"health\":[{\"service\":\"audiology\",\"new\":27.349514,\"established\":13.296296},{\"service\":\"cardiology\",\"new\":14.583333,\"established\":8.076923},{\"service\":\"dermatology\",\"new\":1.90909,\"established\":8.54878},{\"service\":\"gastroenterology\",\"new\":38.0,\"established\":0.0},{\"service\":\"gynecology\",\"new\":19.666666,\"established\":4.794871},{\"service\":\"mentalHealth\",\"new\":39.84375,\"established\":7.601423},{\"service\":\"ophthalmology\",\"new\":65.25,\"established\":13.947368},{\"service\":\"optometry\",\"new\":84.217948,\"established\":11.974619},{\"service\":\"orthopedics\",\"new\":30.222222,\"established\":6.40625},{\"service\":\"primaryCare\",\"new\":28.175438,\"established\":4.359409},{\"service\":\"specialtyCare\",\"new\":34.946478,\"established\":12.140114},{\"service\":\"urology\",\"new\":13.868421,\"established\":2.016129},{\"service\":\"womensHealth\",\"new\":19.666666,\"established\":4.794871}],\"effective_date\":\"2020-03-09\"},\"mobile\":false,\"active_status\":\"A\",\"operating_status\":null,\"detailed_services\":null,\"visn\":\"6\"}}";
    FacilityEntity facilityEntity = FacilityEntity.builder().facility(facility).build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(facilityEntity));
    ResponseEntity<CmsOverlayResponse> response = controller().getOverlay("vha_402");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(
                response.getBody().overlay().detailedServices()))
        .containsAll(combinedServices);
  }

  @Test
  @SneakyThrows
  void verifyServicePathUpdated() {
    Facility f =
        Facility.builder()
            .id("vha_402")
            .attributes(Facility.FacilityAttributes.builder().website("va.gov").build())
            .build();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    FacilityEntity entity =
        FacilityEntity.builder()
            .id(pk)
            .facility(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(FacilityTransformerV1.toVersionAgnostic(f)))
            .build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(entity));
    DatamartCmsOverlay overlay = activeOverlay();
    for (DatamartDetailedService d : overlay.detailedServices()) {
      if (d.serviceInfo().name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
        assertThat(d.path()).isEqualTo("replaceable path here");
      }
    }
    ResponseEntity<Void> response = controller().saveOverlay("vha_402", overlay);
    CmsOverlay cmsOverlay = CmsOverlayTransformerV1.toCmsOverlay(overlay);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    for (DetailedService d : cmsOverlay.detailedServices()) {
      if (d.serviceInfo().name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
        assertThat(d.path())
            .isEqualTo("https://www.va.gov/maine-health-care/programs/covid-19-vaccines/");
      }
    }
  }
}
