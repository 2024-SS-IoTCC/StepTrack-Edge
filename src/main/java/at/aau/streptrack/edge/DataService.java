package at.aau.streptrack.edge;

import at.aau.streptrack.edge.model.SensorData;
import java.util.Optional;

import org.openapitools.client.api.StepsApi;
import org.openapitools.client.model.MainStepData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataService {
  private static final Logger log = LoggerFactory.getLogger(DataService.class);

  private final StepsApi stepsApi;

  public DataService(StepsApi stepsApi) {
    this.stepsApi = stepsApi;
  }

  public void processAndSendData(String payload) {
    Optional<SensorData> stepDataSensor = StepDataTransformer.parsePayload(payload);

    if (stepDataSensor.isEmpty()) {
      return;
    }

    Optional<MainStepData> mainStepDataOptional =
        StepDataTransformer.transform(stepDataSensor.get());

    if (mainStepDataOptional.isEmpty()) {
      return;
    }

    MainStepData mainStepData = mainStepDataOptional.get();

    try {
      log.info("Sending step data to cloud...");
      stepsApi.stepsPost(mainStepData);
    } catch (Exception e) {
      log.error("Error sending step data; abort - [stepData='{}']", mainStepData, e);

      return;
    }

    log.info("Step data sent successfully - [stepData='{}']", mainStepData);
  }
}
