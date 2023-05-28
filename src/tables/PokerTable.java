package tables;

import playingcards.Card;
import playingcards.Deck;
import main.HighHand;
import playingcards.PokerHand;
import players.PokerPlayer;
import simulation_datas.TableSimulationData;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static main.Utils.log;

public abstract class PokerTable {
    protected int numPlayers;
    protected double tableHandsPerHour;

    public PokerTable(int numPlayers, double tableHandsPerHour) {
        this.tableHandsPerHour = tableHandsPerHour;
        this.numPlayers = numPlayers;
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
         return new TableSimulationData(tableHighHandPerSimulationHour, isPloTable());
    }

    protected abstract boolean isPloTable();

    private PokerHand playHourOfHands(double tableHandsPerHour, HighHand highHand) {
        PokerHand tableHighHandWinner = null;
        for (int i = 0; i < tableHandsPerHour; i++) {
            // Play out one hand
            final Deck deck = new Deck();
            final Collection<PokerPlayer> players = filterPlayersPrePreflop(dealPlayers(deck));
            final List<Card> communityCards = dealCommunityCards(deck);
            final PokerHand winner = determineWinningHand(players, communityCards);
            final boolean qualifiesForHighHand = isQualifyingHighHand(winner, highHand);
            if (qualifiesForHighHand) {
                if (tableHighHandWinner == null || winner.compare(tableHighHandWinner) > 0) {
                    tableHighHandWinner = winner;
                }
            }
        }
        return tableHighHandWinner;
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

    protected abstract boolean isQualifyingHighHand(PokerHand winner, HighHand highHand);

    protected PokerHand determineWinningHand(Collection<PokerPlayer> players, List<Card> communityCards) {
        PokerHand winningHand = null;
        for (PokerPlayer pokerPlayer : players) {
            PokerHand pokerPlayerBestHand = pokerPlayer.getBestHand(communityCards);
            if (winningHand == null) {
                winningHand = pokerPlayerBestHand;
                continue;
            }
            if (pokerPlayerBestHand.compare(winningHand) > 0) {
                winningHand = pokerPlayerBestHand;
            }
        }
        return winningHand;
    }

    private List<Card> dealCommunityCards(Deck deck) {
        final List<Card> communityCards = new ArrayList<>();
        int currIndex = numPlayers * 4 + 1;

        // Flop burn
        currIndex += 1;

        // Flop
        communityCards.add(deck.get(currIndex)); // TODO make sure this is in order
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

    protected abstract Collection<PokerPlayer> dealPlayers(Deck deck);
}
