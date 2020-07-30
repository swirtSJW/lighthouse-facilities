package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkState;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
class InternalClientKeyFilterConfiguration {
  @Bean
  public FilterRegistrationBean<ClientKeyFilter> filterRegistration(
      @Value("${internal.client-key}") String clientKey) {
    checkState(!"unset".equals(clientKey), "internal.client-key is unset");
    FilterRegistrationBean<ClientKeyFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(ClientKeyFilter.builder().clientKey(clientKey).build());
    registrationBean.addUrlPatterns("/internal/*");
    return registrationBean;
  }

  @Builder
  static final class ClientKeyFilter extends OncePerRequestFilter {
    @NonNull private final String clientKey;

    @Override
    @SneakyThrows
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
      String requestKey = request.getHeader("client-key");
      if (clientKey.equals(requestKey)) {
        filterChain.doFilter(request, response);
        return;
      }

      response.setStatus(401);
      response.setHeader("Content-Type", "application/json");

      String body =
          JacksonConfig.createMapper()
              .writeValueAsString(
                  ApiError.builder()
                      .errors(
                          List.of(
                              ApiError.ErrorMessage.builder()
                                  .title("Invalid token")
                                  .detail("Invalid token for request header: client-key")
                                  .code("401")
                                  .status("401")
                                  .build()))
                      .build());
      response.getOutputStream().write(body.getBytes("UTF-8"));
    }
  }
}
