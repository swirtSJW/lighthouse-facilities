package gov.va.api.lighthouse.facilitiescollector;

import java.security.cert.X509Certificate;
import lombok.SneakyThrows;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InsecureRestTemplateProvider {
  /** Create RestTemplate with SSL disabled. */
  @SneakyThrows
  public RestTemplate restTemplate() {
    CloseableHttpClient httpClient =
        HttpClients.custom()
            .setSSLSocketFactory(
                new SSLConnectionSocketFactory(
                    SSLContexts.custom()
                        .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                        .build()))
            .build();
    return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
  }
}
