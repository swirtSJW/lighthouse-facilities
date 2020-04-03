package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.lighthouse.facilitiescollector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.emptyToNull;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.hoursToClosed;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.isBlank;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.compareIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.length;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import com.google.common.collect.ListMultimap;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.BooleanUtils;

@Builder
final class HealthTransformer {
  private static final Map<String, Facility.HealthService> HEALTH_SERVICES =
      initHealthServicesMap();

  @NonNull private final ArcGisHealths.Feature gis;

  @NonNull private final ListMultimap<String, AccessToCareEntry> accessToCare;

  @NonNull private final ListMultimap<String, AccessToPwtEntry> accessToPwt;

  @NonNull private final Map<String, String> mentalHealthPhoneNumbers;

  @NonNull private final ListMultimap<String, StopCode> stopCodesMap;

  @NonNull private final Map<String, String> websites;

  private static Map<String, Facility.HealthService> initHealthServicesMap() {
    Map<String, Facility.HealthService> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.put("AUDIOLOGY", Facility.HealthService.Audiology);
    map.put("CARDIOLOGY", Facility.HealthService.Cardiology);
    map.put("COMP WOMEN'S HLTH", Facility.HealthService.WomensHealth);
    map.put("DERMATOLOGY", Facility.HealthService.Dermatology);
    map.put("GASTROENTEROLOGY", Facility.HealthService.Gastroenterology);
    map.put("GYNECOLOGY", Facility.HealthService.Gynecology);
    map.put("MENTAL HEALTH", Facility.HealthService.MentalHealthCare);
    map.put("OPHTHALMOLOGY", Facility.HealthService.Ophthalmology);
    map.put("OPTOMETRY", Facility.HealthService.Optometry);
    map.put("ORTHOPEDICS", Facility.HealthService.Orthopedics);
    map.put("PRIMARY CARE", Facility.HealthService.PrimaryCare);
    map.put("SPECIALTY CARE", Facility.HealthService.SpecialtyCare);
    map.put("UROLOGY CLINIC", Facility.HealthService.Urology);
    return map;
  }

  private static Facility.HealthService serviceName(AccessToCareEntry atc) {
    return atc == null ? null : HEALTH_SERVICES.get(trimToEmpty(atc.apptTypeName()));
  }

  private static LocalDate sliceToDate(String slice) {
    return length(slice) <= 9 ? null : LocalDate.parse(slice.substring(0, 10));
  }

  private static Facility.PatientWaitTime waitTime(AccessToCareEntry atc) {
    if (atc == null
        || isBlank(serviceName(atc))
        || allBlank(waitTimeNumber(atc.newWaitTime()), waitTimeNumber(atc.estWaitTime()))) {
      return null;
    }
    return Facility.PatientWaitTime.builder()
        .service(serviceName(atc))
        .newPatientWaitTime(waitTimeNumber(atc.newWaitTime()))
        .establishedPatientWaitTime(waitTimeNumber(atc.estWaitTime()))
        .build();
  }

  private static BigDecimal waitTimeNumber(BigDecimal waitTime) {
    if (waitTime == null || waitTime.compareTo(new BigDecimal("999")) >= 0) {
      return null;
    }
    return waitTime;
  }

  private List<AccessToCareEntry> accessToCareEntries() {
    return accessToCare.get(trimToEmpty(upperCase(id(), Locale.US)));
  }

  private List<AccessToPwtEntry> accessToPwtEntries() {
    return accessToPwt.get(trimToEmpty(upperCase(id(), Locale.US)));
  }

  private Facility.ActiveStatus activeStatus() {
    if (gis.attributes() == null || allBlank(gis.attributes().pod())) {
      return null;
    }
    return gis.attributes().pod().equalsIgnoreCase("A")
        ? Facility.ActiveStatus.A
        : Facility.ActiveStatus.T;
  }

  private Facility.Addresses address() {
    if (allBlank(physical())) {
      return null;
    }
    return Facility.Addresses.builder().physical(physical()).build();
  }

  private LocalDate atcEffectiveDate() {
    return accessToCareEntries().stream()
        .map(ace -> sliceToDate(ace.sliceEndDate()))
        .filter(Objects::nonNull)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(null);
  }

  private LocalDate atpEffectiveDate() {
    return accessToPwtEntries().stream()
        .map(ape -> sliceToDate(ape.sliceEndDate()))
        .filter(Objects::nonNull)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(null);
  }

