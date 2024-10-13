package animation;

import playingcards.PokerHand;
import players.PokerPlayer;
import playingcards.Card;
import java.util.List;
import java.util.UUID;

public class PlayedHandData {
    private final List<PokerPlayer> players;
    private final List<Card> communityCards;
    private final PokerHand winningHand;
    private final boolean qualifiesForHighHand;
    public boolean isPlo;
    private UUID tableId;

    public PlayedHandData(List<PokerPlayer> players, List<Card> communityCards, PokerHand winningHand, boolean qualifiesForHighHand, boolean isPlo, UUID tableId) {
        this.players = players;
        this.communityCards = communityCards;
        this.winningHand = winningHand;
        this.qualifiesForHighHand = qualifiesForHighHand;
        this.isPlo = isPlo;
        this.tableId = tableId;
    }

    public List<PokerPlayer> getPlayers() {
        return players;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public PokerHand getWinningHand() {
        return winningHand;
    }

    public boolean isQualifiesForHighHand() {
        return qualifiesForHighHand;
    }

    public UUID getTableId() {
        return tableId;
    }
}
