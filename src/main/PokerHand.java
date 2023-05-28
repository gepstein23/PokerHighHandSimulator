package main;

import players.PokerPlayer;

import java.security.KeyPair;
import java.util.*;

public class PokerHand {
    private Card[] fiveHandCards;
    private HandType handType;
    private Boolean flopped;

    public PokerHand(Card... cards) {
        if (cards.length != 5) {
            throw new AssertionError("Invalid num cards" + cards.length);
        }
        List<Card> cardsList = Arrays.asList(cards);
        Collections.sort(cardsList);
        fiveHandCards = cardsList.toArray(new Card[0]);
        this.handType = determineHandType();
    }
    public PokerHand(boolean flopped, Card... cards) {
        this(cards);
        this.flopped = flopped;
    }

    private HandType determineHandType() {
        final boolean isStraight = isStraight(fiveHandCards);
        final boolean isFlush = isFlush(fiveHandCards);
        if (isStraight && isFlush) {
            return HandType.STRAIGHT_FLUSH;
        }
        if (isQuads(fiveHandCards)) {
            return HandType.QUADS;
        }
        if (isFullHouse(fiveHandCards)) {
            return HandType.FULL_HOUSE;
        }
        if (isFlush) {
            return HandType.FLUSH;
        }
        if (isStraight) {
            return HandType.STRAIGHT;
        }
        if (isSet(fiveHandCards)) {
            return HandType.SET;
        }
        if (isTwoPair(fiveHandCards)) {
            return HandType.SET;
        }
        if (isPair(fiveHandCards)) {
            return HandType.PAIR;
        }
        return HandType.HIGH_CARD;
    }

    private boolean isTwoPair(Card[] fiveHandCards) {
        final Map<Card.Value, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        boolean foundPairOne = false;
        for (Map.Entry<Card.Value, Integer> entry : valueToNumOccurrences.entrySet()) {
            if (entry.getValue().equals(2)) {
                if (foundPairOne) {
                    return true;
                }
                foundPairOne = true;
            }
        }
        return false;
    }

    private boolean isQuads(Card[] fiveHandCards) {
        final Map<Card.Value, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        return valueToNumOccurrences.containsValue(4);
    }

    private static Map<Card.Value, Integer> getValueToNumOccurrencesMap(Card[] fiveHandCards) {
        final Map<Card.Value, Integer> valueToNumOccurrences = new HashMap<>();
        for (Card card : fiveHandCards) {
            final Card.Value value = card.getValue();
            Integer numOccurrences = valueToNumOccurrences.getOrDefault(value, 0);
            numOccurrences += 1;
            valueToNumOccurrences.put(value, numOccurrences);
        }
        return valueToNumOccurrences;
    }

    private boolean isFlush(Card[] fiveHandCards) {
        Card.Suit suit = fiveHandCards[0].getSuit();
        if (suit == null) {
            return true;
        }
        for (Card card : fiveHandCards) {
            if (!suit.equals(card.getSuit())) {
                return false;
            }
        }
        return true;
    }

    private boolean isFullHouse(Card[] fiveHandCards) {
        final Map<Card.Value, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        return valueToNumOccurrences.containsValue(3) && valueToNumOccurrences.containsValue(2);
    }

    private boolean isStraight(Card[] fiveHandCards) {
        final Card[] copiedCards = Arrays.copyOf(fiveHandCards, fiveHandCards.length);
        Arrays.sort(copiedCards);
        Card prev = null;
        for (Card card : copiedCards) {
            if (prev == null) {
                prev = card;
                continue;
            }

            if (card.getValue().getRank() - prev.getValue().getRank() != 1) {
                return false;
            }
            prev = card;
        }
        return true; // TODO check this
    }

    private boolean isSet(Card[] fiveHandCards) {
        final Map<Card.Value, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        return valueToNumOccurrences.containsValue(3);
    }

    private boolean isPair(Card[] fiveHandCards) {
        final Map<Card.Value, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        return valueToNumOccurrences.containsValue(2);
    }

    public boolean isFlopped() {
        return flopped;
    }

    public void setFlopped(boolean flopped) {
        this.flopped = flopped;
    }

    public enum HandType {
        HIGH_CARD(0),
        PAIR(1),
        TWO_PAIR(2),
        SET(3),
        STRAIGHT(4),
        FLUSH(5),
        FULL_HOUSE(6),
        QUADS(7),
        STRAIGHT_FLUSH(8);

        private final int rank;

        HandType(int rank) {
            this.rank = rank;
        }
    }

    public Card[] getFiveHandCards() {
        return fiveHandCards;
    }

    public void setFiveHandCards(Card[] fiveHandCards) {
        this.fiveHandCards = fiveHandCards;
    }

    public HandType getHandType() {
        return handType;
    }

    public void setHandType(HandType handType) {
        this.handType = handType;
    }

    public int compare(PokerHand otherHand) {
        if (this.handType.rank > otherHand.handType.rank) {
            return 1;
        }
        if (this.handType.rank < otherHand.handType.rank) {
            return -1;
        }
        switch (this.handType) {
            case HIGH_CARD:
            case PAIR:
            case TWO_PAIR:
            case SET:
            case STRAIGHT:
            case FLUSH:
                return 1; // TODO we don't really care for the time being
            case FULL_HOUSE:
                final Card.Value[] thisFullValues = getFullHouseFullCardValues(this);
                final Card.Value[] otherFullValues = getFullHouseFullCardValues(otherHand);
                final Card.Value thisFullValue = thisFullValues[0];
                final Card.Value otherFullValue = otherFullValues[0];
                if (thisFullValue != otherFullValue) {
                    return otherFullValue.compareTo(thisFullValue);
                }
                final Card.Value thisFullOfValue = thisFullValues[1];
                final Card.Value otherFullOfValue = thisFullValues[1];
                return otherFullOfValue.compareTo(thisFullOfValue);
            case QUADS:
                final Card.Value thisQuadsValue = this.fiveHandCards[0].getValue();
                final Card.Value otherQuadsValue = otherHand.fiveHandCards[0].getValue();
                if (thisQuadsValue != otherQuadsValue) {
                    return otherQuadsValue.compareTo(thisQuadsValue);
                }
                final Card.Value thisKickerValue = this.fiveHandCards[4].getValue();
                final Card.Value otherKickerValue = otherHand.fiveHandCards[4].getValue();
                return thisKickerValue.compareTo(otherKickerValue);
            case STRAIGHT_FLUSH:
                return otherHand.fiveHandCards[0].getValue().compareTo(this.fiveHandCards[0].getValue());
            default:
                throw new AssertionError("Unknown handType: " + this.handType);
        }
    }

    private static Card.Value[] getFullHouseFullCardValues(PokerHand pokerHand) {
        final Card.Value[] result = new Card.Value[2];
        Map<Card.Value, Integer> valueToNumOccurrencesMap = getValueToNumOccurrencesMap(pokerHand.getFiveHandCards());
        for (Map.Entry<Card.Value, Integer> entry : valueToNumOccurrencesMap.entrySet()) {
            if (entry.getValue().equals(3)) {
                result[0] = entry.getKey();
            } else {
                result[1] = entry.getKey();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        final String floppedAddOn = flopped == null ? "" : String.format(" (flopped=%s)", flopped);
        return String.format("[PokerHand: handType=%s {%s %s %s %s %s}%s]",
                handType, fiveHandCards[0],fiveHandCards[1], fiveHandCards[2], fiveHandCards[3], fiveHandCards[4],
                floppedAddOn);
    }
}
