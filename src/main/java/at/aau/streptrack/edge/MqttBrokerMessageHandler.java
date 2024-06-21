package at.aau.streptrack.edge;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class MqttBrokerMessageHandler {

  private final DataService dataService;

  public MqttBrokerMessageHandler(DataService dataService) {
    this.dataService = dataService;
  }

  @Bean
  @ServiceActivator(inputChannel = "mqttInputChannel")
  public MessageHandler mqttMessageHandler() {
    return message -> {
      String payload = message.getPayload().toString();
      dataService.processAndSendData(payload);
    };
  }
}
