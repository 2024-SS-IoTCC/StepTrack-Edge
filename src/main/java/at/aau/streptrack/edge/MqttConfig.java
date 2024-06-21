package at.aau.streptrack.edge;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration(proxyBeanMethods = false)
public class MqttConfig {

  @Bean
  public MqttPahoClientFactory mqttClientFactory(@Value("${mqtt.broker.url}") String brokerUrl) {
    var factory = new DefaultMqttPahoClientFactory();
    var options = new MqttConnectOptions();

    options.setServerURIs(new String[] {brokerUrl});
    factory.setConnectionOptions(options);

    return factory;
  }

  @Bean
  public MessageChannel mqttInputChannel() {
    return new DirectChannel();
  }

  @Bean
  public MqttPahoMessageDrivenChannelAdapter inboundAdapter(
      MqttPahoClientFactory mqttClientFactory,
      MessageChannel mqttInputChannel,
      @Value("${mqtt.topic}") String topic) {
    var adapter = new MqttPahoMessageDrivenChannelAdapter("consumer", mqttClientFactory, topic);

    adapter.setCompletionTimeout(5000);
    adapter.setConverter(new DefaultPahoMessageConverter());
    adapter.setQos(1);
    adapter.setOutputChannel(mqttInputChannel);

    return adapter;
  }
}
