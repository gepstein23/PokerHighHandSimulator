package com.genevieve.pokersim.persistence;

import com.genevieve.pokersim.api.snapshots.HandSnapShot;
import com.genevieve.pokersim.api.snapshots.StatsSnapshot;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of SimulationRepository.
 * Data is lost on application restart.
 */
public class InMemorySimulationRepository implements SimulationRepository {

    private final Map<UUID, Map<Integer, HandSnapShot>> snapshots = new ConcurrentHashMap<>();
    private final Map<UUID, String> statuses = new ConcurrentHashMap<>();
    private final Map<UUID, StatsSnapshot> finalStats = new ConcurrentHashMap<>();

    @Override
    public void saveHandSnapshot(UUID simulationId, int handNum, HandSnapShot snapshot) {
        snapshots.computeIfAbsent(simulationId, k -> new ConcurrentHashMap<>())
                .put(handNum, snapshot);
    }

    @Override
    public Optional<HandSnapShot> getHandSnapshot(UUID simulationId, int handNum) {
        Map<Integer, HandSnapShot> simSnapshots = snapshots.get(simulationId);
        if (simSnapshots == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(simSnapshots.get(handNum));
    }

    @Override
    public int getHandCount(UUID simulationId) {
        Map<Integer, HandSnapShot> simSnapshots = snapshots.get(simulationId);
        return simSnapshots == null ? 0 : simSnapshots.size();
    }

    @Override
    public void saveSimulationStatus(UUID simulationId, String status) {
        statuses.put(simulationId, status);
    }

    @Override
    public Optional<String> getSimulationStatus(UUID simulationId) {
        return Optional.ofNullable(statuses.get(simulationId));
    }

    @Override
    public void saveFinalStats(UUID simulationId, StatsSnapshot stats) {
        finalStats.put(simulationId, stats);
    }

    @Override
    public Optional<StatsSnapshot> getFinalStats(UUID simulationId) {
        return Optional.ofNullable(finalStats.get(simulationId));
    }
}
