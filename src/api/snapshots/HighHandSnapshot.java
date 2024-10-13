package api.snapshots;

import playingcards.PokerHand;

import java.util.UUID;

public class HighHandSnapshot {

    PokerHand highHand;
    UUID tableID;

    public Boolean getPlo() {
        return isPlo;
    }

    public void setPlo(Boolean plo) {
        isPlo = plo;
    }

    Boolean isPlo;

    public HighHandSnapshot() {
        // all null
    }

    public PokerHand getHighHand() {
        return highHand;
    }

    public void setHighHand(PokerHand highHand) {
        this.highHand = highHand;
    }

    public UUID getTableID() {
        return tableID;
    }

    public void setTableID(UUID tableID) {
        this.tableID = tableID;
    }

}
