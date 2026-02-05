package com.genevieve.pokersim.tables;

import com.genevieve.pokersim.animation.PlayedHandData;
import com.genevieve.pokersim.playingcards.PokerHand;

import java.util.UUID;

/**
 * Value object representing the result of playing a single hand at a table.
 * Used by the hand-by-hand simulation orchestration.
 */
public class HandResult {

    private final PlayedHandData playedHandData;
    private final PokerHand winningHand;
    private final boolean qualifiesForHighHand;
    private final boolean isPlo;
    private final UUID tableId;

    public HandResult(PlayedHandData playedHandData, PokerHand winningHand,
                      boolean qualifiesForHighHand, boolean isPlo, UUID tableId) {
        this.playedHandData = playedHandData;
        this.winningHand = winningHand;
        this.qualifiesForHighHand = qualifiesForHighHand;
        this.isPlo = isPlo;
        this.tableId = tableId;
    }

    public PlayedHandData getPlayedHandData() {
        return playedHandData;
    }

    public PokerHand getWinningHand() {
        return winningHand;
    }

    public boolean isQualifiesForHighHand() {
        return qualifiesForHighHand;
    }

    public boolean isPlo() {
        return isPlo;
    }

    public UUID getTableId() {
        return tableId;
    }
}
