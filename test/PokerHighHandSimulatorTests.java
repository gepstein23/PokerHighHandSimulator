import main.HighHand;
import players.PokerPlayer;
import playingcards.*;
import tables.NLHTable;
import tables.PLOTable;

import javax.swing.*;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static playingcards.CardValue.SIX;

public class PokerHighHandSimulatorTests {

    private static final Card KING = Card.card(CardValue.KING);
    private static final Card QUEEN = Card.card(CardValue.QUEEN);
    private static final Card JACK = Card.card(CardValue.JACK);
    private static final Card TEN = Card.card(CardValue.TEN);
    private static final Card NINE = Card.card(CardValue.NINE);
    private static final Card EIGHT = Card.card(CardValue.EIGHT);
    private static final Card SEVEN = Card.card(CardValue.SEVEN);
    private static final Card SIX = Card.card(CardValue.SIX);
    private static final Card FIVE = Card.card(CardValue.FIVE);
    private static final Card FOUR = Card.card(CardValue.FOUR);
    private static final Card THREE = Card.card(CardValue.THREE);
    private static final Card TWO = Card.card(CardValue.TWO);
    private static final Card ACE = Card.card(CardValue.ACE);
    // QUADS
    private static final PokerHand _TTTT8 = new PokerHand(TEN, TEN, TEN, TEN, EIGHT);
    private static final PokerHand _8TTTT = new PokerHand(EIGHT, TEN, TEN, TEN, TEN);
    private static final PokerHand _TTTT9 = new PokerHand(TEN, TEN, TEN, TEN, NINE);
    private static final PokerHand _9TTTT = new PokerHand(NINE, TEN, TEN, TEN, TEN);
    private static final PokerHand _88889 = new PokerHand(EIGHT, EIGHT, EIGHT, EIGHT, NINE);
    private static final PokerHand _8888T = new PokerHand(EIGHT, EIGHT, EIGHT, EIGHT, TEN);
    // FULL HOUSES
    private static final PokerHand _TTT88 = new PokerHand(TEN, TEN, TEN, EIGHT, EIGHT);
    private static final PokerHand _TTT99 = new PokerHand(TEN, TEN, TEN, NINE, NINE);
    private static final PokerHand _88899 = new PokerHand(EIGHT, EIGHT, EIGHT, NINE, NINE);
    private static final PokerHand _999TT = new PokerHand(NINE, NINE, NINE, TEN, TEN);
    // SETS
    private static final PokerHand _TTT8J_SET = new PokerHand(new Card(CardValue.TEN, CardSuit.SPADES), new Card(CardValue.TEN, CardSuit.CLUBS), TEN, EIGHT, JACK);

    public static void main(String[] args) {
        System.out.println("\n\n== TESTING testCompareFullHouses");
        testCompareFullHouses();
        System.out.println("\n\n== TESTING testCompareQuads");
        testCompareQuads();
        System.out.println("\n\n== TESTING testCompareHandTypes");
        testCompareHandTypes();
//        System.out.println("\n\n== TESTING testQualifiesForWinningHandPloFlopRestriction");
//        testQualifiesForWinningHandPloFlopRestriction();
//        System.out.println("\n\n== TESTING testQualifiesForWinningHandPloNoFlopRestriction");
//        testQualifiesForWinningHandPloNoFlopRestriction();
//        System.out.println("\n\n== TESTING testQualifiesForWinningHandNlh");
//        testQualifiesForWinningHandNlh();
        System.out.println("\n\n== TESTING testGetBestHandPlo");
        testGetBestHandPlo();
        System.out.println("\n\n== TESTING testGetBestHandNlh");
        testGetBestHandNlh();
        System.out.println("\n\n== TESTING testShuffle (manual verification)");
        testShuffle();
        System.out.println("\n\n== TESTING testDealCardDeckNlh (manual verification)");
        testDealCardDeckNlh();
        System.out.println("\n\n== TESTING testDealCardDeckPlo (manual verification)");
        testDealCardDeckPlo();
        System.out.println("\n\n== TESTING testHighCardComparison");
        testHighCardComparison();
        System.out.println("\n\n== TESTING testPairComparison");
        testPairComparison();
        System.out.println("\n\n== TESTING testTwoPairComparison");
        testTwoPairComparison();
        System.out.println("\n\n== TESTING testSetComparison");
        testSetComparison();
        System.out.println("\n\n== TESTING testStraightComparison");
        testStraightComparison();
    }

