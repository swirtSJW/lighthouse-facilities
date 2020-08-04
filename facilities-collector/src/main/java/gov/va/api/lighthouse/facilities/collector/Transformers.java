package gov.va.api.lighthouse.facilities.collector;

import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
final class Transformers {
  static boolean allBlank(Object... values) {
    for (Object v : values) {
      if (!isBlank(v)) {
        return false;
      }
    }
    return true;
  }

  static <T> List<T> emptyToNull(List<T> items) {
    if (isEmpty(items)) {
      return null;
    }
    List<T> filtered = items.stream().filter(Objects::nonNull).collect(Collectors.toList());
    return filtered.isEmpty() ? null : filtered;
  }

  static String hoursToClosed(String hours) {
    String trim = trimToNull(hours);
    if (equalsIgnoreCase(trim, "-")) {
      return "Closed";
    }
    return trim;
  }

  static boolean isBlank(Object value) {
    if (value instanceof CharSequence) {
      return StringUtils.isBlank((CharSequence) value);
    }
    if (value instanceof Collection<?>) {
      return ((Collection<?>) value).isEmpty();
    }
    if (value instanceof Optional<?>) {
      return ((Optional<?>) value).isEmpty() || isBlank(((Optional<?>) value).get());
    }
    if (value instanceof Map<?, ?>) {
      return ((Map<?, ?>) value).isEmpty();
    }
    return value == null;
  }

  static String phoneTrim(String phone) {
    String trim = trimToNull(phone);
    if (endsWithIgnoreCase(trim, "x")) {
      return trimToNull(trim.substring(0, trim.length() - 1));
    }
    return trim;
  }

  static String withTrailingSlash(@NonNull String url) {
    return url.endsWith("/") ? url : url + "/";
  }
}
