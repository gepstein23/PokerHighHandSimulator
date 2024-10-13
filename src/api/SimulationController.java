
package api;

import animation.PokerRoomAnimation;
import api.snapshots.HandSnapShot;
import main.HighHand;
import main.HighHandSimulator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import playingcards.PokerHand;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SimulationController {

    private static final boolean shouldFilterPreflopDefault = false;
    private static final boolean ploTurnRestrictionDefault = false;
    private static final boolean animateDefault = false;

    private Map<UUID, HighHandSimulator> simulationMap = new HashMap<>();
    private Map<UUID, PokerRoomAnimation> simulationAnimationMap = new HashMap<>();

    // Start Simulation
    @PostMapping("/simulations/start")
    public ResponseEntity<UUID> startSimulation(@RequestBody SimulationStartRequest request) {
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
        try {
            highHandSimulator.initializeSimulation();
            simulationMap.put(highHandSimulator.simulationID, highHandSimulator);
            return new ResponseEntity<>(highHandSimulator.simulationID, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (InterruptedException e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/simulations/{simulationID}/status")
    public ResponseEntity<String> getSimulationStatus( @PathVariable UUID simulationID) {
       if (! simulationMap.containsKey(simulationID)) {
           return ResponseEntity.badRequest().body("Simulation does not exist: " + simulationID);
       }
        HighHandSimulator highHandSimulator = simulationMap.get(simulationID);
       if (highHandSimulator.getSimulationData() == null) {
           return ResponseEntity.ok().body("IN_PROGRESS");
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
        return ResponseEntity.accepted().body(model);
    }

    private HighHand parseHighHand(String nlhMinimumQualifyingHand, String ploMinimumQualifyingHand, Duration highHandDuration) {
        return new HighHand(PokerHand.from(nlhMinimumQualifyingHand), PokerHand.from(ploMinimumQualifyingHand), highHandDuration);
    }

//    // Start Simulation
//    @PostMapping("/simulations/stop")
//    public ResponseEntity<Response> terminateSimulation(@RequestBody SimulationStopRequest request) {
//        stopSimulation();
//        return new ResponseEntity<>(HttpStatus.ACCEPTED);
//    }
//
//    // Method to stop the simulation
//    public void stopSimulation() {
//        if (simulationThread != null) {
//            simulationThread.interrupt(); // Interrupt the thread if it's still running
//            try {
//                simulationThread.join(); // Wait for the thread to finish'
//                simulationThread = null;
//            } catch (InterruptedException e) {
//                // Handle interruption during join
//                Thread.currentThread().interrupt(); // Restore interrupt status
//            }
//        }
//        System.out.println("Simulation stopped.");
//    }
}

