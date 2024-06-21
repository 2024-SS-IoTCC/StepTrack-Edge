package at.aau.streptrack.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EdgeApplication {

  public static void main(String[] args) {
    SpringApplication.run(EdgeApplication.class, args);
  }

  //  @Bean
  //  MqttPahoClientFactory clientFactory(@Value("${hivemq.uri}") String host) {
  //    var factory = new DefaultMqttPahoClientFactory();
  //    var options = new MqttConnectOptions();
  //
  //    options.setServerURIs(new String[] {host});
  //    factory.setConnectionOptions(options);
  //
  //    return factory;
  //  }
}
