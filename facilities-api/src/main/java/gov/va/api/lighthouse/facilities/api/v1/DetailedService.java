package gov.va.api.lighthouse.facilities.api.v1;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceAddressSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceAppointmentPhoneNumberSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceEmailContactSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceHoursSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceLocationSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Data
@Builder
@JsonIgnoreProperties(
    ignoreUnknown = true,
    value = {"active"},
    allowSetters = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = DetailedServiceSerializer.class)
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
  "name",
  "description_facility",
  "appointment_leadin",
  "appointment_phones",
  "online_scheduling_available",
  "referral_required",
  "walk_ins_accepted",
  "service_locations"
})
@Schema(description = "Detailed information of a facility service.", nullable = true)
public class DetailedService implements CanBeEmpty {
  @Schema(description = "Service name.", example = "COVID-19 vaccines", nullable = true)
  String name;

  @Schema(hidden = true)
  boolean active;

  @JsonIgnore
  @Schema(
      description = "Timestamp of last time detailed service was updated.",
      example = "2021-02-04T22:36:49+00:00",
      nullable = true)
  String changed;

  @JsonIgnore
  @Schema(description = "Deprecated until further notice.", example = "null", nullable = true)
  @JsonProperty("description_facility")
  String descriptionFacility;

  @Schema(
      description =
          "Additional appointment information. May contain html /"
              + " string formatting characters.",
      example =
          "Your VA health care team will contact you if you???re eligible to get a vaccine "
              + "during this time. As the supply of vaccine increases, we'll work with our care "
              + "teams to let Veterans know their options.",
      nullable = true)
  @JsonProperty("appointment_leadin")
  String appointmentLeadIn;

  @Schema(
      description = "String detailing online scheduling availability.",
      example = "True",
      nullable = true)
  @JsonProperty("online_scheduling_available")
  String onlineSchedulingAvailable;

  @Schema(
      description =
          "URL to a page with additional details for this service within"
              + " the associated facility's health care system.",
      example = "https://www.boston.va.gov/services/covid-19-vaccines.asp",
      nullable = true)
  String path;

  @Schema(
      description = "List of phone numbers related to scheduling appointments for this service.",
      nullable = true)
  @JsonProperty("appointment_phones")
  List<AppointmentPhoneNumber> phoneNumbers;

  @Schema(
      description = "String detailing if referrals are required for the service.",
      example = "False",
      nullable = true)
  @JsonProperty("referral_required")
  String referralRequired;

  @Schema(description = "List of service locations.", nullable = true)
  @JsonProperty("service_locations")
  List<DetailedServiceLocation> serviceLocations;

