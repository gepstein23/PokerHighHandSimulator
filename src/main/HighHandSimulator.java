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

    public HighHandSimulator(int numNlhTables, int numPloTables, int numHandsPerHour, int numPlayersPerTable,
                             Duration simulationDuration, HighHand highHand, boolean shouldFilterPreflop, Duration highHandDuration, boolean noPloFlopRestriction, boolean ploTurnRestriction) {
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
    }

    public void runSimulation() {
        final Collection<PokerTable> tables = createTables(numNlhTables, numPloTables, numHandsPerHour,
                shouldFilterPreflop, numPlayersPerTable, noPloFlopRestriction, ploTurnRestriction);
        log(this.toString());
        final SimulationData data = initSimulation(tables, highHand, simulationDuration);
        displaySimulationResults(tables, data);
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
        final SimulationData data = new SimulationData(hourSimulationDatas, tableSimulationDatas, totalNumHH);
        log(String.format("""
                        =============== SIMULATION RESULTS ===============
                        NLH won %s/%s times (%.2f%%)
                        PLO won %s/%s times (%.2f%%)
                        --------------------------------------------------
                        NLH %.2f%%, PLO %.2f%%
                        ==================================================
                        """,
                data.getNumNlhWins(), totalNumHH, data.getNlhWinPercent(),
                data.getNumPloWins(), totalNumHH, data.getPloWinPercent(), data.getNlhWinPercent(), data.getPloWinPercent()));
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
        final PokerRoomAnimation animation = new PokerRoomAnimation(new ArrayList<>(tables), data);
        animation.initUI();
    }
}
