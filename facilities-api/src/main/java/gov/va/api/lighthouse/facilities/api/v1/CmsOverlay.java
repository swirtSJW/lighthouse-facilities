package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "Data provided by CMS to Facilities to be applied on top of known data.")
public class CmsOverlay {
  @Valid Facility.OperatingStatus operatingStatus;

  List<@Valid DetailedService> detailedServices;
}
