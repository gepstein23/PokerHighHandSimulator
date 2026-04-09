package com.genevieve.pokersim.persistence;

import com.genevieve.pokersim.api.snapshots.HandSnapShot;
import com.genevieve.pokersim.api.snapshots.StatsSnapshot;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for persisting simulation data.
 * Allows hand-by-hand snapshots to be saved and queried during simulation execution.
 */
public interface SimulationRepository {

    /**
     * Save a hand snapshot for a specific simulation and hand number.
     */
    void saveHandSnapshot(UUID simulationId, int handNum, HandSnapShot snapshot);

    /**
     * Retrieve a hand snapshot for a specific simulation and hand number.
     */
    Optional<HandSnapShot> getHandSnapshot(UUID simulationId, int handNum);

    /**
     * Get the count of hands saved for a simulation.
     */
    int getHandCount(UUID simulationId);

    /**
     * Save the simulation status (e.g., "IN_PROGRESS", "DONE").
     */
    void saveSimulationStatus(UUID simulationId, String status);

    /**
     * Get the simulation status.
     */
    Optional<String> getSimulationStatus(UUID simulationId);

    /**
     * Save final stats after simulation completes.
     */
    void saveFinalStats(UUID simulationId, StatsSnapshot stats);

    /**
     * Get final stats for a completed simulation.
     */
    Optional<StatsSnapshot> getFinalStats(UUID simulationId);

    /**
     * Remove all data for a simulation (snapshots, status, stats).
     * Used to free memory after a simulation has been completed and consumed.
     */
    void clearSimulation(UUID simulationId);
}
