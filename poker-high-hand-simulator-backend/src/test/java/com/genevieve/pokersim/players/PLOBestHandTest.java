package com.genevieve.pokersim.players;

import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.PokerHand;
import com.genevieve.pokersim.testutil.TestCards;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests PLO (Pot Limit Omaha) best hand selection.
 *
 * PLO rules: player has 4 hole cards + 5 community cards.
 * Best 5-card hand MUST use EXACTLY 2 hole cards + EXACTLY 3 community cards.
 * This is the fundamental PLO constraint. All C(4,2) * C(5,3) = 60 combinations
 * are evaluated.
 *
 * Key cases that prove the 2+3 rule is enforced:
 *   - 3 suited hole cards + 2 suited community = NO flush (max 4 of suit in any 2+3 combo)
 *   - 4 of a kind in hole = only a pair (can only use 2 hole cards)
 *   - Trips in hole + 1 on board = set, not quads (only 2 hole cards used)
 *   - 1 suited hole card + 4 suited community = NO flush (need 2 suited from hole)
 *
 * Also tests flopped/turned tracking:
 *   - flopped = all 3 community cards from flop (indices 0,1,2)
 *   - turned  = all 3 community cards from flop+turn (indices 0,1,2,3)
 *
 * Test cases are in src/test/resources/testdata/plo_best_hand.csv
 */
class PLOBestHandTest {

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/testdata/plo_best_hand.csv", delimiter = '|', numLinesToSkip = 1)
    void testPloBestHandSelection(String description, String hole, String community,
                                   String expectedType, String expectedHand,
                                   String expectedFlopped, String expectedTurned) {
        Card[] holeCards = TestCards.parseCards(hole);
        List<Card> communityCards = TestCards.parseCardList(community);

        PLOPokerPlayer player = new PLOPokerPlayer(holeCards, false);
        PokerHand bestHand = player.getBestHand(communityCards);

        assertNotNull(bestHand, "Best hand should not be null");

        // Check hand type
        PokerHand.HandType expected = PokerHand.HandType.valueOf(expectedType.trim());
        assertEquals(expected, bestHand.getHandType(),
                description.trim() + ": wrong hand type");

        // Check exact cards in the hand (as a set, order doesn't matter)
        Set<String> expectedCards = TestCards.toStringSet(TestCards.parseCards(expectedHand));
        Set<String> actualCards = TestCards.toStringSet(bestHand.getFiveHandCards());
        assertEquals(expectedCards, actualCards,
                description.trim() + ": wrong cards in hand");

        // Check flopped/turned tracking
        boolean flopped = Boolean.parseBoolean(expectedFlopped.trim());
        boolean turned = Boolean.parseBoolean(expectedTurned.trim());
        assertEquals(flopped, bestHand.isFlopped(),
                description.trim() + ": wrong flopped value");
        assertEquals(turned, bestHand.isTurned(),
                description.trim() + ": wrong turned value");
    }
}
