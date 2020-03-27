package gov.va.api.lighthouse.facilities.tests;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.tests.categories.OpenApi;
import io.restassured.http.Method;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Ignore
@Slf4j
public class OpenApiIT {
  @Test
  @Category({OpenApi.class})
  public void openApiJson() {
    assertThat(requestOpenApi("json").statusCode()).isEqualTo(200);
  }

  @Test
  @Category({OpenApi.class})
  public void openApiYaml() {
    assertThat(requestOpenApi("yaml").statusCode()).isEqualTo(200);
  }

  private Response requestOpenApi(String extension) {
    String openApi = "/openApi" + extension;
    log.info("Making request for openApi at {}", openApi);
    return TestClients.facilities()
        .service()
        .requestSpecification()
        .request(Method.GET, TestClients.facilities().service().urlWithApiPath() + openApi);
  }
}
