package gov.va.api.lighthouse.facilitiescollector;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;

@Builder
final class StateCemeteriesCollector {
  @NonNull final String baseUrl;

  @NonNull final Map<String, String> websites;

  @SneakyThrows
  private StateCemeteries load() {
    return new XmlMapper()
        .registerModule(new StringTrimModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .readValue(new URL(baseUrl + "cems/cems.xml"), StateCemeteries.class);
  }

  Collection<Facility> stateCemeteries() {
    return load().cem().stream()
        .filter(Objects::nonNull)
        .map(c -> StateCemeteryTransformer.builder().xml(c).websites(websites).build().toFacility())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static final class StringTrimModule extends SimpleModule {
    StringTrimModule() {
      addDeserializer(
          String.class,
          new StdScalarDeserializer<String>(String.class) {
            @Override
            @SneakyThrows
            public String deserialize(JsonParser p, DeserializationContext ctxt) {
              return trimToNull(p.getValueAsString());
            }
          });
    }
  }
}
