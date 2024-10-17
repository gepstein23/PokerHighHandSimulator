package com.genevieve.pokersim.players;

import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.PokerHand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NLHPokerPlayer extends PokerPlayer {
    public NLHPokerPlayer(Card[] holeCards, boolean shouldFilterPreflop) {
        super(holeCards, shouldFilterPreflop);
        if (holeCards.length != 2) {
            throw new AssertionError("Expected 2 player hole cards for NLH.");
        }
    }

    @Override
    protected int getRandomVpip() {
        return new Random(System.currentTimeMillis()).nextInt(50);
    }

    @Override
    public PokerHand getBestHand(List<Card> communityCards) {
        final Card holeCard1 = holeCards[0];
        final Card holeCard2 = holeCards[1];
        PokerHand bestHand = null;

        // 2 hole card 3 comm
        for (List<Integer> indices : communityCardIndexCombos) {
            PokerHand hand = evaluateHand(holeCard1, holeCard2, communityCards, indices);
            bestHand = updateBestHand(bestHand, hand);
        }

        // 4 comm cards
        for (int i = 0; i < holeCards.length; i++) {
            for (int j = 0; j < communityCards.size(); j++) {
                List<Integer> excludeIndex = Arrays.asList(j); // Exclude one community card
                PokerHand hand = evaluateHandWithOneHole(holeCards[i], communityCards, excludeIndex);
                bestHand = updateBestHand(bestHand, hand);
            }
        }

        // only comm cards
        PokerHand communityOnlyHand = evaluateCommunityOnlyHand(communityCards);
        bestHand = updateBestHand(bestHand, communityOnlyHand);

        return bestHand;
    }

    private PokerHand evaluateHand(Card holeCard1, Card holeCard2, List<Card> communityCards, List<Integer> indices) {
        List<Card> cards = new ArrayList<>();
        cards.add(holeCard1);
        cards.add(holeCard2);
        for (Integer index : indices) {
            cards.add(communityCards.get(index));
        }
        return new PokerHand(cards);
    }

    private PokerHand evaluateHandWithOneHole(Card holeCard, List<Card> communityCards, List<Integer> excludeIndex) {
        List<Card> cards = new ArrayList<>();
        cards.add(holeCard);
        for (int i = 0; i < communityCards.size(); i++) {
            if (!excludeIndex.contains(i)) {
                cards.add(communityCards.get(i));
            }
        }
        return new PokerHand(cards);
    }

    private PokerHand evaluateCommunityOnlyHand(List<Card> communityCards) {
        return new PokerHand(new ArrayList<>(communityCards)); // Assumes the PokerHand constructor can handle more than 5 cards if needed.
    }

    private PokerHand updateBestHand(PokerHand bestHand, PokerHand newHand) {
        if (bestHand == null || newHand.compare(bestHand) > 0) {
            return newHand;
        }
        return bestHand;
    }

}
