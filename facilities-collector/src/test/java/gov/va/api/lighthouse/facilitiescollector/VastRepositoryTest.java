package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class VastRepositoryTest {
  @Autowired VastRepository repository;

  @Test
  public void lastUpdated() {
    var aLongTimeAgo = Instant.parse("2020-01-20T02:20:00Z");
    repository.save(vastEntity(1L, aLongTimeAgo));
    assertThat(repository.findLastUpdated()).isEqualTo(aLongTimeAgo);
    var now = Instant.ofEpochMilli(System.currentTimeMillis());
    repository.save(vastEntity(2L, now));
    assertThat(repository.findLastUpdated()).isEqualTo(now);
  }

  private VastEntity vastEntity(long id, Instant lastUpdated) {
    return VastEntity.builder().vastId(id).lastUpdated(lastUpdated).build();
  }
}
