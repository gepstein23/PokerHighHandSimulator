package main;

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
import java.util.Map;

import static main.Utils.log;

public class HighHandSimulator {
    private int numNlhTables;
    private int numPloTables;
    private int numHandsPerHour;
    private int numPlayersPerTable;
    private Duration simulationDuration;
    private HighHand highHand;

    public HighHandSimulator(int numNlhTables, int numPloTables, int numHandsPerHour, int numPlayersPerTable,
                             Duration simulationDuration, HighHand highHand) {
        this.numNlhTables = numNlhTables;
        this.numPloTables = numPloTables;
        this.numHandsPerHour = numHandsPerHour;
        this.numPlayersPerTable = numPlayersPerTable;
        this.simulationDuration = simulationDuration;
        this.highHand = highHand;
    }

    public void runSimulation() {
        final Collection<PokerTable> tables = createTables(numNlhTables, numPloTables, numHandsPerHour);
        log(this.toString());
        final SimulationData data = initSimulation(tables, highHand, simulationDuration);
        System.out.println(data);

        final int totalNumNlhPlayers = numNlhTables * numPlayersPerTable;
        final int totalNumPloPlayers = numPloTables * numPlayersPerTable;
        final long nlhWinRatio = data.getNumNlhWins() / totalNumNlhPlayers;
        final long ploWinRatio = data.getNumPloWins() / totalNumPloPlayers;
        log(String.format("totalNumNlhPlayers=%s, totalNumPloPlayers=%s\nnlhWinRatio=%s\nploWinRatio=%s", totalNumNlhPlayers, totalNumPloPlayers, nlhWinRatio, ploWinRatio));
    }

    private SimulationData initSimulation(Collection<PokerTable> tables, HighHand highHand, Duration duration) {
        final List<TableSimulationData> tableSimulationDatas = new ArrayList<>();
        for (PokerTable table : tables) {
            tableSimulationDatas.add(table.runSimulation(highHand, duration));
        }
        return determineSimulationWinners(tableSimulationDatas);
    }

    private static Collection<PokerTable> createTables(int numNlhTables, int numPloTables, double tableHandsPerHour) {
        final Collection<PokerTable> tables = new ArrayList<>();
        for (int i = 0; i < numNlhTables; i++) {
            final PokerTable nlhTable = new NLHTable(numNlhTables, tableHandsPerHour);
            tables.add(nlhTable);
        }
        for (int i = 0; i < numPloTables; i++) {
            final PokerTable ploTable = new PLOTable(numPloTables, tableHandsPerHour);
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
        return new SimulationData(hourSimulationDatas);
    }

    @Override
    public String toString() {
        return String.format("================ HighHandSimulator ================\n"
                + "numNlhTables=%s\n"
                + "numPloTables=%s\n"
                + "numHandsPerHour=%s\n"
                + "numPlayersPerTable=%s\n"
                + "simulationDuration=%s\n"
                + "highHand=%s\n",
                numNlhTables, numPloTables, numHandsPerHour, numPlayersPerTable, simulationDuration, highHand);
    }
}
