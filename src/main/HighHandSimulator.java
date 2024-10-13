package main;

import animation.PlayedHandData;
import animation.PokerRoomAnimation;
import api.snapshots.HandSnapShot;
import api.snapshots.HighHandSnapshot;
import api.snapshots.StatsSnapshot;
import playingcards.PokerHand;
import simulation_datas.HourSimulationData;
import simulation_datas.SimulationData;
import simulation_datas.TableSimulationData;
import tables.NLHTable;
import tables.PLOTable;
import tables.PokerTable;
import tables.PokerTableHistory;

import java.time.Duration;
import java.util.*;

import static main.Utils.log;

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
    private final Collection<PokerTable> tables;
    private SimulationData simulationData = null;
    public UUID simulationID;

    // only used for api
    public Map<Integer, HandSnapShot> handNumToHandSnapshot;

    public HighHandSimulator(int numNlhTables, int numPloTables, int numHandsPerHour, int numPlayersPerTable,
                             Duration simulationDuration, HighHand highHand, boolean shouldFilterPreflop, Duration highHandDuration, boolean noPloFlopRestriction, boolean ploTurnRestriction, boolean animate) {
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
    }

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
    }

    public SimulationData runSimulation() throws InterruptedException {
        log(this.toString());
        final SimulationData data = initSimulation(tables, highHand, simulationDuration);
        this.simulationData = data;
     //   displaySimulationResults(tables, data);
        return data;
      //  log(data.toString());
    }

    public Thread initializeSimulation() throws InterruptedException {
        log(this.toString());

        Thread asyncCommandThread = new Thread(() -> {
            try {
                final SimulationData data = initSimulation(tables, highHand, simulationDuration);
                this.simulationData = data;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
     //   displaySimulationResults(tables, data);
      //  log(data.toString());
        asyncCommandThread.start();
        return asyncCommandThread;
    }

    private SimulationData initSimulation(Collection<PokerTable> tables, HighHand highHand, Duration duration) throws InterruptedException {
        final List<TableSimulationData> tableSimulationDatas = new ArrayList<>();
        for (PokerTable table : tables) {
            tableSimulationDatas.add(table.runSimulation(highHand, duration));
        }
        return determineSimulationWinners(tableSimulationDatas);
    }

    private static Collection<PokerTable> createTables(int numNlhTables, int numPloTables, double tableHandsPerHour,
                                                       boolean shouldFilterPreflop, int numPlayersPerTable, boolean noPloFlopRestriction, boolean ploTurnRestriction) {
        final Collection<PokerTable> tables = new ArrayList<>();
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

    private Collection<PokerTable> createTables(Collection<Integer> nlhTablePlayers,
                                                Collection<Integer> ploTablePlayers,
                                                int numHandsPerHour, boolean shouldFilterPreflop,
                                                boolean noPloFlopRestriction, boolean ploTurnRestriction) {
        final Collection<PokerTable> tables = new ArrayList<>();
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
    public SimulationData getSimulationData() {
        return simulationData;
    }

    public Collection<PokerTable> getTables() {
        return tables;
    }

    public void generateApiSnapshots() {
        if (this.simulationData == null) {
            return;
        }

        if (!this.handNumToHandSnapshot.isEmpty()) {
            return;
        }

        // Here, only adding played hand data to handNumToHandSnapshot
        for (PokerTable pokerTable : tables) {
            PokerTableHistory history = pokerTable.getHistory();
            HashMap<Integer, PlayedHandData> handNumToHandData = history.getHandNumToHandData();
            for (Map.Entry<Integer, PlayedHandData> entry : handNumToHandData.entrySet()) {
                HandSnapShot handSnapshot = this.handNumToHandSnapshot.getOrDefault(entry.getKey(), new HandSnapShot(entry.getKey()));
                // Get current handSnapshot, add this table's hand details
                handSnapshot.getTableSnapshots().add(entry.getValue());
                handNumToHandSnapshot.putIfAbsent(entry.getKey(), handSnapshot);
            }
        }

        // Next, iteratively determine high hand over each hand
        int hourHandsRemaining = numHandsPerHour;
        HighHandSnapshot currHighHandSnapshot = new HighHandSnapshot();
        StatsSnapshot statsSnapshot = new StatsSnapshot();
        for (HandSnapShot handSnapShot : this.handNumToHandSnapshot.values()) {
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

            handSnapShot.setHighHandSnapshot(currHighHandSnapshot);
            handSnapShot.setStatsSnapshot(statsSnapshot);
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
