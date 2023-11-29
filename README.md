# PokerHighHandSimulator

## Problem Overview
Poker rooms use [High Hand (HH) promotions](https://www.pokernews.com/strategy/casino-poker-for-beginners-high-hand-bonus-bad-beat-jackpot-21976.htm) to attract [No Limit Hold 'Em (NLH)](https://en.wikipedia.org/wiki/Texas_hold_%27em) and [Pot Limit Omaha (PLO)](https://en.wikipedia.org/wiki/Omaha_hold_%27em) players. 
In these promotions, the player with the [best ranked poker hand](https://www.britannica.com/story/poker-hands-ranked) during a set period, usually an hour, wins a set monetary prize.

To be eligible, players pay a [rake](https://en.wikipedia.org/wiki/Rake_(poker)#:~:text=Rake%20is%20the%20scaled%20commission,casino%20to%20take%20the%20rake.) taken from qualifying pots. 
The challenge arises because PLO and NLH have different odds of getting the strongest hands. 
Some poker rooms address this discrepancy by restricting PLO players from winning the HH unless their hand utilizes the first 3 [community cards](https://upswingpoker.com/glossary/community-cards/).

This project aims to find a fair solution for HH promotions and quantify the fairness of different HH promotion configurations.

## Project Description
This program allows the user to simulate HH promotions running at a poker room with NLH and PLO tables. 
The program plays through poker hands per table, stores qualifying HHs per period, 
and compares them to qualifying HHs from other tables during the same period. Once the simulation concludes, 
the program outputs the winning statistics per game type for the simulation duration.

## Program Usage Instructions
| Option                                | Description                                                                                                 |
| ------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| -nlhT,--numNlhTables \<arg\>           | Number of NLH Tables to simulate. Defaults to 8                                                             |
| -ploT,--numPloTables \<arg\>           | Number of PLO Tables to simulate. Defaults to 4                                                             |
| -p,--numPlayersPerTable \<arg\>        | Number of players per table to simulate. Defaults to 8                                                      |
| -h,--numHandsPerHour \<arg\>           | Number of hands played per hour PLO/NLH table. Defaults 25 |
| -d,--simulationDuration \<arg\>        | Simulation duration in hours, minimum 1 hour. Defaults to PT10000H                                           |
| -hhd,--highHandDuration \<arg\>        | High hand time period. Defaults to PT1H                                                                      |
| -hh,--highHandMinimumQualifier \<arg\> | Minimum qualifying High Hand applicable for both game types. (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to [PokerHand: handType=FULL_HOUSE {2 2 2 3 3}] |
| -phh,--ploHighHandMinimumQualifier \<arg\> | Default minimum qualifying High Hand for PLO (overwrites highHandMinimumQualifier for PLO only if specified). (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to [PokerHand: handType=FULL_HOUSE {2 2 2 3 3}] |
| -npfr,--noPloFlopRestriction           | If this option is added, removes restriction that PLO must flop the HH to qualify       |
| -sfp,--shouldFilterPreflop            | If this option is added, filters players' cards to fold pre-flop if they are not within an individually-randomly-assigned VPIP between 10% and 50% *[NOT YET IMPLEMENTED]* |
| -nlhrl,--includeNlhRiverLikelihood    | If this option is added, terminates NLH hands early if likely to fold IRL *[NOT YET IMPLEMENTED]*             |## Results

### Statistics for Typical HH Promotions

For a typical HH promotion, where PLO players must *flop* the high hand but NLH players must not, and the simulation assumes all players make it to the river, it is 2x easier to get a HH as a NLH player than as a PLO player.

| Run | Description                                  | Options                                       | Results         | Output File         |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|---------------------|
| 1   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, 22233 qualifier     | `--highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 8`          |   ABC  | [run1_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run1_output.txt)     |
| 2   | 500000 HHs, 8 players, Equal 8 NLH/PLO tables, 22233 qualifier      | `--simulationDuration 500000 --highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 8`       |    ABC    | [run2_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run2_output.txt)     |
| 3   | 500000 HHs, 8 players, Equal 1 NLH/PLO tables, AAAKK qualifier      | `--simulationDuration 500000 --highHandMinimumQualifier AAAKK --numNlhTables 8 --numPloTables 8`       |    ABC    | [run3_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run3_output.txt)    |
| 4   | 500000 HHs, 8 players, Equal 1 NLH/PLO table, 22233 qualifier      | `--simulationDuration 500000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1`      |  ABC | [run4_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run4_output.txt)    |
| 5   | 10000 HHs, 8 players, 8 NLH, 4 PLO tables, 22233 qualifier       | `--highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 4`       | ABC | [run5_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run5_output.txt)     |
| 6   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, *NO PLO Flop Restriction*, 22233 qualifier       | `--highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 8 --noPloFlopRestriction`       | ABC | [run6_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run6_output.txt)     |

## Finding a Solution

## Hypotheses
It is clear the current default is unfair for PLO players. 
#### 1) Remove PLO Flop Restriction & Find Equalizing Respective Minimum HH Qualifiers
Can we make the HH promotion equitable for both games if we remove the flop restriction for PLO (to isolate the exact odds of winning), and then come up with different minimum qualifying hands for both games in order to make the chances of winning equal?

| Run       | Description                                                                                                 | Options                                                                                                                               | Results   | Comments          | Output File                                                       |
|-----------|-------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|-----------|----------------------|-------------------------------------------------------------------|
| AA        | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, AAAAK qualifier PLO, 22233 qualifier NLH | `--highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier AAAAK --noPloFlopRestriction` |   XYZ     | Unfair for PLO       | [runAA_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_output.txt)                 |
| AA2       | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 2222A qualifier PLO, 22233 qualifier NLH | `--highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 2222A --noPloFlopRestriction` |   XYZ     | Unfair for NLH       | [runAA2_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA2_output.txt)               |
| AA3       | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 5555A qualifier PLO, 22233 qualifier NLH | `--highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 5555A --noPloFlopRestriction` |   XYZ     | Almost fair          | [runAA3_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA3_output.txt)               |
| AA4       | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777Q qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 7777Q --noPloFlopRestriction` |   XYZ     | Fairness achieved? Let's verify while increasing simulation size   | [runAA4_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA4_output.txt)               |
| AA5       | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777K qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 7777K --noPloFlopRestriction` |   XYZ     | Unfair for NLH       | [runAA5_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA5_output.txt)               |
| AA6 (**WINNER**) | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   XYZ     |                      | [runAA6_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA6_output.txt)               |
| AA7       | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777A qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 7777A --noPloFlopRestriction` |   XYZ     |                      | [runAA7_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA7_output.txt)               |
| AA_final1 | 100000 HHs, 8 players, Equal 2 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 2 --numPloTables 2 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   XYZ     | Shows how minimum qualifier means nothing as tables increase       | [runAA_final1_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final1_output.txt) |
| AA_final2 | 100000 HHs, 8 players, Equal 4 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 4 --numPloTables 4 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   XYZ     | Shows how minimum qualifier means nothing as tables increase       | [runAA_final2_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final2_output.txt) |
| AA_final3 | 100000 HHs, 8 players, Equal 8 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 8 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   XYZ     | Shows how minimum qualifier means nothing as tables increase       | [runAA_final3_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final3_output.txt) |
| AA_final4 | 100000 HHs, 8 players, Equal 16 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 16 --numPloTables 16 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   XYZ     | Shows how minimum qualifier means nothing as tables increase       | [runAA_final4_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final4_output.txt) |

RESULT: This won't work because unfairness will increase proportionally to the number of tables active (i.e., the more tables => the more likely a bigger hand well beyond the qualifier will win)

#### 2) PLO Turn Restriction
Can we make the HH promotion equitable for both games if we update the flop restriction to be a turn restriction?
```TODO```
  
## Algorithm Implementation Details

### Known Limitations
1) No accurate comparisons/ranking for PokerHands below Flushes (not needed for this simulation)

### Testing 
See [PokerHighHandSimulatorTests.java](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/test/PokerHighHandSimulatorTests.java) and [PokerHighHandSimulatorTests_results.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/PokerHighHandSimulatorTests_results.txt)