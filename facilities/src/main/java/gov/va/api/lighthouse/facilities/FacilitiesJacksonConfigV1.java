package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.deserializers.CmsOverlayDeserializer;
import gov.va.api.lighthouse.facilities.api.v1.deserializers.DetailedServiceDeserializer;
import gov.va.api.lighthouse.facilities.api.v1.deserializers.DetailedServicesResponseDeserializer;
import gov.va.api.lighthouse.facilities.api.v1.deserializers.FacilityDeserializer;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacilitiesJacksonConfigV1 {
  public static ObjectMapper createMapper() {
    return new FacilitiesJacksonConfigV1().objectMapperV1();
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  public static <T> T quietlyMap(ObjectMapper mapper, InputStream json, Class<T> type) {
    return mapper.readValue(json, type);
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  static <T> T quietlyMap(ObjectMapper mapper, String json, Class<T> type) {
    return mapper.readValue(json, type);
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  static String quietlyWriteValueAsString(ObjectMapper mapper, Object obj) {
    return mapper.writeValueAsString(obj);
  }

  /** Instantiate V1 Jackson object mapper. */
  @Bean
  public ObjectMapper objectMapperV1() {
    final ObjectMapper mapper = JacksonConfig.createMapper();
    // Register module for serializers
    mapper.registerModule(JacksonSerializersV1.serializersV1());
    // Register module for deserializers
    final SimpleModule deserializerModule = new SimpleModule();
    deserializerModule.addDeserializer(Facility.class, new FacilityDeserializer());
    deserializerModule.addDeserializer(CmsOverlay.class, new CmsOverlayDeserializer());
    deserializerModule.addDeserializer(DetailedService.class, new DetailedServiceDeserializer());
    deserializerModule.addDeserializer(
        DetailedServicesResponse.class, new DetailedServicesResponseDeserializer());
    mapper.registerModule(deserializerModule);
    return mapper;
  }
}
