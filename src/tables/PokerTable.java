package tables;

import animation.PlayedHandData;
import main.HighHand;
import players.PokerPlayer;
import playingcards.Card;
import playingcards.Deck;
import playingcards.PokerHand;
import simulation_datas.TableSimulationData;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static main.Utils.debug;

public abstract class PokerTable {

    private UUID tableID;
    protected int numPlayers;
    protected double tableHandsPerHour;
    protected boolean shouldFilterPreflop;
    protected List<PlayedHandData> playedHands = new ArrayList<PlayedHandData>();

    public PokerTable(int numPlayers, double tableHandsPerHour, boolean shouldFilterPreflop) {
        this.tableID = UUID.randomUUID();
        this.tableHandsPerHour = tableHandsPerHour;
        this.numPlayers = numPlayers;
        this.shouldFilterPreflop = shouldFilterPreflop;
    }

    public TableSimulationData runSimulation(HighHand highHand, Duration duration) {
         Duration clock = Duration.ofHours(duration.toHours());
         if (Duration.ofHours(1).compareTo(clock) > 0) {
             throw new AssertionError("Invalid simulation duration: " + duration);
         }

         final Map<Long, PokerHand> tableHighHandPerSimulationHour = new HashMap<>();
         while (clock.compareTo(Duration.ZERO) > 0) {
             final PokerHand tableHighHandCurrHour = playHourOfHands(tableHandsPerHour, highHand);
             tableHighHandPerSimulationHour.put(clock.toHours(), tableHighHandCurrHour);
             clock = clock.minus(Duration.ofHours(1));
         }
         return new TableSimulationData(tableID, tableHighHandPerSimulationHour, isPloTable());
    }

    protected abstract boolean isPloTable();

    private PokerHand playHourOfHands(double tableHandsPerHour, HighHand highHand) {
        PokerHand tableHighHandWinner = null;
        for (int i = 0; i < tableHandsPerHour; i++) {
            tableHighHandWinner = playOneHand(i, tableHighHandWinner, highHand);
        }
        debug("\n=============================================\n=== TableHighHandWinner: %s", tableHighHandWinner == null ? "null" : Card.getCardStr(tableHighHandWinner.getFiveHandCards()));
        return tableHighHandWinner;
    }

    private PokerHand playOneHand(int handNum, PokerHand currentTableHighHandWinner, HighHand highHand) {
        PokerHand newTableHighHandWinner = currentTableHighHandWinner;
        debug("\n=============== Table %s Hand #%s", tableID, handNum);
        // Play out one hand
        final Deck deck = new Deck();
        final Collection<PokerPlayer> players = filterPlayersPrePreflop(dealPlayers(deck));
        final List<Card> communityCards = dealCommunityCards(deck);
        debug(" = Community Cards: %s", Card.getCardStr(communityCards.toArray(new Card[0])));
        final Map<UUID, PokerHand> winner = determineWinningHand(players, communityCards);
        final PokerHand winningHand = winner.values().iterator().next();
        final boolean usesBothCards = usesThreeCommunityCards(winningHand, communityCards);
        final boolean qualifiesForHighHand = winningHand != null && isQualifyingHighHand(winningHand, highHand, usesBothCards);
        if (winningHand == null) {
            debug("winningHand is null");
        } else {
            debug(" = Winner: %s, handType=%s, playerID=%s, qualifiesForHH=%s", Card.getCardStr(winningHand.getFiveHandCards()),
                    winningHand.getHandType(), winner.keySet().iterator().next(), qualifiesForHighHand);
            if (qualifiesForHighHand) {
                if (currentTableHighHandWinner == null || winningHand.compare(currentTableHighHandWinner) > 0) {
                    newTableHighHandWinner = winningHand;
                }
            }
        }

        recordPlayedHand(players, communityCards, winningHand, qualifiesForHighHand);
        return newTableHighHandWinner;
    }

    private boolean usesThreeCommunityCards(PokerHand winner, List<Card> communityCards) {
        if (winner == null) {
            return false;
        }
        long numWinningCardsInCommunityCards = Arrays.stream(winner.getFiveHandCards()).filter(communityCards::contains).count();
        if (numWinningCardsInCommunityCards == 3) {
            return true;
        }
        return false;
    }

    /**
     * Given a list of dealt {@link PokerPlayer}s for a hand, filter out hands that would fold pre-flop based on
     * the given hand and the player's VPIP & position.
     * @param dealtPlayers
     * @return
     */
    protected Collection<PokerPlayer> filterPlayersPrePreflop(Collection<PokerPlayer> dealtPlayers) {
        return dealtPlayers.stream().filter(player -> player.shouldSeeFlop()).collect(Collectors.toList());
    }

    public abstract boolean isQualifyingHighHand(PokerHand winner, HighHand highHand, boolean usesBothCards);

    /**
     * Returns winning hand to winning playerID
     */
    protected Map<UUID, PokerHand> determineWinningHand(Collection<PokerPlayer> players, List<Card> communityCards) {
        PokerHand winningHand = null;
        UUID winningPlayerUUID = null;
        for (PokerPlayer pokerPlayer : players) {
            PokerHand pokerPlayerBestHand = pokerPlayer.getBestHand(communityCards);
            if (winningHand == null) {
                winningHand = pokerPlayerBestHand;
                winningPlayerUUID = pokerPlayer.getPlayerID();
                continue;
            }
            if (pokerPlayerBestHand.compare(winningHand) > 0) {
                winningHand = pokerPlayerBestHand;
                winningPlayerUUID = pokerPlayer.getPlayerID();
            }
        }
        final HashMap<UUID, PokerHand> res = new HashMap<>();
        res.put(winningPlayerUUID, winningHand);
        return res;
    }

    public List<Card> dealCommunityCards(Deck deck) {
        final List<Card> communityCards = new ArrayList<>();
        final int numPlayerCards = this instanceof PLOTable ? 4 : 2;
        int currIndex = numPlayers * numPlayerCards;

        // Flop burn
        currIndex += 1;

        // Flop
        communityCards.add(deck.get(currIndex));
        currIndex += 1;
        communityCards.add(deck.get(currIndex));
        currIndex += 1;
        communityCards.add(deck.get(currIndex));
        currIndex += 1;

        // Turn burn
        currIndex += 1;

        // Turn
        communityCards.add(deck.get(currIndex));
        currIndex += 1;

        // River burn
        currIndex += 1;

        // River
        communityCards.add(deck.get(currIndex));
        return communityCards;
    }

    public abstract Collection<PokerPlayer> dealPlayers(Deck deck);

    public void recordPlayedHand(Collection<PokerPlayer> players, List<Card> communityCards, PokerHand winningHand, boolean qualifiesForHighHand) {
        playedHands.add(new PlayedHandData(new ArrayList<>(players), communityCards, winningHand, qualifiesForHighHand));
    }

    public List<PlayedHandData> getPlayedHands() {
        return playedHands;
    }
    public UUID getTableID() {
        return tableID;
    }
}
