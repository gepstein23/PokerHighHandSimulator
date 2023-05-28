package main;

public class Card implements Comparable<Card> {
    private Value value;
    private Suit suit;

    public Card(Value value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public static Card card(Value value) {
        return new Card(value, null);
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.getValue().getRank(), o.getValue().getRank());
    }

    public enum Value {
        ACE(13, "A"),
        KING(12, "K"),
        QUEEN(11, "Q"),
        JACK(10, "J"),
        TEN(9, "T"),
        NINE(8, "9"),
        EIGHT(7, "8"),
        SEVEN(6, "7"),
        SIX(5, "6"),
        FIVE(4, "5"),
        FOUR(3, "4"),
        THREE(2, "3"),
        TWO(1, "2");

        public String friendlyName;
        public int rank;

        Value(int rank, String friendlyName) {
            this.rank = rank;
            this.friendlyName = friendlyName;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getFriendlyName() {
            return friendlyName;
        }

        public void setFriendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
        }
    }

    public enum Suit {
        SPADES("s"),
        CLUBS("c"),
        HEARTS("h"),
        DIAMONDS("d");

        public String getFriendlyName() {
            return friendlyName;
        }

        public void setFriendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public String friendlyName;

        Suit(String friendlyName) {
            this.friendlyName = friendlyName;
        }
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.value.getFriendlyName(),
                this.suit == null ? "" : this.suit.getFriendlyName());
    }
}
