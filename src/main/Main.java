package main;

import org.apache.commons.cli.*;
import playingcards.Card;
import playingcards.CardValue;
import playingcards.PokerHand;

import java.time.Duration;

import static main.Utils.log;

public class Main {
    private static final int DEFAULT_HANDS_PER_HOUR = 20;
    private static final int DEFAULT_NUM_PLAYERS = 8;
    private static final int DEFAULT_NUM_NLH_TABLES = 8;
    private static final int DEFAULT_NUM_PLO_TABLES = 4;
    private static final int DEFAULT_HANDS_PER_HOUR_PLO_LOWER_BOUND = 20;
    private static final int DEFAULT_HANDS_PER_HOUR_PLO_UPPER_BOUND = 30;
    private static final int DEFAULT_HANDS_PER_HOUR_NLH_LOWER_BOUND = 20;
    private static final int DEFAULT_HANDS_PER_HOUR_NLH_UPPER_BOUND = 25;
    private static final Duration DEFAULT_SIMULATION_DURATION = Duration.ofHours(10);
    private static final Duration DEFAULT_HIGH_HAND_DURATION = Duration.ofHours(1);
    private static final PokerHand DEFAULT_MINIMUM_QUALIFYING_POKER_HAND = new PokerHand(card(CardValue.TWO),
            card(CardValue.TWO), card(CardValue.TWO), card(CardValue.THREE), card(CardValue.THREE));
    private static final Options COMMAND_LINE_OPTIONS = new Options();
    static {
        final Option numNlhTables = new Option("nlhT", "numNlhTables", true,
                "Number of NLH Tables to simulate. Defaults to " + DEFAULT_NUM_NLH_TABLES);
        numNlhTables.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(numNlhTables);

        final Option numPloTables = new Option("ploT", "numPloTables", true,
                "Number of PLO Tables to simulate. Defaults to " + DEFAULT_NUM_PLO_TABLES);
        numPloTables.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(numPloTables);

        final Option numHandsPerHour = new Option("h", "numHandsPerHour", true,
                String.format("Number of hands played per hour PLO/NLH table. Defaults to a random value between [%s,%s]" +
                        " for PLO and [%s,%s] for NLH", DEFAULT_HANDS_PER_HOUR_PLO_LOWER_BOUND,
                        DEFAULT_HANDS_PER_HOUR_PLO_UPPER_BOUND,
                        DEFAULT_HANDS_PER_HOUR_NLH_LOWER_BOUND, DEFAULT_HANDS_PER_HOUR_NLH_UPPER_BOUND));
        numHandsPerHour.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(numHandsPerHour);

        final Option numPlayersPerTable = new Option("p", "numPlayersPerTable", true,
                "Number of players per table to simulate. Defaults to " + DEFAULT_NUM_PLAYERS);
        numPlayersPerTable.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(numPlayersPerTable);

        final Option simulationDuration = new Option("d", "simulationDuration", true,
                "Simulation duration in hours, minimum 1 hour. Defaults to " + DEFAULT_SIMULATION_DURATION);
        simulationDuration.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(simulationDuration);

        final Option highHandMinimumQualifier = new Option("hh", "highHandMinimumQualifier", true,
                "Default minimum qualifying High Hand. (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to " + DEFAULT_MINIMUM_QUALIFYING_POKER_HAND);
        highHandMinimumQualifier.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(highHandMinimumQualifier);

        final Option highHandDuration = new Option("hhd", "highHandDuration", true,
                "High hand time period. Defaults to " + DEFAULT_HIGH_HAND_DURATION);
        highHandDuration.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(highHandDuration);

        final Option shouldFilterPreflop = new Option("sfp", "shouldFilterPreflop", false,
                "If this option is added, filters players' cards to fold pre-flop if they are not within an individually-randomly-assigned VPIP between 10% and 50% [NOT YET IMPLEMENTED]");
        highHandDuration.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(shouldFilterPreflop);

        final Option includeNlhRiverLikelihood = new Option("nlhrl", "includeNlhRiverLikelihood", false,
                "If this option is added, terminates NLH hands early if likely to fold IRL [NOT YET IMPLEMENTED]");
        highHandDuration.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(includeNlhRiverLikelihood);

        final Option noPloFlopRestriction = new Option("npfr", "noPloFlopRestriction", false,
                "If this option is added, removes restriction that PLO must flop the HH to qualify [NOT YET IMPLEMENTED]");
        highHandDuration.setRequired(false);
        COMMAND_LINE_OPTIONS.addOption(noPloFlopRestriction);
    }

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(COMMAND_LINE_OPTIONS, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("PokerHighHandSimulator", COMMAND_LINE_OPTIONS);
            System.exit(1);
        }

        final String inputNumNlhTables = cmd.getOptionValue("numNlhTables");
        final int numNlhTables = inputNumNlhTables == null ? DEFAULT_NUM_NLH_TABLES : Integer.parseInt(inputNumNlhTables);

        final String inputNumPloTables = cmd.getOptionValue("numPloTables");
        final int numPloTables = inputNumPloTables == null ? DEFAULT_NUM_PLO_TABLES : Integer.parseInt(inputNumPloTables);

        final String inputNumHandsPerHour = cmd.getOptionValue("numHandsPerHour");
        final int numHandsPerHour = inputNumHandsPerHour == null ? 25 : Integer.parseInt(inputNumHandsPerHour);

        final String inputNumPlayersPerTable = cmd.getOptionValue("numPlayersPerTable");
        final int numPlayersPerTable = inputNumPlayersPerTable == null ? DEFAULT_NUM_PLAYERS : Integer.parseInt(inputNumPlayersPerTable);

        final String inputSimulationDuration = cmd.getOptionValue("simulationDuration");
        final Duration simulationDuration = inputSimulationDuration == null ? DEFAULT_SIMULATION_DURATION
                : Duration.ofHours(Integer.parseInt(inputSimulationDuration));

        final String inputHighHandMinimumQualifier = cmd.getOptionValue("highHandMinimumQualifier");
        final PokerHand highHandMinimumQualifier = inputHighHandMinimumQualifier == null ?
                DEFAULT_MINIMUM_QUALIFYING_POKER_HAND : PokerHand.from(inputHighHandMinimumQualifier);

        final String inputHighHandDuration = cmd.getOptionValue("highHandDuration");
        final Duration highHandDuration = inputHighHandDuration == null ? DEFAULT_HIGH_HAND_DURATION
                : Duration.ofHours(Integer.parseInt(inputHighHandDuration));

        final String inputShouldFilterPreflop = cmd.getOptionValue("shouldFilterPreflop");
        final boolean shouldFilterPreflop = inputShouldFilterPreflop != null;

        final String inputNoPloFlopRestriction = cmd.getOptionValue("noPloFlopRestriction");
        final boolean noPloFlopRestriction = inputNoPloFlopRestriction != null;

        final String inputIncludeNlhRiverLikelihood = cmd.getOptionValue("includeNlhRiverLikelihood");
        final boolean includeNlhRiverLikelihood = inputIncludeNlhRiverLikelihood != null;

        final HighHand highHand = new HighHand(highHandMinimumQualifier, highHandDuration);
        final HighHandSimulator highHandSimulator = new HighHandSimulator(numNlhTables, numPloTables, numHandsPerHour,
                numPlayersPerTable, simulationDuration, highHand, shouldFilterPreflop, highHandDuration, noPloFlopRestriction);
        highHandSimulator.runSimulation();
    }

    private static Card card(CardValue value) {
        return Card.card(value);
    }
}
