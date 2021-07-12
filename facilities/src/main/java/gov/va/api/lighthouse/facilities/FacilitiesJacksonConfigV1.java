package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FacilitiesJacksonConfigV1 {
  static ObjectMapper createMapper() {
    return new FacilitiesJacksonConfigV1().objectMapperV1();
  }

  //  /** Mask away checked exception so this Jackson can be used in streams. */
  //  @SneakyThrows
  //  static <T> T quietlyMap(ObjectMapper mapper, String json, Class<T> type) {
  //    return mapper.readValue(json, type);
  //  }
  //
  //  /** Mask away checked exception so this Jackson can be used in streams. */
  //  @SneakyThrows
  //  static String quietlyWriteValueAsString(ObjectMapper mapper, Object obj) {
  //    return mapper.writeValueAsString(obj);
  //  }

  @Bean
  public ObjectMapper objectMapperV1() {
    return JacksonConfig.createMapper().registerModule(JacksonSerializersV1.serializersV1());
  }
}
