package main;

import animation.PokerRoomAnimation;
import playingcards.PokerHand;
import simulation_datas.HourSimulationData;
import simulation_datas.SimulationData;
import simulation_datas.TableSimulationData;
import tables.NLHTable;
import tables.PLOTable;
import tables.PokerTable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public HighHandSimulator(int numNlhTables, int numPloTables, int numHandsPerHour, int numPlayersPerTable,
                             Duration simulationDuration, HighHand highHand, boolean shouldFilterPreflop, Duration highHandDuration, boolean noPloFlopRestriction, boolean ploTurnRestriction, boolean animate) {
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
    }

    public HighHandSimulator(Collection<Integer> nlhTablePlayers, Collection<Integer> ploTablePlayers,  int numHandsPerHour,
                             Duration simulationDuration, HighHand highHand, boolean shouldFilterPreflop, Duration highHandDuration,
                             boolean noPloFlopRestriction, boolean ploTurnRestriction, boolean animate) {
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
    }

    public SimulationData runSimulation() {
        log(this.toString());
        final SimulationData data = initSimulation(tables, highHand, simulationDuration);

        displaySimulationResults(tables, data);
        return data;
      //  log(data.toString());
    }

    private SimulationData initSimulation(Collection<PokerTable> tables, HighHand highHand, Duration duration) {
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
        final PokerRoomAnimation animation = new PokerRoomAnimation(new ArrayList<>(tables), data);
        animation.initUI();
    }
}
