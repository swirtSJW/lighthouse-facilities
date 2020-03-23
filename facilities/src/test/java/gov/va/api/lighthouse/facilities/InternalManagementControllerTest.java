package gov.va.api.lighthouse.facilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InternalManagementControllerTest {

  @Test
  void collect() {
    // TODO not implemented
    controller().reload();
  }

  private InternalManagementController controller() {
    return InternalManagementController.builder().build();
  }
}
