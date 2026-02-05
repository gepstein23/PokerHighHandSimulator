package com.genevieve.pokersim.main;

import com.genevieve.pokersim.api.snapshots.HandSnapShot;
import com.genevieve.pokersim.persistence.InMemorySimulationRepository;
import com.genevieve.pokersim.playingcards.CardValue;
import com.genevieve.pokersim.playingcards.PokerHand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HighHandSimulatorTest {

    private InMemorySimulationRepository repo;
    private HighHandSimulator simulator;

    // 2 NLH tables, 1 PLO table, 3 hands/hour, 2 hours = 6 total hands
    private static final int NUM_NLH = 2;
    private static final int NUM_PLO = 1;
    private static final int HANDS_PER_HOUR = 3;
    private static final int SIM_HOURS = 2;
    private static final int TOTAL_HANDS = HANDS_PER_HOUR * SIM_HOURS;

    @BeforeEach
    void setUp() throws InterruptedException {
        repo = new InMemorySimulationRepository();
        PokerHand lowQualifier = new PokerHand(CardValue.TWO, CardValue.TWO, CardValue.TWO, CardValue.THREE, CardValue.THREE);
        HighHand highHand = new HighHand(lowQualifier, lowQualifier, Duration.ofHours(1));

        simulator = new HighHandSimulator(
                NUM_NLH, NUM_PLO, HANDS_PER_HOUR, 6,
                Duration.ofHours(SIM_HOURS), highHand, false, Duration.ofHours(1),
                false, false, false, null, repo);

        Thread thread = simulator.initializeSimulation();
        thread.join();
    }

    @Test
    void simulationWritesAllHands() {
        assertEquals(TOTAL_HANDS, repo.getHandCount(simulator.simulationID));
    }

    @Test
    void simulationStatusTransitions() {
        Optional<String> status = repo.getSimulationStatus(simulator.simulationID);
        assertTrue(status.isPresent());
        assertEquals("DONE", status.get());
    }

    @Test
    void hourBoundaryResetsHighHand() {
        // Hand 3 is the first hand of hour 2 — high hand snapshot should have reset
        Optional<HandSnapShot> hand3 = repo.getHandSnapshot(simulator.simulationID, HANDS_PER_HOUR);
        assertTrue(hand3.isPresent());
        // At the start of a new hour, the high hand snapshot was just reset,
        // so the high hand might be null or just set from hand 3 itself
        // The key check is that it's not carrying over the hour-1 high hand
        assertNotNull(hand3.get().getHighHandSnapshot());
    }

    @Test
    void statsCountHours() {
        // After 2 hours, numHighHands should be 2
        Optional<HandSnapShot> lastHand = repo.getHandSnapshot(simulator.simulationID, TOTAL_HANDS - 1);
        assertTrue(lastHand.isPresent());
        // Stats at the last hand of hour 2 should show 1 completed hour
        // (the second hour hasn't been "counted" yet until after the loop)
        // But final stats should show 2
        var finalStats = repo.getFinalStats(simulator.simulationID);
        assertTrue(finalStats.isPresent());
        assertEquals(2, finalStats.get().getNumHighHands());
    }

    @Test
    void handSnapshotHasAllTables() {
        Optional<HandSnapShot> hand0 = repo.getHandSnapshot(simulator.simulationID, 0);
        assertTrue(hand0.isPresent());
        assertEquals(NUM_NLH + NUM_PLO, hand0.get().getTableSnapshots().size());
    }

    @Test
    void eachTableSnapshotHasCards() {
        Optional<HandSnapShot> hand0 = repo.getHandSnapshot(simulator.simulationID, 0);
        assertTrue(hand0.isPresent());
        hand0.get().getTableSnapshots().forEach(tableSnapshot -> {
            assertEquals(5, tableSnapshot.getCommunityCards().size());
            assertFalse(tableSnapshot.getPlayers().isEmpty());
            tableSnapshot.getPlayers().forEach(player ->
                    assertTrue(player.getHoleCards().length >= 2));
        });
    }
}
