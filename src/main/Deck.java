package main;

import java.util.*;

public class Deck {
    public static final ArrayList<Card> STANDARD_DECK = new ArrayList<>();
    static {
        STANDARD_DECK.add(new Card(Card.Value.ACE, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.ACE, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.ACE, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.ACE, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.TWO, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.TWO, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.TWO, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.TWO, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.THREE, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.THREE, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.THREE, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.THREE, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.FOUR, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.FOUR, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.FOUR, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.FOUR, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.FIVE, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.FIVE, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.FIVE, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.FIVE, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.SIX, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.SIX, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.SIX, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.SIX, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.SEVEN, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.SEVEN, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.SEVEN, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.SEVEN, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.EIGHT, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.EIGHT, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.EIGHT, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.EIGHT, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.NINE, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.NINE, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.NINE, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.NINE, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.TEN, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.TEN, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.TEN, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.TEN, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.JACK, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.JACK, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.JACK, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.JACK, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.QUEEN, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.QUEEN, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.QUEEN, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.QUEEN, Card.Suit.HEARTS));

        STANDARD_DECK.add(new Card(Card.Value.KING, Card.Suit.SPADES));
        STANDARD_DECK.add(new Card(Card.Value.KING, Card.Suit.CLUBS));
        STANDARD_DECK.add(new Card(Card.Value.KING, Card.Suit.DIAMONDS));
        STANDARD_DECK.add(new Card(Card.Value.KING, Card.Suit.HEARTS));
    }

    List<Card> cards;

    public Deck() {
        cards = shuffle();
    }

    private List<Card> shuffle() {
        final ArrayList<Card> copiedCards = new ArrayList<>(STANDARD_DECK);
        Collections.shuffle(copiedCards);
        return copiedCards;
    }

    public Card get(int index) {
        return cards.get(index);
    }
}
