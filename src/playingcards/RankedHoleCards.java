package playingcards;

public enum RankedHoleCards {
    AA(CardValue.ACE, CardValue.ACE, 1),
    KK(CardValue.KING, CardValue.KING, 2),
    QQ(CardValue.QUEEN, CardValue.QUEEN, 3),
    AK(CardValue.ACE, CardValue.KING, 4, true),
    //JJ() TODO
    ;

    private CardValue cardValue1;
    private CardValue cardValue2;
    private int rank;
    private boolean isSuited;

    RankedHoleCards(CardValue cardValue1, CardValue cardValue2, int rank, boolean isSuited) {
        this.cardValue1 = cardValue1;
        this.cardValue2 = cardValue2;
        this.rank = rank;
        this.isSuited = isSuited;
    }

    RankedHoleCards(CardValue cardValue1, CardValue cardValue2, int rank) {
        this(cardValue1, cardValue2, rank, false);
    }

    RankedHoleCards valueOf(Card card1, Card card2) {
        return null; // TODO
    }
}