  @Schema(
      description = "String detailing if walk-ins are accepted for the service.",
      example = "True",
      nullable = true)
  @JsonProperty("walk_ins_accepted")
  String walkInsAccepted;

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return isBlank(name())
        && isBlank(changed())
        && isBlank(descriptionFacility())
        && isBlank(appointmentLeadIn())
        && isBlank(onlineSchedulingAvailable())
        && isBlank(path())
        && ObjectUtils.isEmpty(phoneNumbers())
        && isBlank(referralRequired())
        && ObjectUtils.isEmpty(serviceLocations())
        && isBlank(walkInsAccepted());
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = DetailedServiceAddressSerializer.class)
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
  @Schema(description = "Service location address.", nullable = true)
  public static final class DetailedServiceAddress implements CanBeEmpty {
    @Schema(example = "50 Irving Street, Northwest", nullable = true)
    @JsonProperty("address_line1")
    String address1;

    @Schema(nullable = true)
    @JsonProperty("address_line2")
    String address2;

    @Schema(example = "DC", nullable = true)
    String state;

    @Schema(
        description = "Building name and/or number of service.",
        example = "Baxter Building",
        nullable = true)
    @JsonProperty("building_name_number")
    String buildingNameNumber;

    @Schema(description = "Clinic name for service.", example = "Baxter Clinic", nullable = true)
    @JsonProperty("clinic_name")
    String clinicName;

    @Schema(example = "US", nullable = true)
    @JsonProperty("country_code")
    String countryCode;

    @Schema(example = "Washington", nullable = true)
    String city;

    @Schema(example = "20422-0001", nullable = true)
    @JsonProperty("zip_code")
    String zipCode;

    @Schema(
        description = "Wing, floor, or room number of service.",
        example = "Wing East",
        nullable = true)
    @JsonProperty("wing_floor_or_room_number")
    String wingFloorOrRoomNumber;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(address1())
          && isBlank(address2())
          && isBlank(state())
          && isBlank(buildingNameNumber())
          && isBlank(clinicName())
          && isBlank(countryCode())
          && isBlank(city())
          && isBlank(zipCode())
          && isBlank(wingFloorOrRoomNumber());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = DetailedServiceAppointmentPhoneNumberSerializer.class)
  @Schema(description = "Phone number information for scheduling an appointment.", nullable = true)
  public static final class AppointmentPhoneNumber implements CanBeEmpty {
    @Schema(example = "71234", nullable = true)
    String extension;

    @Schema(example = "Main phone", nullable = true)
    String label;

    @Schema(example = "937-268-6511", nullable = true)
    String number;

    @Schema(example = "tel", nullable = true)
    String type;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(extension()) && isBlank(label()) && isBlank(number()) && isBlank(type());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = DetailedServiceLocationSerializer.class)
  @JsonPropertyOrder({
    "service_location_address",
    "appointment_phones",
    "email_contacts",
    "facility_service_hours",
    "additional_hours_info"
  })
  @Schema(description = "Details for a location offering a service.", nullable = true)
  public static final class DetailedServiceLocation implements CanBeEmpty {
    @Schema(
        description = "Additional information related to service location hours.",
        example = "Location hours times may vary depending on staff availability",
        nullable = true)
    @JsonProperty("additional_hours_info")
    String additionalHoursInfo;

    @Schema(description = "List of email contact information.", nullable = true)
    @JsonProperty("email_contacts")
    List<DetailedServiceEmailContact> emailContacts;

    @Schema(nullable = true)
    @JsonProperty("facility_service_hours")
    @Valid
    DetailedServiceHours facilityServiceHours;

    @Schema(description = "List of appointment phone information.", nullable = true)
    @JsonProperty("appointment_phones")
    List<AppointmentPhoneNumber> appointmentPhoneNumbers;

    @Schema(nullable = true)
    @JsonProperty("service_location_address")
    DetailedServiceAddress serviceLocationAddress;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(additionalHoursInfo())
          && ObjectUtils.isEmpty(emailContacts())
          && (facilityServiceHours() == null || facilityServiceHours().isEmpty())
          && ObjectUtils.isEmpty(appointmentPhoneNumbers())
          && (serviceLocationAddress() == null || serviceLocationAddress().isEmpty());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = DetailedServiceEmailContactSerializer.class)
  @Schema(description = "Email contact information.", nullable = true)
  public static final class DetailedServiceEmailContact implements CanBeEmpty {
    @Schema(example = "georgea@va.gov", nullable = true)
    @JsonProperty("email_address")
    String emailAddress;

    @Schema(example = "George Anderson", nullable = true)
    @JsonProperty("email_label")
    String emailLabel;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(emailAddress()) && isBlank(emailLabel());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = DetailedServiceHoursSerializer.class)
  @JsonPropertyOrder({"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"})
  @Schema(
      description =
          "Standard hours of operation. Currently formatted as descriptive text suitable for "
              + "display, with no guarantee of a standard parseable format. "
              + "Hours of operation may vary due to holidays or other events.",
      nullable = true)
  public static final class DetailedServiceHours implements CanBeEmpty {
    @Schema(example = "9AM-5PM", nullable = true)
    @JsonProperty("Monday")
    String monday;

    @Schema(example = "9AM-5PM", nullable = true)
    @JsonProperty("Tuesday")
    String tuesday;

    @Schema(example = "9AM-5PM", nullable = true)
    @JsonProperty("Wednesday")
    String wednesday;

    @Schema(example = "9AM-5PM", nullable = true)
    @JsonProperty("Thursday")
    String thursday;

    @Schema(example = "9AM-5PM", nullable = true)
    @JsonProperty("Friday")
    String friday;

    @Schema(example = "Closed", nullable = true)
    @JsonProperty("Saturday")
    String saturday;

    @Schema(example = "Closed", nullable = true)
    @JsonProperty("Sunday")
    String sunday;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(monday())
          && isBlank(tuesday())
          && isBlank(wednesday())
          && isBlank(thursday())
          && isBlank(friday())
          && isBlank(saturday())
          && isBlank(sunday());
    }
  }
}
