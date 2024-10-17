package com.genevieve.pokersim.tables;

import com.genevieve.pokersim.main.HighHand;
import com.genevieve.pokersim.players.PLOPokerPlayer;
import com.genevieve.pokersim.players.PokerPlayer;
import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.Deck;
import com.genevieve.pokersim.playingcards.PokerHand;

import java.util.ArrayList;
import java.util.Collection;

public class PLOTable extends PokerTable {
    boolean noPloFlopRestriction;
    boolean ploTurnRestriction;
    public PLOTable(int numPlayers, double tableHandsPerHour, boolean shouldFilterPreflop, boolean noPloFlopRestriction, boolean ploTurnRestriction) {
        super(numPlayers, tableHandsPerHour, shouldFilterPreflop);
        this.noPloFlopRestriction = ploTurnRestriction || noPloFlopRestriction;
        this.ploTurnRestriction = ploTurnRestriction;
    }

    @Override
    protected boolean isPloTable() {
        return true;
    }

    @Override
    public boolean isQualifyingHighHand(PokerHand winner, HighHand highHand, boolean usesBothCards) {
        final boolean floppedConditionMet = noPloFlopRestriction || winner.isFlopped();
        final boolean turnConditionMet = !ploTurnRestriction || winner.isTurned();
        return winner.compare(highHand.getPloMinimumQualifyingHand()) >= 0 && floppedConditionMet && turnConditionMet;
    }

    @Override
    public Collection<PokerPlayer> dealPlayers(Deck deck) {
        final Collection<PokerPlayer> dealtPlayers = new ArrayList<>();
        for (int i = 0; i < this.numPlayers; i++) {
            final int holeCard1Index = i;
            final int holeCard2Index = i + numPlayers;
            final int holeCard3Index = i + (numPlayers * 2);
            final int holeCard4Index = i + (numPlayers * 3);
            final Card holeCard1 = deck.get(holeCard1Index);
            final Card holeCard2 = deck.get(holeCard2Index);
            final Card holeCard3 = deck.get(holeCard3Index);
            final Card holeCard4 = deck.get(holeCard4Index);
            final PLOPokerPlayer ploPokerPlayer =
                    new PLOPokerPlayer(new Card[] { holeCard1, holeCard2, holeCard3, holeCard4}, shouldFilterPreflop);
            dealtPlayers.add(ploPokerPlayer);
        }
        return dealtPlayers;
    }
}
