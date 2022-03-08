package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility.Properties;
import gov.va.api.lighthouse.facilities.api.v0.deserializers.CmsOverlayDeserializer;
import gov.va.api.lighthouse.facilities.api.v0.deserializers.DetailedServiceDeserializer;
import gov.va.api.lighthouse.facilities.api.v0.deserializers.DetailedServicesResponseDeserializer;
import gov.va.api.lighthouse.facilities.api.v0.deserializers.FacilityAttributesDeserializer;
import gov.va.api.lighthouse.facilities.api.v0.deserializers.GeoFacilityPropertiesDeserializer;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacilitiesJacksonConfigV0 {
  public static ObjectMapper createMapper() {
    return new FacilitiesJacksonConfigV0().objectMapper();
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  public static <T> T quietlyMap(ObjectMapper mapper, InputStream json, Class<T> type) {
    return mapper.readValue(json, type);
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  public static <T> T quietlyMap(ObjectMapper mapper, String json, Class<T> type) {
    return mapper.readValue(json, type);
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  public static String quietlyWriteValueAsString(ObjectMapper mapper, Object obj) {
    return mapper.writeValueAsString(obj);
  }

  /** Instantiate V0 Jackson object mapper. */
  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper mapper = JacksonConfig.createMapper();
    // Register module for serializers
    mapper.registerModule(JacksonSerializersV0.serializersV0());
    // Register module for deserializers
    final SimpleModule deserializerModule = new SimpleModule();
    deserializerModule.addDeserializer(CmsOverlay.class, new CmsOverlayDeserializer());
    deserializerModule.addDeserializer(DetailedService.class, new DetailedServiceDeserializer());
    deserializerModule.addDeserializer(
        DetailedServicesResponse.class, new DetailedServicesResponseDeserializer());
    deserializerModule.addDeserializer(
        FacilityAttributes.class, new FacilityAttributesDeserializer());
    deserializerModule.addDeserializer(Properties.class, new GeoFacilityPropertiesDeserializer());
    mapper.registerModule(deserializerModule);
    return mapper;
  }
}
