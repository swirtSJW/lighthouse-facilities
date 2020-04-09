package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetadataController {
  private final BuildProperties buildProperties;

  private final String linkBase;

  @Builder
  MetadataController(
      @Autowired BuildProperties buildProperties,
      @Value("${facilities.base-path}") String basePath) {
    this.buildProperties = buildProperties;
    String strip = basePath.replaceAll("^/", "").replaceAll("/$", "");
    linkBase = strip.isEmpty() ? "/" : "/" + strip + "/";
  }

  /** Metadata. */
  @GetMapping(value = "/metadata", produces = "application/json")
  public Metadata metadata() {
    return Metadata.builder()
        .meta(
            Versions.builder()
                .versions(
                    List.of(
                        Version.builder()
                            .version(buildProperties.getVersion())
                            .internalOnly(false)
                            .status("Current Version")
                            .path(linkBase + "docs/v0/api")
                            .healthcheck(linkBase + "actuator/health")
                            .build()))
                .build())
        .build();
  }

  @Builder
  @lombok.Value
  static final class Metadata {
    Versions meta;
  }

  @Builder
  @lombok.Value
  static final class Versions {
    List<Version> versions;
  }

  @Builder
  @lombok.Value
  static final class Version {
    String version;

    @JsonProperty("internal_only")
    Boolean internalOnly;

    String status;

    String path;

    String healthcheck;
  }
}