  private Facility.FacilityAttributes attributes() {
    if (allBlank(
        name(),
        classification(),
        website(),
        latitude(),
        longitude(),
        address(),
        phone(),
        hours(),
        services(),
        satisfaction(),
        waitTimes(),
        mobile(),
        activeStatus(),
        visn())) {
      return null;
    }
    return Facility.FacilityAttributes.builder()
        .name(name())
        .facilityType(Facility.FacilityType.va_health_facility)
        .classification(classification())
        .website(website())
        .latitude(latitude())
        .longitude(longitude())
        .address(address())
        .phone(phone())
        .hours(hours())
        .services(services())
        .satisfaction(satisfaction())
        .waitTimes(waitTimes())
        .mobile(mobile())
        .activeStatus(activeStatus())
        .visn(visn())
        .build();
  }

  String classification() {
    if (gis.attributes() == null) {
      return null;
    }
    switch (trimToEmpty(gis.attributes().cocClassificationId())) {
      case "1":
        return "VA Medical Center (VAMC)";
      case "2":
        return "Health Care Center (HCC)";
      case "3":
        return "Multi-Specialty CBOC";
      case "4":
        return "Primary Care CBOC";
      case "5":
        return "Other Outpatient Services (OOS)";
      case "7":
        return "Residential Care Site (MH RRTP/DRRTP) (Stand-Alone)";
      case "8":
        return "Extended Care Site (Community Living Center) (Stand-Alone)";
      default:
        if (isNotBlank(gis.attributes().cocClassificationId())) {
          return gis.attributes().cocClassificationId();
        }
        return gis.attributes().featureCode();
    }
  }

  private Facility.Hours hours() {
    ArcGisHealths.Attributes attr = gis.attributes();
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
    if (gis.attributes() == null || allBlank(gis.attributes().stationNum())) {
      return null;
    }
    return "vha_" + gis.attributes().stationNum();
  }

  private BigDecimal latitude() {
    if (gis.geometry() == null) {
      return null;
    }
    return gis.geometry().latitude();
  }

  private BigDecimal longitude() {
    if (gis.geometry() == null) {
      return null;
    }
    return gis.geometry().longitude();
  }

  private Boolean mobile() {
    if (gis.attributes() == null || allBlank(gis.attributes().mobile())) {
      return null;
    }
    return gis.attributes().mobile() == 1;
  }

  private String name() {
    return gis.attributes() == null ? null : gis.attributes().name();
  }

  private Facility.Phone phone() {
    ArcGisHealths.Attributes attr = gis.attributes();
    if (attr == null) {
      return null;
    }
    String fax = phoneTrim(attr.staFax());
    String main = phoneTrim(attr.staPhone());
    String pharmacy = phoneTrim(attr.pharmacyPhone());
    String afterHours = phoneTrim(attr.afterHoursPhone());
    String patientAdvocate = phoneTrim(attr.patientAdvocatePhone());
    String mentalHealth = phoneMentalHealth();
    String enrollmentCoordinator = phoneTrim(attr.enrollmentCoordinatorPhone());
    if (allBlank(
        fax, main, pharmacy, afterHours, patientAdvocate, mentalHealth, enrollmentCoordinator)) {
      return null;
    }
    return Facility.Phone.builder()
        .fax(fax)
        .main(main)
        .pharmacy(pharmacy)
        .afterHours(afterHours)
        .patientAdvocate(patientAdvocate)
        .mentalHealthClinic(mentalHealth)
        .enrollmentCoordinator(enrollmentCoordinator)
        .build();
  }

  private String phoneMentalHealth() {
    return gis.attributes() == null ? null : phoneTrim(mentalHealthPhoneNumbers.get(id()));
  }

  private Facility.Address physical() {
    ArcGisHealths.Attributes attr = gis.attributes();
    if (attr == null) {
      return null;
    }
    if (allBlank(
        zip(),
        attr.municipality(),
        attr.state(),
        attr.address2(),
        attr.address1(),
        attr.address3())) {
      return null;
    }
    // address1 and address2 swapped
    return Facility.Address.builder()
        .zip(zip())
        .city(attr.municipality())
        .state(upperCase(attr.state(), Locale.US))
        .address1(attr.address2())
        .address2(attr.address1())
        .address3(attr.address3())
        .build();
  }

