package api;

import java.util.UUID;

public class SimulationStopRequest extends SimulationRequest{
    protected SimulationStopRequest(UUID simulationId) {
        super(simulationId);
    }
}
