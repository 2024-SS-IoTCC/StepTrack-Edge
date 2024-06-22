package at.aau.streptrack.edge;

import at.aau.streptrack.edge.model.SensorData;
import at.aau.streptrack.edge.model.StepEvent;
import at.aau.streptrack.edge.openapi.model.MainStepData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StepDataTransformer {
  private static final Logger log = LoggerFactory.getLogger(StepDataTransformer.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private StepDataTransformer() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static Optional<SensorData> parsePayload(String payload) {
    try {
      log.info("Parsing payload...");

      return Optional.ofNullable(MAPPER.readValue(payload, SensorData.class));
    } catch (Exception e) {
      log.error("Error parsing payload; return empty", e);

      return Optional.empty();
    }
  }

  public static Optional<MainStepData> transform(SensorData sensorData) {
    log.info("Transforming sensor data...");
    var username = sensorData.username();

    if (StringUtils.isBlank(username)) {
      log.error("Empty username; abort");

      return Optional.empty();
    }

    var stepEvents = sensorData.stepEvents();

    if (stepEvents == null || stepEvents.size() < 2) {
      log.error("Not enough step events; abort");

      return Optional.empty();
    }

    if (stepEvents.stream().anyMatch(stepEvent -> stepEvent == null || stepEvent.steps() < 0)) {
      log.error("Steps are negative or null; abort");

      return Optional.empty();
    }

    if (stepEvents.stream().anyMatch(stepEvent -> stepEvent == null || stepEvent.timestamp() < 0)) {
      log.error("Timestamps are negative or null; abort");

      return Optional.empty();
    }

    log.info("Calculating dates and steps...");
    StepEvent firstStepEvent = stepEvents.getFirst();
    StepEvent lastStepEvent = stepEvents.getLast();
    var totalSteps = lastStepEvent.steps() - firstStepEvent.steps();
    var startTime = formatDateTime(firstStepEvent.timestamp());
    var endTime = formatDateTime(lastStepEvent.timestamp());

    var mainStepData =
        new MainStepData().username(username).steps(totalSteps).start(startTime).end(endTime);

    return Optional.of(mainStepData);
  }

  public static String formatDateTime(long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        .format(DateTimeFormatter.ISO_DATE_TIME);
  }
}
