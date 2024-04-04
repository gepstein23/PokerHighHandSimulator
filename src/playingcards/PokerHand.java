package playingcards;

import java.util.*;
import java.util.stream.Collectors;

import static playingcards.Card.card;

/**
 * Represents a five card hand of playing cards.
 */
public class PokerHand {
    private Card[] fiveHandCards;

    public HandType getHandType() {
        return handType;
    }

    public void setHandType(HandType handType) {
        this.handType = handType;
    }

    private HandType handType;
    private Boolean flopped;
    private Boolean turned;

    public PokerHand(Card... cards) {
        if (cards.length != 5) {
            throw new AssertionError("Invalid num cards " + cards.length);
        }
        List<Card> cardsList = Arrays.asList(cards);
        Collections.sort(cardsList);
        fiveHandCards = cardsList.toArray(new Card[0]);
        this.handType = determineHandType();
    }
    public PokerHand(CardValue... cardValues) {
        this(Arrays.stream(cardValues).map(Card::card).collect(Collectors.toList()));
    }
    public PokerHand(List<Card> cards) {
        if (cards.size() != 5) {
            throw new AssertionError("Invalid num cards " + cards.size());
        }
        Collections.sort(cards);
        fiveHandCards = cards.toArray(new Card[0]);
        this.handType = determineHandType();
    }
    public PokerHand(boolean flopped, boolean turned, Card... cards) {
        this(cards);
        this.flopped = flopped;
        this.turned = turned;
    }

    public PokerHand(boolean flopped, Card... cards) {
        this(cards);
        this.flopped = flopped;
    }

