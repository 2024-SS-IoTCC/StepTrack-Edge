package at.aau.streptrack.edge;

import static org.junit.jupiter.api.Assertions.*;

import at.aau.streptrack.edge.model.SensorData;
import at.aau.streptrack.edge.model.StepEvent;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

class StepDataTransformerTest {

  @Nested
  class ParsePayload {

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnEmpty_whenPayloadEmpty(String payload) {
      var stepDataCloud = StepDataTransformer.parsePayload(payload);

      assertTrue(stepDataCloud.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenPayloadInvalid() {
      var stepDataCloud = StepDataTransformer.parsePayload("invalid");

      assertTrue(stepDataCloud.isEmpty());
    }

    @Test
    void shouldReturnSensorData_whenPayloadValid() {
      var payload =
          """
          {
            "stepEvents": [
              {
                "steps": 56588,
                "timestamp": "1714570191948"
              },
              {
                "steps": 56592,
                "timestamp": "1714570194783"
              },
              {
                "steps": 56593,
                "timestamp": "1714570195725"
              },
              {
                "steps": 56594,
                "timestamp": "1714570196307"
              }
            ],
            "username": "name"
          }
          """;

      var stepDataCloud = StepDataTransformer.parsePayload(payload);

      assertTrue(stepDataCloud.isPresent());

      SensorData sensorData = stepDataCloud.get();
      assertEquals("name", sensorData.username());
      assertEquals(4, sensorData.stepEvents().size());
    }
  }

  @Nested
  class Transform {

    @ParameterizedTest
    @NullSource
    void shouldReturnEmpty_whenSensorDataNull(SensorData sensorData) {
      var mainStepData = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepData.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenUsernameEmpty() {
      var sensorData = new SensorData("", Collections.emptyList());
      var mainStepData = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepData.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenStepEventsEmpty() {
      var sensorData = new SensorData("name", Collections.emptyList());
      var mainStepData = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepData.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenStepEventsLessThanTwo() {
      var sensorData = new SensorData("name", Collections.singletonList(new StepEvent(1, 1)));
      var mainStepData = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepData.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenStepEventsInvalid() {
      var sensorData = new SensorData("name", Collections.singletonList(null));
      var mainStepData = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepData.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenStepEventsStepsNegative() {
      var sensorData = new SensorData("name", Collections.singletonList(new StepEvent(-1, 1)));
      var mainStepData = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepData.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenStepEventsTimestampNegative() {
      var sensorData = new SensorData("name", Collections.singletonList(new StepEvent(1, -1)));
      var mainStepData = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepData.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenFirstStepEventGreaterThanLastStepEvent() {
      var sensorData = new SensorData("name", List.of(new StepEvent(1, 1), new StepEvent(0, 0)));
      var mainStepData = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepData.isEmpty());
    }

    @Test
    void shouldReturnMainStepData_whenSensorDataValid() {
      var sensorData =
          new SensorData(
              "name",
              List.of(
                  new StepEvent(56588, 1714570191948L),
                  new StepEvent(56592, 1714570194783L),
                  new StepEvent(56593, 1714570195725L),
                  new StepEvent(56594, 1714570196307L)));
      var mainStepDataOptional = StepDataTransformer.transform(sensorData);

      assertTrue(mainStepDataOptional.isPresent());

      var mainStepData = mainStepDataOptional.get();
      assertEquals("name", mainStepData.getUsername());
      assertEquals(6, mainStepData.getSteps());
      assertEquals("2024-05-01T15:29:51.948", mainStepData.getStart());
      assertEquals("2024-05-01T15:29:56.307", mainStepData.getEnd());
    }
  }
}
