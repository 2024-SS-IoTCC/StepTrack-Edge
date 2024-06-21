package at.aau.streptrack.edge;

import at.aau.streptrack.edge.model.CloudData;
import at.aau.streptrack.edge.model.SensorData;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DataService {
  private static final Logger log = LoggerFactory.getLogger(DataService.class);

  private final RestClient restClient;

  @Value("${dashboard.api.url}")
  private String apiUrl;

  public DataService() {
    restClient = RestClient.create(apiUrl);
  }

  public void processAndSendData(String payload) {
    Optional<SensorData> stepDataSensor = StepDataTransformer.parsePayload(payload);

    if (stepDataSensor.isEmpty()) {
      return;
    }

    Optional<CloudData> stepDataCloud = StepDataTransformer.transform(stepDataSensor.get());

    if (stepDataCloud.isEmpty()) {
      return;
    }

    try {
      restClient.post().uri("/api/steps").body(stepDataCloud.get()).retrieve().toBodilessEntity();
    } catch (Exception e) {
      log.error("Error sending step data", e);
    }

    log.info("Data posted successfully");
  }
}
