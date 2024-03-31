package players;

import playingcards.Card;
import playingcards.PokerHand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static main.Utils.debug;

public abstract class PokerPlayer {
    protected static final List<List<Integer>> communityCardIndexCombos = new ArrayList<>();
    static {
        communityCardIndexCombos.add(Arrays.asList(0, 1, 2));
        communityCardIndexCombos.add(Arrays.asList(0, 1, 3));
        communityCardIndexCombos.add(Arrays.asList(0, 1, 4));
        communityCardIndexCombos.add(Arrays.asList(0, 2, 3));
        communityCardIndexCombos.add(Arrays.asList(0, 2, 4));
        communityCardIndexCombos.add(Arrays.asList(0, 3, 4));
        communityCardIndexCombos.add(Arrays.asList(1, 3, 4));
        communityCardIndexCombos.add(Arrays.asList(1, 2, 3));
        communityCardIndexCombos.add(Arrays.asList(1, 2, 4));
        communityCardIndexCombos.add(Arrays.asList(2, 3, 4));
    }
    protected static final List<List<Integer>> holeCardIndexCombos = new ArrayList<>();
    static {
        holeCardIndexCombos.add(Arrays.asList(0, 1));
        holeCardIndexCombos.add(Arrays.asList(0, 2));
        holeCardIndexCombos.add(Arrays.asList(0, 3));
        holeCardIndexCombos.add(Arrays.asList(1, 2));
        holeCardIndexCombos.add(Arrays.asList(1, 3));
        holeCardIndexCombos.add(Arrays.asList(2, 3));
    }
    protected Card[] holeCards;
    protected int vpip;
    private boolean shouldFilterPreflop;

    public UUID getPlayerID() {
        return playerID;
    }

    private final UUID playerID;

    public PokerPlayer(Card[] holeCards, boolean shouldFilterPreflop) {
        this.shouldFilterPreflop = shouldFilterPreflop;
        this.holeCards = holeCards;
        this.vpip = getRandomVpip();
        this.playerID = UUID.randomUUID();
    }

    protected abstract int getRandomVpip();

    public abstract PokerHand getBestHand(List<Card> communityCards);

    protected static boolean isFlopped(List<Integer> communityCardIndices) {
        return communityCardIndices.stream().allMatch(i -> i <= 2);
    }

    protected static boolean isTurned(List<Integer> communityCardIndices) {
        return communityCardIndices.stream().allMatch(i -> i <= 3);
    }

    public boolean shouldSeeFlop() {
        boolean shouldSeeFlop = true;
        if (shouldFilterPreflop) {
            shouldSeeFlop = areHoleCardsInRange(getHoleCardRanking());
        }
        debug("  - Player %s cards=%s, shouldSeeFlop=%s", playerID, Card.getCardStr(holeCards), shouldSeeFlop);
        return shouldSeeFlop;
    }

    protected boolean areHoleCardsInRange(int holeCardRanking) {
        return holeCardRanking < vpip;
    }

    private int getHoleCardRanking() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("~Player Cards~: ");
        for (Card card : holeCards) {
            str.append(card).append(" ");
        }
        return str.toString();
    }

    public Card[] getHoleCards() {
        return holeCards;
    }
}
