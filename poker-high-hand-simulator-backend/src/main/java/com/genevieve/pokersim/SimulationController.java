package com.genevieve.pokersim;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin(origins = {"https://pokersim.genevieveepstein.com", "http://pokersim.genevieveepstein.com"})
public class SimulationController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();


    private static final boolean shouldFilterPreflopDefault = false;
    private static final boolean ploTurnRestrictionDefault = false;
    private static final boolean animateDefault = false;

    private static final int MAX_SIMS_PER_IP_PER_DAY = 10;
    private static final int MAX_SIMS_PER_DAY = 500;
    private static final int MAX_CONCURRENT_SIMS = 3;
    private static final long CLEANUP_DELAY_HOURS = 1;

    private final SimulationRepository repository = createRepository();
    private Map<UUID, HighHandSimulator> simulationMap = new HashMap<>();
    private Map<UUID, PokerRoomAnimation> simulationAnimationMap = new HashMap<>();

    // Concurrent simulation tracking
    private final AtomicInteger runningSimulations = new AtomicInteger();

    // Scheduled cleanup of completed simulations
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "sim-cleanup");
        t.setDaemon(true);
        return t;
    });

    // Rate limiting state — resets daily
    private volatile LocalDate rateLimitDate = LocalDate.now(ZoneOffset.UTC);
    private final AtomicInteger dailyTotal = new AtomicInteger();
    private final ConcurrentHashMap<String, AtomicInteger> dailyPerIp = new ConcurrentHashMap<>();


    @GetMapping("/greeting")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return ResponseEntity.ok("hello!");
    }
    // Start Simulation
    @PostMapping("/simulations/start")
    public ResponseEntity<?> startSimulation(@RequestBody SimulationStartRequest request, HttpServletRequest httpRequest) throws InterruptedException {
        // Input validation
        String validationError = validateRequest(request);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }

        // Concurrent simulation limit
        if (runningSimulations.get() >= MAX_CONCURRENT_SIMS) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many simulations running. Please wait for a simulation to complete and try again."));
        }

        // Rate limiting
        String clientIp = getClientIp(httpRequest);
        resetRateLimitsIfNewDay();

        if (dailyTotal.get() >= MAX_SIMS_PER_DAY) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Daily simulation limit reached. Please try again tomorrow."));
        }

        int ipCount = dailyPerIp.computeIfAbsent(clientIp, k -> new AtomicInteger()).get();
        if (ipCount >= MAX_SIMS_PER_IP_PER_DAY) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "You have reached the limit of " + MAX_SIMS_PER_IP_PER_DAY + " simulations per day. Please try again tomorrow."));
        }

        dailyTotal.incrementAndGet();
        dailyPerIp.get(clientIp).incrementAndGet();
        runningSimulations.incrementAndGet();

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

        HighHandSimulator highHandSimulator = new HighHandSimulator(numNlhTables, numPloTables, numHandsPerHour,
                numPlayersPerTable, simulationDuration, highHand, shouldFilterPreflop, highHandDuration,
                noPloFlopRestriction, ploTurnRestriction, animate, notificationPhoneNumber, repository);

        UUID simId = highHandSimulator.simulationID;
        highHandSimulator.setOnComplete(() -> {
            runningSimulations.decrementAndGet();
            cleanupExecutor.schedule(() -> {
                simulationMap.remove(simId);
                repository.clearSimulation(simId);
                System.out.println("Cleaned up simulation " + simId + " from memory");
            }, CLEANUP_DELAY_HOURS, TimeUnit.HOURS);
        });

        highHandSimulator.initializeSimulation();
        simulationMap.put(simId, highHandSimulator);
        return ResponseEntity.accepted().body(simId);
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

    private String validateRequest(SimulationStartRequest request) {
        int nlh = request.getNumNlhTables();
        int plo = request.getNumPloTables();
        int players = request.getNumPlayersPerTable();
        int handsPerHour = request.getNumHandsPerHour();
        int duration = request.getSimulationDuration();

        if (nlh < 0 || nlh > 20) {
            return "numNlhTables must be between 0 and 20.";
        }
        if (plo < 0 || plo > 20) {
            return "numPloTables must be between 0 and 20.";
        }
        if (nlh + plo < 1) {
            return "At least 1 table is required (numNlhTables + numPloTables >= 1).";
        }
        if (nlh + plo > 20) {
            return "Total tables (numNlhTables + numPloTables) must not exceed 20.";
        }
        if (players < 2 || players > 10) {
            return "numPlayersPerTable must be between 2 and 10.";
        }
        if (handsPerHour < 1 || handsPerHour > 50) {
            return "numHandsPerHour must be between 1 and 50.";
        }
        if (duration < 1 || duration > 10000) {
            return "simulationDuration must be between 1 and 10,000 hours.";
        }
        long totalHands = (long) duration * handsPerHour;
        if (totalHands > 100_000) {
            return "Total hands (simulationDuration * numHandsPerHour) must not exceed 100,000. Requested: " + totalHands + ".";
        }
        return null;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    private HighHand parseHighHand(String nlhMinimumQualifyingHand, String ploMinimumQualifyingHand, Duration highHandDuration) {
        return new HighHand(PokerHand.from(nlhMinimumQualifyingHand), PokerHand.from(ploMinimumQualifyingHand), highHandDuration);
    }

    private void resetRateLimitsIfNewDay() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        if (!today.equals(rateLimitDate)) {
            synchronized (this) {
                if (!today.equals(rateLimitDate)) {
                    dailyTotal.set(0);
                    dailyPerIp.clear();
                    rateLimitDate = today;
                }
            }
        }
    }

    private static String getClientIp(HttpServletRequest request) {
        // X-Forwarded-For is set by API Gateway
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
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
