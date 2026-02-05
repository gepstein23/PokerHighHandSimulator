package com.genevieve.pokersim.persistence;

import com.genevieve.pokersim.api.snapshots.HandSnapShot;
import com.genevieve.pokersim.api.snapshots.StatsSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySimulationRepositoryTest {

    private InMemorySimulationRepository repo;
    private UUID simId;

    @BeforeEach
    void setUp() {
        repo = new InMemorySimulationRepository();
        simId = UUID.randomUUID();
    }

    @Test
    void saveAndGetHandSnapshot() {
        HandSnapShot snapshot = new HandSnapShot(0);
        repo.saveHandSnapshot(simId, 0, snapshot);

        Optional<HandSnapShot> result = repo.getHandSnapshot(simId, 0);
        assertTrue(result.isPresent());
        assertEquals(0, result.get().getHandNum());
    }

    @Test
    void getHandSnapshot_notFound() {
        Optional<HandSnapShot> result = repo.getHandSnapshot(UUID.randomUUID(), 0);
        assertTrue(result.isEmpty());

        // Sim exists but hand doesn't
        repo.saveHandSnapshot(simId, 0, new HandSnapShot(0));
        Optional<HandSnapShot> result2 = repo.getHandSnapshot(simId, 99);
        assertTrue(result2.isEmpty());
    }

    @Test
    void getHandCount() {
        assertEquals(0, repo.getHandCount(simId));

        repo.saveHandSnapshot(simId, 0, new HandSnapShot(0));
        repo.saveHandSnapshot(simId, 1, new HandSnapShot(1));
        repo.saveHandSnapshot(simId, 2, new HandSnapShot(2));

        assertEquals(3, repo.getHandCount(simId));
    }

    @Test
    void saveAndGetStatus() {
        repo.saveSimulationStatus(simId, "IN_PROGRESS");

        Optional<String> status = repo.getSimulationStatus(simId);
        assertTrue(status.isPresent());
        assertEquals("IN_PROGRESS", status.get());
    }

    @Test
    void getStatus_notFound() {
        Optional<String> status = repo.getSimulationStatus(UUID.randomUUID());
        assertTrue(status.isEmpty());
    }

    @Test
    void saveAndGetFinalStats() {
        StatsSnapshot stats = new StatsSnapshot();
        stats.addHour(true, false);  // PLO win
        stats.addHour(false, true);  // NLH win
        stats.addHour(true, false);  // PLO win

        repo.saveFinalStats(simId, stats);

        Optional<StatsSnapshot> result = repo.getFinalStats(simId);
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getNumHighHands());
        assertEquals(2, result.get().getNumPloWins());
        assertEquals(1, result.get().getNumHoldEmWins());
    }

    @Test
    void statusOverwrite() {
        repo.saveSimulationStatus(simId, "IN_PROGRESS");
        assertEquals("IN_PROGRESS", repo.getSimulationStatus(simId).get());

        repo.saveSimulationStatus(simId, "DONE");
        assertEquals("DONE", repo.getSimulationStatus(simId).get());
    }
}
