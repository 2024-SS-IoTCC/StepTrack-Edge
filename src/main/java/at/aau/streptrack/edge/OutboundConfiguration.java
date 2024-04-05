package at.aau.streptrack.edge;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
class OutboundConfiguration {

  @Bean
  MessageChannel out() {
    return MessageChannels.direct().getObject();
  }

  @Bean
  RouterFunction<ServerResponse> routes(MessageChannel out) {
    return route()
        .GET("/send/{name}", request -> sendMessage(out, request))
        .build();
  }

  private static Mono<ServerResponse> sendMessage(MessageChannel out, ServerRequest request) {
    var name = request.pathVariable("name");
    var message = MessageBuilder.withPayload("Hello %s".formatted(name)).build();

    out.send(message);

    return ServerResponse.ok().build();
  }

  @Bean
  MqttPahoMessageHandler outboundAdapter(@Value("${hivemq.topic}") String topic, MqttPahoClientFactory clientFactory) {
    var messageHandler = new MqttPahoMessageHandler("producer", clientFactory);

    messageHandler.setDefaultTopic(topic);

    return messageHandler;
  }

  @Bean
  IntegrationFlow outboundFlow(MessageChannel out, MqttPahoMessageHandler outboundAdapter) {
    return IntegrationFlow.from(out)
        .handle(outboundAdapter)
        .get();
  }

}
