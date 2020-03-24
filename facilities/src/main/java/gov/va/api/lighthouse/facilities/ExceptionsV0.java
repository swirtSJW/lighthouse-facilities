package gov.va.api.lighthouse.facilities;

import lombok.experimental.UtilityClass;

@UtilityClass
final class ExceptionsV0 {
  static final class NotFound extends RuntimeException {
    public NotFound(String id) {
      super(String.format("The record identified by %s could not be found", id));
    }

    public NotFound(String id, Throwable cause) {
      super(String.format("The record identified by %s could not be found", id), cause);
    }
  }
}
