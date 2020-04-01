package gov.va.api.lighthouse.facilities;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {FacilitiesHomeController.class})
public class FacilitiesHomeControllerTest {

  @Autowired private MockMvc mvc;

  @Test
  @SneakyThrows
  public void openapiJson() {
    mvc.perform(get("/v0/facilities/openapi.json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.openapi", equalTo("3.0.1")));
    mvc.perform(get("/docs/v0/api"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.openapi", equalTo("3.0.1")));
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.openapi", equalTo("3.0.1")));
  }
}
