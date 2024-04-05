package at.aau.streptrack.edge;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class EdgeApplicationTests {

  @Container
  static final HiveMQContainer hivemqCe = new HiveMQContainer(
      DockerImageName.parse("hivemq/hivemq-ce").withTag("2024.3")
  ).withLogLevel(Level.DEBUG).withExposedPorts(1883);

  @Test
  void contextLoads() {}

}
