package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import java.util.List;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

public class WebExceptionHandlerV0Test {
  private static HttpHeaders jsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  @Test
  public void notAcceptable() {
    assertThat(
            new WebExceptionHandlerV0()
                .handleNotAcceptable(
                    new HttpMediaTypeNotAcceptableException(List.of(MediaType.ALL))))
        .isEqualTo(
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .headers(jsonHeaders())
                .body(
                    ApiError.builder()
                        .errors(
                            List.of(
                                ApiError.ErrorMessage.builder()
                                    .title("Not acceptable")
                                    .detail(
                                        "The resource could not be returned in the requested format")
                                    .code("406")
                                    .status("406")
                                    .build()))
                        .build()));
  }

  @Test
  public void notFound() {
    assertThat(new WebExceptionHandlerV0().handleNotFound(new ExceptionsV0.NotFound("vha_555")))
        .isEqualTo(
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .headers(jsonHeaders())
                .body(
                    ApiError.builder()
                        .errors(
                            List.of(
                                ApiError.ErrorMessage.builder()
                                    .title("Record not found")
                                    .detail("The record identified by vha_555 could not be found")
                                    .code("404")
                                    .status("404")
                                    .build()))
                        .build()));
  }
}
