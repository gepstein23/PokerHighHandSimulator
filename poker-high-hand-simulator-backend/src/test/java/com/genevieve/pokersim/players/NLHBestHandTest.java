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
 * Tests NLH (No Limit Hold'em) best hand selection.
 *
 * NLH rules: player has 2 hole cards + 5 community cards.
 * Best 5-card hand is chosen from ANY combination of these 7 cards:
 *   - 2 hole + 3 community
 *   - 1 hole + 4 community
 *   - 0 hole + 5 community (community only)
 *
 * Test cases are in src/test/resources/testdata/nlh_best_hand.csv
 */
class NLHBestHandTest {

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/testdata/nlh_best_hand.csv", delimiter = '|', numLinesToSkip = 1)
    void testNlhBestHandSelection(String description, String hole, String community,
                                   String expectedType, String expectedHand) {
        Card[] holeCards = TestCards.parseCards(hole);
        List<Card> communityCards = TestCards.parseCardList(community);

        NLHPokerPlayer player = new NLHPokerPlayer(holeCards, false);
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
    }
}
