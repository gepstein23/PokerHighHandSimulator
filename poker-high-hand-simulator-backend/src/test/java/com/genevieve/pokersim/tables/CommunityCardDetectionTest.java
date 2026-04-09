package com.genevieve.pokersim.tables;

import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.CardSuit;
import com.genevieve.pokersim.playingcards.CardValue;
import com.genevieve.pokersim.playingcards.PokerHand;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the "uses three community cards" detection logic.
 *
 * In the simulation, after finding the winning hand, PokerTable checks whether
 * exactly 3 of the 5 winning hand cards are community cards. This determines
 * whether the player used both hole cards (for NLH qualification).
 *
 * The check uses object identity (Card doesn't override equals), so the same
 * Card objects must be shared between the hand and the community cards list.
 * This replicates the detection logic since usesThreeCommunityCards is private.
 */
class CommunityCardDetectionTest {

    // Replicates PokerTable.usesThreeCommunityCards logic
    private boolean usesThreeCommunityCards(PokerHand winner, List<Card> communityCards) {
        long count = Arrays.stream(winner.getFiveHandCards())
                .filter(communityCards::contains)
                .count();
        return count == 3;
    }

    @Test
    void handWith2Hole3CommunityDetectsThreeCommunityCards() {
        Card hole1 = new Card(CardValue.ACE, CardSuit.SPADES);
        Card hole2 = new Card(CardValue.KING, CardSuit.HEARTS);
        Card comm1 = new Card(CardValue.QUEEN, CardSuit.DIAMONDS);
        Card comm2 = new Card(CardValue.JACK, CardSuit.CLUBS);
        Card comm3 = new Card(CardValue.TEN, CardSuit.SPADES);
        Card comm4 = new Card(CardValue.NINE, CardSuit.HEARTS);
        Card comm5 = new Card(CardValue.EIGHT, CardSuit.DIAMONDS);

        List<Card> community = Arrays.asList(comm1, comm2, comm3, comm4, comm5);

        // Hand uses 2 hole + 3 community (same object references)
        PokerHand hand = new PokerHand(hole1, hole2, comm1, comm2, comm3);
        assertTrue(usesThreeCommunityCards(hand, community));
    }

    @Test
    void handWith1Hole4CommunityDoesNotDetectThree() {
        Card hole1 = new Card(CardValue.ACE, CardSuit.HEARTS);
        Card comm1 = new Card(CardValue.THREE, CardSuit.HEARTS);
        Card comm2 = new Card(CardValue.FIVE, CardSuit.HEARTS);
        Card comm3 = new Card(CardValue.EIGHT, CardSuit.HEARTS);
        Card comm4 = new Card(CardValue.JACK, CardSuit.HEARTS);
        Card comm5 = new Card(CardValue.KING, CardSuit.DIAMONDS);

        List<Card> community = Arrays.asList(comm1, comm2, comm3, comm4, comm5);

        // Hand uses 1 hole + 4 community (ace-high flush)
        PokerHand hand = new PokerHand(hole1, comm1, comm2, comm3, comm4);
        assertFalse(usesThreeCommunityCards(hand, community),
                "1 hole + 4 community should NOT count as 3 community cards");
    }

    @Test
    void handWith0Hole5CommunityDoesNotDetectThree() {
        Card comm1 = new Card(CardValue.TEN, CardSuit.SPADES);
        Card comm2 = new Card(CardValue.JACK, CardSuit.HEARTS);
        Card comm3 = new Card(CardValue.QUEEN, CardSuit.DIAMONDS);
        Card comm4 = new Card(CardValue.KING, CardSuit.CLUBS);
        Card comm5 = new Card(CardValue.ACE, CardSuit.SPADES);

        List<Card> community = Arrays.asList(comm1, comm2, comm3, comm4, comm5);

        // Hand is community only (straight on board)
        PokerHand hand = new PokerHand(comm1, comm2, comm3, comm4, comm5);
        assertFalse(usesThreeCommunityCards(hand, community),
                "Community-only hand should NOT count as 3 community cards");
    }

    @Test
    void differentCardObjectsWithSameValueAreNotDetectedAsCommunity() {
        Card hole1 = new Card(CardValue.ACE, CardSuit.SPADES);
        Card hole2 = new Card(CardValue.KING, CardSuit.HEARTS);
        // Create community cards with DIFFERENT Card objects (different references)
        Card comm1 = new Card(CardValue.QUEEN, CardSuit.DIAMONDS);
        Card comm2 = new Card(CardValue.JACK, CardSuit.CLUBS);
        Card comm3 = new Card(CardValue.TEN, CardSuit.SPADES);

        // Use different objects for the community list than what's in the hand
        Card comm1Copy = new Card(CardValue.QUEEN, CardSuit.DIAMONDS);
        Card comm2Copy = new Card(CardValue.JACK, CardSuit.CLUBS);
        Card comm3Copy = new Card(CardValue.TEN, CardSuit.SPADES);

        List<Card> community = Arrays.asList(comm1Copy, comm2Copy, comm3Copy,
                new Card(CardValue.NINE, CardSuit.HEARTS),
                new Card(CardValue.EIGHT, CardSuit.DIAMONDS));

        // Hand uses comm1/2/3 but community list has different objects
        PokerHand hand = new PokerHand(hole1, hole2, comm1, comm2, comm3);
        assertFalse(usesThreeCommunityCards(hand, community),
                "Detection uses object identity, not value equality");
    }

    @Test
    void realWorldScenarioNlhPlayerUsesBothHoleCards() {
        // Simulate dealing from same card objects
        Card hole1 = new Card(CardValue.ACE, CardSuit.SPADES);
        Card hole2 = new Card(CardValue.ACE, CardSuit.HEARTS);
        Card comm1 = new Card(CardValue.ACE, CardSuit.DIAMONDS);
        Card comm2 = new Card(CardValue.KING, CardSuit.CLUBS);
        Card comm3 = new Card(CardValue.KING, CardSuit.SPADES);
        Card comm4 = new Card(CardValue.TWO, CardSuit.CLUBS);
        Card comm5 = new Card(CardValue.THREE, CardSuit.DIAMONDS);

        List<Card> community = Arrays.asList(comm1, comm2, comm3, comm4, comm5);

        // Best hand: full house aces full of kings (hole1 + hole2 + comm1 + comm2 + comm3)
        PokerHand hand = new PokerHand(hole1, hole2, comm1, comm2, comm3);
        assertEquals(PokerHand.HandType.FULL_HOUSE, hand.getHandType());
        assertTrue(usesThreeCommunityCards(hand, community),
                "Full house using both aces from hole + 3 community should detect 3 community");
    }
}
