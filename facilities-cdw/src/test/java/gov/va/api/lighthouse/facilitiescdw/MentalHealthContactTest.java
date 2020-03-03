package gov.va.api.lighthouse.facilitiescdw;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Test;

public final class MentalHealthContactTest {
  @Test
  public void mentalHealthContacts() {
    MentalHealthContactRepository repo = mock(MentalHealthContactRepository.class);
    when(repo.findAll())
        .thenReturn(
            List.of(
                MentalHealthContactEntity.builder()
                    .id("12345")
                    .region("1")
                    .visn("V09")
                    .adminParent("503")
                    .stationNumber("503GA")
                    .mhClinicPhone("0005559999")
                    .mhPhone("000-555-9999")
                    .extension("7410")
                    .officialStationName("Johnstown VA Clinic POC")
                    .pocEmail("bobnelson@va.gov")
                    .status("UPDATE DUE")
                    .modified("2020-03-06 11:00:00.0")
                    .created("2020-03-03 12:00:00.0")
                    .addedToOutbox("2020-03-09 13:00:00.0")
                    .build()));
    assertThat(new Controller(repo, null).mentalHealthContacts())
        .isEqualTo(
            MentalHealthContactResponse.builder()
                .contacts(
                    List.of(
                        MentalHealthContactResponse.Contact.builder()
                            .id("12345")
                            .region("1")
                            .visn("V09")
                            .adminParent("503")
                            .stationNumber("503GA")
                            .mhClinicPhone("0005559999")
                            .mhPhone("000-555-9999")
                            .extension("7410")
                            .officialStationName("Johnstown VA Clinic POC")
                            .pocEmail("bobnelson@va.gov")
                            .status("UPDATE DUE")
                            .modified("2020-03-06 11:00:00.0")
                            .created("2020-03-03 12:00:00.0")
                            .addedToOutbox("2020-03-09 13:00:00.0")
                            .build()))
                .build());
  }
}
