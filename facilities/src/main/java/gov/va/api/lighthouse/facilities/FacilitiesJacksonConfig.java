package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FacilitiesJacksonConfig {
  static ObjectMapper createMapper() {
    return new FacilitiesJacksonConfig().objectMapper();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return JacksonConfig.createMapper().registerModule(JacksonSerializersV0.serializersV0());
  }
}