    private static void testCompareFullHouses() {
        assertWinningHand(_TTT99, _TTT88);
        assertWinningHand(_TTT99, _88899);
        assertWinningHand(_TTT99, _999TT);
        assertEqualHands(_TTT99, _TTT99);
    }

    private static void testCompareQuads() {
        assertWinningHand(_TTTT9, _TTTT8);
        assertWinningHand(_9TTTT, _8TTTT);
        assertWinningHand(_TTTT9, _88889);
        assertWinningHand(_TTTT8, _8888T);
        assertEqualHands(_TTTT8, _TTTT8);
    }

    private static void testHighCardComparison() {
        // High card comparison
        assertWinningHand(new PokerHand(JACK, TEN, NINE, EIGHT, TWO), new PokerHand(NINE, EIGHT, TWO, SIX, FIVE));
        assertWinningHand(new PokerHand(TEN, NINE, EIGHT, JACK, FOUR), new PokerHand(EIGHT, SEVEN, SIX, FIVE, THREE));
        assertWinningHand(new PokerHand(JACK, TEN, NINE, EIGHT, SIX), new PokerHand(TEN, NINE, EIGHT, SEVEN, FOUR));
        assertEqualHands(new PokerHand(KING, QUEEN, JACK, TEN, NINE), new PokerHand(KING, QUEEN, JACK, TEN, NINE));
        assertWinningHand(new PokerHand(TEN, NINE, EIGHT, SEVEN, FIVE), new PokerHand(TEN, NINE, EIGHT, SEVEN, FOUR));
    }

    private static void testPairComparison() {
        // Pair comparison
        assertWinningHand(new PokerHand(ACE, ACE, KING, EIGHT, SEVEN), new PokerHand(ACE, ACE, KING, EIGHT, FOUR));
        assertWinningHand(new PokerHand(JACK, JACK, NINE, EIGHT, TEN), new PokerHand(TEN, TEN, EIGHT, SEVEN, SIX));
        assertWinningHand(new PokerHand(JACK, JACK, EIGHT, SEVEN, SIX), new PokerHand(TEN, TEN, NINE, EIGHT, JACK));
        assertWinningHand(new PokerHand(JACK, JACK, NINE, EIGHT, TEN), new PokerHand(JACK, JACK, NINE, EIGHT, SIX));
        assertWinningHand(new PokerHand(JACK, JACK, NINE, EIGHT, TEN), new PokerHand(JACK, JACK, NINE, SEVEN, SIX));
        assertWinningHand(new PokerHand(TEN, TEN, EIGHT, SEVEN, SIX), new PokerHand(NINE, NINE, EIGHT, SEVEN, SIX));
        assertEqualHands(new PokerHand(TEN, TEN, EIGHT, SEVEN, SIX), new PokerHand(TEN, TEN, EIGHT, SEVEN, SIX));
    }

    private static void testTwoPairComparison() {
        // Two pair comparison
        assertWinningHand(new PokerHand(JACK, JACK, TEN, TEN, EIGHT), new PokerHand(EIGHT, EIGHT, NINE, SEVEN, SIX));
        assertWinningHand(new PokerHand(JACK, JACK, EIGHT, EIGHT, SIX), new PokerHand(TEN, TEN, NINE, NINE, JACK));
        assertEqualHands(new PokerHand(JACK, JACK, TEN, TEN, EIGHT), new PokerHand(JACK, JACK, TEN, TEN, EIGHT));
        assertWinningHand(new PokerHand(TEN, TEN, NINE, NINE, EIGHT), new PokerHand(TEN, TEN, NINE, NINE, SEVEN));
        assertWinningHand(new PokerHand(TEN, TEN, NINE, NINE, SEVEN), new PokerHand(TEN, TEN, EIGHT, EIGHT, SEVEN));
    }

