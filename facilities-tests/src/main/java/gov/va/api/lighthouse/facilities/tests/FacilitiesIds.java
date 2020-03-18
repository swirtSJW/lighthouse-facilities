package gov.va.api.lighthouse.facilities.tests;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class FacilitiesIds {
  @NonNull String facility;
  @NonNull String facilitiesList;
  @NonNull String latitude;
  @NonNull String longitude;
  @NonNull String bbox;
  @NonNull String zip;
  @NonNull String state;
  @NonNull String city;
  @NonNull String streetAddress;
}
