package api;

import java.util.UUID;

public abstract class SimulationRequest {
    private final UUID simulationId;

    protected SimulationRequest(UUID simulationId) {
        this.simulationId = simulationId;
    }
}