    public static PokerHand from(String inputHighHandMinimumQualifier) {
        final char[] chars = inputHighHandMinimumQualifier.toCharArray();
        if (chars.length != 5) {
            throw new AssertionError("Invalid inputHighHandMinimumQualifier " + inputHighHandMinimumQualifier + ", must be 5 cards.");
        }
        final Card[] cards = new Card[5];
        for (int i = 0; i < 5; i++) {
            final String val = String.valueOf(chars[i]);
            final CardValue cardValue = CardValue.fromFriendlyName(val);
            final Card card = card(cardValue);
            cards[i] = card;
        }
        return new PokerHand(cards);
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
            return HandType.TWO_PAIR;
        }
        if (isPair(fiveHandCards)) {
            return HandType.PAIR;
        }
        return HandType.HIGH_CARD;
    }

    private boolean isTwoPair(Card[] fiveHandCards) {
        final Map<CardValue, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        boolean foundPairOne = false;
        for (Map.Entry<CardValue, Integer> entry : valueToNumOccurrences.entrySet()) {
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
        final Map<CardValue, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        return valueToNumOccurrences.containsValue(4);
    }

    private static Map<CardValue, Integer> getValueToNumOccurrencesMap(Card[] fiveHandCards) {
        final Map<CardValue, Integer> valueToNumOccurrences = new HashMap<>();
        for (Card card : fiveHandCards) {
            final CardValue value = card.getValue();
            Integer numOccurrences = valueToNumOccurrences.getOrDefault(value, 0);
            numOccurrences += 1;
            valueToNumOccurrences.put(value, numOccurrences);
        }
        return valueToNumOccurrences;
    }

    private boolean isFlush(Card[] fiveHandCards) {
        CardSuit suit = fiveHandCards[0].getSuit();
        if (suit == null) {
            return false;
        }
        for (Card card : fiveHandCards) {
            if (!suit.equals(card.getSuit())) {
                return false;
            }
        }
        return true;
    }

    private boolean isFullHouse(Card[] fiveHandCards) {
        final Map<CardValue, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        return valueToNumOccurrences.containsValue(3) && valueToNumOccurrences.containsValue(2);
    }

    private boolean isStraight(Card[] fiveHandCards) {
        final Card[] copiedCards = Arrays.copyOf(fiveHandCards, fiveHandCards.length);
        Arrays.sort(copiedCards, Comparator.comparingInt(c -> c.getValue().getRank()));

        boolean isStraight = true;
        for (int i = 1; i < copiedCards.length; i++) {
            if (copiedCards[i].getValue().getRank() - copiedCards[i - 1].getValue().getRank() != 1) {
                isStraight = false;
                break;
            }
        }

        // check for A2345
        if (!isStraight) {
            if (copiedCards[0].getValue() == CardValue.TWO && copiedCards[4].getValue() == CardValue.ACE) {
                for (int i = 1; i < 4; i++) { // Check 2,3,4,5
                    if (copiedCards[i].getValue().getRank() - copiedCards[i - 1].getValue().getRank() != 1) {
                        return false;
                    }
                }
                return true;
            }
        }

        return isStraight;
    }


    private boolean isSet(Card[] fiveHandCards) {
        final Map<CardValue, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        return valueToNumOccurrences.containsValue(3);
    }

    private boolean isPair(Card[] fiveHandCards) {
        final Map<CardValue, Integer> valueToNumOccurrences = getValueToNumOccurrencesMap(fiveHandCards);
        return valueToNumOccurrences.containsValue(2);
    }

    public boolean isFlopped() {
        return flopped;
    }

    public void setFlopped(boolean flopped) {
        this.flopped = flopped;
    }

    public Boolean isTurned() {
        return turned;
    }

    public PokerHand chooseHand(boolean increase) {
        switch (getHandType()) {
            case FULL_HOUSE:
                final CardValue[] thisFullValues = getFullHouseFullCardValues(this);
                final CardValue thisFullValue = thisFullValues[0];
                final CardValue thisPairValue = thisFullValues[1];
                if (increase) {
                    // try to increase pair value
                    CardValue newPairValue = thisPairValue.getIncreasedValue();
                    if (newPairValue == thisFullValue) {
                        newPairValue = newPairValue.getIncreasedValue();
                    }
                    // if you can't, increase full value and reset pair value to lowest
                    if (newPairValue == null || newPairValue == thisFullValue) {
                        final CardValue newFullValue = thisFullValue.getIncreasedValue();
                        if (newFullValue == null) {
                            // if you can't increase the full value, return the lowest quads
                            return new PokerHand(card(CardValue.TWO), card(CardValue.TWO), card(CardValue.TWO), card(CardValue.TWO), card(CardValue.THREE));
                        } else {
                            return new PokerHand(card(newFullValue), card(newFullValue), card(newFullValue), card(CardValue.TWO), card(CardValue.TWO));
                        }
                    } else {
                        return new PokerHand(card(thisFullValue), card(thisFullValue), card(thisFullValue), card(newPairValue), card(newPairValue));
                    }
                } else {
                    // try to decrease pair value
                    CardValue newPairValue = thisPairValue.getDecreasedValue();
                    if (newPairValue == thisFullValue) {
                        newPairValue = newPairValue.getDecreasedValue();
                    }
                    if (newPairValue == null) {
                        // if you can't, decrease the full value and reset pair value to highest
                        final CardValue newFullValue = thisFullValue.getDecreasedValue();
                        if (newFullValue == null) {
                            // if you can't decrease the full value, throw error, no longer valid simulation
                            return null;
                        } else {
                            return new PokerHand(card(newFullValue), card(newFullValue), card(newFullValue), card(CardValue.ACE), card(CardValue.ACE));
                        }
                    } else {
                        return new PokerHand(card(thisFullValue), card(thisFullValue), card(thisFullValue), card(newPairValue), card(newPairValue));
                    }
                }
            case QUADS:
                final CardValue[] thisQuadsValues = getQuadsValues(this);
                final CardValue thisQuadsValue = thisQuadsValues[0];
                final CardValue thisKickerValue = thisQuadsValues[1];
                if (increase) {
                    // try to increase kicker value
                    CardValue newKickerValue = thisKickerValue.getIncreasedValue();
                    if (newKickerValue == thisQuadsValue) {
                        newKickerValue = newKickerValue.getIncreasedValue();
                    }
                    if (newKickerValue == null) {
                        // if you can't, increase quads value and reset kicker value to lowest
                        final CardValue newQuadsValue = thisQuadsValue.getIncreasedValue();
                        if (newQuadsValue == null) {
                            // lowest SF
                            return new PokerHand(new Card(CardValue.ACE, CardSuit.SPADES), new Card(CardValue.TWO, CardSuit.SPADES),
                                    new Card(CardValue.THREE, CardSuit.SPADES), new Card(CardValue.FOUR, CardSuit.SPADES), new Card(CardValue.FIVE, CardSuit.SPADES));
                        } else {
                            return new PokerHand(card(newQuadsValue), card(newQuadsValue), card(newQuadsValue), card(newQuadsValue), card(CardValue.TWO));
                        }
                    } else {
                        return new PokerHand(card(thisQuadsValue), card(thisQuadsValue), card(thisQuadsValue), card(thisQuadsValue), card(newKickerValue));
                    }
                } else {
                    // try to decrease kicker value
                    CardValue newKickerValue = thisKickerValue.getDecreasedValue();
                    if (newKickerValue == thisQuadsValue) {
                        newKickerValue = newKickerValue.getDecreasedValue();
                    }
                    if (newKickerValue == null) {
                        // if you can't, decrease the quads value and reset kicker value to highest
                        final CardValue newQuadsValue = thisQuadsValue.getDecreasedValue();
                        if (newQuadsValue == null) {
                            // if you can't decrease the quads value, return the highest fullhouse
                            return new PokerHand(card(CardValue.ACE), card(CardValue.ACE), card(CardValue.ACE), card(CardValue.KING), card(CardValue.KING));
                        } else {
                            return new PokerHand(card(newQuadsValue), card(newQuadsValue), card(newQuadsValue), card(newQuadsValue), card(CardValue.ACE));
                        }
                    } else {
                        return new PokerHand(card(thisQuadsValue), card(thisQuadsValue), card(thisQuadsValue), card(thisQuadsValue), card(newKickerValue));
                    }
                }
            case STRAIGHT_FLUSH:
                final CardValue highestValue = fiveHandCards[0].getValue(); // todo check
                if (increase) {
                    // try to increase highestvalue
                    // if you can't throw bad request, no longer value
                    // build new poker hand building using highest value
                } else {
                    // try to decrease highestvalue
                    // if highest value is 4 or lower, then return highest quads
                }
                break;
            default:
                throw new AssertionError("cannot choose next hand, not implemented, sorry!");
        }
        return null;
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

        public int rank() {
            return rank;
        }
    }

    public Card[] getFiveHandCards() {
        return fiveHandCards;
    }

    public int compare(PokerHand otherHand) {
        if (this.handType.rank > otherHand.handType.rank) {
            return 1;
        }
        if (this.handType.rank < otherHand.handType.rank) {
            return -1;
        }

        // Same hand type, need to compare individual cards
        switch (this.handType) {
            case HIGH_CARD:
                Card[] sortedThisHand = Arrays.copyOf(this.fiveHandCards, this.fiveHandCards.length);
                Card[] sortedOtherHand = Arrays.copyOf(otherHand.fiveHandCards, otherHand.fiveHandCards.length);
                Arrays.sort(sortedThisHand, (card1, card2) -> card2.getValue().compareTo(card1.getValue()));
                Arrays.sort(sortedOtherHand, (card1, card2) -> card2.getValue().compareTo(card1.getValue()));

                for (int i = sortedThisHand.length - 1; i >= 0; i--) {
                    Card thisCard = sortedThisHand[i];
                    Card otherCard = sortedOtherHand[i];
                    int comparisonResult = thisCard.compareTo(otherCard);
                    if (comparisonResult != 0) {
                        return comparisonResult;
                    }
                }

                return 0;
            case PAIR:
                int thisPairValue = getPairValue(this.fiveHandCards);
                int otherPairValue = getPairValue(otherHand.fiveHandCards);

                if (thisPairValue != otherPairValue) {
                    return Integer.compare(thisPairValue, otherPairValue);
                } else {
                    return compareRemainingCards(this, otherHand, 2);
                }
            case TWO_PAIR:
                int[] thisTwoPairValues = getTwoPairValues(this.fiveHandCards);
                int[] otherTwoPairValues = getTwoPairValues(otherHand.fiveHandCards);

                if (thisTwoPairValues[1] != otherTwoPairValues[1]) {
                    return Integer.compare(thisTwoPairValues[1], otherTwoPairValues[1]);
                } else if (thisTwoPairValues[0] != otherTwoPairValues[0]) {
                    return Integer.compare(thisTwoPairValues[0], otherTwoPairValues[0]);
                } else {
                    return compareRemainingCards(this, otherHand, 1);
                }
            case SET:
                int thisSetValue = getSetValue(this.fiveHandCards);
                int otherSetValue = getSetValue(otherHand.fiveHandCards);

                if (thisSetValue != otherSetValue) {
                    return Integer.compare(thisSetValue, otherSetValue);
                } else {
                    return compareRemainingCards(this, otherHand, 3);
                }
            case STRAIGHT:
                // Compare highest card in the straight
                return otherHand.fiveHandCards[0].getValue().compareTo(this.fiveHandCards[0].getValue());
            case FLUSH:
                return compareIndividualCards(this, otherHand);
            case FULL_HOUSE:
                final CardValue[] thisFullValues = getFullHouseFullCardValues(this);
                final CardValue[] otherFullValues = getFullHouseFullCardValues(otherHand);
                final CardValue thisFullValue = thisFullValues[0];
                final CardValue otherFullValue = otherFullValues[0];
                if (thisFullValue != otherFullValue) {
                    return otherFullValue.compareTo(thisFullValue);
                }
                final CardValue thisPairValuee = thisFullValues[1];
                final CardValue otherPairValuee = otherFullValues[1];
                return otherPairValuee.compareTo(thisPairValuee);
            case QUADS:
                final CardValue[] thisQuadsValues = getQuadsValues(this);
                final CardValue[] otherQuadsValues = getQuadsValues(otherHand);
                final CardValue thisQuadsValue = thisQuadsValues[0];
                final CardValue otherQuadsValue = otherQuadsValues[0];
                if (thisQuadsValue != otherQuadsValue) {
                    return otherQuadsValue.compareTo(thisQuadsValue);
                }
                final CardValue thisKickerValue = thisQuadsValues[1];
                final CardValue otherKickerValue = otherQuadsValues[1];
                return otherKickerValue.compareTo(thisKickerValue);
            case STRAIGHT_FLUSH:
                return otherHand.fiveHandCards[0].getValue().compareTo(this.fiveHandCards[0].getValue());
            default:
                throw new AssertionError("Unknown handType: " + this.handType);
        }
    }

    private int compareRemainingCards(PokerHand hand1, PokerHand hand2, int numCardsToCompare) {
        for (int i = 4; i >= 0; i--) {
            if (hand1.fiveHandCards[i].getValue().getRank() != hand2.fiveHandCards[i].getValue().getRank()) {
                return hand1.fiveHandCards[i].compareTo(hand2.fiveHandCards[i]);
            }
        }
        return 0;
    }

    private int compareIndividualCards(PokerHand hand1, PokerHand hand2) {
        for (int i = 4; i >= 0; i--) {
            int comparison = hand1.fiveHandCards[i].getValue().compareTo(hand2.fiveHandCards[i].getValue());
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }


    private static CardValue[] getFullHouseFullCardValues(PokerHand pokerHand) {
        final CardValue[] result = new CardValue[2];
        Map<CardValue, Integer> valueToNumOccurrencesMap = getValueToNumOccurrencesMap(pokerHand.getFiveHandCards());
        for (Map.Entry<CardValue, Integer> entry : valueToNumOccurrencesMap.entrySet()) {
            if (entry.getValue().equals(3)) {
                result[0] = entry.getKey();
            } else {
                result[1] = entry.getKey();
            }
        }
        return result;
    }

    /**
     * Returns [QuadValue, KickerValue]
     * @param pokerHand
     * @return
     */
    private static CardValue[] getQuadsValues(PokerHand pokerHand) {
        final CardValue[] result = new CardValue[2];
        Map<CardValue, Integer> valueToNumOccurrencesMap = getValueToNumOccurrencesMap(pokerHand.getFiveHandCards());
        for (Map.Entry<CardValue, Integer> entry : valueToNumOccurrencesMap.entrySet()) {
            if (entry.getValue().equals(4)) {
                result[0] = entry.getKey();
            } else {
                result[1] = entry.getKey();
            }
        }
        return result;
    }

    private int getSetValue(Card[] cards) {
        Map<CardValue, Integer> valueToOccurrences = getValueToOccurrencesMap(cards);
        for (Map.Entry<CardValue, Integer> entry : valueToOccurrences.entrySet()) {
            if (entry.getValue() == 3) {
                return entry.getKey().getRank();
            }
        }
        return -1; // Not found
    }

    private int[] getTwoPairValues(Card[] cards) {
        int[] pairValues = new int[2];
        Map<CardValue, Integer> valueToOccurrences = getValueToOccurrencesMap(cards);
        int count = 0;
        for (Map.Entry<CardValue, Integer> entry : valueToOccurrences.entrySet()) {
            if (entry.getValue() == 2) {
                pairValues[count++] = entry.getKey().getRank();
            }
        }
        Arrays.sort(pairValues);
        return pairValues;
    }

    private int getPairValue(Card[] cards) {
        Map<CardValue, Integer> valueToOccurrences = getValueToOccurrencesMap(cards);
        for (Map.Entry<CardValue, Integer> entry : valueToOccurrences.entrySet()) {
            if (entry.getValue() == 2) {
                return entry.getKey().getRank();
            }
        }
        return -1; // Not found
    }

    private Map<CardValue, Integer> getValueToOccurrencesMap(Card[] cards) {
        Map<CardValue, Integer> valueToOccurrences = new HashMap<>();
        for (Card card : cards) {
            CardValue value = card.getValue();
            valueToOccurrences.put(value, valueToOccurrences.getOrDefault(value, 0) + 1);
        }
        return valueToOccurrences;
    }


    @Override
    public String toString() {
        final String floppedAddOn = flopped == null ? "" : String.format(" (flopped=%s)", flopped);
        //final String turnedAddOn = turned == null ? "" : String.format(" (turned=%s)", turned);
        return String.format("[PokerHand: handType=%s {%s %s %s %s %s}%s]",
                handType, fiveHandCards[0],fiveHandCards[1], fiveHandCards[2], fiveHandCards[3], fiveHandCards[4],
                floppedAddOn);
    }
}
