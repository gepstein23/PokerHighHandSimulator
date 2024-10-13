package tables;

import animation.PlayedHandData;

import java.util.HashMap;
import java.util.UUID;

/**
 * Easy API data grabber
 */
public class PokerTableHistory {

    UUID tableID;
    HashMap<Integer, PlayedHandData> handNumToHandData;

    public PokerTableHistory(UUID tableID) {
        this.tableID = tableID;
        this.handNumToHandData = new HashMap<>();
    }

    public UUID getTableID() {
        return tableID;
    }

    public HashMap<Integer, PlayedHandData> getHandNumToHandData() {
        return handNumToHandData;
    }

    public void addHandData(PlayedHandData data, int handNum) {
        this.handNumToHandData.put(handNum, data);
    }

}
