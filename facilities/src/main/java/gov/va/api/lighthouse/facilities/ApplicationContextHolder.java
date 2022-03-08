package gov.va.api.lighthouse.facilities;

import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware {
  private static ApplicationContext context;

  public static ApplicationContext getContext() {
    return context;
  }

  private static synchronized void setContext(ApplicationContext applicationContext) {
    context = applicationContext;
  }

  @Override
  @SneakyThrows
  public void setApplicationContext(ApplicationContext applicationContext) {
    setContext(applicationContext);
  }
}
