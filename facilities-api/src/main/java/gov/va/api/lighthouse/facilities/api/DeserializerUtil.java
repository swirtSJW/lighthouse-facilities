package gov.va.api.lighthouse.facilities.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeserializerUtil {
  /** Obtain active status node using snake_case or camelCase notation. */
  public static JsonNode getActiveStatus(@NonNull JsonNode node) {
    JsonNode activeStatusNode = node.get("active_status");
    if (isNull(activeStatusNode)) {
      activeStatusNode = node.get("activeStatus");
    }
    return activeStatusNode;
  }

  /** Obtain additional hours info node using snake_case or camelCase notation. */
  public static JsonNode getAdditionalHoursInfo(@NonNull JsonNode node) {
    JsonNode additionalHoursInfoNode = node.get("additional_hours_info");
    if (isNull(additionalHoursInfoNode)) {
      additionalHoursInfoNode = node.get("additionalHoursInfo");
    }
    return additionalHoursInfoNode;
  }

  /** Obtain additional info node using snake_case or camelCase notation. */
  public static JsonNode getAdditionalInfo(@NonNull JsonNode node) {
    JsonNode additionalInfoNode = node.get("additional_info");
    if (isNull(additionalInfoNode)) {
      additionalInfoNode = node.get("additionalInfo");
    }
    return additionalInfoNode;
  }

  /** Obtain address 1 node using snake_case or camelCase notation. */
  public static JsonNode getAddress1(@NonNull JsonNode node) {
    JsonNode address1Node = node.get("address_1");
    if (isNull(address1Node)) {
      address1Node = node.get("address1");
    }
    return address1Node;
  }

  /** Obtain address 2 node using snake_case or camelCase notation. */
  public static JsonNode getAddress2(@NonNull JsonNode node) {
    JsonNode address2Node = node.get("address_2");
    if (isNull(address2Node)) {
      address2Node = node.get("address2");
    }
    return address2Node;
  }

  /** Obtain address 3 node using snake_case or camelCase notation. */
  public static JsonNode getAddress3(@NonNull JsonNode node) {
    JsonNode address3Node = node.get("address_3");
    if (isNull(address3Node)) {
      address3Node = node.get("address3");
    }
    return address3Node;
  }

  /** Obtain address line 1 node using snake_case or camelCase notation. */
  public static JsonNode getAddressLine1(@NonNull JsonNode node) {
    JsonNode addressLine1Node = node.get("address_line1");
    if (isNull(addressLine1Node)) {
      addressLine1Node = node.get("addressLine1");
    }
    return addressLine1Node;
  }

  /** Obtain address line 2 node using snake_case or camelCase notation. */
  public static JsonNode getAddressLine2(@NonNull JsonNode node) {
    JsonNode addressLine2Node = node.get("address_line2");
    if (isNull(addressLine2Node)) {
      addressLine2Node = node.get("addressLine2");
    }
    return addressLine2Node;
  }

  /** Obtain after hours node using snake_case or camelCase notation. */
  public static JsonNode getAfterHours(@NonNull JsonNode node) {
    JsonNode afterHoursNode = node.get("after_hours");
    if (isNull(afterHoursNode)) {
      afterHoursNode = node.get("afterHours");
    }
    return afterHoursNode;
  }

  /** Obtain appointment leadin node using snake_case or camelCase notation. */
  public static JsonNode getAppointmentLeadin(@NonNull JsonNode node) {
    JsonNode appointmentLeadInNode = node.get("appointment_leadin");
    if (isNull(appointmentLeadInNode)) {
      appointmentLeadInNode = node.get("appointmentLeadIn");
    }
    if (isNull(appointmentLeadInNode)) {
      appointmentLeadInNode = node.get("appointmentLeadin");
    }
    return appointmentLeadInNode;
  }

  /** Obtain building name number node using snake_case or camelCase notation. */
  public static JsonNode getBuildingNameNumber(@NonNull JsonNode node) {
    JsonNode buildingNameNumberNode = node.get("building_name_number");
    if (isNull(buildingNameNumberNode)) {
      buildingNameNumberNode = node.get("buildingNameNumber");
    }
    return buildingNameNumberNode;
  }

  /** Obtain clinic name node using snake_case or camelCase notation. */
  public static JsonNode getClinicName(@NonNull JsonNode node) {
    JsonNode clinicNameNode = node.get("clinic_name");
    if (isNull(clinicNameNode)) {
      clinicNameNode = node.get("clinicName");
    }
    return clinicNameNode;
  }

  /** Obtain country code node using snake_case or camelCase notation. */
  public static JsonNode getCountryCode(@NonNull JsonNode node) {
    JsonNode countryCodeNode = node.get("country_code");
    if (isNull(countryCodeNode)) {
      countryCodeNode = node.get("countryCode");
    }
    return countryCodeNode;
  }

  /** Obtain detailed services node using snake_case or camelCase notation. */
  public static JsonNode getDetailedServices(@NonNull JsonNode node) {
    JsonNode detailedServicesNode = node.get("detailed_services");
    if (isNull(detailedServicesNode)) {
      detailedServicesNode = node.get("detailedServices");
    }
    return detailedServicesNode;
  }

  /** Obtain effective date node using snake_case or camelCase notation. */
  public static JsonNode getEffectiveDate(@NonNull JsonNode node) {
    JsonNode effectiveDateNode = node.get("effective_date");
    if (isNull(effectiveDateNode)) {
      effectiveDateNode = node.get("effectiveDate");
    }
    return effectiveDateNode;
  }

  /** Obtain email address node using snake_case or camelCase notation. */
  public static JsonNode getEmailAddress(@NonNull JsonNode node) {
    JsonNode emailAddressNode = node.get("email_address");
    if (isNull(emailAddressNode)) {
      emailAddressNode = node.get("emailAddress");
    }
    return emailAddressNode;
  }

  /** Obtain email contacts node using snake_case or camelCase notation. */
  public static JsonNode getEmailContacts(@NonNull JsonNode node) {
    JsonNode emailContactsNode = node.get("email_contacts");
    if (isNull(emailContactsNode)) {
      emailContactsNode = node.get("emailContacts");
    }
    return emailContactsNode;
  }

  /** Obtain email label node using snake_case or camelCase notation. */
  public static JsonNode getEmailLabel(@NonNull JsonNode node) {
    JsonNode emailLabelNode = node.get("email_label");
    if (isNull(emailLabelNode)) {
      emailLabelNode = node.get("emailLabel");
    }
    return emailLabelNode;
  }

  /** Obtain enrollment coordinator node using snake_case or camelCase notation. */
  public static JsonNode getEnrollmentCoordinator(@NonNull JsonNode node) {
    JsonNode enrollmentCoordinatorNode = node.get("enrollment_coordinator");
    if (isNull(enrollmentCoordinatorNode)) {
      enrollmentCoordinatorNode = node.get("enrollmentCoordinator");
    }
    return enrollmentCoordinatorNode;
  }

  /** Obtain description facility node using snake_case or camelCase notation. */
  public static JsonNode getFacilityDescription(@NonNull JsonNode node) {
    JsonNode descriptionFacilityNode = node.get("description_facility");
    if (isNull(descriptionFacilityNode)) {
      descriptionFacilityNode = node.get("descriptionFacility");
    }
    return descriptionFacilityNode;
  }

  /** Obtain facility service hours node using snake_case or camelCase notation. */
  public static JsonNode getFacilityServiceHours(@NonNull JsonNode node) {
    JsonNode facilityServiceHoursNode = node.get("facility_service_hours");
    if (isNull(facilityServiceHoursNode)) {
      facilityServiceHoursNode = node.get("facilityServiceHours");
    }
    return facilityServiceHoursNode;
  }

  /** Obtain facility type node using snake_case or camelCase notation. */
  public static JsonNode getFacilityType(@NonNull JsonNode node) {
    JsonNode facilityTypeNode = node.get("facility_type");
    if (isNull(facilityTypeNode)) {
      facilityTypeNode = node.get("facilityType");
    }
    return facilityTypeNode;
  }

  /** Obtain friday hours node using snake_case or camelCase notation. */
  public static JsonNode getFridayHours(@NonNull JsonNode node) {
    JsonNode fridayHoursNode = node.get("Friday");
    if (isNull(fridayHoursNode)) {
      fridayHoursNode = node.get("friday");
    }
    return fridayHoursNode;
  }

  /** Obtain last updated node using snake_case or camelCase notation. */
  public static JsonNode getLastUpdated(@NonNull JsonNode node) {
    JsonNode lastUpdatedNode = node.get("last_updated");
    if (isNull(lastUpdatedNode)) {
      lastUpdatedNode = node.get("lastUpdated");
    }
    return lastUpdatedNode;
  }

  /** Obtain mental health clinic node using snake_case or camelCase notation. */
  public static JsonNode getMentalHealthClinic(@NonNull JsonNode node) {
    JsonNode mentalHealthClinicNode = node.get("mental_health_clinic");
    if (isNull(mentalHealthClinicNode)) {
      mentalHealthClinicNode = node.get("mentalHealthClinic");
    }
    return mentalHealthClinicNode;
  }

  /** Obtain monday hours node using snake_case or camelCase notation. */
  public static JsonNode getMondayHours(@NonNull JsonNode node) {
    JsonNode mondayHoursNode = node.get("Monday");
    if (isNull(mondayHoursNode)) {
      mondayHoursNode = node.get("monday");
    }
    return mondayHoursNode;
  }

  /** Obtain online scheduling available node using snake_case or camelCase notation. */
  public static JsonNode getOnlineSchedulingAvailable(@NonNull JsonNode node) {
    JsonNode onlineSchedulingAvailableNode = node.get("online_scheduling_available");
    if (isNull(onlineSchedulingAvailableNode)) {
      onlineSchedulingAvailableNode = node.get("onlineSchedulingAvailable");
    }
    return onlineSchedulingAvailableNode;
  }

  /** Obtain operational hours special instructions node using snake_case or camelCase notation. */
  public static JsonNode getOperationalHoursSpecialInstructions(@NonNull JsonNode node) {
    JsonNode operationalHoursSpecialInstructionsNode =
        node.get("operational_hours_special_instructions");
    if (isNull(operationalHoursSpecialInstructionsNode)) {
      operationalHoursSpecialInstructionsNode = node.get("operationalHoursSpecialInstructions");
    }
    return operationalHoursSpecialInstructionsNode;
  }

  /** Obtain operating status node using snake_case or camelCase notation. */
  public static JsonNode getOpertingStatus(@NonNull JsonNode node) {
    JsonNode operatingStatusNode = node.get("operating_status");
    if (isNull(operatingStatusNode)) {
      operatingStatusNode = node.get("operatingStatus");
    }
    return operatingStatusNode;
  }

  /** Obtain patient advocate node using snake_case or camelCase notation. */
  public static JsonNode getPatientAdvocate(@NonNull JsonNode node) {
    JsonNode patientAdvocateNode = node.get("patient_advocate");
    if (isNull(patientAdvocateNode)) {
      patientAdvocateNode = node.get("patientAdvocate");
    }
    return patientAdvocateNode;
  }

  /** Obtain phone numbers node using snake_case or camelCase notation. */
  public static JsonNode getPhoneNumbers(@NonNull JsonNode node) {
    JsonNode phoneNumbersNode = node.get("appointment_phones");
    if (isNull(phoneNumbersNode)) {
      phoneNumbersNode = node.get("appointmentPhones");
    }
    return phoneNumbersNode;
  }

  /** Obtain primary care routine node using snake_case or camelCase notation. */
  public static JsonNode getPrimaryCareRoutine(@NonNull JsonNode node) {
    JsonNode primaryCareRoutineNode = node.get("primary_care_routine");
    if (isNull(primaryCareRoutineNode)) {
      primaryCareRoutineNode = node.get("primaryCareRoutine");
    }
    return primaryCareRoutineNode;
  }

  /** Obtain primary care urgent node using snake_case or camelCase notation. */
  public static JsonNode getPrimaryCareUrgent(@NonNull JsonNode node) {
    JsonNode primaryCareUrgentNode = node.get("primary_care_urgent");
    if (isNull(primaryCareUrgentNode)) {
      primaryCareUrgentNode = node.get("primaryCareUrgent");
    }
    return primaryCareUrgentNode;
  }

  /** Obtain referral required node using snake_case or camelCase notation. */
  public static JsonNode getReferralRequired(@NonNull JsonNode node) {
    JsonNode referralRequiredNode = node.get("referral_required");
    if (isNull(referralRequiredNode)) {
      referralRequiredNode = node.get("referralRequired");
    }
    return referralRequiredNode;
  }

  /** Obtain saturday hours node using snake_case or camelCase notation. */
  public static JsonNode getSaturdayHours(@NonNull JsonNode node) {
    JsonNode saturdayHoursNode = node.get("Saturday");
    if (isNull(saturdayHoursNode)) {
      saturdayHoursNode = node.get("saturday");
    }
    return saturdayHoursNode;
  }

  /** Obtain service location address node using snake_case or camelCase notation. */
  public static JsonNode getServiceLocationAddress(@NonNull JsonNode node) {
    JsonNode serviceLocationAddressNode = node.get("service_location_address");
    if (isNull(serviceLocationAddressNode)) {
      serviceLocationAddressNode = node.get("serviceLocationAddress");
    }
    return serviceLocationAddressNode;
  }

  /** Obtain service locations node using snake_case or camelCase notation. */
  public static JsonNode getServiceLocations(@NonNull JsonNode node) {
    JsonNode serviceLocationsNode = node.get("service_locations");
    if (isNull(serviceLocationsNode)) {
      serviceLocationsNode = node.get("serviceLocations");
    }
    return serviceLocationsNode;
  }

  /** Obtain specialty care routine node using snake_case or camelCase notation. */
  public static JsonNode getSpecialtyCareRoutine(@NonNull JsonNode node) {
    JsonNode specialtyCareRoutineNode = node.get("specialty_care_routine");
    if (isNull(specialtyCareRoutineNode)) {
      specialtyCareRoutineNode = node.get("specialtyCareRoutine");
    }
    return specialtyCareRoutineNode;
  }

  /** Obtain specialty care urgent node using snake_case or camelCase notation. */
  public static JsonNode getSpecialtyCareUrgent(@NonNull JsonNode node) {
    JsonNode specialtyCareUrgentNode = node.get("specialty_care_urgent");
    if (isNull(specialtyCareUrgentNode)) {
      specialtyCareUrgentNode = node.get("specialtyCareUrgent");
    }
    return specialtyCareUrgentNode;
  }

  /** Obtain sunday hours node using snake_case or camelCase notation. */
  public static JsonNode getSundayHours(@NonNull JsonNode node) {
    JsonNode sundayHoursNode = node.get("Sunday");
    if (isNull(sundayHoursNode)) {
      sundayHoursNode = node.get("sunday");
    }
    return sundayHoursNode;
  }

  /** Obtain thursday hours node using snake_case or camelCase notation. */
  public static JsonNode getThursdayHours(@NonNull JsonNode node) {
    JsonNode thursdayHoursNode = node.get("Thursday");
    if (isNull(thursdayHoursNode)) {
      thursdayHoursNode = node.get("thursday");
    }
    return thursdayHoursNode;
  }

  /** Obtain time zone node using snake_case or camelCase notation. */
  public static JsonNode getTimeZone(@NonNull JsonNode node) {
    JsonNode timeZoneNode = node.get("time_zone");
    if (isNull(timeZoneNode)) {
      timeZoneNode = node.get("timeZone");
    }
    return timeZoneNode;
  }

  /** Obtain tuesday hours node using snake_case or camelCase notation. */
  public static JsonNode getTuesdayHours(@NonNull JsonNode node) {
    JsonNode tuesdayHoursNode = node.get("Tuesday");
    if (isNull(tuesdayHoursNode)) {
      tuesdayHoursNode = node.get("tuesday");
    }
    return tuesdayHoursNode;
  }

  /** Obtain wait times node using snake_case or camelCase notation. */
  public static JsonNode getWaitTimes(@NonNull JsonNode node) {
    JsonNode waitTimesNode = node.get("wait_times");
    if (isNull(waitTimesNode)) {
      waitTimesNode = node.get("waitTimes");
    }
    return waitTimesNode;
  }

  /** Obtain walk ins accepted node using snake_case or camelCase notation. */
  public static JsonNode getWalkInsAccepted(@NonNull JsonNode node) {
    JsonNode walkInsAcceptedNode = node.get("walk_ins_accepted");
    if (isNull(walkInsAcceptedNode)) {
      walkInsAcceptedNode = node.get("walkInsAccepted");
    }
    return walkInsAcceptedNode;
  }

  /** Obtain wednesday hours node using snake_case or camelCase notation. */
  public static JsonNode getWednesdayHours(@NonNull JsonNode node) {
    JsonNode wednesdayHoursNode = node.get("Wednesday");
    if (isNull(wednesdayHoursNode)) {
      wednesdayHoursNode = node.get("wednesday");
    }
    return wednesdayHoursNode;
  }

  /** Obtain wing, floor, or room number node using snake_case or camelCase notation. */
  public static JsonNode getWingFloorOrRoomNumber(@NonNull JsonNode node) {
    JsonNode wingFloorOrRoomNumberNode = node.get("wing_floor_or_room_number");
    if (isNull(wingFloorOrRoomNumberNode)) {
      wingFloorOrRoomNumberNode = node.get("wingFloorOrRoomNumber");
    }
    return wingFloorOrRoomNumberNode;
  }

  /** Obtain zip code node using snake_case or camelCase notation. */
  public static JsonNode getZipCode(@NonNull JsonNode node) {
    JsonNode zipCodeNode = node.get("zip_code");
    if (isNull(zipCodeNode)) {
      zipCodeNode = node.get("zipCode");
    }
    return zipCodeNode;
  }

  private static boolean isNull(JsonNode node) {
    return node == null || node.isNull();
  }
}
