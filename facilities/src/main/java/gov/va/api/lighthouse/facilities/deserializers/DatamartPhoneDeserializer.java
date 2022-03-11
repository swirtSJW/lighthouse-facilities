package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAfterHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEnrollmentCoordinator;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getMentalHealthClinic;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPatientAdvocate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import lombok.SneakyThrows;

public class DatamartPhoneDeserializer extends BaseDeserializer<Phone> {
  public DatamartPhoneDeserializer() {
    this(null);
  }

  public DatamartPhoneDeserializer(Class<Phone> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Phone deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);

    // Read values using snake_case or camelCase representations
    JsonNode faxNode = node.get("fax");
    JsonNode mainNode = node.get("main");
    JsonNode pharmacyNode = node.get("pharmacy");
    JsonNode afterHoursNode = getAfterHours(node);
    JsonNode patientAdvocateNode = getPatientAdvocate(node);
    JsonNode mentalHealthClinicNode = getMentalHealthClinic(node);
    JsonNode enrollmentCoordinator = getEnrollmentCoordinator(node);

    return Phone.builder()
        .fax(isNotNull(faxNode) ? faxNode.asText() : null)
        .main(isNotNull(mainNode) ? mainNode.asText() : null)
        .pharmacy(isNotNull(pharmacyNode) ? pharmacyNode.asText() : null)
        .afterHours(isNotNull(afterHoursNode) ? afterHoursNode.asText() : null)
        .patientAdvocate(isNotNull(patientAdvocateNode) ? patientAdvocateNode.asText() : null)
        .mentalHealthClinic(
            isNotNull(mentalHealthClinicNode) ? mentalHealthClinicNode.asText() : null)
        .enrollmentCoordinator(
            isNotNull(enrollmentCoordinator) ? enrollmentCoordinator.asText() : null)
        .build();
  }
}
