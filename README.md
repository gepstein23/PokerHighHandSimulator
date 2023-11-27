# PokerHighHandSimulator


Is it fair that NLH players and PLO players compete for the same high hand?

## Usage Instructions
| Command                                 | Description                                                                                           |
|-----------------------------------------|-------------------------------------------------------------------------------------------------------|
| `-d,--simulationDuration <arg>`         | Simulation duration in hours, minimum 1 hour. Defaults to PT10H                                       |
| `-h,--numHandsPerHour <arg>`            | Number of hands played per hour PLO/NLH table. Defaults to a random value between [20,30] for PLO and [20,25] for NLH |
| `-hh,--highHandMinimumQualifier <arg>`  | Default minimum qualifying High Hand. (Format: 'AAATT', 'AKQJT'. Must be full house or better). Defaults to [PokerHand: handType=FULL_HOUSE {2 2 2 3 3}] |
| `-hhd,--highHandDuration <arg>`         | High hand time period. Defaults to PT1H                                                              |
| `-nlhrl,--includeNlhRiverLikelihood`   | If this option is added, terminates NLH hands early if likely to fold IRL [NOT YET IMPLEMENTED]    |
| `-nlhT,--numNlhTables <arg>`            | Number of NLH Tables to simulate. Defaults to 8                                                       |
| `-npfr,--noPloFlopRestriction`          | If this option is added, removes restriction that PLO must flop the HH to qualify [NOT YET IMPLEMENTED] |
| `-p,--numPlayersPerTable <arg>`         | Number of players per table to simulate. Defaults to 8                                                |
| `-ploT,--numPloTables <arg>`            | Number of PLO Tables to simulate. Defaults to 4                                                       |
| `-sfp,--shouldFilterPreflop`            | If this option is added, filters players' cards to fold pre-flop if they are not within an individually-randomly-assigned VPIP between 10% and 50% [NOT YET IMPLEMENTED] |

## Results

### Conclusive Data

| Run | Description                                  | Command                                       | Output File         |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|
| [1](#Run-1)   | Description of the first run parameters      | `java YourProgram -d 5 -h 25 -nlhT 6`         | run1_output.txt     |
| [2](#Run-2)   | Description of the second run parameters     | `java YourProgram -d 8 -h 30 -ploT 4 -sfp`   | run2_output.txt     |
| [3](#Run-3)   | Description of the third run parameters      | `java YourProgram -d 12 -h 20 -nlhT 10 -npfr` | run3_output.txt     |
| ... | ...                                          | ...                                           | ...                 |

#### Run 1
```
                        %s Total NLH Players spread over %s NLH tables
                        %s Total PLO Players spread over %s PLO tables
                        NLH won HH %s/%s times (%.2f%%)
                        PLO won HH %s/%s times (%.2f%%)
                        Odds of winning the HH as a NLH player: %.4f (1 in %.0f)
                        Odds of winning the HH as a PLO player: %.4f (1 in %.0f)                 
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

### Proof the Algorithm Works

| Run | Description                                  | Command                                       | Output File         |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|
| A   | Description of the first run parameters      | `java YourProgram -d 5 -h 25 -nlhT 6`         | runA_output.txt     |
| B   | Description of the second run parameters     | `java YourProgram -d 8 -h 30 -ploT 4 -sfp`   | runB_output.txt     |
| C   | Description of the third run parameters      | `java YourProgram -d 12 -h 20 -nlhT 10 -npfr` | runC_output.txt     |
| ... | ...                                          | ...                                           | ...                 |
