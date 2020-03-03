package gov.va.api.lighthouse.facilitiescdw;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTest {
  @Test
  public void contextLoads() {
    assertThat(true).isTrue();
  }
}
