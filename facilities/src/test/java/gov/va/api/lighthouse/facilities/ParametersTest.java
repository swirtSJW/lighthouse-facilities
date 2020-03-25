package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Value;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;

public class ParametersTest {
  @Test
  public void add() {
    assertThat(Parameters.builder().add("val", new Foo("hello")).build())
        .isEqualTo(new LinkedMultiValueMap<>(ImmutableMap.of("val", List.of("hello"))));
  }

  @Test
  public void addAll() {
    assertThat(
            Parameters.builder()
                .addAll("list", List.of(new Foo("hello"), new Foo("world")))
                .build())
        .isEqualTo(new LinkedMultiValueMap<>(ImmutableMap.of("list", List.of("hello", "world"))));
  }

  @Test
  public void addAll_empty() {
    assertThat(Parameters.builder().addAll("list", null).build())
        .isEqualTo(new LinkedMultiValueMap<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addAll_nullElement() {
    Parameters.builder().addAll("list", Lists.newArrayList((String) null));
  }

  @Test
  public void addIgnoreNull() {
    assertThat(Parameters.builder().addIgnoreNull("ignore", null).build())
        .isEqualTo(new LinkedMultiValueMap<>());
    assertThat(Parameters.builder().addIgnoreNull("val", new Foo("hello")).build())
        .isEqualTo(new LinkedMultiValueMap<>(ImmutableMap.of("val", List.of("hello"))));
  }

  @Test
  public void addInt() {
    assertThat(Parameters.builder().add("int", 1).build())
        .isEqualTo(new LinkedMultiValueMap<>(ImmutableMap.of("int", List.of("1"))));
  }

  @Test(expected = NullPointerException.class)
  public void add_null() {
    Parameters.builder().add("val", null);
  }

  @Test
  public void pageOf() {
    assertThat(Parameters.pageOf(new LinkedMultiValueMap<>(ImmutableMap.of("page", List.of("1")))))
        .isEqualTo(1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void pageOf_notFound() {
    Parameters.pageOf(new LinkedMultiValueMap<>(ImmutableMap.of("foo", List.of("bar"))));
  }

  @Test(expected = NumberFormatException.class)
  public void pageOf_numberFormat() {
    Parameters.pageOf(new LinkedMultiValueMap<>(ImmutableMap.of("page", List.of("x"))));
  }

  @Test
  public void perPageOf() {
    assertThat(
            Parameters.perPageOf(
                new LinkedMultiValueMap<>(ImmutableMap.of("per_page", List.of("0")))))
        .isEqualTo(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void perPageOf_notFound() {
    Parameters.perPageOf(new LinkedMultiValueMap<>(ImmutableMap.of("foo", List.of("bar"))));
  }

  @Test(expected = NumberFormatException.class)
  public void perPageOf_numberFormat() {
    Parameters.perPageOf(new LinkedMultiValueMap<>(ImmutableMap.of("per_page", List.of("x"))));
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
