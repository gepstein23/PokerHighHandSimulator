package playingcards;

/**
 * Represents a standard playing card.
 */
public class Card implements Comparable<Card> {
    private CardValue value;
    private CardSuit suit;

    public Card(CardValue value, CardSuit suit) {
        this.value = value;
        this.suit = suit;
    }

    public static Card card(CardValue value) {
        return new Card(value, null);
    }

    public CardValue getValue() {
        return value;
    }

    public CardSuit getSuit() {
        return suit;
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.getValue().getRank(), o.getValue().getRank());
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.value.getFriendlyName(),
                this.suit == null ? "" : this.suit.getFriendlyName());
    }
}
