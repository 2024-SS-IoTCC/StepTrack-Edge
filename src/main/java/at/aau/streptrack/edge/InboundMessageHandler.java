//package at.aau.streptrack.edge;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.integration.core.GenericHandler;
//import org.springframework.messaging.MessageHeaders;
//
//public class InboundMessageHandler implements GenericHandler<String> {
//
//  private static final Logger log = LoggerFactory.getLogger(InboundMessageHandler.class);
//
//  @Override
//  public Object handle(String payload, MessageHeaders headers) {
//    log.info("new message: {}", payload);
//    headers.forEach((s, o) -> log.info("{}: {}", s, o));
//
//    return null;
//  }
//}
