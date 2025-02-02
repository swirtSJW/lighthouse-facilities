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
  "descriptionFacility",
  "appointmentLeadIn",
  "appointmentPhones",
  "onlineSchedulingAvailable",
  "referralRequired",
  "walkInsAccepted",
  "serviceLocations"
})
@Schema(description = "Detailed information of a facility service.", nullable = true)
public class DetailedService implements CanBeEmpty {
  @Schema(description = "Service identifier.", example = "covid19Vaccine", nullable = true)
  String serviceId;

  @Schema(description = "Service name.", example = "COVID-19 vaccines", nullable = true)
  String name;

  @Schema(hidden = true)
  boolean active;

  @JsonIgnore
  @Schema(
      description = "Timestamp of last time service was updated.",
      example = "2021-02-04T22:36:49+00:00",
      nullable = true)
  String changed;

  @JsonIgnore
  @Schema(description = "Deprecated until further notice.", example = "null", nullable = true)
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
  String appointmentLeadIn;

  @Schema(
      description = "String detailing online scheduling availability.",
      example = "True",
      nullable = true)
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
  @JsonProperty("appointmentPhones")
  List<AppointmentPhoneNumber> phoneNumbers;

  @Schema(
      description = "String detailing if referrals are required for the service.",
      example = "False",
      nullable = true)
  String referralRequired;

  @Schema(description = "List of service locations.", nullable = true)
  List<DetailedServiceLocation> serviceLocations;

  @Schema(
      description = "String detailing if walk-ins are accepted for the service.",
      example = "True",
      nullable = true)
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
    "buildingNameNumber",
    "clinicName",
    "wingFloorOrRoomNumber",
    "addressLine1",
    "addressLine2",
    "city",
    "state",
    "zipCode",
    "countryCode"
  })
  @Schema(description = "Service location address.", nullable = true)
  public static final class DetailedServiceAddress implements CanBeEmpty {
    @Schema(
        description = "Street name and number.",
        example = "50 Irving Street, Northwest",
        nullable = true)
    @JsonProperty("addressLine1")
    String address1;

    @Schema(description = "Building number.", example = "Bldg 2", nullable = true)
    @JsonProperty("addressLine2")
    String address2;

    @Schema(description = "State code.", example = "DC", nullable = true)
    String state;

    @Schema(
        description = "Building name and/or number of service.",
        example = "Baxter Building",
        nullable = true)
    String buildingNameNumber;

    @Schema(description = "Clinic name for service.", example = "Baxter Clinic", nullable = true)
    String clinicName;

    @Schema(description = "Country code.", example = "US", nullable = true)
    String countryCode;

    @Schema(description = "City name.", example = "Washington", nullable = true)
    String city;

    @Schema(description = "Postal (ZIP) code.", example = "20422-0001", nullable = true)
    String zipCode;

    @Schema(
        description = "Wing, floor, or room number of service.",
        example = "Wing East",
        nullable = true)
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
    @Schema(description = "Appointment phone number extension.", example = "71234", nullable = true)
    String extension;

    @Schema(
        description =
            "Appointment phone number label (e.g. 'Main phone', 'Appointment phone', etc).",
        example = "Main phone",
        nullable = true)
    String label;

    @Schema(description = "Appointment phone number.", example = "937-268-6511", nullable = true)
    String number;

    @Schema(
        description = "Appointment contact number type (e.g. 'tel', 'fax', etc)",
        example = "tel",
        nullable = true)
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
    "serviceLocationAddress",
    "appointmentPhones",
    "emailContacts",
    "facilityServiceHours",
    "additionalHoursInfo"
  })
  @Schema(description = "Details for a location offering a service.", nullable = true)
  public static final class DetailedServiceLocation implements CanBeEmpty {
    @Schema(
        description = "Additional information related to service location hours.",
        example = "Location hours times may vary depending on staff availability",
        nullable = true)
    String additionalHoursInfo;

    @Schema(
        description = "List of email contact information regarding facility services.",
        nullable = true)
    List<DetailedServiceEmailContact> emailContacts;

    @Schema(nullable = true)
    @Valid
    DetailedServiceHours facilityServiceHours;

    @Schema(description = "List of appointment phone information.", nullable = true)
    @JsonProperty("appointmentPhones")
    List<AppointmentPhoneNumber> appointmentPhoneNumbers;

    @Schema(nullable = true)
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
  @Schema(description = "Email contact information for facility service.", nullable = true)
  public static final class DetailedServiceEmailContact implements CanBeEmpty {
    @Schema(
        description = "Email address for facility service contact.",
        example = "georgea@va.gov",
        nullable = true)
    String emailAddress;

    @Schema(description = "Email address label.", example = "George Anderson", nullable = true)
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
  @JsonPropertyOrder({"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"})
  @Schema(
      description =
          "Standard hours of operation. Currently formatted as descriptive text suitable for "
              + "display, with no guarantee of a standard parseable format. "
              + "Hours of operation may vary due to holidays or other events.",
      nullable = true)
  public static final class DetailedServiceHours implements CanBeEmpty {
    @Schema(description = "Service availability on Mondays.", example = "9AM-5PM", nullable = true)
    String monday;

    @Schema(description = "Service availability on Tuesdays.", example = "9AM-5PM", nullable = true)
    String tuesday;

    @Schema(
        description = "Service availability on Wednesdays.",
        example = "9AM-5PM",
        nullable = true)
    String wednesday;

    @Schema(
        description = "Service availability on Thursdays.",
        example = "9AM-5PM",
        nullable = true)
    String thursday;

    @Schema(description = "Service availability on Fridays.", example = "9AM-5PM", nullable = true)
    String friday;

    @Schema(description = "Service availability on Saturdays.", example = "Closed", nullable = true)
    String saturday;

    @Schema(description = "Service availability on Sundays.", example = "Closed", nullable = true)
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
