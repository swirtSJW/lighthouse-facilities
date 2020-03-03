package gov.va.api.lighthouse.facilitiescdw;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

@RunWith(PowerMockRunner.class)
public final class StopCodeTest {
  @Test
  @SneakyThrows
  @PrepareForTest(DataSourceUtils.class)
  public void stopCodes() {
    AtomicInteger resultSetTimes = new AtomicInteger(0);
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getString("DIVISION_FCDMD")).thenReturn("(503GA) Melbourne, FL");
    when(resultSet.getString("CocClassification")).thenReturn("Primary Care CBOC");
    when(resultSet.getString("Sta6a")).thenReturn("402GA");
    when(resultSet.getString("PrimaryStopCode")).thenReturn("123");
    when(resultSet.getString("PrimaryStopCodeName")).thenReturn("PRIMARY CARE/MEDICINE");
    when(resultSet.getString("NumberOfAppointmentsLinkedToConsult")).thenReturn("99");
    when(resultSet.getString("NumberOfLocations")).thenReturn("3");
    when(resultSet.getString("AvgWaitTimeNew")).thenReturn("14.15");
    when(resultSet.next()).thenAnswer((inv) -> resultSetTimes.incrementAndGet() == 1);

    Statement statement = mock(Statement.class);
    when(statement.executeQuery(any(String.class))).thenReturn(resultSet);

    Connection connection = mock(Connection.class);
    when(connection.createStatement()).thenReturn(statement);

    DataSource dataSource = mock(DataSource.class);
    mockStatic(DataSourceUtils.class);
    when(DataSourceUtils.getConnection(dataSource)).thenReturn(connection);

    assertThat(new Controller(null, new JdbcTemplate(dataSource)).stopCodes())
        .isEqualTo(
            StopCodeResponse.builder()
                .stopCodes(
                    List.of(
                        StopCodeResponse.StopCode.builder()
                            .divisionFcdmd("(503GA) Melbourne, FL")
                            .cocClassification("Primary Care CBOC")
                            .sta6a("402GA")
                            .primaryStopCode("123")
                            .primaryStopCodeName("PRIMARY CARE/MEDICINE")
                            .numberOfAppointmentsLinkedToConsult("99")
                            .numberOfLocations("3")
                            .avgWaitTimeNew("14.15")
                            .build()))
                .build());
  }
}
