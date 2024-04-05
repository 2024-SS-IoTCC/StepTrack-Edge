package at.aau.streptrack.edge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

@Configuration(proxyBeanMethods = false)
class InboundConfiguration {

  @Bean
  MqttPahoMessageDrivenChannelAdapter inboundAdapter(@Value("${hivemq.topic}") String topic,
                                                     MqttPahoClientFactory clientFactory) {
    return new MqttPahoMessageDrivenChannelAdapter("consumer", clientFactory, topic);
  }

  @Bean
  IntegrationFlow inboundFlow(MqttPahoMessageDrivenChannelAdapter inboundAdapter) {
    return IntegrationFlow.from(inboundAdapter)
        .handle(new InboundMessageHandler())
        .get();
  }

}
