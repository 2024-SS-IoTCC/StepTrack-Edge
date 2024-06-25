package at.aau.streptrack.edge;

import at.aau.streptrack.edge.model.SensorData;
import at.aau.streptrack.edge.model.StepEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.client.model.MainStepData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StepDataTransformer {
  private static final Logger log = LoggerFactory.getLogger(StepDataTransformer.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private StepDataTransformer() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static Optional<SensorData> parsePayload(String payload) {
    if (StringUtils.isBlank(payload)) {
      log.error("Empty payload; return empty");

      return Optional.empty();
    }

    try {
      log.info("Parsing payload...");

      return Optional.ofNullable(MAPPER.readValue(payload, SensorData.class));
    } catch (Exception e) {
      log.error("Error parsing payload; return empty", e);

      return Optional.empty();
    }
  }

  public static Optional<MainStepData> transform(SensorData sensorData) {
    if (sensorData == null) {
      log.error("Empty sensor data; abort");

      return Optional.empty();
    }

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

    if (stepEvents.stream().anyMatch(Objects::isNull)) {
      log.error("Some step event is null; abort");

      return Optional.empty();
    }

    if (stepEvents.stream().anyMatch(stepEvent -> stepEvent.steps() < 0)) {
      log.error("Some steps are negative; abort");

      return Optional.empty();
    }

    if (stepEvents.stream().anyMatch(stepEvent -> stepEvent.timestamp() < 0)) {
      log.error("Some timestamp is negative; abort");

      return Optional.empty();
    }

    log.info("Calculating dates and steps...");
    StepEvent firstStepEvent = stepEvents.getFirst();
    StepEvent lastStepEvent = stepEvents.getLast();

    if (firstStepEvent.steps() > lastStepEvent.steps()) {
      log.error("Steps from first event are bigger than steps from last event; abort");

      return Optional.empty();
    }

    var totalSteps = lastStepEvent.steps() - firstStepEvent.steps();
    var startTime = formatDateTime(firstStepEvent.timestamp());
    var endTime = formatDateTime(lastStepEvent.timestamp());

    var mainStepData =
        new MainStepData().username(username).steps(totalSteps).start(startTime).end(endTime);

    return Optional.of(mainStepData);
  }

  private static String formatDateTime(long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        .format(DateTimeFormatter.ISO_DATE_TIME);
  }
}