    private static void testSetComparison() {
        // Set comparison
        assertWinningHand(new PokerHand(JACK, JACK, JACK, NINE, EIGHT), new PokerHand(TEN, TEN, TEN, SEVEN, SIX));
        assertWinningHand(new PokerHand(JACK, JACK, JACK, SEVEN, SIX), new PokerHand(TEN, TEN, TEN, NINE, JACK));
        assertEqualHands(new PokerHand(JACK, JACK, JACK, NINE, EIGHT), new PokerHand(JACK, JACK, JACK, NINE, EIGHT));
        assertWinningHand(new PokerHand(TEN, TEN, TEN, NINE, EIGHT), new PokerHand(TEN, TEN, TEN, NINE, SEVEN));
        assertWinningHand(new PokerHand(TEN, TEN, TEN, NINE, SEVEN), new PokerHand(TEN, TEN, TEN, EIGHT, SEVEN));
        assertWinningHand(new PokerHand(JACK, JACK, JACK, NINE, SEVEN), new PokerHand(TEN, TEN, TEN, NINE, SEVEN));
    }

    private static void testStraightComparison() {
        // Straight comparison
        assertWinningHand(new PokerHand(JACK, TEN, NINE, EIGHT, SEVEN), new PokerHand(TEN, NINE, EIGHT, SIX, FIVE));
        assertWinningHand(new PokerHand(TEN, NINE, EIGHT, SEVEN, JACK), new PokerHand(JACK, TEN, NINE, EIGHT, SIX));
        assertEqualHands(new PokerHand(ACE, TWO, THREE, FOUR, FIVE), new PokerHand(ACE, TWO, THREE, FOUR, FIVE));
        assertWinningHand(new PokerHand(TEN, JACK, QUEEN, KING, ACE), new PokerHand(ACE, TWO, THREE, FOUR, FIVE));
    }


    private static void testCompareHandTypes() {
        // Quads vs Full House
        assertWinningHand(_TTTT9, _TTT88);

        // Full House vs Set
        assertWinningHand(_TTT99, _TTT8J_SET);

        // Full House vs Flush
        assertWinningHand(_TTT99, new PokerHand(new Card(CardValue.JACK, CardSuit.SPADES), new Card(CardValue.TEN, CardSuit.SPADES),
                new Card(CardValue.EIGHT, CardSuit.SPADES), new Card(CardValue.SIX, CardSuit.SPADES), new Card(CardValue.TWO, CardSuit.SPADES)));

        // Straight Flush vs Set
        assertWinningHand(new PokerHand(new Card(CardValue.JACK, CardSuit.SPADES), new Card(CardValue.TEN, CardSuit.SPADES),
                new Card(CardValue.NINE, CardSuit.SPADES), new Card(CardValue.EIGHT, CardSuit.SPADES), new Card(CardValue.SEVEN, CardSuit.SPADES)), _TTT8J_SET);

        // Straight Flush vs Straight
        assertWinningHand(new PokerHand(new Card(CardValue.JACK, CardSuit.SPADES), new Card(CardValue.TEN, CardSuit.SPADES),
                new Card(CardValue.NINE, CardSuit.SPADES), new Card(CardValue.EIGHT, CardSuit.SPADES), new Card(CardValue.SEVEN, CardSuit.SPADES)), new PokerHand(JACK, TEN, NINE, EIGHT, SEVEN));

        // Flush vs Straight
        assertWinningHand(new PokerHand(new Card(CardValue.JACK, CardSuit.SPADES), new Card(CardValue.TEN, CardSuit.SPADES),
                        new Card(CardValue.EIGHT, CardSuit.SPADES), new Card(CardValue.SIX, CardSuit.SPADES), new Card(CardValue.TWO, CardSuit.SPADES)),
                new PokerHand(JACK, TEN, NINE, EIGHT, SEVEN));

        // Straight vs Set
        assertWinningHand(new PokerHand(JACK, TEN, NINE, EIGHT, SEVEN), _TTT8J_SET);

        // Flush vs Set
        assertWinningHand(new PokerHand(new Card(CardValue.JACK, CardSuit.SPADES), new Card(CardValue.TEN, CardSuit.SPADES),
                new Card(CardValue.EIGHT, CardSuit.SPADES), new Card(CardValue.SIX, CardSuit.SPADES), new Card(CardValue.TWO, CardSuit.SPADES)), _TTT8J_SET);

        // Set vs Two Pair
        assertWinningHand(_TTT8J_SET, new PokerHand(EIGHT, EIGHT, JACK, JACK, TEN));

        // Two Pair vs Pair
        assertWinningHand(new PokerHand(EIGHT, EIGHT, JACK, JACK, TEN), new PokerHand(TEN, TEN, SEVEN, SIX, FIVE));

        // Pair vs High Card
        assertWinningHand(new PokerHand(TEN, TEN, SEVEN, SIX, FIVE), new PokerHand(ACE, JACK, TWO, THREE, EIGHT));
    }


