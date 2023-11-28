import playingcards.Card;
import playingcards.CardValue;
import playingcards.PokerHand;

import javax.swing.*;

public class HandComparisonTests {

    private static final Card JACK = Card.card(CardValue.JACK);
    private static final Card TEN = Card.card(CardValue.TEN);
    private static final Card NINE = Card.card(CardValue.NINE);
    private static final Card EIGHT = Card.card(CardValue.EIGHT);
    // QUADS
    private static final PokerHand _TTTT8 = new PokerHand(TEN, TEN, TEN, TEN, EIGHT);
    private static final PokerHand _TTTT9 = new PokerHand(TEN, TEN, TEN, TEN, NINE);
    private static final PokerHand _88889 = new PokerHand(EIGHT, EIGHT, EIGHT, EIGHT, NINE);
    private static final PokerHand _8888T = new PokerHand(EIGHT, EIGHT, EIGHT, EIGHT, TEN);
    // FULL HOUSES
    private static final PokerHand _TTT88 = new PokerHand(TEN, TEN, TEN, EIGHT, EIGHT);
    private static final PokerHand _TTT99 = new PokerHand(TEN, TEN, TEN, NINE, NINE);
    private static final PokerHand _88899 = new PokerHand(EIGHT, EIGHT, EIGHT, NINE, NINE);
    private static final PokerHand _999TT = new PokerHand(NINE, NINE, NINE, TEN, TEN);
    // SETS
    private static final PokerHand _TTT8J = new PokerHand(TEN, TEN, TEN, EIGHT, JACK);
    private static final PokerHand _9998J = new PokerHand(NINE, NINE, NINE, EIGHT, JACK);
    private static final PokerHand _9998T = new PokerHand(NINE, NINE, NINE, EIGHT, JACK);

    public static void main(String[] args) {
        testCompareSets();
        testCompareFullHouses();
        testCompareQuads();
        testCompareStraightFlushes();
        testCompareHandTypes();
    }

    private static void testCompareSets() {
    }

    private static void testCompareFullHouses() {
        assertWinningHand(_TTT99, _TTT88);
        assertWinningHand(_TTT99, _88899);
        assertWinningHand(_TTT99, _999TT);
    }

    private static void testCompareQuads() {
        assertWinningHand(_TTTT9, _TTTT8);
        assertWinningHand(_TTTT9, _88889);
        assertWinningHand(_TTTT8, _8888T);
    }

    private static void testCompareStraightFlushes() {
    }

    private static void testCompareHandTypes() {

    }

    private static void assertWinningHand(PokerHand expectedWinner, PokerHand expectedLoser) {
        final int result = expectedWinner.compare(expectedLoser);
        if (result > 0) {
            System.out.println(expectedWinner + " beats " + expectedLoser + " as expected");
        } else if (result == 0) {
            System.out.println("FAILURE!!!!!: " + expectedWinner + " is equal to " + expectedLoser + " which is not expected");
        } else {
            System.out.println("FAILURE!!!!!: " + expectedWinner + " lost to " + expectedLoser + "!!!!!");
        }
    }
}
