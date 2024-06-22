package at.aau.streptrack.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class MqttBrokerMessageHandler {

  private static final Logger log = LoggerFactory.getLogger(MqttBrokerMessageHandler.class);

  private final DataService dataService;

  public MqttBrokerMessageHandler(DataService dataService) {
    this.dataService = dataService;
  }

  @Bean
  @ServiceActivator(inputChannel = "mqttInputChannel")
  public MessageHandler mqttMessageHandler() {
    return message -> {
      log.info("Received message from MQTT-broker - [message='{}']", message);

      String payload = message.getPayload().toString();

      dataService.processAndSendData(payload);
    };
  }
}
