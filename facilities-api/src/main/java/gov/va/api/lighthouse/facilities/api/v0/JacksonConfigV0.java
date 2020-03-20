package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public final class JacksonConfigV0 {
  /** Configure ObjectMapper. */
  public static ObjectMapper createMapper() {
    SimpleModule customSerializers = new SimpleModule();
    customSerializers.addSerializer(Facility.Address.class, new AddressSerializer());
    customSerializers.addSerializer(Facility.Hours.class, new HoursSerializer());
    customSerializers.addSerializer(Facility.Phone.class, new PhoneSerializer());
    customSerializers.addSerializer(Facility.Satisfaction.class, new SatisfactionSerializer());
    customSerializers.addSerializer(Facility.Services.class, new ServicesSerializer());
    customSerializers.addSerializer(Facility.WaitTimes.class, new WaitTimesSerializer());
    return new ObjectMapper()
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .registerModule(customSerializers)
        .setAnnotationIntrospector(new LombokAnnotationIntrospector())
        .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .enable(MapperFeature.AUTO_DETECT_FIELDS)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  private static Optional<String> id(Object obj) {
    if (obj instanceof Facility) {
      return Optional.ofNullable(((Facility) obj).id());
    }
    if (obj instanceof GeoFacility.Properties) {
      return Optional.ofNullable(((GeoFacility.Properties) obj).id());
    }
    if (obj instanceof NearbyFacility.Nearby) {
      return Optional.ofNullable(((NearbyFacility.Nearby) obj).id());
    }
    return Optional.empty();
  }

  private static boolean idStartsWith(JsonGenerator jgen, String prefix) {
    return parents(jgen).stream()
        .map(p -> id(p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .anyMatch(id -> StringUtils.startsWithIgnoreCase(id, prefix));
  }

  private static List<Object> parents(JsonGenerator jgen) {
    List<Object> parents = new ArrayList<>();
    JsonStreamContext parent = jgen.getOutputContext().getParent();
    while (parent != null) {
      if (parent.getCurrentValue() != null) {
        parents.add(parent.getCurrentValue());
      }
      parent = parent.getParent();
    }
    return parents;
  }

  @SneakyThrows
  private static void writeNonNull(JsonGenerator jgen, String fieldName, Object value) {
    if (value != null) {
      jgen.writeObjectField(fieldName, value);
    }
  }

  private static final class AddressSerializer extends StdSerializer<Facility.Address> {
    public AddressSerializer() {
      this(null);
    }

    public AddressSerializer(Class<Facility.Address> t) {
      super(t);
    }

    private static boolean empty(Facility.Address value) {
      return value.zip() == null
          && value.city() == null
          && value.state() == null
          && value.address1() == null
          && value.address2() == null
          && value.address3() == null;
    }

    private static boolean emptyExcludeAddr1(Facility.Address value) {
      return value.zip() == null
          && value.city() == null
          && value.state() == null
          && value.address2() == null
          && value.address3() == null;
    }

    @SneakyThrows
    private static void writeNonNullOrNonStateCem(
        JsonGenerator jgen, String fieldName, String value) {
      if (value != null || !idStartsWith(jgen, "nca_s")) {
        jgen.writeStringField(fieldName, value);
      }
    }

    @Override
    @SneakyThrows
    public void serialize(Facility.Address value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        if (emptyExcludeAddr1(value) && idStartsWith(jgen, "nca_s")) {
          jgen.writeStringField("address_1", value.address1());
        } else {
          jgen.writeStringField("zip", value.zip());
          writeNonNullOrNonStateCem(jgen, "city", value.city());
          writeNonNullOrNonStateCem(jgen, "state", value.state());
          writeNonNullOrNonStateCem(jgen, "address_1", value.address1());
          writeNonNullOrNonStateCem(jgen, "address_2", value.address2());
          writeNonNullOrNonStateCem(jgen, "address_3", value.address3());
        }
      } else if (idStartsWith(jgen, "nca_") && !idStartsWith(jgen, "nca_s")) {
        jgen.writeStringField("zip", null);
        jgen.writeStringField("city", null);
        jgen.writeStringField("state", null);
        jgen.writeStringField("address_1", null);
        jgen.writeStringField("address_2", null);
        jgen.writeStringField("address_3", null);
      }
      jgen.writeEndObject();
    }
  }

  /**
   * The lombok class annotation inspector provides support for this project's style of builders.
   * This allows @Value classes with @Builders to be automatically supported for deserialization. It
   * will look for a builder class using the standard Lombok naming conventions and assume builder
   * methods do not have a prefix, e.g. "property" instead of "setProperty" or "withProperty".
   * However, you can still use @JsonPOJOBuilder if you need to override this inspectors default
   * behavior.
   */
  private static final class LombokAnnotationIntrospector extends JacksonAnnotationIntrospector {
    private static boolean hasDefaultConstructor(Class<?> ac) {
      try {
        return ac.getDeclaredConstructor() != null;
      } catch (NoSuchMethodException e) {
        return false;
      }
    }

    @Override
    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
      // Attempt to allow the default mechanism work.
      // However, if no builder is found using Jackson
      // annotations, try to find a lombok style builder.
      Class<?> pojoBuilder = super.findPOJOBuilder(ac);
      if (pojoBuilder != null) {
        return pojoBuilder;
      }
      if (hasDefaultConstructor(ac.getAnnotated())) {
        return null;
      }
      String className = ac.getAnnotated().getSimpleName();
      String lombokBuilder = ac.getAnnotated().getName() + "$" + className + "Builder";
      try {
        return Class.forName(lombokBuilder);
      } catch (ClassNotFoundException e) {
        // Default lombok builder does not exist.
      }
      return null;
    }

    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
      if (ac.hasAnnotation(JsonPOJOBuilder.class)) {
        return super.findPOJOBuilderConfig(ac);
      }
      return new JsonPOJOBuilder.Value("build", "");
    }
  }

  private static final class HoursSerializer extends StdSerializer<Facility.Hours> {
    public HoursSerializer() {
      this(null);
    }

    public HoursSerializer(Class<Facility.Hours> t) {
      super(t);
    }

    private static boolean empty(Facility.Hours value) {
      return value.mon() == null
          && value.tues() == null
          && value.wed() == null
          && value.thurs() == null
          && value.fri() == null
          && value.sat() == null
          && value.sun() == null
          && value.monday() == null
          && value.tuesday() == null
          && value.wednesday() == null
          && value.thursday() == null
          && value.friday() == null
          && value.saturday() == null
          && value.sunday() == null;
    }

    @SneakyThrows
    private static void writeEitherNonNull(
        JsonGenerator jgen, String fieldName, String left, String right) {
      if (left != null) {
        writeNonNull(jgen, fieldName, left);
      } else {
        writeNonNull(jgen, fieldName, right);
      }
    }

    private static void writeLower(Facility.Hours value, JsonGenerator jgen) {
      writeEitherNonNull(jgen, "monday", value.monday(), value.mon());
      writeEitherNonNull(jgen, "tuesday", value.tuesday(), value.tues());
      writeEitherNonNull(jgen, "wednesday", value.wednesday(), value.wed());
      writeEitherNonNull(jgen, "thursday", value.thursday(), value.thurs());
      writeEitherNonNull(jgen, "friday", value.friday(), value.fri());
      writeEitherNonNull(jgen, "saturday", value.saturday(), value.sat());
      writeEitherNonNull(jgen, "sunday", value.sunday(), value.sun());
    }

    private static void writeLowerScrambled(Facility.Hours value, JsonGenerator jgen) {
      writeEitherNonNull(jgen, "friday", value.friday(), value.fri());
      writeEitherNonNull(jgen, "monday", value.monday(), value.mon());
      writeEitherNonNull(jgen, "sunday", value.sunday(), value.sun());
      writeEitherNonNull(jgen, "tuesday", value.tuesday(), value.tues());
      writeEitherNonNull(jgen, "saturday", value.saturday(), value.sat());
      writeEitherNonNull(jgen, "thursday", value.thursday(), value.thurs());
      writeEitherNonNull(jgen, "wednesday", value.wednesday(), value.wed());
    }

    private static void writeUpper(Facility.Hours value, JsonGenerator jgen) {
      writeEitherNonNull(jgen, "Monday", value.mon(), value.monday());
      writeEitherNonNull(jgen, "Tuesday", value.tues(), value.tuesday());
      writeEitherNonNull(jgen, "Wednesday", value.wed(), value.wednesday());
      writeEitherNonNull(jgen, "Thursday", value.thurs(), value.thursday());
      writeEitherNonNull(jgen, "Friday", value.fri(), value.friday());
      writeEitherNonNull(jgen, "Saturday", value.sat(), value.saturday());
      writeEitherNonNull(jgen, "Sunday", value.sun(), value.sunday());
    }

    private static void writeUpperScrambled(Facility.Hours value, JsonGenerator jgen) {
      writeEitherNonNull(jgen, "Friday", value.fri(), value.friday());
      writeEitherNonNull(jgen, "Monday", value.mon(), value.monday());
      writeEitherNonNull(jgen, "Sunday", value.sun(), value.sunday());
      writeEitherNonNull(jgen, "Tuesday", value.tues(), value.tuesday());
      writeEitherNonNull(jgen, "Saturday", value.sat(), value.saturday());
      writeEitherNonNull(jgen, "Thursday", value.thurs(), value.thursday());
      writeEitherNonNull(jgen, "Wednesday", value.wed(), value.wednesday());
    }

    private boolean hasParent(JsonGenerator jgen, Class<?> clazz) {
      return parents(jgen).stream().anyMatch(p -> clazz.isInstance(p));
    }

    @Override
    @SneakyThrows
    public void serialize(Facility.Hours value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        if (hasParent(jgen, FacilityReadResponse.class)) {
          writeLower(value, jgen);
        } else if (hasParent(jgen, Facility.class)) {
          writeLowerScrambled(value, jgen);
        } else if (hasParent(jgen, GeoFacilityReadResponse.class)) {
          if (idStartsWith(jgen, "vc_")) {
            writeLower(value, jgen);
          } else {
            writeUpper(value, jgen);
          }
        } else if (hasParent(jgen, GeoFacility.class)) {
          if (idStartsWith(jgen, "vc_")) {
            writeLowerScrambled(value, jgen);
          } else {
            writeUpperScrambled(value, jgen);
          }
        }
      } else if (idStartsWith(jgen, "nca_")) {
        jgen.writeObjectField("Friday", null);
        jgen.writeObjectField("Monday", null);
        jgen.writeObjectField("Sunday", null);
        jgen.writeObjectField("Tuesday", null);
        jgen.writeObjectField("Saturday", null);
        jgen.writeObjectField("Thursday", null);
        jgen.writeObjectField("Wednesday", null);
      }
      jgen.writeEndObject();
    }
  }

  private static final class PhoneSerializer extends StdSerializer<Facility.Phone> {
    public PhoneSerializer() {
      this(null);
    }

    public PhoneSerializer(Class<Facility.Phone> t) {
      super(t);
    }

    private static boolean empty(Facility.Phone value) {
      return value.fax() == null
          && value.main() == null
          && value.pharmacy() == null
          && value.afterHours() == null
          && value.patientAdvocate() == null
          && value.mentalHealthClinic() == null
          && value.enrollmentCoordinator() == null;
    }

    @SneakyThrows
    private static void writeNonNullOrBenOrHealth(
        JsonGenerator jgen, String fieldName, String value) {
      if (value != null || idStartsWith(jgen, "vba_") || idStartsWith(jgen, "vha_")) {
        jgen.writeObjectField(fieldName, value);
      }
    }

    @SneakyThrows
    private static void writeNonNullOrHealth(JsonGenerator jgen, String fieldName, String value) {
      if (value != null || idStartsWith(jgen, "vha_")) {
        jgen.writeObjectField(fieldName, value);
      }
    }

    @Override
    @SneakyThrows
    public void serialize(Facility.Phone value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        writeNonNullOrBenOrHealth(jgen, "fax", value.fax());
        jgen.writeObjectField("main", value.main());
        writeNonNullOrHealth(jgen, "pharmacy", value.pharmacy());
        writeNonNullOrHealth(jgen, "after_hours", value.afterHours());
        writeNonNullOrHealth(jgen, "patient_advocate", value.patientAdvocate());
        writeNonNull(jgen, "mental_health_clinic", value.mentalHealthClinic());
        writeNonNullOrHealth(jgen, "enrollment_coordinator", value.enrollmentCoordinator());
      } else {
        if (idStartsWith(jgen, "nca_")) {
          jgen.writeObjectField("fax", null);
        }
        jgen.writeObjectField("main", null);
      }
      jgen.writeEndObject();
    }
  }

  private static final class SatisfactionSerializer extends StdSerializer<Facility.Satisfaction> {
    public SatisfactionSerializer() {
      this(null);
    }

    public SatisfactionSerializer(Class<Facility.Satisfaction> t) {
      super(t);
    }

    private static boolean empty(Facility.Satisfaction value) {
      return value.health() == null && value.effectiveDate() == null;
    }

    @Override
    @SneakyThrows
    public void serialize(
        Facility.Satisfaction value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        jgen.writeObjectField("health", value.health());
        jgen.writeObjectField("effective_date", value.effectiveDate());
      }
      jgen.writeEndObject();
    }
  }

  private static final class ServicesSerializer extends StdSerializer<Facility.Services> {
    public ServicesSerializer() {
      this(null);
    }

    public ServicesSerializer(Class<Facility.Services> t) {
      super(t);
    }

    private static boolean empty(Facility.Services value) {
      return value.other() == null
          && value.health() == null
          && value.benefits() == null
          && value.lastUpdated() == null;
    }

    @Override
    @SneakyThrows
    public void serialize(
        Facility.Services value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        writeNonNull(jgen, "other", value.other());
        writeNonNull(jgen, "health", value.health());
        writeNonNull(jgen, "benefits", value.benefits());
        if (value.lastUpdated() != null || value.health() != null) {
          jgen.writeObjectField("last_updated", value.lastUpdated());
        }
      }
      jgen.writeEndObject();
    }
  }

  private static final class WaitTimesSerializer extends StdSerializer<Facility.WaitTimes> {
    public WaitTimesSerializer() {
      this(null);
    }

    public WaitTimesSerializer(Class<Facility.WaitTimes> t) {
      super(t);
    }

    private static boolean empty(Facility.WaitTimes value) {
      return value.health() == null && value.effectiveDate() == null;
    }

    @Override
    @SneakyThrows
    public void serialize(
        Facility.WaitTimes value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        jgen.writeObjectField("health", value.health());
        jgen.writeObjectField("effective_date", value.effectiveDate());
      }
      jgen.writeEndObject();
    }
  }
}
