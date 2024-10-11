package main;

import playingcards.Card;
import playingcards.CardValue;
import playingcards.PokerHand;
import simulation_datas.SimulationData;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static main.Main.DEFAULT_HANDS_PER_HOUR;
import static main.Main.DEFAULT_HIGH_HAND_DURATION;
import static main.Utils.log;

public class MachineLearningHandler {

    private static final Duration MINIMUM_MEANINGFUL_DURATION = Duration.ofHours(20_000);
    private static final Duration SEMI_MEANINGFUL_DURATION = Duration.ofHours(5000);
    private static final int MAX_TABLES = 25;

    public void initMl() {
        processGameCombinations(nlhTablePlayers -> {
            processGameCombinations(ploTablePlayers -> {
                log("============= ML CASE =============");
                log("Finding the ideal HighHand for %s nlhPlayers over %s tables and %s ploPlayers over %s tables", nlhTablePlayers.stream().mapToInt(ii -> ii).sum(), nlhTablePlayers.size(),
                        ploTablePlayers.stream().mapToInt(ii -> ii).sum(), ploTablePlayers.size());

                PokerHand startingNlhMinimumQualifyingHand =
                        new PokerHand(Card.card(CardValue.TWO), Card.card(CardValue.TWO), Card.card(CardValue.TWO), Card.card(CardValue.THREE), Card.card(CardValue.THREE));
                PokerHand startingPloMinimumQualifyingHand = new PokerHand(Card.card(CardValue.TWO), Card.card(CardValue.TWO), Card.card(CardValue.TWO), Card.card(CardValue.THREE), Card.card(CardValue.THREE));
                HighHand highHand = new HighHand(startingNlhMinimumQualifyingHand, startingPloMinimumQualifyingHand, DEFAULT_HIGH_HAND_DURATION);
                boolean isEquilibriumFound = false;
                boolean isEquilibriumClose = false;
                Collection<PokerHand> seenPloHighHands = new ArrayList<>(); // Map<> shouldn't repeat combos TODO
                Collection<PokerHand> seenNlhHighHands = new ArrayList<>();
                while (!isEquilibriumFound) {
                    final Duration simulationDuration = isEquilibriumClose ? MINIMUM_MEANINGFUL_DURATION : SEMI_MEANINGFUL_DURATION;
                    log("======= Trying %s for %s", highHand, simulationDuration);
                    HighHandSimulator highHandSimulator =
                            new HighHandSimulator(nlhTablePlayers, ploTablePlayers, DEFAULT_HANDS_PER_HOUR,
                                    simulationDuration, highHand, true, DEFAULT_HIGH_HAND_DURATION,
                                    /*noPloFlopRestriction*/ true, /*ploTurnRestriction*/ false, /*animate*/ false);
                    final SimulationData data = highHandSimulator.runSimulation();
                    seenPloHighHands.add(highHand.getPloMinimumQualifyingHand());
                    seenNlhHighHands.add(highHand.getNlhMinimumQualifyingHand());
                    long numPloWins = data.getNumPloWins();
                    long numNlhWins = data.getNumNlhWins();
                    long totalWins = numPloWins + numNlhWins;
                    isEquilibriumFound = isEquilibriumFound(numNlhWins, numPloWins);
                    isEquilibriumClose = isEquilibriumClose(numNlhWins, numPloWins);
                    log("======= numNlhWins=%s/%s, numPloWins=%s/%s", numNlhWins, totalWins, numPloWins, totalWins);
                    highHand = pickNextHighHand(highHand, numNlhWins, numPloWins, seenPloHighHands, seenNlhHighHands);
                }
                log("============== Chose %s\n", highHand);
            });
        });
    }

    private HighHand pickNextHighHand(HighHand highHand, long numNlhWins, long numPloWins, Collection<PokerHand> seenPloHighHands, Collection<PokerHand> seenNlhHighHands) {
        boolean increase = numPloWins > numNlhWins;

        PokerHand newPloMinimumQualifyingHand = adjustPokerHand(highHand.getPloMinimumQualifyingHand(), increase);
        final PokerHand newNlhMinimumQualifyingHand;
        PokerHand finalNewPloMinimumQualifyingHand = newPloMinimumQualifyingHand;
        if (newPloMinimumQualifyingHand == null || seenPloHighHands.stream().anyMatch(seen -> seen.compare(finalNewPloMinimumQualifyingHand) == 0)) {
            // if you can't increase/decrease PLO, do the opposite to holdem
            newNlhMinimumQualifyingHand = highHand.getNlhMinimumQualifyingHand().chooseHand(increase);
            if (seenNlhHighHands.stream().anyMatch(seen -> seen.compare(newNlhMinimumQualifyingHand) == 0)) {
                // already seen both, try something else

            }
            newPloMinimumQualifyingHand = newPloMinimumQualifyingHand == null ? highHand.getPloMinimumQualifyingHand() : newPloMinimumQualifyingHand;
        } else {
            newNlhMinimumQualifyingHand = highHand.getNlhMinimumQualifyingHand();
        }


        final HighHand newHighHand = new HighHand(newNlhMinimumQualifyingHand, newPloMinimumQualifyingHand, DEFAULT_HIGH_HAND_DURATION);
        return newHighHand;
    }

    private PokerHand adjustPokerHand(PokerHand ploMinimumQualifyingHand, boolean increase) {
         return ploMinimumQualifyingHand.chooseHand(increase);
    }

    private boolean isEquilibriumFound(long numNlhWins, long numPloWins) {
        double totalWins = numNlhWins + numPloWins;
        double nlhWinRate = (double) numNlhWins / totalWins;
        return nlhWinRate >= 0.499 && nlhWinRate <= 0.501;
    }

    private boolean isEquilibriumClose(long numNlhWins, long numPloWins) {
        double totalWins = numNlhWins + numPloWins;
        double nlhWinRate = (double) numNlhWins / totalWins;
        return nlhWinRate >= 0.48 && nlhWinRate <= 0.52;
    }


    private void processGameCombinations(Consumer<Collection<Integer>> processor) {
        for (int numTables = 1; numTables <= MAX_TABLES; numTables++) {
            generateCombinationsForTableCount(processor, new ArrayList<>(), 0, numTables);
        }
    }

    private static void generateCombinationsForTableCount(Consumer<Collection<Integer>> processor, List<Integer> current, int sum, int numTables) {
        if (current.size() == numTables) {
            if (sum >= numTables * 4 && sum <= numTables * 8) {
                processor.accept(new ArrayList<>(current));
            }
            return;
        }

        for (int playersPerTable = 4; playersPerTable <= 8; playersPerTable++) {
            if (sum + playersPerTable <= 400) {
                List<Integer> newCurrent = new ArrayList<>(current);
                newCurrent.add(playersPerTable);
                generateCombinationsForTableCount(processor, newCurrent, sum + playersPerTable, numTables);
            }
        }
    }

}
