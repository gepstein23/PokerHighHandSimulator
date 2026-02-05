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
import com.genevieve.pokersim.persistence.DynamoDBSimulationRepository;
import com.genevieve.pokersim.persistence.InMemorySimulationRepository;
import com.genevieve.pokersim.persistence.SimulationRepository;
import com.genevieve.pokersim.playingcards.PokerHand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin(origins = "http://genevieveepstein.com:3000")
public class SimulationController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();


    private static final boolean shouldFilterPreflopDefault = false;
    private static final boolean ploTurnRestrictionDefault = false;
    private static final boolean animateDefault = false;

    private final SimulationRepository repository = createRepository();
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
        String notificationPhoneNumber = request.getNotificationPhoneNumber();

        // TODO first verify phone number here
        // TODO validate params

        HighHandSimulator highHandSimulator = new HighHandSimulator(numNlhTables, numPloTables, numHandsPerHour,
                numPlayersPerTable, simulationDuration, highHand, shouldFilterPreflop, highHandDuration,
                noPloFlopRestriction, ploTurnRestriction, animate, notificationPhoneNumber, repository);
        highHandSimulator.initializeSimulation();
        simulationMap.put(highHandSimulator.simulationID, highHandSimulator);
        return ResponseEntity.accepted().body(highHandSimulator.simulationID);
    }

    @GetMapping("/")
    public ResponseEntity<String> get() {
        return null;
    }

    @GetMapping("/simulations/{simulationID}/status")
    public ResponseEntity<String> getSimulationStatus(@PathVariable UUID simulationID) {
        return repository.getSimulationStatus(simulationID)
                .map(status -> ResponseEntity.ok().body(status))
                .orElseThrow(() -> new IllegalArgumentException("Simulation does not exist: " + simulationID));
    }

    @GetMapping("/simulations/{simulationID}/hands/{handNum}")
    public ResponseEntity<HandSnapShot.HandSnapshotApiModel> getNextSimulationData(@PathVariable UUID simulationID, @PathVariable int handNum) {
        // Check if simulation exists
        if (!repository.getSimulationStatus(simulationID).isPresent()) {
            throw new IllegalArgumentException(String.format("Simulation [%s] does not exist.", simulationID));
        }

        // Try to get the hand snapshot from repository (works during simulation)
        return repository.getHandSnapshot(simulationID, handNum)
                .map(snapshot -> ResponseEntity.ok().body(snapshot.transform()))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Hand %s is not yet available for simulation [%s].", handNum, simulationID)));
    }

    @GetMapping("/simulations/{simulationID}/progress")
    public ResponseEntity<Map<String, Object>> getProgress(@PathVariable UUID simulationID) {
        String status = repository.getSimulationStatus(simulationID)
                .orElseThrow(() -> new IllegalArgumentException("Simulation does not exist: " + simulationID));
        int handsCompleted = repository.getHandCount(simulationID);

        Map<String, Object> progress = new HashMap<>();
        progress.put("status", status);
        progress.put("handsCompleted", handsCompleted);
        return ResponseEntity.ok(progress);
    }

    private HighHand parseHighHand(String nlhMinimumQualifyingHand, String ploMinimumQualifyingHand, Duration highHandDuration) {
        return new HighHand(PokerHand.from(nlhMinimumQualifyingHand), PokerHand.from(ploMinimumQualifyingHand), highHandDuration);
    }

    private static SimulationRepository createRepository() {
        String dynamoDbTable = System.getenv("DYNAMODB_TABLE");
        String awsRegion = System.getenv("AWS_REGION");

        if (dynamoDbTable != null && !dynamoDbTable.isEmpty()
                && awsRegion != null && !awsRegion.isEmpty()) {
            System.out.println("Using DynamoDB repository: table=" + dynamoDbTable + ", region=" + awsRegion);
            return new DynamoDBSimulationRepository(dynamoDbTable, awsRegion);
        }

        System.out.println("DYNAMODB_TABLE or AWS_REGION not set, using in-memory repository");
        return new InMemorySimulationRepository();
    }
}