  private Facility.Satisfaction satisfaction() {
    if (allBlank(satisfactionScores(), atpEffectiveDate())) {
      return null;
    }
    return Facility.Satisfaction.builder()
        .health(satisfactionScores())
        .effectiveDate(atpEffectiveDate())
        .build();
  }

  private BigDecimal satisfactionScore(String type) {
    return accessToPwtEntries().stream()
        .filter(atp -> equalsIgnoreCase(atp.apptTypeName(), type))
        .map(atp -> atp.shepScore())
        .min(Comparator.naturalOrder())
        .orElse(null);
  }

  private Facility.PatientSatisfaction satisfactionScores() {
    if (allBlank(
        satisfactionScore("Primary Care (Urgent)"),
        satisfactionScore("Primary Care (Routine)"),
        satisfactionScore("Specialty Care (Urgent)"),
        satisfactionScore("Specialty Care (Routine)"))) {
      return null;
    }
    return Facility.PatientSatisfaction.builder()
        .primaryCareUrgent(satisfactionScore("Primary Care (Urgent)"))
        .primaryCareRoutine(satisfactionScore("Primary Care (Routine)"))
        .specialtyCareUrgent(satisfactionScore("Specialty Care (Urgent)"))
        .specialtyCareRoutine(satisfactionScore("Specialty Care (Routine)"))
        .build();
  }

  private Facility.Services services() {
    if (allBlank(servicesHealth(), atcEffectiveDate())) {
      return null;
    }
    return Facility.Services.builder()
        .health(servicesHealth())
        .lastUpdated(atcEffectiveDate())
        .build();
  }

  private List<Facility.HealthService> servicesHealth() {
    List<Facility.HealthService> services =
        accessToCareEntries().stream()
            .map(ace -> serviceName(ace))
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));
    if (accessToCareEntries().stream().anyMatch(ace -> BooleanUtils.isTrue(ace.emergencyCare()))) {
      services.add(Facility.HealthService.EmergencyCare);
    }
    if (accessToCareEntries().stream().anyMatch(ace -> BooleanUtils.isTrue(ace.urgentCare()))) {
      services.add(Facility.HealthService.UrgentCare);
    }
    if (stopCodes().stream().anyMatch(sc -> StopCode.DENTISTRY.contains(trimToEmpty(sc.code())))) {
      services.add(Facility.HealthService.DentalServices);
    }
    if (stopCodes().stream().anyMatch(sc -> StopCode.NUTRITION.contains(trimToEmpty(sc.code())))) {
      services.add(Facility.HealthService.Nutrition);
    }
    if (stopCodes().stream().anyMatch(sc -> StopCode.PODIATRY.contains(trimToEmpty(sc.code())))) {
      services.add(Facility.HealthService.Podiatry);
    }
    Collections.sort(services, (left, right) -> left.name().compareToIgnoreCase(right.name()));
    return emptyToNull(services);
  }

  private List<StopCode> stopCodes() {
    return stopCodesMap.get(trimToEmpty(upperCase(id(), Locale.US)));
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

  private String visn() {
    return gis.attributes() == null ? null : gis.attributes().visn();
  }

  private Facility.WaitTimes waitTimes() {
    if (allBlank(waitTimesHealth(), atcEffectiveDate())) {
      return null;
    }
    return Facility.WaitTimes.builder()
        .health(waitTimesHealth())
        .effectiveDate(atcEffectiveDate())
        .build();
  }

  private List<Facility.PatientWaitTime> waitTimesHealth() {
    List<PatientWaitTime> results =
        accessToCareEntries().stream()
            .map(ace -> waitTime(ace))
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));

    Collections.sort(
        results, (left, right) -> compareIgnoreCase(left.service().name(), right.service().name()));
    return emptyToNull(results);
  }

  String website() {
    return allBlank(id()) ? null : websites.get(id());
  }

  private String zip() {
    if (gis.attributes() == null) {
      return null;
    }
    String zip = gis.attributes().zip();
    String zipPlus4 = gis.attributes().zip4();
    if (isNotBlank(zip) && isNotBlank(zipPlus4) && !zipPlus4.matches("^[0]+$")) {
      return zip + "-" + zipPlus4;
    }
    return zip;
  }
}
