package playingcards;

public enum CardValue {
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

    CardValue(int rank, String friendlyName) {
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

    public static CardValue fromFriendlyName(final String friendlyName) {
        for (CardValue card : CardValue.values()) {
            if (card.friendlyName.equals(friendlyName)) {
                return card;
            }
        }
        return null;
    }
}
