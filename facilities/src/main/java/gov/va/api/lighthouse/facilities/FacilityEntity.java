package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;

import gov.va.api.lighthouse.facilities.api.v0.Facility.ServiceType;
import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@Builder
@Table(name = "facility", schema = "app")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FacilityEntity {

  /**
   * API V0 searches by {type}_{faciliityId}. We might want to change that in the future, so we are
   * keeping those two pieces of information separate. However, the two (type + facility) are
   * unique, since it is possible for a facility to have multiple types, e.g. benefits and health.
   */
  @EqualsAndHashCode.Include @EmbeddedId private Pk id;

  @Column(length = 5)
  private String zip;

  /** States are two letter abbreviations, except when they are jacked up and say "South". */
  @Column(length = 5)
  private String state;

  @Column private double latitude;

  @Column private double longitude;

  @ElementCollection(targetClass = String.class)
  @CollectionTable(
      name = "facility_services",
      schema = "app",
      joinColumns = {@JoinColumn(name = "station_number"), @JoinColumn(name = "type")})
  @Column(length = 48)
  private Set<String> services;

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @Column
  private String facility;

  @Version private Integer version;

  /** Builder alternative that allows enums to be specified instead of strings. */
  @Builder(
      builderMethodName = "typeSafeBuilder",
      builderClassName = "FacilityEntityTypeSafeBuilder")
  public FacilityEntity(
      Pk id,
      String zip,
      String state,
      double latitude,
      double longitude,
      String facility,
      Integer version,
      Set<ServiceType> servicesTypes) {
    this(
        id,
        zip,
        state,
        latitude,
        longitude,
        servicesTypes.stream().map(Object::toString).collect(Collectors.toSet()),
        facility,
        version);
  }

  /** Populate services from a type safe collection. */
  public void servicesFromServiceTypes(Set<ServiceType> serviceTypes) {
    services(serviceTypes.stream().map(Object::toString).collect(Collectors.toSet()));
  }

  public enum Type {
    /** Health facility. */
    vha,
    /** Benefits facility. */
    vba,
    /** Cemetery. */
    nca,
    /** Vet Center. */
    vc;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor(staticName = "of")
  @Embeddable
  public static class Pk implements Serializable {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "station_number", nullable = false)
    private String stationNumber;

    /** Create a Pk from the {type}_{id} style used in the Facilities API id. */
    public static Pk fromIdString(@NonNull String typeAndStationNumber) {
      int separator = typeAndStationNumber.indexOf('_');
      checkArgument(separator > 0, typeAndStationNumber);
      checkArgument(separator + 1 < typeAndStationNumber.length() - 1, typeAndStationNumber);
      String typeValue = typeAndStationNumber.substring(0, separator);
      String stationNumber = typeAndStationNumber.substring(separator + 1);
      checkArgument(!stationNumber.isBlank(), typeAndStationNumber);
      return of(Type.valueOf(typeValue), stationNumber);
    }

    /**
     * Create a Pk from the {type}_{id} style used in the Facilities API id, suppressing exceptions.
     */
    public static Optional<Pk> optionalFromIdString(@NonNull String typeAndStationNumber) {
      try {
        return Optional.ofNullable(fromIdString(typeAndStationNumber));
      } catch (Exception ex) {
        return Optional.empty();
      }
    }
  }
}
