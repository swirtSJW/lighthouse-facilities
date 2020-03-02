package gov.va.api.lighthouse.facilitiescdw;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
// @NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
// @Schema(example = "SWAGGER_EXAMPLE_COMMUNITY_CARE_ELIGIBILITY_RESPONSE")
public final class MentalHealthContactResponse {
  @Builder.Default List<Contact> contacts = new ArrayList<>();

  //  /** Lazy getter. */
  //  public List<Contact> contacts() {
  //    if (contacts == null) {
  //      contacts = new ArrayList<>();
  //    }
  //    return contacts;
  //  }

  @Value
  @Builder
  // @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Contact {
    String id;

    String region;

    String visn;

    String adminParent;

    String stationNumber;

    String mhClinicPhone;

    String mhPhone;

    String extension;

    String officialStationName;

    String pocEmail;

    String status;

    String modified;

    String created;

    String addedToOutbox;
  }
}
