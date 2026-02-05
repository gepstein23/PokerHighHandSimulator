package com.genevieve.pokersim.playingcards;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void standardDeckHas52Cards() {
        assertEquals(52, Deck.STANDARD_DECK.size());
    }

    @Test
    void standardDeckHasAllUniqueCards() {
        Set<String> seen = new HashSet<>();
        for (Card card : Deck.STANDARD_DECK) {
            assertTrue(seen.add(card.toString()),
                    "Duplicate card in standard deck: " + card);
        }
        assertEquals(52, seen.size());
    }

    @Test
    void standardDeckHas13ValuesPerSuit() {
        for (CardSuit suit : CardSuit.values()) {
            long count = Deck.STANDARD_DECK.stream()
                    .filter(c -> c.getSuit() == suit)
                    .count();
            assertEquals(13, count, "Suit " + suit + " should have 13 cards");
        }
    }

    @Test
    void standardDeckHas4SuitsPerValue() {
        for (CardValue value : CardValue.values()) {
            long count = Deck.STANDARD_DECK.stream()
                    .filter(c -> c.getValue() == value)
                    .count();
            assertEquals(4, count, "Value " + value + " should have 4 cards");
        }
    }

    @Test
    void shuffledDeckHas52Cards() {
        Deck deck = new Deck();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            Card card = deck.get(i);
            assertNotNull(card);
            seen.add(card.toString());
        }
        assertEquals(52, seen.size(), "Shuffled deck should have 52 unique cards");
    }

    @Test
    void twoShuffledDecksAreLikelyDifferentOrder() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        boolean anyDifferent = false;
        for (int i = 0; i < 52; i++) {
            if (!deck1.get(i).toString().equals(deck2.get(i).toString())) {
                anyDifferent = true;
                break;
            }
        }
        assertTrue(anyDifferent, "Two shuffled decks should very likely differ");
    }

    @Test
    void customDeckPreservesCardOrder() {
        Deck deck = new Deck(Deck.STANDARD_DECK);
        for (int i = 0; i < 52; i++) {
            assertSame(Deck.STANDARD_DECK.get(i), deck.get(i));
        }
    }
}
