package at.aau.streptrack.edge.model;

import java.time.LocalDateTime;

public record CloudData(
    String username, int steps, LocalDateTime startTime, LocalDateTime endTime) {

  public static CloudData of(
      String username, int steps, LocalDateTime startTime, LocalDateTime endTime) {
    return new CloudData(username, steps, startTime, endTime);
  }
}
