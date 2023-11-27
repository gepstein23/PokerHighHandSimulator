package playingcards;

public enum RankedHoleCards {
    // Thank you ChatGPT
    ACE_ACE(CardValue.ACE, CardValue.ACE, 1),
    KING_KING(CardValue.KING, CardValue.KING, 2),
    QUEEN_QUEEN(CardValue.QUEEN, CardValue.QUEEN, 3),
    ACE_KING_s(CardValue.ACE, CardValue.KING, 4, true),
    JACK_JACK(CardValue.JACK, CardValue.JACK, 5),
    ACE_QUEEN_s(CardValue.ACE, CardValue.QUEEN, 6, true),
    KING_QUEEN_s(CardValue.KING, CardValue.QUEEN, 7, true),
    ACE_JACK_s(CardValue.ACE, CardValue.JACK, 8, true),
    KING_JACK_s(CardValue.KING, CardValue.JACK, 9, true),
    TEN_TEN(CardValue.TEN, CardValue.TEN, 10),
    ACE_KING_o(CardValue.ACE, CardValue.KING, 11, false),
    ACE_TEN_s(CardValue.ACE, CardValue.TEN, 12, true),
    QUEEN_JACK_s(CardValue.QUEEN, CardValue.JACK, 13, true),
    KING_TEN_s(CardValue.KING, CardValue.TEN, 14, true),
    QUEEN_TEN_s(CardValue.QUEEN, CardValue.TEN, 15, true),
    JACK_TEN_s(CardValue.JACK, CardValue.TEN, 16, true),
    NINE_NINE(CardValue.NINE, CardValue.NINE, 17),
    ACE_QUEEN_o(CardValue.ACE, CardValue.QUEEN, 18, false),
    ACE_NINE_s(CardValue.ACE, CardValue.NINE, 19, true),
    KING_QUEEN_o(CardValue.KING, CardValue.QUEEN, 20, false),
    EIGHT_EIGHT(CardValue.EIGHT, CardValue.EIGHT, 21),
    KING_NINE_s(CardValue.KING, CardValue.NINE, 22, true),
    TEN_NINE_s(CardValue.TEN, CardValue.NINE, 23, true),
    ACE_EIGHT_s(CardValue.ACE, CardValue.EIGHT, 24, true),
    QUEEN_NINE_s(CardValue.QUEEN, CardValue.NINE, 25, true),
    JACK_NINE_s(CardValue.JACK, CardValue.NINE, 26, true),
    ACE_JACK_o(CardValue.ACE, CardValue.JACK, 27, false),
    ACE_FIVE_s(CardValue.ACE, CardValue.FIVE, 28, true),
    SEVEN_SEVEN(CardValue.SEVEN, CardValue.SEVEN, 29),
    ACE_SEVEN_s(CardValue.ACE, CardValue.SEVEN, 30, true),
    KING_JACK_o(CardValue.KING, CardValue.JACK, 31, false),
    ACE_FOUR_s(CardValue.ACE, CardValue.FOUR, 32, true),
    ACE_THREE_s(CardValue.ACE, CardValue.THREE, 33, true),
    ACE_SIX_s(CardValue.ACE, CardValue.SIX, 34, true),
    QUEEN_JACK_o(CardValue.QUEEN, CardValue.JACK, 35, false),
    SIX_SIX(CardValue.SIX, CardValue.SIX, 36),
    KING_EIGHT_s(CardValue.KING, CardValue.EIGHT, 37, true),
    TEN_EIGHT_s(CardValue.TEN, CardValue.EIGHT, 38, true),
    ACE_TWO_s(CardValue.ACE, CardValue.TWO, 39, true),

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
