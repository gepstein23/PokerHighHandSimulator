package com.genevieve.pokersim.main;//package main;
//
//import org.apache.commons.cli.*;
//import playingcards.Card;
//import playingcards.CardValue;
//import playingcards.PokerHand;
//
//import java.time.Duration;
//
//public class Main {
//    public static final int DEFAULT_HANDS_PER_HOUR = 25;
//    private static final int DEFAULT_NUM_PLAYERS = 8;
//    private static final int DEFAULT_NUM_NLH_TABLES = 8;
//    private static final int DEFAULT_NUM_PLO_TABLES = 4;
//    private static final Duration DEFAULT_SIMULATION_DURATION = Duration.ofHours(10000);
//    public static final Duration DEFAULT_HIGH_HAND_DURATION = Duration.ofHours(1);
//    private static final PokerHand DEFAULT_MINIMUM_QUALIFYING_POKER_HAND = new PokerHand(card(CardValue.TWO),
//            card(CardValue.TWO), card(CardValue.TWO), card(CardValue.THREE), card(CardValue.THREE));
//    private static final Options COMMAND_LINE_OPTIONS = new Options();
//    public static final String SIMULATION_DURATION = "simulationDuration";
//    public static final String NUM_PLAYERS_PER_TABLE = "numPlayersPerTable";
//    public static final String NUM_HANDS_PER_HOUR = "numHandsPerHour";
//    public static final String NUM_PLO_TABLES = "numPloTables";
//    public static final String NUM_NLH_TABLES = "numNlhTables";
//    public static final String HIGH_HAND_MINIMUM_QUALIFIER = "highHandMinimumQualifier";
//    public static final String PLO_HIGH_HAND_MINIMUM_QUALIFIER = "ploHighHandMinimumQualifier";
//    public static final String HIGH_HAND_DURATION = "highHandDuration";
//    public static final String SHOULD_FILTER_PREFLOP = "shouldFilterPreflop";
//    public static final String INCLUDE_NLH_RIVER_LIKELIHOOD = "includeNlhRiverLikelihood";
//    public static final String NO_PLO_FLOP_RESTRICTION = "noPloFlopRestriction";
//    public static final String PLO_TURN_RESTRICTION = "ploTurnRestriction";
//    private static final String MACHINE_LEARNING = "machineLearning";
//    private static final String SHOULD_DISPLAY_ANIMATION = "animate";
//
//    static {
//        final Option numNlhTables = new Option("nlhT", NUM_NLH_TABLES, true,
//                "Number of NLH Tables to simulate. Defaults to " + DEFAULT_NUM_NLH_TABLES);
//        numNlhTables.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(numNlhTables);
//
//        final Option numPloTables = new Option("ploT", NUM_PLO_TABLES, true,
//                "Number of PLO Tables to simulate. Defaults to " + DEFAULT_NUM_PLO_TABLES);
//        numPloTables.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(numPloTables);
//
//        final Option numHandsPerHour = new Option("h", NUM_HANDS_PER_HOUR, true,
//                String.format("Number of hands played per hour PLO/NLH table. Defaults to " + DEFAULT_HANDS_PER_HOUR));
//        numHandsPerHour.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(numHandsPerHour);
//
//        final Option numPlayersPerTable = new Option("p", NUM_PLAYERS_PER_TABLE, true,
//                "Number of players per table to simulate. Defaults to " + DEFAULT_NUM_PLAYERS);
//        numPlayersPerTable.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(numPlayersPerTable);
//
//        final Option simulationDuration = new Option("d", SIMULATION_DURATION, true,
//                "Simulation duration in hours, minimum 1 hour. Defaults to " + DEFAULT_SIMULATION_DURATION);
//        simulationDuration.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(simulationDuration);
//
//        final Option highHandMinimumQualifier = new Option("hh", HIGH_HAND_MINIMUM_QUALIFIER, true,
//                "Minimum qualifying High Hand applicable for both game types. (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to " + DEFAULT_MINIMUM_QUALIFYING_POKER_HAND);
//        highHandMinimumQualifier.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(highHandMinimumQualifier);
//
//        final Option ploHighHandMinimumQualifier = new Option("phh", PLO_HIGH_HAND_MINIMUM_QUALIFIER, true,
//                "Default minimum qualifying High Hand for PLO (overwrites highHandMinimumQualifier for PLO only if specified). (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to " + DEFAULT_MINIMUM_QUALIFYING_POKER_HAND);
//        ploHighHandMinimumQualifier.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(ploHighHandMinimumQualifier);
//
//        final Option highHandDuration = new Option("hhd", HIGH_HAND_DURATION, true,
//                "High hand time period. Defaults to " + DEFAULT_HIGH_HAND_DURATION);
//        highHandDuration.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(highHandDuration);
//
//        final Option shouldFilterPreflop = new Option("sfp", SHOULD_FILTER_PREFLOP, false,
//                "If this option is added, filters players' cards to fold pre-flop if they are not within an individually-randomly-assigned VPIP between 10% and 50% [NOT YET IMPLEMENTED]");
//        shouldFilterPreflop.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(shouldFilterPreflop);
//
//        final Option includeNlhRiverLikelihood = new Option("nlhrl", INCLUDE_NLH_RIVER_LIKELIHOOD, false,
//                "If this option is added, terminates NLH hands early if likely to fold IRL [NOT YET IMPLEMENTED]");
//        includeNlhRiverLikelihood.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(includeNlhRiverLikelihood);
//
//        final Option noPloFlopRestriction = new Option("npfr", NO_PLO_FLOP_RESTRICTION, false,
//                "If this option is added, removes restriction that PLO must flop the HH to qualify");
//        noPloFlopRestriction.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(noPloFlopRestriction);
//
//        final Option ploTurnRestriction = new Option("ptr", PLO_TURN_RESTRICTION, false,
//                "If this option is added, PLO must get their HH by the turn to qualify. Overrides noPloFlopRestriction");
//        ploTurnRestriction.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(ploTurnRestriction);
//
//        final Option animate = new Option("a", SHOULD_DISPLAY_ANIMATION, false,
//                "If this option is added, the animation will play once the simulation concludes");
//        animate.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(animate);
//
//        final Option machineLearning = new Option("ml", MACHINE_LEARNING, false,
//                "If this option is added, initialize the machine learning algorithm");
//        machineLearning.setRequired(false);
//        COMMAND_LINE_OPTIONS.addOption(machineLearning);
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        CommandLineParser parser = new DefaultParser();
//        HelpFormatter formatter = new HelpFormatter();
//        CommandLine cmd = null;
//
//        try {
//            cmd = parser.parse(COMMAND_LINE_OPTIONS, args);
//        } catch (ParseException e) {
//            System.out.println(e.getMessage());
//            formatter.printHelp("PokerHighHandSimulator", COMMAND_LINE_OPTIONS);
//            System.exit(1);
//        }
//
////        final boolean runMachineLearning = cmd.hasOption(MACHINE_LEARNING);
////        if (runMachineLearning) {
////            runMachineLearning();
////            return;
////        }
//
//        final String inputNumNlhTables = cmd.getOptionValue(NUM_NLH_TABLES);
//        final int numNlhTables = inputNumNlhTables == null ? DEFAULT_NUM_NLH_TABLES : Integer.parseInt(inputNumNlhTables);
//
//        final String inputNumPloTables = cmd.getOptionValue(NUM_PLO_TABLES);
//        final int numPloTables = inputNumPloTables == null ? DEFAULT_NUM_PLO_TABLES : Integer.parseInt(inputNumPloTables);
//
//        final String inputNumHandsPerHour = cmd.getOptionValue(NUM_HANDS_PER_HOUR);
//        final int numHandsPerHour = inputNumHandsPerHour == null ? DEFAULT_HANDS_PER_HOUR : Integer.parseInt(inputNumHandsPerHour);
//
//        final String inputNumPlayersPerTable = cmd.getOptionValue(NUM_PLAYERS_PER_TABLE);
//        final int numPlayersPerTable = inputNumPlayersPerTable == null ? DEFAULT_NUM_PLAYERS : Integer.parseInt(inputNumPlayersPerTable);
//
//        final String inputSimulationDuration = cmd.getOptionValue(SIMULATION_DURATION);
//        final Duration simulationDuration = inputSimulationDuration == null ? DEFAULT_SIMULATION_DURATION
//                : Duration.ofHours(Integer.parseInt(inputSimulationDuration));
//
//        final String inputHighHandMinimumQualifier = cmd.getOptionValue(HIGH_HAND_MINIMUM_QUALIFIER);
//        final PokerHand nlhHighHandMinimumQualifier = inputHighHandMinimumQualifier == null ?
//                DEFAULT_MINIMUM_QUALIFYING_POKER_HAND : PokerHand.from(inputHighHandMinimumQualifier);
//
//        final String inputPloHighHandMinimumQualifier = cmd.getOptionValue(PLO_HIGH_HAND_MINIMUM_QUALIFIER);
//        final PokerHand ploHighHandMinimumQualifier = inputPloHighHandMinimumQualifier == null ?
//                nlhHighHandMinimumQualifier : PokerHand.from(inputPloHighHandMinimumQualifier);
//
//        final String inputHighHandDuration = cmd.getOptionValue(HIGH_HAND_DURATION);
//        final Duration highHandDuration = inputHighHandDuration == null ? DEFAULT_HIGH_HAND_DURATION
//                : Duration.ofHours(Integer.parseInt(inputHighHandDuration));
//
//        final boolean shouldFilterPreflop = cmd.hasOption(SHOULD_FILTER_PREFLOP);
//        final boolean animate = cmd.hasOption(SHOULD_DISPLAY_ANIMATION);
//        final boolean noPloFlopRestriction = cmd.hasOption(NO_PLO_FLOP_RESTRICTION);
//        final boolean ploTurnRestriction = cmd.hasOption(PLO_TURN_RESTRICTION); // TODO consolidate
//        // final boolean includeNlhRiverLikelihood = cmd.hasOption(INCLUDE_NLH_RIVER_LIKELIHOOD);
//
//        final HighHand highHand = new HighHand(nlhHighHandMinimumQualifier, ploHighHandMinimumQualifier, highHandDuration);
//        final HighHandSimulator highHandSimulator = new HighHandSimulator(numNlhTables, numPloTables, numHandsPerHour,
//                numPlayersPerTable, simulationDuration, highHand, shouldFilterPreflop, highHandDuration, noPloFlopRestriction, ploTurnRestriction, animate);
//        highHandSimulator.runSimulation();
//    }
//
//    private static void runMachineLearning() {
//        new MachineLearningHandler().initMl();
//    }
//
//    private static Card card(CardValue value) {
//        return Card.card(value);
//    }
//}
