package at.aau.streptrack.edge;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.StepsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Value("${dashboard.api.url}")
  private String apiUrl;

  @Bean
  public StepsApi apiClient() {
    var apiClient = new ApiClient();

    apiClient.setBasePath(apiUrl);

    return new StepsApi(apiClient);
  }
}