    private static final HighHand HIGH_HAND = new HighHand(_TTTT9, _TTTT9, Duration.ofHours(1));

//    private static void testQualifiesForWinningHandPloFlopRestriction() {
//        final PokerHand flopped_TTTT9 =  new PokerHand(true, TEN, TEN, TEN, TEN, NINE);
//        final PokerHand notFlopped_TTTT9 =  new PokerHand(false, TEN, TEN, TEN, TEN, NINE);
//        final PokerHand flopped_TTTT8 =  new PokerHand(true, TEN, TEN, TEN, TEN, EIGHT);
//        final PokerHand notFlopped_TTTT8 =  new PokerHand(false, TEN, TEN, TEN, TEN, EIGHT);
//        final PokerHand flopped_TTTTJ =  new PokerHand(true, TEN, TEN, TEN, TEN, JACK);
//        final PokerHand notFlopped_TTTTJ =  new PokerHand(false, TEN, TEN, TEN, TEN, JACK);
//        final PLOTable ploTableFlopRestriction =
//                new PLOTable(1, 25, false, /*noPloFlopRestriction*/ false, false);
//
//        boolean doesEqualFloppedHHQualify = ploTableFlopRestriction.isQualifyingHighHand(flopped_TTTT9, HIGH_HAND);
//        assertTrue(doesEqualFloppedHHQualify);
//        boolean doesLowerFloppedHHQualify = ploTableFlopRestriction.isQualifyingHighHand(flopped_TTTT8, HIGH_HAND);
//        assertFalse(doesLowerFloppedHHQualify);
//        boolean doesHigherFloppedHHQualify = ploTableFlopRestriction.isQualifyingHighHand(flopped_TTTTJ, HIGH_HAND);
//        assertTrue(doesHigherFloppedHHQualify);
//
//        boolean doesEqualNonFloppedHHQualify = ploTableFlopRestriction.isQualifyingHighHand(notFlopped_TTTT9, HIGH_HAND);
//        assertFalse(doesEqualNonFloppedHHQualify);
//        boolean doesLowerNonFloppedHHQualify = ploTableFlopRestriction.isQualifyingHighHand(notFlopped_TTTT8, HIGH_HAND);
//        assertFalse(doesLowerNonFloppedHHQualify);
//        boolean doesHigherNonFloppedHHQualify = ploTableFlopRestriction.isQualifyingHighHand(notFlopped_TTTTJ, HIGH_HAND);
//        assertFalse(doesHigherNonFloppedHHQualify);
//
//    }
//
//    private static void testQualifiesForWinningHandPloNoFlopRestriction() {
//        final PokerHand TTTT9 =  new PokerHand(TEN, TEN, TEN, TEN, NINE);
//        final PokerHand TTTT8 =  new PokerHand(TEN, TEN, TEN, TEN, EIGHT);
//        final PokerHand TTTTJ =  new PokerHand(TEN, TEN, TEN, TEN, JACK);
//        final PLOTable ploTableNoFlopRestriction =
//                new PLOTable(1, 25, false, /*noPloFlopRestriction*/ true, false);
//        boolean doesEqualHHQualify = ploTableNoFlopRestriction.isQualifyingHighHand(TTTT9, HIGH_HAND);
//        assertTrue(doesEqualHHQualify);
//        boolean doesLowerHHQualify = ploTableNoFlopRestriction.isQualifyingHighHand(TTTT8, HIGH_HAND);
//        assertFalse(doesLowerHHQualify);
//        boolean doesHigherHHQualify = ploTableNoFlopRestriction.isQualifyingHighHand(TTTTJ, HIGH_HAND);
//        assertTrue(doesHigherHHQualify);
//    }

//    private static void testQualifiesForWinningHandNlh() {
//        final PokerHand TTTT9 =  new PokerHand(TEN, TEN, TEN, TEN, NINE);
//        final PokerHand TTTT8 =  new PokerHand(TEN, TEN, TEN, TEN, EIGHT);
//        final PokerHand TTTTJ =  new PokerHand(TEN, TEN, TEN, TEN, JACK);
//        final NLHTable nlhTable = new NLHTable(1, 25, false);
//        boolean doesEqualHHQualify = nlhTable.isQualifyingHighHand(TTTT9, HIGH_HAND);
//        assertTrue(doesEqualHHQualify);
//        boolean doesLowerHHQualify = nlhTable.isQualifyingHighHand(TTTT8, HIGH_HAND);
//        assertFalse(doesLowerHHQualify);
//        boolean doesHigherHHQualify = nlhTable.isQualifyingHighHand(TTTTJ, HIGH_HAND);
//        assertTrue(doesHigherHHQualify);
//    }

