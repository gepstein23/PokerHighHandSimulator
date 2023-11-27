# PokerHighHandSimulator

Is it fair that NLH players and PLO players compete for the same high hand?

## Usage Instructions
 -d,--simulationDuration <arg>          Simulation duration in hours,
                                        minimum 1 hour. Defaults to PT10H
                                        
 -sfp,--shouldFilterPreflop               If this option is added, filters
                                        players' cards to fold pre-flop if
                                        they are not within an
                                        individually-randomly-assigned
                                        VPIP between 10% and 50%

 -h,--numHandsPerHour <arg>             Number of hands played per hour
                                        PLO/NLH table. Defaults to a
                                        random value between [20,30] for
                                        PLO and [20,25] for NLH
                                        
 -hh,--highHandMinimumQualifier <arg>   Default minimum qualifying High
                                        Hand. (Format: 'AAATT', 'AKQJT'.
                                        Must be full house or better).
                                        Defaults to [PokerHand:
                                        handType=FULL_HOUSE {2 2 2 3 3}]
                                        
 -hhd,--highHandDuration <arg>          High hand time period. Defaults to
                                        PT1H
                                        
 -nlhT,--numNlhTables <arg>             Number of NLH Tables to simulate.
                                        Defaults to 8
                                        
 -p,--numPlayersPerTable <arg>          Number of players per table to
                                        simulate. Defaults to 8
                                        
 -ploT,--numPloTables <arg>             Number of PLO Tables to simulate.
                                        Defaults to 4
                                        

## Results

### Proof of Algorithm
#### --simulationDuration 1 --numHandsPerHour 10 --highHandMinimumQualifier AAATT --highHandDuration 1 --numNlhTables 2 --numPloTables 2 --numPlayersPerTable 5 --debug
