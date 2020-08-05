package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

public class ParametersTest {
  @Test
  void add() {
    assertThat(Parameters.builder().add("val", new Foo("hello")).build())
        .isEqualTo(new LinkedMultiValueMap<>(ImmutableMap.of("val", List.of("hello"))));
  }

  @Test
  void addAll() {
    assertThat(
            Parameters.builder()
                .addAll("list", List.of(new Foo("hello"), new Foo("world")))
                .build())
        .isEqualTo(new LinkedMultiValueMap<>(ImmutableMap.of("list", List.of("hello", "world"))));
  }

  @Test
  void addAll_empty() {
    assertThat(Parameters.builder().addAll("list", null).build())
        .isEqualTo(new LinkedMultiValueMap<>());
  }

  @Test
  void addAll_nullElement() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Parameters.builder().addAll("list", Lists.newArrayList((String) null)));
  }

  @Test
  void addIgnoreNull() {
    assertThat(Parameters.builder().addIgnoreNull("ignore", null).build())
        .isEqualTo(new LinkedMultiValueMap<>());
    assertThat(Parameters.builder().addIgnoreNull("val", new Foo("hello")).build())
        .isEqualTo(new LinkedMultiValueMap<>(ImmutableMap.of("val", List.of("hello"))));
  }

  @Test
  void addInt() {
    assertThat(Parameters.builder().add("int", 1).build())
        .isEqualTo(new LinkedMultiValueMap<>(ImmutableMap.of("int", List.of("1"))));
  }

  @Test
  void add_null() {
    assertThrows(NullPointerException.class, () -> Parameters.builder().add("val", null));
  }

  @Test
  void pageOf() {
    assertThat(Parameters.pageOf(new LinkedMultiValueMap<>(ImmutableMap.of("page", List.of("1")))))
        .isEqualTo(1);
  }

  @Test
  void pageOf_notFound() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Parameters.pageOf(new LinkedMultiValueMap<>(ImmutableMap.of("foo", List.of("bar")))));
  }

  @Test
  void pageOf_numberFormat() {
    assertThrows(
        NumberFormatException.class,
        () -> Parameters.pageOf(new LinkedMultiValueMap<>(ImmutableMap.of("page", List.of("x")))));
  }

  @Test
  void perPageOf() {
    assertThat(
            Parameters.perPageOf(
                new LinkedMultiValueMap<>(ImmutableMap.of("per_page", List.of("0")))))
        .isEqualTo(0);
  }

  @Test
  void perPageOf_notFound() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            Parameters.perPageOf(
                new LinkedMultiValueMap<>(ImmutableMap.of("foo", List.of("bar")))));
  }

  @Test
  void perPageOf_numberFormat() {
    assertThrows(
        NumberFormatException.class,
        () ->
            Parameters.perPageOf(
                new LinkedMultiValueMap<>(ImmutableMap.of("per_page", List.of("x")))));
  }

  @Value
  private static final class Foo {
    String bar;

    @Override
    public String toString() {
      return bar;
    }
  }
}
