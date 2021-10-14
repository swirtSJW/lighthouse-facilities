package gov.va.api.lighthouse.facilities;

import lombok.experimental.UtilityClass;

@UtilityClass
final class ExceptionsUtilsV0 {
  static final class BingException extends RuntimeException {
    public BingException(String msg) {
      super("Bing error: " + msg);
    }

    public BingException(Throwable cause) {
      super("Bing error: " + cause.getMessage(), cause);
    }
  }
}
