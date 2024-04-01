package simulation_datas;

import playingcards.PokerHand;

import java.util.Map;
import java.util.UUID;

public class TableSimulationData {
    private final UUID tableID;
    private Map<Long, PokerHand> tableHighHandPerSimulationHour;
    private boolean isPloTable;

    public TableSimulationData(UUID tableID, Map<Long, PokerHand> tableHighHandPerSimulationHour, boolean isPloTable) {
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