    private static void testGetBestHandPlo() {
    }

    private static void testGetBestHandNlh() {
    }

    private static void testDealCardDeckNlh() {
        final NLHTable nlhTable = new NLHTable(4, 25, false);
        final Deck deck = new Deck();
        System.out.println(deck);
        Collection<PokerPlayer> players = nlhTable.dealPlayers(deck);
        for (PokerPlayer player : players) {
            System.out.println(player);
        }
        List<Card> commCards = nlhTable.dealCommunityCards(deck);
        System.out.println("Community Cards = " + commCards);
    }

    private static void testDealCardDeckPlo() {
        final PLOTable ploTable =
                new PLOTable(4, 25, false, /*noPloFlopRestriction*/ true, false);
        final Deck deck = new Deck();
        System.out.println(deck);
        Collection<PokerPlayer> players = ploTable.dealPlayers(deck);
        for (PokerPlayer player : players) {
            System.out.println(player);
        }
        List<Card> commCards = ploTable.dealCommunityCards(deck);
        System.out.println("Community Cards = " + commCards);
    }

    private static void testShuffle() {
        final Deck deck = new Deck();
        System.out.println(deck);
        final Deck shuffledDeck = new Deck(deck.shuffle());
        System.out.println("Shuffled deck");
        System.out.println(shuffledDeck);
    }

    private static void assertWinningHand(PokerHand expectedWinner, PokerHand expectedLoser) {
        final int result = expectedWinner.compare(expectedLoser);
        if (result > 0) {
            System.out.println(expectedWinner + " beats " + expectedLoser + " as expected");
        } else if (result == 0) {
            throw new AssertionError(expectedWinner + " is equal to " + expectedLoser + " which is not expected");
        } else {
            throw new AssertionError(expectedWinner + " lost to " + expectedLoser + " which is not expected!!!!!");
        }
    }

    private static void assertEqualHands(PokerHand hand1, PokerHand hand2) {
        final int result = hand1.compare(hand2);
        if (result != 0) {
            throw new AssertionError(hand1 + " is not equal to " + hand2 + " which is not expected");
        } else {
            System.out.println(hand1 + " equal to " + hand2 + " as expected");
        }
    }

    private static void assertTrue(boolean val) {
        assertVal(true, val);
    }

    private static void assertFalse(boolean val) {
        assertVal(false, val);
    }

    private static void assertVal(boolean expected, boolean actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + ", but found " + actual);
        } else {
            System.out.println(actual + " equal to " + expected + " as expected");
        }
    }
}
