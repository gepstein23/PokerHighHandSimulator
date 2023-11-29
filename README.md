# PokerHighHandSimulator

## Problem Overview
Poker rooms use [High Hand (HH) promotions](https://www.pokernews.com/strategy/casino-poker-for-beginners-high-hand-bonus-bad-beat-jackpot-21976.htm) to attract [No Limit Hold 'Em (NLH)](https://en.wikipedia.org/wiki/Texas_hold_%27em) and [Pot Limit Omaha (PLO)](https://en.wikipedia.org/wiki/Omaha_hold_%27em) players. 
In these promotions, the player with the [best ranked poker hand](https://www.britannica.com/story/poker-hands-ranked) during a set period, usually an hour, wins a set monetary prize.

To be eligible, players pay a [rake](https://en.wikipedia.org/wiki/Rake_(poker)#:~:text=Rake%20is%20the%20scaled%20commission,casino%20to%20take%20the%20rake.) taken from qualifying pots. 
The challenge arises because PLO and NLH have different odds of getting the strongest hands. 
Some poker rooms address this discrepancy by restricting PLO players from winning the HH unless their hand utilizes the first 3 [community cards](https://upswingpoker.com/glossary/community-cards/).

This project aims to find a fair solution for HH promotions and quantify the fairness of different HH promotion configurations.

## Project Description
This program allows the user to simulate HH promotions running at a Poker Room with NLH and PLO tables. 
The program plays through poker hands per table, stores qualifying HHs per period, 
and compares them to qualifying HHs from other tables during the same period. Once the simulation concludes, 
the program outputs the winning statistics per game type for the simulation duration.

## Program Usage Instructions
| Option                                | Description                                                                                                 |
| ------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| -nlhT,--numNlhTables \<arg\>           | Number of NLH Tables to simulate. Defaults to 8                                                             |
| -ploT,--numPloTables \<arg\>           | Number of PLO Tables to simulate. Defaults to 4                                                             |
| -p,--numPlayersPerTable \<arg\>        | Number of players per table to simulate. Defaults to 8                                                      |
| -h,--numHandsPerHour \<arg\>           | Number of hands played per hour PLO/NLH table. Defaults to a random value between [20,30] for PLO and [20,25] for NLH |
| -d,--simulationDuration \<arg\>        | Simulation duration in hours, minimum 1 hour. Defaults to PT10H                                              |
| -hhd,--highHandDuration \<arg\>        | High hand time period. Defaults to PT1H                                                                      |
| -hh,--highHandMinimumQualifier \<arg\> | Minimum qualifying High Hand applicable for both game types. (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to [PokerHand: handType=FULL_HOUSE {2 2 2 3 3}] |
| -phh,--ploHighHandMinimumQualifier \<arg\> | Default minimum qualifying High Hand for PLO (overwrites highHandMinimumQualifier for PLO only if specified). (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to [PokerHand: handType=FULL_HOUSE {2 2 2 3 3}] |
| -npfr,--noPloFlopRestriction           | If this option is added, removes restriction that PLO must flop the HH to qualify [NOT YET IMPLEMENTED]      |
| -sfp,--shouldFilterPreflop            | If this option is added, filters players' cards to fold pre-flop if they are not within an individually-randomly-assigned VPIP between 10% and 50% [NOT YET IMPLEMENTED] |
| -nlhrl,--includeNlhRiverLikelihood    | If this option is added, terminates NLH hands early if likely to fold IRL [NOT YET IMPLEMENTED]             |## Results

### Conclusive Data

For a typical HH promotion, where PLO players must *flop* the high hand but NLH players must not, and the simulation assumes all players make it to the river, it is 2x easier to get a HH as a NLH player than as a PLO player.

| Run | Description                                  | Command                                       | Output File         |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|
| [1](#Run-1)   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, 22233 qualifier     | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8`         | [run1_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run1_output.txt)     |
| [2](#Run-2)   | 500000 HHs, 8 players, Equal 8 NLH/PLO tables, 22233 qualifier      | `--simulationDuration 1000000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8`         | [run2_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run2_output.txt)     |
| [3](#Run-3)   | 500000 HHs, 8 players, Equal 1 NLH/PLO tables, AAAKK qualifier      | `--simulationDuration 1000000 --numHandsPerHour 25 --highHandMinimumQualifier AAAKK --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8`         | [run3_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run3_output.txt)    |
| [4](#Run-4)   | 500000 HHs, 8 players, Equal 1 NLH/PLO table, 22233 qualifier      | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8`   | [run4_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run4_output.txt)    |
| [5](#Run-5)   | 10000 HHs, 8 players, 8 NLH, 4 PLO tables, 22233 qualifier       | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 4 --numPlayersPerTable 8` | [run5_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run5_output.txt)     |
| [6](#Run-6)   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, NO PLO Flop Restriction, 22233 qualifier       | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8 --noPloFlopRestriction` | [run6_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run6_output.txt)     |

#### Run 1
```
64 Total NLH Players spread over 8 NLH tables
64 Total PLO Players spread over 8 PLO tables
NLH won HH 6735/10000 times (67.35%)
PLO won HH 3265/10000 times (32.65%)
Odds Ratio (NLH vs PLO): (0.99)              
```
#### Run 2
```
            TODO (in progress)        
```
#### Run 3
```
            TODO (in progress)
```
#### Run 4
```
8 Total NLH Players spread over 1 NLH tables
8 Total PLO Players spread over 1 PLO tables
NLH won HH 7068/10000 times (70.68%)
PLO won HH 2618/10000 times (26.18%)
Odds Ratio (NLH vs PLO): (1.00)       
```
#### Run 5
```
64 Total NLH Players spread over 8 NLH tables
32 Total PLO Players spread over 4 PLO tables
NLH won HH 8108/10000 times (81.08%)
PLO won HH 1892/10000 times (18.92%)
Odds Ratio (NLH vs PLO): (0.99)               
```
#### Run 6 (PLO does not need to flop the high hand)
```
64 Total NLH Players spread over 8 NLH tables
64 Total PLO Players spread over 8 PLO tables
NLH won HH 1582/10000 times (15.82%)
PLO won HH 8418/10000 times (84.18%)
Odds Ratio (NLH vs PLO): (1.03)             
```

## Finding a Solution

## Hypotheses
It is clear the current default is unfair for PLO players. 
#### 1) We can make the HH promotion equitable for both games if we remove the flop restriction for PLO (to isolate the exact odds of winning), and then come up with different minimum qualifying hands for both games in order to make the chances of winning equal.
| Run | Description                                  | Command                                       | Output File         | Conclusion          |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|----------------------|
| AA   | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, AAAAK qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8 --ploHighHandMinimumQualifier AAAAK --noPloFlopRestriction`         | [runAA_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_output.txt)     | Unfair for PLO: |
| AA2   | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 2222A qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 2222A --noPloFlopRestriction`         | [runAA2_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA2_output.txt)     | Unfair for NLH:  |
| AA3   | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 5555A qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 5555A --noPloFlopRestriction`         | [runAA3_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA3_output.txt)     | Almost fair:  |
| AA4   | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777Q qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 100000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 7777Q --noPloFlopRestriction`         | [runAA4_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA4_output.txt)     | Fairness achieved? Let's verify while increasing simulation size:   |
| AA5   | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777K qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 100000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 7777K --noPloFlopRestriction`         | [runAA5_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA5_output.txt)     | Unfair for NLH?  |
| AA6 WINNER   | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 100000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction`         | [runAA6_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA6_output.txt)     |   |
| AA7   | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777A qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 100000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 7777A --noPloFlopRestriction`         | [runAA7_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA7_output.txt)     |   |
| AA_final1   | 100000 HHs, 8 players, Equal 2 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 100000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 2 --numPloTables 2 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction`         | [runAA_final1_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final1_output.txt)     | Shows how minimum qualifier means nothing as tables increase  |
| AA_final1   | 100000 HHs, 8 players, Equal 4 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 100000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 4 --numPloTables 4 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction`         | [runAA_final1_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final1_output.txt)     | Shows how minimum qualifier means nothing as tables increase  |
| AA_final1   | 100000 HHs, 8 players, Equal 8 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 100000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction`         | [runAA_final1_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final1_output.txt)     | Shows how minimum qualifier means nothing as tables increase  |
| AA_final2   | 100000 HHs, 8 players, Equal 16 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 100000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 16 --numPloTables 16 --numPlayersPerTable 8 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction`         | [runAA_final2_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final2_output.txt)     | Shows how minimum qualifier means nothing as tables increase  |

RESULT: This won't work because unfairness will increase proportionally to the number of tables active (i.e., the more tables => the more likely a bigger hand well beyond the qualifier will win)

#### 2) We can make the HH promotion equitable for both games if we update the flop restriction to be a turn restriction
| Run | Description                                  | Command                                       | Output File         | Conclusion          |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|----------------------|
| BB   | 10000 HHs, 8 players, Equal 4 NLH/PLO tables, NO PLO flop restriction, AAAAK qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 4 --numPloTables 4 --numPlayersPerTable 8 --ploHighHandMinimumQualifier AAAAK --noPloFlopRestriction`         | [runBB_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runBB_output.txt)     | Your conclusion here |
| BB2   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, NO PLO flop restriction, AAAAK qualifier PLO, 22233 qualifier NLH     | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8 --ploHighHandMinimumQualifier AAAAK --noPloFlopRestriction`         | [runCC_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runCC_output.txt)     | Your conclusion here |
  
  
## Algorithm Implementation Details

### Known Limitations
1) No accurate comparisons/ranking for PokerHands below Flushes (not needed for this simulation)

### Testing 
See [PokerHighHandSimulatorTests.java](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/test/PokerHighHandSimulatorTests.java) and [PokerHighHandSimulatorTests_results.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/PokerHighHandSimulatorTests_results.txt)