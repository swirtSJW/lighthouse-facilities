package gov.va.api.lighthouse.facilitiescollector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JacksonXmlRootElement(localName = "cems")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class StateCemeteries {
  @JacksonXmlElementWrapper(useWrapping = false)
  public List<StateCemetery> cem;

  public List<StateCemetery> cem() {
    if (cem == null) {
      cem = new ArrayList<>();
    }
    return cem;
  }

  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class StateCemetery {
    @JacksonXmlProperty(isAttribute = true, localName = "fac_id")
    String facId;

    @JacksonXmlProperty(isAttribute = true, localName = "statecode")
    String stateCode;

    @JacksonXmlProperty(isAttribute = true, localName = "cem_name")
    String cemName;

    @JacksonXmlProperty(isAttribute = true, localName = "cem_url")
    String cemUrl;

    @JacksonXmlProperty(isAttribute = true, localName = "address_line1")
    String addressLine1;

    @JacksonXmlProperty(isAttribute = true, localName = "address_line2")
    String addressLine2;

    @JacksonXmlProperty(isAttribute = true, localName = "address_line3")
    String addressLine3;

    @JacksonXmlProperty(isAttribute = true, localName = "mailing_line1")
    String mailingLine1;

    @JacksonXmlProperty(isAttribute = true, localName = "mailing_line2")
    String mailingLine2;

    @JacksonXmlProperty(isAttribute = true, localName = "mailing_line3")
    String mailingLine3;

    @JacksonXmlProperty(isAttribute = true)
    String phone;

    @JacksonXmlProperty(isAttribute = true)
    String fax;

    @JacksonXmlProperty(isAttribute = true, localName = "lat")
    String latitude;

    @JacksonXmlProperty(isAttribute = true, localName = "long")
    String longitude;

    // Unused fields:
    // contact1
    // contact2
    // email
    // funded
    // state
    // stationid
  }
}
