package playingcards;

import java.util.*;

public class Deck {
    public static final ArrayList<Card> STANDARD_DECK = new ArrayList<>();
    static {
        STANDARD_DECK.add(new Card(CardValue.ACE, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.ACE, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.ACE, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.ACE, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.TWO, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.TWO, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.TWO, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.TWO, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.THREE, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.THREE, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.THREE, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.THREE, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.FOUR, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.FOUR, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.FOUR, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.FOUR, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.FIVE, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.FIVE, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.FIVE, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.FIVE, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.SIX, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.SIX, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.SIX, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.SIX, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.SEVEN, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.SEVEN, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.SEVEN, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.SEVEN, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.EIGHT, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.EIGHT, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.EIGHT, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.EIGHT, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.NINE, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.NINE, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.NINE, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.NINE, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.TEN, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.TEN, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.TEN, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.TEN, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.JACK, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.JACK, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.JACK, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.JACK, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.QUEEN, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.QUEEN, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.QUEEN, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.QUEEN, CardSuit.HEARTS));

        STANDARD_DECK.add(new Card(CardValue.KING, CardSuit.SPADES));
        STANDARD_DECK.add(new Card(CardValue.KING, CardSuit.CLUBS));
        STANDARD_DECK.add(new Card(CardValue.KING, CardSuit.DIAMONDS));
        STANDARD_DECK.add(new Card(CardValue.KING, CardSuit.HEARTS));
    }

    List<Card> cards;

    public Deck() {
        cards = shuffle();
    }

    public Deck(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> shuffle() {
        final ArrayList<Card> copiedCards = new ArrayList<>(STANDARD_DECK);
        Collections.shuffle(copiedCards);
        return copiedCards;
    }

    public Card get(int index) {
        return cards.get(index);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("=======Deck=======\n");
        for (Card card : cards) {
            str.append(card + " ");
        }
        return str.toString();
    }
}
