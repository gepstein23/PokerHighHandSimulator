package players;

import main.Card;
import main.PokerHand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NLHPokerPlayer extends PokerPlayer {
    public NLHPokerPlayer(Card[] holeCards) {
        super(holeCards);
        if (holeCards.length != 2) {
            throw new AssertionError("Expected 2 player hole cards for NLH.");
        }
    }

    @Override
    public PokerHand getBestHand(List<Card> communityCards) {
        final Card holdCard1 = holeCards[0];
        final Card holdCard2 = holeCards[1];
        PokerHand bestHand = null;
        for (List<Integer> communityCardIndices : communityCardIndexCombos) {
            final Card communityCard1 = communityCards.get(communityCardIndices.get(0));
            final Card communityCard2 = communityCards.get(communityCardIndices.get(1));
            final Card communityCard3 = communityCards.get(communityCardIndices.get(2));
            final PokerHand pokerHand = new PokerHand(holdCard1, holdCard2, communityCard1, communityCard2, communityCard3);
            if (bestHand == null) {
                bestHand = pokerHand;
                continue;
            }
            if (pokerHand.compare(bestHand) > 0) {
                bestHand = pokerHand;
            }
        }
        return bestHand;
    }
}
