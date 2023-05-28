package simulation_datas;

import main.PokerHand;

import java.util.Map;

public class TableSimulationData {
    private Map<Long, PokerHand> tableHighHandPerSimulationHour;
    private boolean isPloTable;

    public TableSimulationData(Map<Long, PokerHand> tableHighHandPerSimulationHour, boolean isPloTable) {
        this.tableHighHandPerSimulationHour = tableHighHandPerSimulationHour;
        this.isPloTable = isPloTable;
    }

    public Map<Long, PokerHand> getTableHighHandPerSimulationHour() {
        return tableHighHandPerSimulationHour;
    }

    public void setTableHighHandPerSimulationHour(Map<Long, PokerHand> tableHighHandPerSimulationHour) {
        this.tableHighHandPerSimulationHour = tableHighHandPerSimulationHour;
    }

    public boolean isPloTable() {
        return isPloTable;
    }

    public void setPloTable(boolean ploTable) {
        isPloTable = ploTable;
    }
}
