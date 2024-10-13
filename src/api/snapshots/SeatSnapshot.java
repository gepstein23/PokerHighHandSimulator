package api.snapshots;

public class SeatSnapshot {
    String[] cards;
    boolean isHandWinner;

    public String[] getCards() {
        return cards;
    }

    public void setCards(String[] cards) {
        this.cards = cards;
    }

    public boolean isHandWinner() {
        return isHandWinner;
    }

    public void setHandWinner(boolean handWinner) {
        isHandWinner = handWinner;
    }
}
