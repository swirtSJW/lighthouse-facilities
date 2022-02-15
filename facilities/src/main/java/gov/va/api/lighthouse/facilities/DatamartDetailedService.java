package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.capitalize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gov.va.api.lighthouse.facilities.deserializers.DatamartDetailedServiceDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@JsonInclude()
@JsonIgnoreProperties(
    ignoreUnknown = true,
    value = {"active"},
    allowSetters = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
  "serviceInfo",
  "description_facility",
  "appointment_leadin",
  "appointment_phones",
  "online_scheduling_available",
  "referral_required",
  "walk_ins_accepted",
  "service_locations"
})
@JsonDeserialize(using = DatamartDetailedServiceDeserializer.class)
public class DatamartDetailedService {
  @NonNull ServiceInfo serviceInfo;

  boolean active;

  @JsonIgnore String changed;

  @JsonProperty("description_facility")
  String descriptionFacility;

  @JsonProperty("appointment_leadin")
  String appointmentLeadIn;

  @JsonProperty("online_scheduling_available")
  String onlineSchedulingAvailable;

  String path;

  @JsonProperty("appointment_phones")
  List<AppointmentPhoneNumber> phoneNumbers;

  @JsonProperty("referral_required")
  String referralRequired;

  @JsonProperty("service_locations")
  List<DetailedServiceLocation> serviceLocations;

  @JsonProperty("walk_ins_accepted")
  String walkInsAccepted;

  public enum ServiceType {
    @JsonProperty("benefits")
    Benefits,
    @JsonProperty("health")
    Health,
    @JsonProperty("other")
    Other;

    /** Ensure that Jackson can create ServiceType enum regardless of capitalization. */
    @JsonCreator
    public static ServiceType fromString(String name) {
      return valueOf(capitalize(name));
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
  @JsonPropertyOrder({"name", "serviceId", "serviceType"})
  @Schema(description = "Service information.")
  public static final class ServiceInfo {
    @JsonIgnore public static final String INVALID_SVC_ID = "INVALID_ID";

    @Schema(description = "Service id.", example = "covid19Vaccine")
    @NonNull
    String serviceId;

    @Schema(description = "Service name.", example = "COVID-19 vaccines", nullable = true)
    String name;

    @Schema(description = "Service type.", example = "Health")
    @NonNull
    ServiceType serviceType;
  }

  @Data
  @Builder
  @JsonInclude()
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonPropertyOrder({
    "building_name_number",
    "clinic_name",
    "wing_floor_or_room_number",
    "address_line1",
    "address_line2",
    "city",
    "state",
    "zip_code",
    "country_code"
  })
  public static final class DetailedServiceAddress {
    @JsonProperty("address_line1")
    String address1;

    @JsonProperty("address_line2")
    String address2;

    String state;

    @JsonProperty("building_name_number")
    String buildingNameNumber;

    @JsonProperty("clinic_name")
    String clinicName;

    @JsonProperty("country_code")
    String countryCode;

    String city;

    @JsonProperty("zip_code")
    String zipCode;

    @JsonProperty("wing_floor_or_room_number")
    String wingFloorOrRoomNumber;
  }

  @Data
  @Builder
  @JsonInclude()
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class AppointmentPhoneNumber {
    String extension;

    String label;

    String number;

    String type;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonPropertyOrder({
    "service_location_address",
    "appointment_phones",
    "email_contacts",
    "facility_service_hours",
    "additional_hours_info"
  })
  public static final class DetailedServiceLocation {
    @JsonProperty("additional_hours_info")
    String additionalHoursInfo;

    @JsonProperty("email_contacts")
    List<DetailedServiceEmailContact> emailContacts;

    @JsonProperty("facility_service_hours")
    @Valid
    DetailedServiceHours facilityServiceHours;

    @JsonProperty("appointment_phones")
    List<AppointmentPhoneNumber> appointmentPhoneNumbers;

    @JsonProperty("service_location_address")
    DetailedServiceAddress serviceLocationAddress;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class DetailedServiceEmailContact {
    @JsonProperty("email_address")
    String emailAddress;

    @JsonProperty("email_label")
    String emailLabel;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonPropertyOrder({"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"})
  public static final class DetailedServiceHours {
    @JsonProperty("Monday")
    String monday;

    @JsonProperty("Tuesday")
    String tuesday;

    @JsonProperty("Wednesday")
    String wednesday;

    @JsonProperty("Thursday")
    String thursday;

    @JsonProperty("Friday")
    String friday;

    @JsonProperty("Saturday")
    String saturday;

    @JsonProperty("Sunday")
    String sunday;
  }
}
