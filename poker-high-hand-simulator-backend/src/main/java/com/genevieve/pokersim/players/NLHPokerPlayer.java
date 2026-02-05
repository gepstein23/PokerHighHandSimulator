package com.genevieve.pokersim.players;

import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.PokerHand;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NLHPokerPlayer extends PokerPlayer {
    public NLHPokerPlayer(Card[] holeCards, boolean shouldFilterPreflop) {
        super(holeCards, shouldFilterPreflop);
        if (holeCards.length != 2) {
            throw new AssertionError("Expected 2 player hole cards for NLH.");
        }
    }

    @Override
    protected int getRandomVpip() {
        return ThreadLocalRandom.current().nextInt(50);
    }

    @Override
    public PokerHand getBestHand(List<Card> communityCards) {
        final Card h1 = holeCards[0];
        final Card h2 = holeCards[1];
        PokerHand bestHand = null;

        // 2 hole cards + 3 community cards (C(5,3) = 10 combos)
        for (List<Integer> indices : communityCardIndexCombos) {
            PokerHand hand = new PokerHand(h1, h2,
                    communityCards.get(indices.get(0)),
                    communityCards.get(indices.get(1)),
                    communityCards.get(indices.get(2)));
            if (bestHand == null || hand.compare(bestHand) > 0) {
                bestHand = hand;
            }
        }

        // 1 hole card + 4 community cards (exclude one community card at a time)
        for (int i = 0; i < 2; i++) {
            final Card hole = holeCards[i];
            for (int skip = 0; skip < 5; skip++) {
                int ci = 0;
                Card c1 = null, c2 = null, c3 = null, c4 = null;
                for (int j = 0; j < 5; j++) {
                    if (j == skip) continue;
                    switch (ci++) {
                        case 0: c1 = communityCards.get(j); break;
                        case 1: c2 = communityCards.get(j); break;
                        case 2: c3 = communityCards.get(j); break;
                        case 3: c4 = communityCards.get(j); break;
                    }
                }
                PokerHand hand = new PokerHand(hole, c1, c2, c3, c4);
                if (bestHand == null || hand.compare(bestHand) > 0) {
                    bestHand = hand;
                }
            }
        }

        // 0 hole cards + 5 community cards
        PokerHand communityOnly = new PokerHand(
                communityCards.get(0), communityCards.get(1), communityCards.get(2),
                communityCards.get(3), communityCards.get(4));
        if (bestHand == null || communityOnly.compare(bestHand) > 0) {
            bestHand = communityOnly;
        }

        return bestHand;
    }
}
