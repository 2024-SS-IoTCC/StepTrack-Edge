package at.aau.streptrack.edge;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StepDataTransformerTest {

  @Test
  void parsePayload() {
    var payload =
        """
        {
          "stepEvents": [
            {
              "steps": 56588,
              "timestamp": "1714570191948"
            },
            {
              "steps": 56589,
              "timestamp": "1714570193000"
            },
            {
              "steps": 56590,
              "timestamp": "1714570193696"
            },
            {
              "steps": 56591,
              "timestamp": "1714570194258"
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
    var stepDataSensor = StepDataTransformer.transform(stepDataCloud.get());

    assertEquals(1, 1);
  }

  @Test
  void calculateTotalSteps() {}

  @Test
  void convertTimestampToDateTime() {}
}
