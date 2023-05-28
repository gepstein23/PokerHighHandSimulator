package playingcards;

public enum CardSuit {
    SPADES("s"),
    CLUBS("c"),
    HEARTS("h"),
    DIAMONDS("d");

    public String getFriendlyName() {
        return friendlyName;
    }

    public String friendlyName;

    CardSuit(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
