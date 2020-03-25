package gov.va.api.lighthouse.facilities;

import com.google.common.collect.Iterables;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public final class WebExceptionHandlerV0 {
  @SneakyThrows
  private static ResponseEntity<ApiError> response(
      HttpStatus status, Throwable tr, ApiError error) {
    log.error("Response {}", JacksonConfig.createMapper().writeValueAsString(error), tr);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return ResponseEntity.status(status).headers(headers).body(error);
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  ResponseEntity<ApiError> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
    ApiError error =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Not acceptable")
                        .detail("The resource could not be returned in the requested format")
                        .code("406")
                        .status("406")
                        .build()))
            .build();
    return response(HttpStatus.NOT_ACCEPTABLE, ex, error);
  }

  @ExceptionHandler(ExceptionsV0.NotFound.class)
  ResponseEntity<ApiError> handleNotFound(ExceptionsV0.NotFound ex) {
    ApiError error =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Record not found")
                        .detail(ex.getMessage())
                        .code("404")
                        .status("404")
                        .build()))
            .build();
    return response(HttpStatus.NOT_FOUND, ex, error);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<ApiError> handleValidationException(ConstraintViolationException ex) {
    String violations =
        ex.getConstraintViolations().stream()
            .map(v -> Iterables.getLast(v.getPropertyPath()) + " " + v.getMessage())
            .collect(Collectors.joining(", "));
    ApiError response =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Invalid field value")
                        .detail(violations)
                        .code("400")
                        .status("400")
                        .build()))
            .build();
    return response(HttpStatus.BAD_REQUEST, ex, response);
  }
}
