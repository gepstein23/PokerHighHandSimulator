package com.genevieve.pokersim.main;

import com.genevieve.pokersim.animation.PlayedHandData;
import com.genevieve.pokersim.animation.PokerRoomAnimation;
import com.genevieve.pokersim.api.snapshots.HandSnapShot;
import com.genevieve.pokersim.api.snapshots.HighHandSnapshot;
import com.genevieve.pokersim.api.snapshots.StatsSnapshot;
import com.genevieve.pokersim.playingcards.PokerHand;
import com.genevieve.pokersim.simulation_datas.HourSimulationData;
import com.genevieve.pokersim.simulation_datas.SimulationData;
import com.genevieve.pokersim.simulation_datas.TableSimulationData;
import com.genevieve.pokersim.persistence.SimulationRepository;
import com.genevieve.pokersim.tables.HandResult;
import com.genevieve.pokersim.tables.NLHTable;
import com.genevieve.pokersim.tables.PLOTable;
import com.genevieve.pokersim.tables.PokerTable;
import com.genevieve.pokersim.tables.PokerTableHistory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.genevieve.pokersim.main.Utils.log;

public class HighHandSimulator {
    private final int numNlhTables;
    private final int numPloTables;
    private final int numHandsPerHour;
    private final int numPlayersPerTable;
    private final Duration highHandDuration;
    private final Duration simulationDuration;
    private final HighHand highHand;
    private final boolean shouldFilterPreflop;
    private final boolean noPloFlopRestriction;
    private final boolean ploTurnRestriction;
    private final boolean animate;
    private final List<PokerTable> tables;
    private final String notifPhoneNumber;
    private final SimulationRepository repository;
    private Runnable onComplete;
    private SimulationData simulationData = null;
    public UUID simulationID;

    // only used for api
    public Map<Integer, HandSnapShot> handNumToHandSnapshot;

    public HighHandSimulator(int numNlhTables, int numPloTables, int numHandsPerHour, int numPlayersPerTable,
                             Duration simulationDuration, HighHand highHand, boolean shouldFilterPreflop, Duration highHandDuration,
                             boolean noPloFlopRestriction, boolean ploTurnRestriction, boolean animate,
                             String notificationPhoneNumber, SimulationRepository repository) {
        this.simulationID = UUID.randomUUID();
        this.numNlhTables = numNlhTables;
        this.numPloTables = numPloTables;
        this.numHandsPerHour = numHandsPerHour;
        this.numPlayersPerTable = numPlayersPerTable;
        this.simulationDuration = simulationDuration;
        this.highHand = highHand;
        this.noPloFlopRestriction = noPloFlopRestriction;
        this.ploTurnRestriction = ploTurnRestriction;
        this.shouldFilterPreflop = shouldFilterPreflop; //TODO
        this.highHandDuration = highHandDuration; //TODO
        this.animate = animate;
        this.tables = createTables(numNlhTables, numPloTables, numHandsPerHour,
                shouldFilterPreflop, numPlayersPerTable, noPloFlopRestriction, ploTurnRestriction);
        this.handNumToHandSnapshot = new HashMap<>();
        this.notifPhoneNumber = notificationPhoneNumber;
        this.repository = repository;
    }

    /**
     * @deprecated Use constructor with SimulationRepository parameter instead.
     */
    @Deprecated
    public HighHandSimulator(Collection<Integer> nlhTablePlayers, Collection<Integer> ploTablePlayers,  int numHandsPerHour,
                             Duration simulationDuration, HighHand highHand, boolean shouldFilterPreflop, Duration highHandDuration,
                             boolean noPloFlopRestriction, boolean ploTurnRestriction, boolean animate) {
        this.simulationID = UUID.randomUUID();
        this.numNlhTables = nlhTablePlayers.size();
        this.numPloTables = ploTablePlayers.size();
        this.numPlayersPerTable = -1;
        this.numHandsPerHour = numHandsPerHour;
        this.simulationDuration = simulationDuration;
        this.highHand = highHand;
        this.noPloFlopRestriction = noPloFlopRestriction;
        this.ploTurnRestriction = ploTurnRestriction;
        this.shouldFilterPreflop = shouldFilterPreflop; //TODO
        this.highHandDuration = highHandDuration; //TODO
        this.animate = animate;
        this.tables = createTables(nlhTablePlayers, ploTablePlayers, numHandsPerHour,
                shouldFilterPreflop, noPloFlopRestriction, ploTurnRestriction);
        this.handNumToHandSnapshot = new HashMap<>();
        this.notifPhoneNumber = null;
        this.repository = null;
    }

