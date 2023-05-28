package players;

import playingcards.Card;
import playingcards.PokerHand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public PokerPlayer(Card[] holeCards) {
        this.holeCards = holeCards;
        this.vpip = getRandomVpip();
    }

    protected abstract int getRandomVpip();

    public abstract PokerHand getBestHand(List<Card> communityCards);

    protected static boolean isFlopped(List<Integer> communityCardIndices) {
        return communityCardIndices.stream().allMatch(i -> i <= 2);
    }

    public boolean shouldSeeFlop() {
        return areHoleCardsInRange(getHoleCardRanking());
    }

    protected boolean areHoleCardsInRange(int holeCardRanking) {
        return holeCardRanking < vpip;
    }

    private int getHoleCardRanking() {
        return 0;
    }
}
