package com.genevieve.pokersim;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.genevieve.pokersim.animation.PokerRoomAnimation;
import com.genevieve.pokersim.api.SimulationStartRequest;
import com.genevieve.pokersim.api.snapshots.HandSnapShot;
import com.genevieve.pokersim.main.HighHand;
import com.genevieve.pokersim.main.HighHandSimulator;
import com.genevieve.pokersim.playingcards.PokerHand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin(origins = "genevieveepstein.com")
public class SimulationController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();


    private static final boolean shouldFilterPreflopDefault = false;
    private static final boolean ploTurnRestrictionDefault = false;
    private static final boolean animateDefault = false;

    private Map<UUID, HighHandSimulator> simulationMap = new HashMap<>();
    private Map<UUID, PokerRoomAnimation> simulationAnimationMap = new HashMap<>();


    @GetMapping("/greeting")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return ResponseEntity.ok("hello!");
    }
    // Start Simulation
    @PostMapping("/simulations/start")
    public ResponseEntity<UUID> startSimulation(@RequestBody SimulationStartRequest request) throws InterruptedException {
        int numNlhTables = request.getNumNlhTables();
        int numPloTables = request.getNumPloTables();
        int numHandsPerHour = request.getNumHandsPerHour();
        int numPlayersPerTable = request.getNumPlayersPerTable();
        Duration highHandDuration = Duration.ofHours(1);
        Duration simulationDuration = Duration.ofHours(request.getSimulationDuration());
        HighHand highHand = parseHighHand(request.getNlhMinimumQualifyingHand(), request.getPloMinimumQualifyingHand(), highHandDuration);
        boolean shouldFilterPreflop = shouldFilterPreflopDefault;
        boolean noPloFlopRestriction = request.isNoPloFlopRestriction();
        boolean ploTurnRestriction = ploTurnRestrictionDefault;
        boolean animate = animateDefault;

        HighHandSimulator highHandSimulator = new HighHandSimulator(numNlhTables, numPloTables, numHandsPerHour,
                numPlayersPerTable, simulationDuration, highHand, shouldFilterPreflop, highHandDuration, noPloFlopRestriction, ploTurnRestriction, animate);
        highHandSimulator.initializeSimulation();
        simulationMap.put(highHandSimulator.simulationID, highHandSimulator);
        return ResponseEntity.accepted().body(highHandSimulator.simulationID);
    }

    @GetMapping("/")
    public ResponseEntity<String> get() {
        return null;
    }

    @GetMapping("/simulations/{simulationID}/status")
    public ResponseEntity<String> getSimulationStatus( @PathVariable UUID simulationID) {
        if (! simulationMap.containsKey(simulationID)) {
            throw new IllegalArgumentException("Simulation does not exist: " + simulationID);
        }
        HighHandSimulator highHandSimulator = simulationMap.get(simulationID);
        if (highHandSimulator.getSimulationData() == null) {
            return ResponseEntity.ok("IN_PROGRESS");
        }
        return ResponseEntity.ok().body("DONE"); // TODO enums
    }

    @GetMapping("/simulations/{simulationID}/hands/{handNum}")
    public ResponseEntity<HandSnapShot.HandSnapshotApiModel> getNextSimulationData(@PathVariable UUID simulationID, @PathVariable int handNum) {
        if (!simulationMap.containsKey(simulationID)) {
            throw new IllegalArgumentException(String.format("Simulation [%s] does not exist.", simulationID));
        }
        HighHandSimulator highHandSimulator = simulationMap.get(simulationID);
        if (highHandSimulator.getSimulationData() == null) {
            throw new IllegalArgumentException(String.format("Simulation [%s] is not finished.", simulationID));
        }

        // First hand => must gather data
        if (handNum == 0) {
            highHandSimulator.generateApiSnapshots();
        } else {
            if (highHandSimulator.handNumToHandSnapshot.isEmpty()) {
                throw new IllegalArgumentException(String.format("You must first call this API with handNum=0 for simulation [%s].", simulationID));
            }
        }

        HandSnapShot simulationSnapshot = highHandSimulator.getSnapshot(handNum);
        if (simulationSnapshot == null) {
            throw new IllegalArgumentException(String.format("There is no hand with handNum=%s for simulation [%s].", handNum, simulationID));
        }
        HandSnapShot.HandSnapshotApiModel model = simulationSnapshot.transform();
        return ResponseEntity.ok().body(model);
    }

    private HighHand parseHighHand(String nlhMinimumQualifyingHand, String ploMinimumQualifyingHand, Duration highHandDuration) {
        return new HighHand(PokerHand.from(nlhMinimumQualifyingHand), PokerHand.from(ploMinimumQualifyingHand), highHandDuration);
    }
}
