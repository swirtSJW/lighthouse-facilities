package gov.va.api.lighthouse.facilities.tests.addressvalidation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import javax.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressValidationRequest {

  @Valid private RequestAddress requestAddress;

  @Data
  @Builder
  public static final class RequestAddress {
    @JsonProperty("addressLine1")
    String address1;

    @JsonProperty("addressLine2")
    String address2;

    @JsonProperty("addressLine3")
    String address3;

    @JsonProperty("addressPOU")
    Optional<String> addressPou;

    String city;

    String internationalPostalCode;

    RequestCountry requestCountry;

    StateProvince stateProvince;

    String zipCode4;

    String zipCode5;
  }

  @Data
  @Builder
  public static final class RequestCountry {
    String countryCode;

    String countryName;
  }

  @Data
  @Builder
  public static final class StateProvince {
    String code;

    String name;
  }
}
