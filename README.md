# PokerHighHandSimulator

## Problem Overview

NLH and PLO players pay the same rake to compete for the same High Hands. 
Therefore, a NLH and PLO player should have the same chances to win.

## Algorithm Description
TODO

## Usage Instructions
| Command                                 | Description                                                                                           |
|-----------------------------------------|-------------------------------------------------------------------------------------------------------|
| `-d,--simulationDuration <arg>`         | Simulation duration in hours, minimum 1 hour. Defaults to PT10H                                       |
| `-h,--numHandsPerHour <arg>`            | Number of hands played per hour PLO/NLH table. Defaults to a random value between [20,30] for PLO and [20,25] for NLH |
| `-hh,--highHandMinimumQualifier <arg>`  | Default minimum qualifying High Hand. (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to [PokerHand: handType=FULL_HOUSE {2 2 2 3 3}] |
| `-hhd,--highHandDuration <arg>`         | High hand time period. Defaults to PT1H                                                              |
| `-nlhrl,--includeNlhRiverLikelihood`   | If this option is added, terminates NLH hands early if likely to fold IRL *[NOT YET IMPLEMENTED]*    |
| `-nlhT,--numNlhTables <arg>`            | Number of NLH Tables to simulate. Defaults to 8                                                       |
| `-npfr,--noPloFlopRestriction`          | If this option is added, removes restriction that PLO must flop the HH to qualify  |
| `-p,--numPlayersPerTable <arg>`         | Number of players per table to simulate. Defaults to 8                                                |
| `-ploT,--numPloTables <arg>`            | Number of PLO Tables to simulate. Defaults to 4                                                       |
| `-sfp,--shouldFilterPreflop`            | If this option is added, filters players' cards to fold pre-flop if they are not within an individually-randomly-assigned VPIP between 10% and 50% *[NOT YET IMPLEMENTED]* |

## Results

### Conclusive Data

| Run | Description                                  | Command                                       | Output File         |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|
| [1](#Run-1)   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, No Pre-flop Filter, 22233 qualifier     | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8`         | [run1_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run1_output.txt)     |
| [2](#Run-2)   | 1000000 HHs, 8 players, Equal 8 NLH/PLO tables, No Pre-flop Filter, 22233 qualifier      | `--simulationDuration 1000000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8`         | [run2_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run2_output.txt)     |
| [3](#Run-3)   | 1000000 HHs, 8 players, Equal 8 NLH/PLO tables, No Pre-flop Filter, AAAKK qualifier      | `--simulationDuration 1000000 --numHandsPerHour 25 --highHandMinimumQualifier AAAKK --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8`         | [run3_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run3_output.txt)    |
| [4](#Run-4)   | 10000 HHs, 8 players, Equal 1 NLH/PLO table, No Pre-flop Filter, 22233 qualifier      | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 1 --numPlayersPerTable 8`   | [run4_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run4_output.txt)    |
| [5](#Run-5)   | 10000 HHs, 8 players, 8 NLH, 4 PLO tables, No Pre-flop Filter, 22233 qualifier       | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 4 --numPlayersPerTable 8` | [run5_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run5_output.txt)     |
| [6](#Run-6)   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, No Pre-flop Filter, NO PLO Flop Restriction, 22233 qualifier       | `--simulationDuration 10000 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 8 --numPloTables 8 --numPlayersPerTable 8 --noPloFlopRestriction` | [run6_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run6_output.txt)     |
| 7   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, YES Pre-flop Filter, 22233 qualifier       | `TODO` | TODO     |

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
                        %s Total NLH Players spread over %s NLH tables
                        %s Total PLO Players spread over %s PLO tables
                        NLH won HH %s/%s times (%.2f%%)
                        PLO won HH %s/%s times (%.2f%%)
                        Odds of winning the HH as a NLH player: %.4f (1 in %.0f)
                        Odds of winning the HH as a PLO player: %.4f (1 in %.0f)                 
```
#### Run 3
```
                        %s Total NLH Players spread over %s NLH tables
                        %s Total PLO Players spread over %s PLO tables
                        NLH won HH %s/%s times (%.2f%%)
                        PLO won HH %s/%s times (%.2f%%)
                        Odds of winning the HH as a NLH player: %.4f (1 in %.0f)
                        Odds of winning the HH as a PLO player: %.4f (1 in %.0f)                 
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
#### Run 6
```
                        %s Total NLH Players spread over %s NLH tables
                        %s Total PLO Players spread over %s PLO tables
                        NLH won HH %s/%s times (%.2f%%)
                        PLO won HH %s/%s times (%.2f%%)
                        Odds of winning the HH as a NLH player: %.4f (1 in %.0f)
                        Odds of winning the HH as a PLO player: %.4f (1 in %.0f)                 
```

### Proof the Algorithm Works

| Run | Description                                  | Command                                       | Output File         |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|
| A   | PLO Table Mechanics      | `--simulationDuration 1 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 0 --numPloTables 1 --numPlayersPerTable 3`         | [runA_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runA_output.txt)     |
| B   | NLH Table Mechanics     | `--simulationDuration 1 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 1 --numPloTables 0 --numPlayersPerTable 3`   | [runB_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runB_output.txt)     |
| C   | NLH & PLO Basic Test     | `--simulationDuration 1 --numHandsPerHour 25 --highHandMinimumQualifier 22233 --highHandDuration 1 --numNlhTables 2 --numPloTables 2 --numPlayersPerTable 4`   | [runC_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runC_output.txt)     |
| D   | Minimum Qualifier      | `--simulationDuration 2 --numHandsPerHour 30 --highHandMinimumQualifier 33339 --highHandDuration 1 --numNlhTables 2 --numPloTables 2 --numPlayersPerTable 4` | [runD_output.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runD_output.txt)     |
