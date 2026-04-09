package com.genevieve.pokersim.testutil;

import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.CardSuit;
import com.genevieve.pokersim.playingcards.CardValue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestCards {

    public static Card parseCard(String notation) {
        if (notation.length() != 2) {
            throw new IllegalArgumentException("Card notation must be 2 chars: " + notation);
        }
        String value = notation.substring(0, 1);
        String suit = notation.substring(1, 2);
        CardValue cardValue = CardValue.fromFriendlyName(value);
        if (cardValue == null) {
            throw new IllegalArgumentException("Unknown card value: " + value);
        }
        return new Card(cardValue, parseSuit(suit));
    }

    private static CardSuit parseSuit(String s) {
        switch (s) {
            case "s": return CardSuit.SPADES;
            case "c": return CardSuit.CLUBS;
            case "h": return CardSuit.HEARTS;
            case "d": return CardSuit.DIAMONDS;
            default: throw new IllegalArgumentException("Unknown suit: " + s);
        }
    }

    public static Card[] parseCards(String spaceDelimited) {
        return Arrays.stream(spaceDelimited.trim().split("\\s+"))
                .map(TestCards::parseCard)
                .toArray(Card[]::new);
    }

    public static List<Card> parseCardList(String spaceDelimited) {
        return Arrays.stream(spaceDelimited.trim().split("\\s+"))
                .map(TestCards::parseCard)
                .collect(Collectors.toList());
    }

    public static Set<String> toStringSet(Card[] cards) {
        return Arrays.stream(cards)
                .map(Card::toString)
                .collect(Collectors.toSet());
    }
}
