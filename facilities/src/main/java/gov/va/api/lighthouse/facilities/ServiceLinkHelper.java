package gov.va.api.lighthouse.facilities;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ServiceLinkHelper {
  @Value("${facilities.url}")
  private String baseUrl = "http://base.url";

  @Value("${facilities.base-path}")
  private String basePath = "/base/path";
}