    /**
     * @deprecated Use initializeSimulation() with repository-based persistence instead.
     */
    @Deprecated
    public SimulationData runSimulation() throws InterruptedException {
        log(this.toString());
        final SimulationData data = initSimulation(tables, highHand, simulationDuration);
        this.simulationData = data;
     //   displaySimulationResults(tables, data);
        return data;
      //  log(data.toString());
    }

    /**
     * Starts the hand-by-hand simulation in a background thread.
     * Each hand is persisted to the repository as it completes.
     */
    public Thread initializeSimulation() {
        log(this.toString());

        if (repository != null) {
            repository.saveSimulationStatus(simulationID, "IN_PROGRESS");
        }

        Thread asyncCommandThread = new Thread(() -> {
            try {
                if (repository != null) {
                    runHandByHandSimulation();
                } else {
                    // Fallback to legacy behavior for deprecated constructor
                    final SimulationData data = initSimulation(tables, highHand, simulationDuration);
                    this.simulationData = data;
                }
                if (repository != null) {
                    repository.saveSimulationStatus(simulationID, "DONE");
                }
                notifyUser();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        asyncCommandThread.start();
        return asyncCommandThread;
    }

    /**
     * Run the simulation hand-by-hand across all tables.
     * Persists each hand snapshot to the repository as it completes.
     */
    private void runHandByHandSimulation() throws InterruptedException {
        final int totalHands = (int) (simulationDuration.toHours() * numHandsPerHour);
        int handsPlayedInCurrentHour = 0;
        HighHandSnapshot currentHighHandSnapshot = new HighHandSnapshot();
        StatsSnapshot statsSnapshot = new StatsSnapshot();

        for (int handNum = 0; handNum < totalHands; handNum++) {
            // Hour boundary check → update stats if needed
            if (handsPlayedInCurrentHour == numHandsPerHour) {
                recordHourWinner(statsSnapshot, currentHighHandSnapshot);
                currentHighHandSnapshot = new HighHandSnapshot();
                handsPlayedInCurrentHour = 0;
            }

            // Play hand N on every table in parallel
            final int currentHandNum = handNum;
            List<HandResult> results = tables.parallelStream()
                    .map(table -> table.playSingleHand(currentHandNum, highHand))
                    .collect(Collectors.toList());

            HandSnapShot handSnapshot = new HandSnapShot(handNum);
            PokerHand bestQualifyingHand = null;
            Boolean bestIsPlo = null;
            UUID bestTableId = null;

            for (HandResult result : results) {
                handSnapshot.getTableSnapshots().add(result.getPlayedHandData());

                // Check if this table's hand beats the current best for this hand
                if (result.isQualifiesForHighHand()) {
                    if (beatsCurrentHighHand(result.getWinningHand(), bestQualifyingHand)) {
                        bestQualifyingHand = result.getWinningHand();
                        bestIsPlo = result.isPlo();
                        bestTableId = result.getTableId();
                    }
                }
            }

            // Update the global high hand if this hand beat it
            if (beatsCurrentHighHand(bestQualifyingHand, currentHighHandSnapshot.getHighHand())) {
                currentHighHandSnapshot = new HighHandSnapshot();
                currentHighHandSnapshot.setHighHand(bestQualifyingHand);
                currentHighHandSnapshot.setPlo(bestIsPlo);
                currentHighHandSnapshot.setTableID(bestTableId);
            }

            handSnapshot.setHighHandSnapshot(currentHighHandSnapshot);
            handSnapshot.setStatsSnapshot(statsSnapshot.deepCopy());
            repository.saveHandSnapshot(simulationID, handNum, handSnapshot);

            // Also populate the legacy map for backwards compatibility
            handNumToHandSnapshot.put(handNum, handSnapshot);

            handsPlayedInCurrentHour++;
        }

        // Final hour stats
        recordHourWinner(statsSnapshot, currentHighHandSnapshot);
        repository.saveFinalStats(simulationID, statsSnapshot);

        // Mark as complete for legacy getSimulationData() checks
        this.simulationData = new SimulationData(new ArrayList<>(), new ArrayList<>(),
                simulationDuration.toHours(), numHandsPerHour);
    }

    private void recordHourWinner(StatsSnapshot statsSnapshot, HighHandSnapshot highHandSnapshot) {
        if (highHandSnapshot.getHighHand() == null) {
            statsSnapshot.addHour(false, false);
        } else if (highHandSnapshot.getPlo() != null && highHandSnapshot.getPlo()) {
            statsSnapshot.addHour(true, false);
        } else {
            statsSnapshot.addHour(false, true);
        }
    }

    private void notifyUser() {
        if (this.notifPhoneNumber == null || this.notifPhoneNumber.length() < 7) {
            return; // TODO better validation
        }


        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_2)
                .build();
        try {

            String message = "Poker High Hand Simulation " + simulationID + " is complete! Navigate to https://genevieveepstein.com to see the results.";

            // Create a Publish request for SMS
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(notifPhoneNumber)
                    .build();

            // Send SMS
            PublishResponse response = snsClient.publish(request);
            System.out.println("Message sent. Message ID: " + response.messageId());
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
        snsClient.close();
    }

    /**
     * @deprecated Use runHandByHandSimulation() instead.
     */
    @Deprecated
    private SimulationData initSimulation(Collection<PokerTable> tables, HighHand highHand, Duration duration) throws InterruptedException {
        final List<TableSimulationData> tableSimulationDatas = new ArrayList<>();
        for (PokerTable table : tables) {
            tableSimulationDatas.add(table.runSimulation(highHand, duration));
        }
        return determineSimulationWinners(tableSimulationDatas);
    }

    private static List<PokerTable> createTables(int numNlhTables, int numPloTables, double tableHandsPerHour,
                                                       boolean shouldFilterPreflop, int numPlayersPerTable, boolean noPloFlopRestriction, boolean ploTurnRestriction) {
        final List<PokerTable> tables = new ArrayList<>();
        for (int i = 0; i < numNlhTables; i++) {
            final PokerTable nlhTable = new NLHTable(numPlayersPerTable, tableHandsPerHour, shouldFilterPreflop);
            tables.add(nlhTable);
        }
        for (int i = 0; i < numPloTables; i++) {
            final PokerTable ploTable = new PLOTable(numPlayersPerTable, tableHandsPerHour, shouldFilterPreflop, noPloFlopRestriction, ploTurnRestriction);
            tables.add(ploTable);
        }
        return tables;
    }

    private List<PokerTable> createTables(Collection<Integer> nlhTablePlayers,
                                                Collection<Integer> ploTablePlayers,
                                                int numHandsPerHour, boolean shouldFilterPreflop,
                                                boolean noPloFlopRestriction, boolean ploTurnRestriction) {
        final List<PokerTable> tables = new ArrayList<>();
        for (Integer numNlhPlayersAtTable : nlhTablePlayers) {
            final PokerTable nlhTable = new NLHTable(numNlhPlayersAtTable, numHandsPerHour, shouldFilterPreflop);
            tables.add(nlhTable);
        }
        for (Integer numPloPlayersAtTable : ploTablePlayers) {
            final PokerTable ploTable = new PLOTable(numPloPlayersAtTable, numHandsPerHour, shouldFilterPreflop, noPloFlopRestriction, ploTurnRestriction);
            tables.add(ploTable);
        }
        return tables;
    }

    /**
     * @deprecated Statistics are now computed during hand-by-hand simulation.
     */
    @Deprecated
    private SimulationData determineSimulationWinners(List<TableSimulationData> tableSimulationDatas) {
        final List<HourSimulationData> hourSimulationDatas = new ArrayList<>();
        for (long i = 0; i < simulationDuration.toHours(); i++) {
            PokerHand highHandAtHour = null;
            Boolean isPlo = null;
            for (TableSimulationData tableSimulationData : tableSimulationDatas) {
                final PokerHand tableHighHandAtHour = tableSimulationData.getTableHighHandPerSimulationHour().get(i + 1);
                if (tableHighHandAtHour == null) {
                    continue;
                }
                if (highHandAtHour == null) {
                    highHandAtHour = tableHighHandAtHour;
                    isPlo = tableSimulationData.isPloTable();
                    continue;
                }
                if (tableHighHandAtHour.compare(highHandAtHour) > 0) {
                    highHandAtHour = tableHighHandAtHour;
                    isPlo = tableSimulationData.isPloTable();
                }
            }
            hourSimulationDatas.add(new HourSimulationData(i, highHandAtHour, isPlo));
        }

        final long totalNumHH = simulationDuration.toHours() / highHandDuration.toHours();
        final SimulationData data = new SimulationData(hourSimulationDatas, tableSimulationDatas, totalNumHH, numHandsPerHour);
        final long totalNumHHWon = data.getNumNlhWins() + data.getNumPloWins();
        final double nlhPercent = data.getNumNlhWins() * 1.0 / totalNumHHWon * 100.0;
        final double ploPercent = data.getNumPloWins() * 1.0 / totalNumHHWon * 100.0;
        log(String.format("""
                        =============== SIMULATION RESULTS ===============
                        NLH won %s/%s times (%.2f%%)
                        PLO won %s/%s times (%.2f%%)
                        --------------------------------------------------
                        NLH %.2f%%, PLO %.2f%%
                        ==================================================
                        """,
                data.getNumNlhWins(), totalNumHHWon, nlhPercent,
                data.getNumPloWins(), totalNumHHWon, ploPercent, nlhPercent, ploPercent));
        return data;
    }

    @Override
    public String toString() {
        return String.format("================ HighHandSimulator ================\n"
                + "numNlhTables=%s\n"
                + "numPloTables=%s\n"
                + "numHandsPerHour=%s\n"
                + "numPlayersPerTable=%s\n"
                + "simulationDuration=%s\n"
                + "highHand=%s\n"
                + "shouldFilterPreflop=%s\n",
                numNlhTables, numPloTables, numHandsPerHour, numPlayersPerTable, simulationDuration, highHand, shouldFilterPreflop);
    }

    private void displaySimulationResults(Collection<PokerTable> tables, SimulationData data) {
        if (!animate) {
            return;
        }
        final PokerRoomAnimation animation = new PokerRoomAnimation(new ArrayList<>(tables), data, false);
        animation.initUI();
    }
    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    public SimulationData getSimulationData() {
        return simulationData;
    }

    public List<PokerTable> getTables() {
        return tables;
    }

    /**
     * @deprecated Snapshots are now generated during hand-by-hand simulation and saved to repository.
     */
    @Deprecated
    public void generateApiSnapshots() {
        if (this.simulationData == null) {
            return;
        }

        if (!this.handNumToHandSnapshot.isEmpty()) {
            return;
        }

        // Here, only adding played hand data to handNumToHandSnapshot
        for (PokerTable pokerTable : tables) {
            for (int i = 0; i<pokerTable.getPlayedHands().size();i++) {
                HandSnapShot handSnapshot = this.handNumToHandSnapshot.getOrDefault(i, new HandSnapShot(i));
                // Get current handSnapshot, add this table's hand details
                handSnapshot.getTableSnapshots().add(pokerTable.getPlayedHands().get(i));
                handNumToHandSnapshot.putIfAbsent(i, handSnapshot);
            }
        }

        // Next, iteratively determine high hand over each hand
        int hourHandsRemaining = numHandsPerHour;
        HighHandSnapshot currHighHandSnapshot = new HighHandSnapshot();
        StatsSnapshot statsSnapshot = new StatsSnapshot();
        List<HandSnapShot> values = new ArrayList<>(this.handNumToHandSnapshot.values());
        for (int i = 0; i < values.size() ; i++) {
            HandSnapShot handSnapShot = values.get(i);
            if (hourHandsRemaining == 0) {  // Hour has passed, reset high hand

                // Update (dont reset) the stats
                if (currHighHandSnapshot.getHighHand() == null) { // there was NOT!!! a HH
                    statsSnapshot.addHour(false, false);
                } else {
                    if (currHighHandSnapshot.getPlo()) {
                        statsSnapshot.addHour(true, false);
                    } else {
                        statsSnapshot.addHour(false, true);
                    }
                }

                // Reset the HH board and handsRemaining
                hourHandsRemaining = numHandsPerHour;
                currHighHandSnapshot = new HighHandSnapshot();
            }

            // Now, process the current hand (reuse algo from animation)
            PokerHand qualifyingHandForHandNum = null;
            Boolean isPlo = null;
            UUID tableId = null;
            for (PlayedHandData tableSnapshot : handSnapShot.getTableSnapshots()) {
                if (tableSnapshot.isQualifiesForHighHand()) {
                    if (beatsCurrentHighHand(tableSnapshot.getWinningHand(), qualifyingHandForHandNum)) {
                        qualifyingHandForHandNum = tableSnapshot.getWinningHand();
                        isPlo = tableSnapshot.isPlo;
                        tableId = tableSnapshot.getTableId();
                    }
                }
            }

            // Update the high hand for all tables if applicable
            PokerHand currentHighHand = currHighHandSnapshot.getHighHand();
            if (beatsCurrentHighHand(qualifyingHandForHandNum, currentHighHand)) {
                // Set high hand
                currHighHandSnapshot = new HighHandSnapshot();
                currHighHandSnapshot.setHighHand(qualifyingHandForHandNum);
                currHighHandSnapshot.setPlo(isPlo);
                currHighHandSnapshot.setTableID(tableId);
            }

            if (i == values.size() - 1) {
                // Update (dont reset) the stats
                if (currHighHandSnapshot.getHighHand() == null) { // there was NOT!!! a HH
                    statsSnapshot.addHour(false, false);
                } else {
                    if (currHighHandSnapshot.getPlo()) {
                        statsSnapshot.addHour(true, false);
                    } else {
                        statsSnapshot.addHour(false, true);
                    }
                }
            }

            handSnapShot.setHighHandSnapshot(currHighHandSnapshot);
            handSnapShot.setStatsSnapshot(statsSnapshot.deepCopy());
            hourHandsRemaining--;
        }
    }

    private boolean beatsCurrentHighHand(PokerHand winningQualifyingHand, PokerHand currentHighHand) {
        if (winningQualifyingHand == null) {
            return false;
        }
        if (currentHighHand == null) {
            return true;
        }
        return winningQualifyingHand.compare(currentHighHand) > 0;
    }

    public HandSnapShot getSnapshot(int handNum) {
        return handNumToHandSnapshot.get(handNum);
    }

//    public SimulationSnapshot getNextData() {
//        if (simulationData == null) {
//            return null;
//        }
//
//        if (this.simulationIterator == null) {
//            this.simulationIterator = new SimulationIterator(simulationData, tables);
//            return this.simulationIterator.getNextSnapshot();
//        } else {
//            return this.simulationIterator.getNextSnapshot();
//        }
//    }

//    class SimulationIterator {
//
//        private final SimulationData simulationData;
//        Collection<TableIterator> tableIterators;
//
//        public SimulationIterator(SimulationData simulationData, Collection<PokerTable> tables) {
//            this.simulationData = simulationData;
//            this.tableIterators = tables.stream().map(TableIterator::new).collect(Collectors.toList());
//        }
//
//        public SimulationSnapshot getNextSnapshot() {
//            PokerHand newHighHand = null;
//            boolean handsRemaining = false;
//            UUID newTableID = null;
//            for (TableIterator tableIterator : this.tableIterators) {
//                boolean thisHandsRemaining = tableIterator.hasHand();
//                handsRemaining |= thisHandsRemaining;
//                if (thisHandsRemaining) {
//                    final PokerTableSnapshot pokerTableSnapshot = new PokerTableSnapshot();
//                    if (tableIterator.currentHandData.) { // potentialQualifier
//                        if (beatsCurrentHighHand(winningQualifyingHand)) {
//                            newHighHand = winningQualifyingHand;
//                            newTableID = panel.getTableID();
//                        }
//                    }
//                }
//            }
//            if (newHighHand != null) {
//                this.currentHighHand = newHighHand;
//                this.highHandTableUUID = newTableID;
//                displayNewHighHand();
//            }
//            if (!handsRemaining) {
//                displaySimulationResultsPanel();
//            }
//            int currHandIndex = tablePanels.iterator().next().getCurrHandIndex();
//            if (currHandIndex % numHandsPerHour == 0 && handsRemaining) {
//                updateHighHandSummaryBoard(); // Call the method when an hour of play is completed
//                resetCurrentHighHand();
//            }
//            return handsRemaining;
//        }
//
//        private class TableIterator {
//            private final List<PlayedHandData> playedHandData;
//            private final UUID tabelId;
//            private int currentHandIndex = 0;
//            private PlayedHandData currentHandData;
//
//            public TableIterator(PokerTable table) {
//                this.playedHandData = table.getPlayedHands();
//                this.tabelId = table.getTableID();
//            }
//
//            public boolean hasHand() {
//                return currentHandIndex < playedHandData.size();
//            }
//            public PokerHand getNextHand() {
//                this.currentHandData = playedHandData.get(currentHandIndex);
//                return currentHandData.
//            }
//
//        }
    }
//}
