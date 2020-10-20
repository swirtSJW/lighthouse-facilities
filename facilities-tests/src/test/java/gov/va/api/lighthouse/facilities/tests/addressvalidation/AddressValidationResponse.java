package gov.va.api.lighthouse.facilities.tests.addressvalidation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressValidationResponse {

  private Address address;

  private AddressMetaData addressMetaData;

  private Geocode geocode;

  private List<Messages> messages;

  private String usCongressionalDistrict;

  @Data
  @Builder
  public static final class Messages {
    String code;

    String key;

    boolean potentiallySelfCorrectingOnRetry;

    String severity;

    String text;
  }

  @Data
  @Builder
  public static final class AddressMetaData {
    String addressType;

    double confidenceScore;

    String deliveryPointValidation;

    List<String> nonPostalInputData;

    String residentialDeliveryIndicator;

    int validationKey;
  }

  @Data
  @Builder
  public static final class Geocode {
    String calcDate;

    double latitude;

    double locationPrecision;

    double longitude;
  }

  @Data
  @Builder
  public static final class Address {
    @JsonProperty("addressLine1")
    String address1;

    @JsonProperty("addressLine2")
    String address2;

    @JsonProperty("addressLine3")
    String address3;

    String city;

    Country country;

    County county;

    String internationalPostalCode;

    StateProvince stateProvince;

    String zipCode4;

    String zipCode5;
  }

  @Data
  @Builder
  public static final class Country {
    String code;

    String fipsCode;

    String iso2Code;

    String iso3Code;

    String name;
  }

  @Data
  @Builder
  public static final class County {
    String countyFipsCode;

    String name;
  }

  @Data
  @Builder
  public static final class StateProvince {
    String code;

    String name;
  }
}
