package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;
import org.junit.Test;
import org.springframework.boot.info.BuildProperties;

public class MetadataControllerTest {
  @Test
  public void metadata() {
    Properties properties = new Properties();
    properties.put("version", "3.14");
    assertThat(
            MetadataController.builder()
                .buildProperties(new BuildProperties(properties))
                .basePath("/bp")
                .build()
                .metadata())
        .isEqualTo(
            MetadataController.Metadata.builder()
                .meta(
                    MetadataController.Versions.builder()
                        .versions(
                            List.of(
                                MetadataController.Version.builder()
                                    .version("3.14")
                                    .internalOnly(false)
                                    .status("Current Version")
                                    .path("/bp/docs/v0/api")
                                    .healthcheck("/bp/actuator/health")
                                    .build()))
                        .build())
                .build());
  }
}
