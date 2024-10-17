package com.genevieve.pokersim.api;

import java.util.UUID;

public abstract class SimulationRequest {
    public UUID getSimulationId() {
        return simulationId;
    }

    private final UUID simulationId;

    protected SimulationRequest(UUID simulationId) {
        this.simulationId = simulationId;
    }
}
