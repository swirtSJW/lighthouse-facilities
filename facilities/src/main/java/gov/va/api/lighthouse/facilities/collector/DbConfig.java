package gov.va.api.lighthouse.facilities.collector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
class DbConfig {
  @Bean
  JdbcTemplate jdbcTemplate(
      @Value("${spring.fc-datasource.driver-class-name}") String driverClassName,
      @Value("${spring.fc-datasource.password}") String password,
      @Value("${spring.fc-datasource.url}") String url,
      @Value("${spring.fc-datasource.username}") String username) {
    return new JdbcTemplate(
        DataSourceBuilder.create()
            .driverClassName(driverClassName)
            .password(password)
            .url(url)
            .username(username)
            .build());
  }
}
