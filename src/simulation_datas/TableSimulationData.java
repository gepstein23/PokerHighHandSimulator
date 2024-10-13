package simulation_datas;

import playingcards.PokerHand;
import tables.PokerTableHistory;

import java.util.Map;
import java.util.UUID;

public class TableSimulationData {
    private final UUID tableID;
    private final PokerTableHistory history;
    private Map<Long, PokerHand> tableHighHandPerSimulationHour;
    private boolean isPloTable;

    public TableSimulationData(UUID tableID, Map<Long, PokerHand> tableHighHandPerSimulationHour, boolean isPloTable, PokerTableHistory his) {
        this.history = his;
        this.tableHighHandPerSimulationHour = tableHighHandPerSimulationHour;
        this.isPloTable = isPloTable;
        this.tableID = tableID;
    }

    public Map<Long, PokerHand> getTableHighHandPerSimulationHour() {
        return tableHighHandPerSimulationHour;
    }

    public boolean isPloTable() {
        return isPloTable;
    }

    public UUID getTableID() {
        return tableID;
    }
}
