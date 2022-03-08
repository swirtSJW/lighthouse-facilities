package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.deserializers.DatamartCmsOverlayDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartDetailedServiceDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartFacilityDeserializer;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatamartFacilitiesJacksonConfig {
  public static ObjectMapper createMapper() {
    return new DatamartFacilitiesJacksonConfig().datamartMapper();
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  static <T> T quietlyMap(ObjectMapper mapper, InputStream json, Class<T> type) {
    return mapper.readValue(json, type);
  }

  /** Method used to obtain datamart object mapper. */
  @Bean
  public ObjectMapper datamartMapper() {
    final ObjectMapper mapper = JacksonConfig.createMapper();
    // Register module for serializers
    mapper.registerModule(DatamartJacksonSerializers.datamartSerializers());
    // Register module for deserializers
    final SimpleModule deserializerModule = new SimpleModule();
    deserializerModule.addDeserializer(DatamartFacility.class, new DatamartFacilityDeserializer());
    deserializerModule.addDeserializer(
        DatamartCmsOverlay.class, new DatamartCmsOverlayDeserializer());
    deserializerModule.addDeserializer(
        DatamartDetailedService.class, new DatamartDetailedServiceDeserializer());
    mapper.registerModule(deserializerModule);
    return mapper;
  }
}
