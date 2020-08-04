package gov.va.api.lighthouse.facilities.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exceptions that escape the rest controllers will be processed by this handler. It will convert
 * exception into different HTTP status codes and produce an error response payload.
 */
@Slf4j
@RestControllerAdvice
@RequestMapping(produces = "application/json")
public final class WebExceptionHandler {
  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleSnafu(Exception e) {
    return responseFor(e);
  }

  private ErrorResponse responseFor(Exception e) {
    ErrorResponse response = ErrorResponse.of(e);
    log.error("{}: {}", response.type(), response.message(), e);
    return response;
  }
}
