package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

public class PageLinkerTestV1 {
  @Test
  void links_afterAfterLastPage() {
    assertThat(
            PageLinkerV1.builder()
                .url("http://foo")
                .params(
                    Parameters.builder()
                        .add("foo", "bar")
                        .add("page", "3")
                        .add("per_page", "100")
                        .build())
                .totalEntries(10)
                .build()
                .links())
        .isEqualTo(
            PageLinks.builder()
                .self("http://foo?foo=bar&page=3&per_page=100")
                .first("http://foo?foo=bar&page=1&per_page=100")
                .last("http://foo?foo=bar&page=1&per_page=100")
                .build());
  }

  @Test
  void links_afterLastPage() {
    assertThat(
            PageLinkerV1.builder()
                .url("http://foo")
                .params(
                    Parameters.builder()
                        .add("foo", "bar")
                        .add("page", "2")
                        .add("per_page", "100")
                        .build())
                .totalEntries(10)
                .build()
                .links())
        .isEqualTo(
            PageLinks.builder()
                .self("http://foo?foo=bar&page=2&per_page=100")
                .first("http://foo?foo=bar&page=1&per_page=100")
                .prev("http://foo?foo=bar&page=1&per_page=100")
                .last("http://foo?foo=bar&page=1&per_page=100")
                .build());
  }

  @Test
  void links_firstPage() {
    assertThat(
            PageLinkerV1.builder()
                .url("http://foo")
                .params(
                    Parameters.builder()
                        .add("foo", "bar")
                        .add("page", "1")
                        .add("per_page", "1")
                        .build())
                .totalEntries(10)
                .build()
                .links())
        .isEqualTo(
            PageLinks.builder()
                .self("http://foo?foo=bar&page=1&per_page=1")
                .first("http://foo?foo=bar&page=1&per_page=1")
                .next("http://foo?foo=bar&page=2&per_page=1")
                .last("http://foo?foo=bar&page=10&per_page=1")
                .build());
  }

  @Test
  void links_lastPage() {
    assertThat(
            PageLinkerV1.builder()
                .url("http://foo")
                .params(
                    Parameters.builder()
                        .add("foo", "bar")
                        .add("page", "10")
                        .add("per_page", "1")
                        .build())
                .totalEntries(10)
                .build()
                .links())
        .isEqualTo(
            PageLinks.builder()
                .self("http://foo?foo=bar&page=10&per_page=1")
                .first("http://foo?foo=bar&page=1&per_page=1")
                .prev("http://foo?foo=bar&page=9&per_page=1")
                .last("http://foo?foo=bar&page=10&per_page=1")
                .build());
  }

  @Test
  void links_middlePage() {
    assertThat(
            PageLinkerV1.builder()
                .url("http://foo")
                .params(
                    Parameters.builder()
                        .add("foo", "bar")
                        .add("page", "5")
                        .add("per_page", "1")
                        .build())
                .totalEntries(10)
                .build()
                .links())
        .isEqualTo(
            PageLinks.builder()
                .self("http://foo?foo=bar&page=5&per_page=1")
                .first("http://foo?foo=bar&page=1&per_page=1")
                .prev("http://foo?foo=bar&page=4&per_page=1")
                .next("http://foo?foo=bar&page=6&per_page=1")
                .last("http://foo?foo=bar&page=10&per_page=1")
                .build());
  }

  @Test
  void links_singlePage() {
    assertThat(
            PageLinkerV1.builder()
                .url("http://foo")
                .params(
                    Parameters.builder()
                        .add("foo[]", "bar")
                        .add("page", "1")
                        .add("per_page", "100")
                        .build())
                .totalEntries(10)
                .build()
                .links())
        .isEqualTo(
            PageLinks.builder()
                .self("http://foo?foo%5B%5D=bar&page=1&per_page=100")
                .first("http://foo?foo%5B%5D=bar&page=1&per_page=100")
                .last("http://foo?foo%5B%5D=bar&page=1&per_page=100")
                .build());
  }

  @Test
  void links_zeroEntries() {
    assertThat(
            PageLinkerV1.builder()
                .url("http://foo")
                .params(
                    Parameters.builder()
                        .add("foo", "bar")
                        .add("page", "1")
                        .add("per_page", "100")
                        .build())
                .totalEntries(0)
                .build()
                .links())
        .isEqualTo(
            PageLinks.builder()
                .self("http://foo?foo=bar&page=1&per_page=100")
                .first("http://foo?foo=bar&page=1&per_page=100")
                .last("http://foo?foo=bar&page=1&per_page=100")
                .build());
  }

  @Test
  void pagination() {
    assertThat(
            PageLinkerV1.builder()
                .url("unused")
                .params(Parameters.builder().add("page", "1").add("per_page", "1").build())
                .totalEntries(10)
                .build()
                .pagination())
        .isEqualTo(
            Pagination.builder()
                .currentPage(1)
                .entriesPerPage(1)
                .totalPages(10)
                .totalEntries(10)
                .build());
  }

  @Test
  void pagination_zeroEntries() {
    assertThat(
            PageLinkerV1.builder()
                .url("unused")
                .params(Parameters.builder().add("page", "1").add("per_page", "1").build())
                .totalEntries(0)
                .build()
                .pagination())
        .isEqualTo(
            Pagination.builder()
                .currentPage(1)
                .entriesPerPage(1)
                .totalPages(1)
                .totalEntries(0)
                .build());
  }

  @Test
  void perPageZero() {
    String url = "http://foo";
    MultiValueMap<String, String> params =
        Parameters.builder().add("foo", "bar").add("page", "5").add("per_page", "0").build();
    assertThat(PageLinkerV1.builder().url(url).params(params).totalEntries(10).build().links())
        .isEqualTo(PageLinks.builder().self("http://foo?foo=bar&page=5&per_page=0").build());
    assertThat(PageLinkerV1.builder().url(url).params(params).totalEntries(10).build().pagination())
        .isEqualTo(
            Pagination.builder()
                .currentPage(5)
                .entriesPerPage(0)
                .totalPages(0)
                .totalEntries(10)
                .build());
  }

  @Test
  void validation_page() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            PageLinkerV1.builder()
                .url("unused")
                .params(Parameters.builder().add("page", "0").add("per_page", "0").build())
                .totalEntries(0)
                .build()
                .pagination());
  }

  @Test
  void validation_perPage() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            PageLinkerV1.builder()
                .url("unused")
                .params(Parameters.builder().add("page", "1").add("per_page", "-1").build())
                .totalEntries(0)
                .build()
                .pagination());
  }

  @Test
  void validation_totalEntries() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            PageLinkerV1.builder()
                .url("unused")
                .params(Parameters.builder().add("page", "1").add("per_page", "0").build())
                .totalEntries(-1)
                .build()
                .pagination());
  }
}
