package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.sentinel.categories.Local;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Slf4j
public class NoOpIT {

  @Test
  @Category({Local.class})
  public void noOperation() {
    String path = TestClients.facilities().service().apiPath() + "NoOpResource";
    log.info("NoOpIT path is: " + path);
  }
}
