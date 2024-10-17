package com.genevieve.pokersim.api.snapshots;

import org.springframework.web.bind.annotation.ResponseBody;
import com.genevieve.pokersim.playingcards.Card;

import java.util.UUID;

@ResponseBody
public class HighHandSnapshotApiModel {

    private Card[] highHand;
    private UUID tableID;

    public HighHandSnapshotApiModel(Card[] highHand, UUID tableID, Boolean isPlo) {
        this.highHand = highHand;
        this.tableID = tableID;
        this.isPlo = isPlo;
    }

    private  Boolean isPlo;

    public HighHandSnapshotApiModel() {

    }

    public HighHandSnapshotApiModel(HighHandSnapshot snapshot) {
        this.isPlo = snapshot.getPlo();
        this.highHand = snapshot.getHighHand() == null ? null : snapshot.getHighHand().getFiveHandCards();
        this.tableID = snapshot.getTableID();
    }

    public UUID getTableID() {
        return tableID;
    }

    public void setTableID(UUID tableID) {
        this.tableID = tableID;
    }

    public Card[] getHighHand() {
        return highHand;
    }

    public void setHighHand(Card[] highHand) {
        this.highHand = highHand;
    }


    public Boolean getPlo() {
        return isPlo;
    }

    public void setPlo(Boolean plo) {
        isPlo = plo;
    }

}