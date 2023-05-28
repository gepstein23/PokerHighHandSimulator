package main;

import tables.NLHTable;
import tables.PLOTable;
import tables.PokerTable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static main.Utils.log;

public class Main {

    private static final int DEFAULT_HANDS_PER_HOUR = 20;
    private static final int DEFAULT_NUM_PLAYERS = 8;
    private static final Duration DEFAULT_SIMULATION_DURATION = Duration.ofHours(10000);
    private static final Duration DEFAULT_HIGH_HAND_DURATION = Duration.ofHours(1);
    private static final PokerHand DEFAULT_MINIMUM_QUALIFYING_POKER_HAND = new PokerHand(card(Card.Value.TWO),
            card(Card.Value.TWO), card(Card.Value.TWO), card(Card.Value.THREE), card(Card.Value.THREE));

    public static void main(String[] args) {
        final int numNlhTables = 8;
        final int numPloTables = 5;
        final int numHandsPerHour = DEFAULT_HANDS_PER_HOUR;
        final int numPlayersPerTable = DEFAULT_NUM_PLAYERS;
        final Duration simulationDuration = DEFAULT_SIMULATION_DURATION;
        final HighHand highHand = new HighHand(DEFAULT_MINIMUM_QUALIFYING_POKER_HAND, DEFAULT_HIGH_HAND_DURATION);
        final HighHandSimulator highHandSimulator = new HighHandSimulator(numNlhTables, numPloTables, numHandsPerHour,
                numPlayersPerTable, simulationDuration, highHand);
        highHandSimulator.runSimulation();
    }

    private static Card card(Card.Value value) {
        return Card.card(value);
    }
}
