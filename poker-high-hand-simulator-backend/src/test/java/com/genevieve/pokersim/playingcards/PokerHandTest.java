package com.genevieve.pokersim.playingcards;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.genevieve.pokersim.playingcards.CardSuit.*;
import static com.genevieve.pokersim.playingcards.CardValue.*;
import static org.junit.jupiter.api.Assertions.*;

class PokerHandTest {

    // Helper to create a card with a suit
    private static Card c(CardValue value, CardSuit suit) {
        return new Card(value, suit);
    }

    // ========== Card Display ==========

    @Nested
    class CardDisplayTests {
        @Test
        void tenDisplaysAsT() {
            Card ten = new Card(TEN, SPADES);
            assertEquals("Ts", ten.toString());
        }

        @Test
        void aceDisplaysAsA() {
            Card ace = new Card(ACE, HEARTS);
            assertEquals("Ah", ace.toString());
        }

        @Test
        void twoDisplaysAs2() {
            Card two = new Card(TWO, CLUBS);
            assertEquals("2c", two.toString());
        }

        @Test
        void allValuesDisplayCorrectly() {
            assertEquals("As", new Card(ACE, SPADES).toString());
            assertEquals("Kh", new Card(KING, HEARTS).toString());
            assertEquals("Qd", new Card(QUEEN, DIAMONDS).toString());
            assertEquals("Jc", new Card(JACK, CLUBS).toString());
            assertEquals("Ts", new Card(TEN, SPADES).toString());
            assertEquals("9h", new Card(NINE, HEARTS).toString());
            assertEquals("8d", new Card(EIGHT, DIAMONDS).toString());
            assertEquals("7c", new Card(SEVEN, CLUBS).toString());
            assertEquals("6s", new Card(SIX, SPADES).toString());
            assertEquals("5h", new Card(FIVE, HEARTS).toString());
            assertEquals("4d", new Card(FOUR, DIAMONDS).toString());
            assertEquals("3c", new Card(THREE, CLUBS).toString());
            assertEquals("2s", new Card(TWO, SPADES).toString());
        }

        @Test
        void nullSuitDisplaysAsLowercaseS() {
            Card card = Card.card(ACE);
            assertEquals("As", card.toString());
        }
    }

    // ========== Hand Type Classification ==========

    @Nested
    class HandTypeClassificationTests {
        @Test
        void highCard() {
            PokerHand hand = new PokerHand(
                    c(ACE, SPADES), c(KING, HEARTS), c(QUEEN, DIAMONDS),
                    c(JACK, CLUBS), c(NINE, SPADES));
            assertEquals(PokerHand.HandType.HIGH_CARD, hand.getHandType());
        }

        @Test
        void pair() {
            PokerHand hand = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(KING, DIAMONDS),
                    c(QUEEN, CLUBS), c(JACK, SPADES));
            assertEquals(PokerHand.HandType.PAIR, hand.getHandType());
        }

