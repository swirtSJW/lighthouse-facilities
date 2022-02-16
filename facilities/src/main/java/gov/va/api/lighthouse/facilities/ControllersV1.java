package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
final class ControllersV1 {
  private static final Map<String, FacilityEntity.Type> ENTITY_TYPE_LOOKUP =
      caseInsensitiveMap(
          ImmutableMap.of(
              "benefits",
              FacilityEntity.Type.vba,
              "cemetery",
              FacilityEntity.Type.nca,
              "health",
              FacilityEntity.Type.vha,
              "vet_center",
              FacilityEntity.Type.vc));

  private static final Map<String, ServiceType> SERVICE_LOOKUP =
      caseInsensitiveMap(
          Streams.stream(
                  Iterables.concat(
                      List.<ServiceType>of(Facility.HealthService.values()),
                      List.<ServiceType>of(Facility.BenefitsService.values()),
                      List.<ServiceType>of(Facility.OtherService.values())))
              .collect(toMap(v -> v.toString(), Function.identity())));

  private static <T> Map<String, T> caseInsensitiveMap(@NonNull Map<String, T> source) {
    Map<String, T> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.putAll(source);
    return Collections.unmodifiableMap(map);
  }

  static <T> List<T> page(List<T> objects, int page, int perPage) {
    checkArgument(page >= 1);
    checkArgument(perPage >= 0);
    if (perPage == 0) {
      return emptyList();
    }
    int fromIndex = (page - 1) * perPage;
    if (objects.size() < fromIndex) {
      return emptyList();
    }
    return objects.subList(fromIndex, Math.min(fromIndex + perPage, objects.size()));
  }

  static Specification<FacilityEntity> validateBoundingBox(List<BigDecimal> bbox, Specification<FacilityEntity> spec){
    if (bbox != null && bbox.size() != 4) {
      throw new ExceptionsUtils.InvalidParameter("bbox", bbox);
    }
    if(bbox == null){
      return spec;
    }
    Specification<FacilityEntity> facilitySpec = FacilityRepository.BoundingBoxSpecification.builder()
            .minLongitude(bbox.get(0).min(bbox.get(2)))
            .maxLongitude(bbox.get(0).max(bbox.get(2)))
            .minLatitude(bbox.get(1).min(bbox.get(3)))
            .maxLatitude(bbox.get(1).max(bbox.get(3))).build();
    return spec == null ? facilitySpec : spec.and(facilitySpec);
  }

  static FacilityEntity.Type validateFacilityType(String type) {
    FacilityEntity.Type mapped = ENTITY_TYPE_LOOKUP.get(trimToEmpty(type));
    if (mapped == null && isNotBlank(type)) {
      throw new ExceptionsUtils.InvalidParameter("type", type);
    }
    return mapped;
  }

  static Specification<FacilityEntity> validateFacilityType(String type, Specification<FacilityEntity> spec){
    FacilityEntity.Type validatedType = validateFacilityType(type);
    if(validatedType == null){
      return spec;
    }
    Specification<FacilityEntity> facilitySpec = FacilityRepository.FacilityTypeSpecification.builder().facilityType(validatedType).build();
    return spec == null ? facilitySpec : spec.and(facilitySpec);
  }

  static Specification<FacilityEntity> validateIds(String ids, Specification<FacilityEntity> spec) {
    List<FacilityEntity.Pk> validIds;
    try {
      validIds = FacilityUtils.entityIds(ids);
    } catch (Exception e) {
      throw new ExceptionsUtils.InvalidParameter("ids", ids);
    }
    if(validIds.isEmpty()){
      return spec;
    }
    Specification<FacilityEntity> facilitySpec = FacilityRepository.TypeServicesIdsSpecification.builder().ids(validIds).build();
    return spec == null ? facilitySpec : spec.and(facilitySpec);
  }



  static void validateLatLong(
      BigDecimal latitude, BigDecimal longitude, BigDecimal radius) {
    if (latitude == null && longitude != null) {
      throw new ExceptionsUtils.ParameterInvalidWithoutOthers("longitude", "latitude");
    }
    if (longitude == null && latitude != null) {
      throw new ExceptionsUtils.ParameterInvalidWithoutOthers("latitude", "longitude");
    }
    if (latitude == null && longitude == null && radius != null) {
      throw new ExceptionsUtils.ParameterInvalidWithoutOthers("radius", "latitude, longitude");
    }
    if (radius != null && radius.compareTo(BigDecimal.ZERO) < 0) {
      throw new ExceptionsUtils.InvalidParameter("radius", radius);
    }
  }

  static Specification<FacilityEntity> validateMobile(Boolean rawMobile, Specification<FacilityEntity> spec){
    if(rawMobile == null){
      return spec;
    }
    Specification<FacilityEntity> facilitySpec = FacilityRepository.MobileSpecification.builder().mobile(rawMobile).build();
    //return spec == null ? facilitySpec : spec.and(facilitySpec);
    var x = spec.and(facilitySpec);
    return x;
  }

  static Specification<FacilityEntity> validateServices(Collection<String> rawServices, Specification<FacilityEntity> spec){
    Set<ServiceType> services = validateServices(rawServices);
    if(services.isEmpty()){
      return spec;
    }
    Specification<FacilityEntity> facilitySpec = FacilityRepository.ServicesSpecification.builder().services(services).build();
    return spec == null ? facilitySpec : spec.and(facilitySpec);
  }

  static Specification<FacilityEntity> validateState(String rawState, Specification<FacilityEntity> spec){
    //Add logic to validate state
    if(rawState == null){
      return spec;
    }
    Specification<FacilityEntity> facilitySpec = FacilityRepository.StateSpecification.builder().state(rawState).build();
    return spec == null ? facilitySpec : spec.and(facilitySpec);
  }

  static Specification<FacilityEntity> validateZip(String zip, Specification<FacilityEntity> spec){
    //Add logic to validate zip
    if(zip == null){
      return spec;
    }
    Specification<FacilityEntity> facilitySpec = FacilityRepository.ZipSpecification.builder().zip(zip).build();
    return spec == null ? facilitySpec : spec.and(facilitySpec);
  }

  static Specification<FacilityEntity> validateVisn(String visn, Specification<FacilityEntity> spec){
    //Add logic to validate visn
    if(visn == null){
      return spec;
    }
    Specification<FacilityEntity> facilitySpec = FacilityRepository.VisnSpecification.builder().visn(visn).build();
    return spec == null ? facilitySpec : spec.and(facilitySpec);
  }

  static Set<ServiceType> validateServices(Collection<String> services) {
    if (isEmpty(services)) {
      return emptySet();
    }
    List<ServiceType> results = new ArrayList<>(services.size());
    for (String service : services) {
      ServiceType mapped = SERVICE_LOOKUP.get(trimToEmpty(service));
      if (mapped == null) {
        throw new ExceptionsUtils.InvalidParameter("services", service);
      }
      results.add(mapped);
    }
    return ImmutableSet.copyOf(results);
  }
}
