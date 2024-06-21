package at.aau.streptrack.edge.model;

import java.util.List;

public record SensorData(String username, List<StepEvent> stepEvents) {}