        @Test
        void twoPair() {
            PokerHand hand = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(KING, DIAMONDS),
                    c(KING, CLUBS), c(QUEEN, SPADES));
            assertEquals(PokerHand.HandType.TWO_PAIR, hand.getHandType());
        }

        @Test
        void set() {
            PokerHand hand = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(KING, CLUBS), c(QUEEN, SPADES));
            assertEquals(PokerHand.HandType.SET, hand.getHandType());
        }

        @Test
        void straight() {
            PokerHand hand = new PokerHand(
                    c(FIVE, SPADES), c(SIX, HEARTS), c(SEVEN, DIAMONDS),
                    c(EIGHT, CLUBS), c(NINE, SPADES));
            assertEquals(PokerHand.HandType.STRAIGHT, hand.getHandType());
        }

        @Test
        void aceLowStraight() {
            PokerHand hand = new PokerHand(
                    c(ACE, SPADES), c(TWO, HEARTS), c(THREE, DIAMONDS),
                    c(FOUR, CLUBS), c(FIVE, SPADES));
            assertEquals(PokerHand.HandType.STRAIGHT, hand.getHandType());
        }

        @Test
        void aceHighStraight() {
            PokerHand hand = new PokerHand(
                    c(TEN, SPADES), c(JACK, HEARTS), c(QUEEN, DIAMONDS),
                    c(KING, CLUBS), c(ACE, SPADES));
            assertEquals(PokerHand.HandType.STRAIGHT, hand.getHandType());
        }

        @Test
        void flush() {
            PokerHand hand = new PokerHand(
                    c(TWO, HEARTS), c(FIVE, HEARTS), c(EIGHT, HEARTS),
                    c(JACK, HEARTS), c(ACE, HEARTS));
            assertEquals(PokerHand.HandType.FLUSH, hand.getHandType());
        }

        @Test
        void fullHouse() {
            PokerHand hand = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(KING, CLUBS), c(KING, SPADES));
            assertEquals(PokerHand.HandType.FULL_HOUSE, hand.getHandType());
        }

        @Test
        void quads() {
            PokerHand hand = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(ACE, CLUBS), c(KING, SPADES));
            assertEquals(PokerHand.HandType.QUADS, hand.getHandType());
        }

        @Test
        void straightFlush() {
            PokerHand hand = new PokerHand(
                    c(FIVE, HEARTS), c(SIX, HEARTS), c(SEVEN, HEARTS),
                    c(EIGHT, HEARTS), c(NINE, HEARTS));
            assertEquals(PokerHand.HandType.STRAIGHT_FLUSH, hand.getHandType());
        }

        @Test
        void aceLowStraightFlush() {
            PokerHand hand = new PokerHand(
                    c(ACE, SPADES), c(TWO, SPADES), c(THREE, SPADES),
                    c(FOUR, SPADES), c(FIVE, SPADES));
            assertEquals(PokerHand.HandType.STRAIGHT_FLUSH, hand.getHandType());
        }

        @Test
        void royalFlush() {
            PokerHand hand = new PokerHand(
                    c(TEN, SPADES), c(JACK, SPADES), c(QUEEN, SPADES),
                    c(KING, SPADES), c(ACE, SPADES));
            assertEquals(PokerHand.HandType.STRAIGHT_FLUSH, hand.getHandType());
        }
    }

    // ========== Flush Comparison ==========

    @Nested
    class FlushComparisonTests {
        @Test
        void aceHighFlushBeatsKingHighFlush() {
            PokerHand aceHigh = new PokerHand(
                    c(TWO, HEARTS), c(FIVE, HEARTS), c(EIGHT, HEARTS),
                    c(JACK, HEARTS), c(ACE, HEARTS));
            PokerHand kingHigh = new PokerHand(
                    c(TWO, HEARTS), c(FIVE, HEARTS), c(EIGHT, HEARTS),
                    c(JACK, HEARTS), c(KING, HEARTS));
            assertTrue(aceHigh.compare(kingHigh) > 0);
            assertTrue(kingHigh.compare(aceHigh) < 0);
        }

        @Test
        void flushComparesFallsThroughToNextCard() {
            PokerHand higher = new PokerHand(
                    c(TWO, HEARTS), c(FIVE, HEARTS), c(EIGHT, HEARTS),
                    c(QUEEN, HEARTS), c(ACE, HEARTS));
            PokerHand lower = new PokerHand(
                    c(TWO, HEARTS), c(FIVE, HEARTS), c(EIGHT, HEARTS),
                    c(JACK, HEARTS), c(ACE, HEARTS));
            assertTrue(higher.compare(lower) > 0);
        }

        @Test
        void identicalFlushTies() {
            PokerHand flush1 = new PokerHand(
                    c(TWO, HEARTS), c(FIVE, HEARTS), c(EIGHT, HEARTS),
                    c(JACK, HEARTS), c(ACE, HEARTS));
            PokerHand flush2 = new PokerHand(
                    c(TWO, DIAMONDS), c(FIVE, DIAMONDS), c(EIGHT, DIAMONDS),
                    c(JACK, DIAMONDS), c(ACE, DIAMONDS));
            assertEquals(0, flush1.compare(flush2));
        }
    }

    // ========== Straight Comparison ==========

    @Nested
    class StraightComparisonTests {
        @Test
        void wheelLosesToSixHighStraight() {
            PokerHand wheel = new PokerHand(
                    c(ACE, SPADES), c(TWO, HEARTS), c(THREE, DIAMONDS),
                    c(FOUR, CLUBS), c(FIVE, SPADES));
            PokerHand sixHigh = new PokerHand(
                    c(TWO, HEARTS), c(THREE, DIAMONDS), c(FOUR, CLUBS),
                    c(FIVE, SPADES), c(SIX, HEARTS));
            assertTrue(wheel.compare(sixHigh) < 0);
            assertTrue(sixHigh.compare(wheel) > 0);
        }

        @Test
        void broadwayBeatsAllOtherStraights() {
            PokerHand broadway = new PokerHand(
                    c(TEN, SPADES), c(JACK, HEARTS), c(QUEEN, DIAMONDS),
                    c(KING, CLUBS), c(ACE, SPADES));
            PokerHand nineHigh = new PokerHand(
                    c(FIVE, SPADES), c(SIX, HEARTS), c(SEVEN, DIAMONDS),
                    c(EIGHT, CLUBS), c(NINE, SPADES));
            assertTrue(broadway.compare(nineHigh) > 0);
        }

        @Test
        void identicalStraightsTie() {
            PokerHand straight1 = new PokerHand(
                    c(FIVE, SPADES), c(SIX, HEARTS), c(SEVEN, DIAMONDS),
                    c(EIGHT, CLUBS), c(NINE, SPADES));
            PokerHand straight2 = new PokerHand(
                    c(FIVE, HEARTS), c(SIX, DIAMONDS), c(SEVEN, CLUBS),
                    c(EIGHT, SPADES), c(NINE, HEARTS));
            assertEquals(0, straight1.compare(straight2));
        }

        @Test
        void higherStraightBeatsLower() {
            PokerHand higher = new PokerHand(
                    c(SIX, SPADES), c(SEVEN, HEARTS), c(EIGHT, DIAMONDS),
                    c(NINE, CLUBS), c(TEN, SPADES));
            PokerHand lower = new PokerHand(
                    c(FIVE, SPADES), c(SIX, HEARTS), c(SEVEN, DIAMONDS),
                    c(EIGHT, CLUBS), c(NINE, SPADES));
            assertTrue(higher.compare(lower) > 0);
            assertTrue(lower.compare(higher) < 0);
        }
    }

    // ========== Straight Flush Comparison ==========

    @Nested
    class StraightFlushComparisonTests {
        @Test
        void wheelStraightFlushLosesToSixHighStraightFlush() {
            PokerHand wheel = new PokerHand(
                    c(ACE, SPADES), c(TWO, SPADES), c(THREE, SPADES),
                    c(FOUR, SPADES), c(FIVE, SPADES));
            PokerHand sixHigh = new PokerHand(
                    c(TWO, HEARTS), c(THREE, HEARTS), c(FOUR, HEARTS),
                    c(FIVE, HEARTS), c(SIX, HEARTS));
            assertTrue(wheel.compare(sixHigh) < 0);
            assertTrue(sixHigh.compare(wheel) > 0);
        }

        @Test
        void royalFlushBeatsAllStraightFlushes() {
            PokerHand royal = new PokerHand(
                    c(TEN, SPADES), c(JACK, SPADES), c(QUEEN, SPADES),
                    c(KING, SPADES), c(ACE, SPADES));
            PokerHand nineHigh = new PokerHand(
                    c(FIVE, HEARTS), c(SIX, HEARTS), c(SEVEN, HEARTS),
                    c(EIGHT, HEARTS), c(NINE, HEARTS));
            assertTrue(royal.compare(nineHigh) > 0);
        }

        @Test
        void identicalStraightFlushesTie() {
            PokerHand sf1 = new PokerHand(
                    c(FIVE, SPADES), c(SIX, SPADES), c(SEVEN, SPADES),
                    c(EIGHT, SPADES), c(NINE, SPADES));
            PokerHand sf2 = new PokerHand(
                    c(FIVE, HEARTS), c(SIX, HEARTS), c(SEVEN, HEARTS),
                    c(EIGHT, HEARTS), c(NINE, HEARTS));
            assertEquals(0, sf1.compare(sf2));
        }
    }

    // ========== Full House Comparison ==========

    @Nested
    class FullHouseComparisonTests {
        @Test
        void higherTripsWins() {
            PokerHand acesFullOfKings = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(KING, CLUBS), c(KING, SPADES));
            PokerHand kingsFullOfAces = new PokerHand(
                    c(KING, SPADES), c(KING, HEARTS), c(KING, DIAMONDS),
                    c(ACE, CLUBS), c(ACE, SPADES));
            assertTrue(acesFullOfKings.compare(kingsFullOfAces) > 0);
            assertTrue(kingsFullOfAces.compare(acesFullOfKings) < 0);
        }

        @Test
        void sameTripsHigherPairWins() {
            PokerHand acesFullOfKings = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(KING, CLUBS), c(KING, SPADES));
            PokerHand acesFullOfQueens = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(QUEEN, CLUBS), c(QUEEN, SPADES));
            assertTrue(acesFullOfKings.compare(acesFullOfQueens) > 0);
        }

        @Test
        void identicalFullHousesTie() {
            PokerHand fh1 = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(KING, CLUBS), c(KING, SPADES));
            PokerHand fh2 = new PokerHand(
                    c(ACE, CLUBS), c(ACE, DIAMONDS), c(ACE, HEARTS),
                    c(KING, HEARTS), c(KING, DIAMONDS));
            assertEquals(0, fh1.compare(fh2));
        }
    }

    // ========== Quads Comparison ==========

    @Nested
    class QuadsComparisonTests {
        @Test
        void higherQuadsWins() {
            PokerHand aceQuads = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(ACE, CLUBS), c(KING, SPADES));
            PokerHand kingQuads = new PokerHand(
                    c(KING, SPADES), c(KING, HEARTS), c(KING, DIAMONDS),
                    c(KING, CLUBS), c(ACE, SPADES));
            assertTrue(aceQuads.compare(kingQuads) > 0);
            assertTrue(kingQuads.compare(aceQuads) < 0);
        }

        @Test
        void sameQuadsHigherKickerWins() {
            PokerHand quadsWithKing = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(ACE, CLUBS), c(KING, SPADES));
            PokerHand quadsWithQueen = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(ACE, CLUBS), c(QUEEN, SPADES));
            assertTrue(quadsWithKing.compare(quadsWithQueen) > 0);
        }

        @Test
        void identicalQuadsTie() {
            PokerHand q1 = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(ACE, CLUBS), c(KING, SPADES));
            PokerHand q2 = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(ACE, CLUBS), c(KING, HEARTS));
            assertEquals(0, q1.compare(q2));
        }
    }

    // ========== Pair / Two Pair / Set Kicker Comparison ==========

    @Nested
    class KickerComparisonTests {
        @Test
        void pairWithHigherKickerWins() {
            PokerHand pairAceKicker = new PokerHand(
                    c(KING, SPADES), c(KING, HEARTS), c(ACE, DIAMONDS),
                    c(QUEEN, CLUBS), c(JACK, SPADES));
            PokerHand pairQueenKicker = new PokerHand(
                    c(KING, SPADES), c(KING, HEARTS), c(QUEEN, DIAMONDS),
                    c(JACK, CLUBS), c(TEN, SPADES));
            assertTrue(pairAceKicker.compare(pairQueenKicker) > 0);
        }

        @Test
        void higherPairBeatsLowerPair() {
            PokerHand pairOfAces = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(KING, DIAMONDS),
                    c(QUEEN, CLUBS), c(JACK, SPADES));
            PokerHand pairOfKings = new PokerHand(
                    c(KING, SPADES), c(KING, HEARTS), c(ACE, DIAMONDS),
                    c(QUEEN, CLUBS), c(JACK, SPADES));
            assertTrue(pairOfAces.compare(pairOfKings) > 0);
        }

        @Test
        void twoPairHigherTopPairWins() {
            PokerHand acesAndKings = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(KING, DIAMONDS),
                    c(KING, CLUBS), c(QUEEN, SPADES));
            PokerHand acesAndQueens = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(QUEEN, DIAMONDS),
                    c(QUEEN, CLUBS), c(KING, SPADES));
            assertTrue(acesAndKings.compare(acesAndQueens) > 0);
        }

        @Test
        void setWithHigherKickerWins() {
            PokerHand setAceKicker = new PokerHand(
                    c(KING, SPADES), c(KING, HEARTS), c(KING, DIAMONDS),
                    c(ACE, CLUBS), c(QUEEN, SPADES));
            PokerHand setJackKicker = new PokerHand(
                    c(KING, SPADES), c(KING, HEARTS), c(KING, DIAMONDS),
                    c(JACK, CLUBS), c(TEN, SPADES));
            assertTrue(setAceKicker.compare(setJackKicker) > 0);
        }

        @Test
        void higherSetBeatsLowerSet() {
            PokerHand setOfAces = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(KING, CLUBS), c(QUEEN, SPADES));
            PokerHand setOfKings = new PokerHand(
                    c(KING, SPADES), c(KING, HEARTS), c(KING, DIAMONDS),
                    c(ACE, CLUBS), c(QUEEN, SPADES));
            assertTrue(setOfAces.compare(setOfKings) > 0);
        }
    }

    // ========== Cross-Type Comparison ==========

    @Nested
    class CrossTypeComparisonTests {
        @Test
        void straightFlushBeatsQuads() {
            PokerHand straightFlush = new PokerHand(
                    c(FIVE, HEARTS), c(SIX, HEARTS), c(SEVEN, HEARTS),
                    c(EIGHT, HEARTS), c(NINE, HEARTS));
            PokerHand quads = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(ACE, CLUBS), c(KING, SPADES));
            assertTrue(straightFlush.compare(quads) > 0);
            assertTrue(quads.compare(straightFlush) < 0);
        }

        @Test
        void quadsBeatsFullHouse() {
            PokerHand quads = new PokerHand(
                    c(TWO, SPADES), c(TWO, HEARTS), c(TWO, DIAMONDS),
                    c(TWO, CLUBS), c(THREE, SPADES));
            PokerHand fullHouse = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(KING, CLUBS), c(KING, SPADES));
            assertTrue(quads.compare(fullHouse) > 0);
        }

        @Test
        void fullHouseBeatsFlush() {
            PokerHand fullHouse = new PokerHand(
                    c(TWO, SPADES), c(TWO, HEARTS), c(TWO, DIAMONDS),
                    c(THREE, CLUBS), c(THREE, SPADES));
            PokerHand flush = new PokerHand(
                    c(TWO, HEARTS), c(FIVE, HEARTS), c(EIGHT, HEARTS),
                    c(JACK, HEARTS), c(ACE, HEARTS));
            assertTrue(fullHouse.compare(flush) > 0);
        }

        @Test
        void flushBeatsStraight() {
            PokerHand flush = new PokerHand(
                    c(TWO, HEARTS), c(FIVE, HEARTS), c(EIGHT, HEARTS),
                    c(JACK, HEARTS), c(ACE, HEARTS));
            PokerHand straight = new PokerHand(
                    c(TEN, SPADES), c(JACK, HEARTS), c(QUEEN, DIAMONDS),
                    c(KING, CLUBS), c(ACE, SPADES));
            assertTrue(flush.compare(straight) > 0);
        }

        @Test
        void straightBeatsSet() {
            PokerHand straight = new PokerHand(
                    c(FIVE, SPADES), c(SIX, HEARTS), c(SEVEN, DIAMONDS),
                    c(EIGHT, CLUBS), c(NINE, SPADES));
            PokerHand set = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(ACE, DIAMONDS),
                    c(KING, CLUBS), c(QUEEN, SPADES));
            assertTrue(straight.compare(set) > 0);
        }

        @Test
        void setBeatsTwoPair() {
            PokerHand set = new PokerHand(
                    c(TWO, SPADES), c(TWO, HEARTS), c(TWO, DIAMONDS),
                    c(THREE, CLUBS), c(FOUR, SPADES));
            PokerHand twoPair = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(KING, DIAMONDS),
                    c(KING, CLUBS), c(QUEEN, SPADES));
            assertTrue(set.compare(twoPair) > 0);
        }

        @Test
        void twoPairBeatsPair() {
            PokerHand twoPair = new PokerHand(
                    c(TWO, SPADES), c(TWO, HEARTS), c(THREE, DIAMONDS),
                    c(THREE, CLUBS), c(FOUR, SPADES));
            PokerHand pair = new PokerHand(
                    c(ACE, SPADES), c(ACE, HEARTS), c(KING, DIAMONDS),
                    c(QUEEN, CLUBS), c(JACK, SPADES));
            assertTrue(twoPair.compare(pair) > 0);
        }

        @Test
        void pairBeatsHighCard() {
            PokerHand pair = new PokerHand(
                    c(TWO, SPADES), c(TWO, HEARTS), c(THREE, DIAMONDS),
                    c(FOUR, CLUBS), c(FIVE, SPADES));
            PokerHand highCard = new PokerHand(
                    c(ACE, SPADES), c(KING, HEARTS), c(QUEEN, DIAMONDS),
                    c(JACK, CLUBS), c(NINE, SPADES));
            assertTrue(pair.compare(highCard) > 0);
        }
    }

    // ========== PokerHand.from() Parsing ==========

    @Nested
    class FromParsingTests {
        @Test
        void parsesFullHouse() {
            PokerHand hand = PokerHand.from("AAAKK");
            assertEquals(PokerHand.HandType.FULL_HOUSE, hand.getHandType());
        }

        @Test
        void parsesLowFullHouse() {
            PokerHand hand = PokerHand.from("22233");
            assertEquals(PokerHand.HandType.FULL_HOUSE, hand.getHandType());
        }

        @Test
        void parsesQuads() {
            PokerHand hand = PokerHand.from("7777J");
            assertEquals(PokerHand.HandType.QUADS, hand.getHandType());
        }

        @Test
        void parsesInvalidLengthThrows() {
            assertThrows(IllegalArgumentException.class, () -> PokerHand.from("AAAA"));
        }

        @Test
        void parsesInvalidCharacterThrows() {
            assertThrows(IllegalArgumentException.class, () -> PokerHand.from("XXXXK"));
        }
    }

    // ========== High Card Comparison ==========

    @Nested
    class HighCardComparisonTests {
        @Test
        void aceHighBeatsKingHigh() {
            PokerHand aceHigh = new PokerHand(
                    c(ACE, SPADES), c(KING, HEARTS), c(QUEEN, DIAMONDS),
                    c(JACK, CLUBS), c(NINE, SPADES));
            PokerHand kingHigh = new PokerHand(
                    c(KING, SPADES), c(QUEEN, HEARTS), c(JACK, DIAMONDS),
                    c(TEN, CLUBS), c(EIGHT, SPADES));
            assertTrue(aceHigh.compare(kingHigh) > 0);
        }

        @Test
        void secondCardBreaksTie() {
            PokerHand higherSecond = new PokerHand(
                    c(ACE, SPADES), c(KING, HEARTS), c(QUEEN, DIAMONDS),
                    c(JACK, CLUBS), c(NINE, SPADES));
            PokerHand lowerSecond = new PokerHand(
                    c(ACE, HEARTS), c(QUEEN, HEARTS), c(JACK, DIAMONDS),
                    c(TEN, CLUBS), c(NINE, HEARTS));
            assertTrue(higherSecond.compare(lowerSecond) > 0);
        }
    }
}
