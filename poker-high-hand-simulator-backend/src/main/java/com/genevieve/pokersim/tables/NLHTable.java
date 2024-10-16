package com.genevieve.pokersim.tables;

import com.genevieve.pokersim.main.HighHand;
import com.genevieve.pokersim.players.NLHPokerPlayer;
import com.genevieve.pokersim.players.PokerPlayer;
import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.Deck;
import com.genevieve.pokersim.playingcards.PokerHand;

import java.util.ArrayList;
import java.util.Collection;

public class NLHTable extends PokerTable {
    public NLHTable(int numPlayers, double tableHandsPerHour, boolean shouldFilterPreflop) {
        super(numPlayers, tableHandsPerHour, shouldFilterPreflop);
    }

    @Override
    protected boolean isPloTable() {
        return false;
    }

    @Override
    public boolean isQualifyingHighHand(PokerHand winner, HighHand highHand, boolean usesBothCards) {
        return winner.compare(highHand.getNlhMinimumQualifyingHand()) >= 0 && usesBothCards;
    }

    @Override
    public Collection<PokerPlayer> dealPlayers(Deck deck) {
        final Collection<PokerPlayer> dealtPlayers = new ArrayList<>();
        for (int i = 0; i < this.numPlayers; i++) {
            final int holeCard1Index = i;
            final int holeCard2Index = i + numPlayers;
            final Card holeCard1 = deck.get(holeCard1Index);
            final Card holeCard2 = deck.get(holeCard2Index);
            final NLHPokerPlayer nlhPokerPlayer =
                    new NLHPokerPlayer(new Card[] { holeCard1, holeCard2}, shouldFilterPreflop);
            dealtPlayers.add(nlhPokerPlayer);
        }
        return dealtPlayers;
    }
}
