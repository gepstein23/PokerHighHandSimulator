# Poker High Hand Simulator - Backend

## Project Overview

This is a Spring Boot 3.3.0 (Java 17) backend that simulates poker **high hand (HH) promotions** to measure fairness between No Limit Hold'em (NLH) and Pot Limit Omaha (PLO) games. A high hand promotion awards a cash prize to the player with the best poker hand during a set period (typically 1 hour). Because PLO players receive 4 hole cards vs NLH's 2, PLO inherently produces stronger hands more often, creating a fairness problem that this simulator quantifies.

The frontend lives in a separate repository and connects to this API.

## Build & Run

```bash
cd poker-high-hand-simulator-backend
./mvnw clean package          # Build
java -jar target/*.jar         # Run on port 8080
```

**Dependencies:** Spring Boot Web, Spring Boot Test, AWS SDK SNS (v2.28.24 for SMS notifications).

No `application.properties` file exists -- everything uses Spring Boot defaults (port 8080).

## Architecture

```
poker-high-hand-simulator-backend/src/main/java/com/genevieve/pokersim/
├── AppApplication.java              # Spring Boot entry point
├── SimulationController.java        # REST controller (3 endpoints)
├── WebConfig.java                   # Global CORS config (allows all origins)
├── api/
│   ├── SimulationRequest.java       # Abstract base request (holds UUID)
│   ├── SimulationStartRequest.java  # POST body DTO for /simulations/start
│   ├── SimulationStopRequest.java   # Unused stop request DTO
│   ├── Main.java                    # Old CLI entry point (unused by API)
│   ├── WebConfig.java               # Duplicate CORS config (entirely commented out)
│   └── snapshots/
│       ├── HandSnapShot.java        # Per-hand snapshot with nested API model + TableSnapshot
│       ├── HighHandSnapshot.java    # Current high hand state (internal)
│       ├── HighHandSnapshotApiModel.java  # High hand state (API response)
│       ├── StatsSnapshot.java       # Running NLH/PLO win counts
│       └── SeatSnapshot.java        # Unused seat-level snapshot
├── main/
│   ├── HighHandSimulator.java       # Core orchestrator: runs simulation, generates snapshots
│   ├── HighHand.java                # Config object: NLH/PLO minimum qualifying hands + period
│   ├── MachineLearningHandler.java  # Entirely commented out -- equilibrium finder prototype
│   ├── Main.java                    # Old CLI runner with Apache Commons CLI (unused by API)
│   └── Utils.java                   # log() and debug() helpers (debug is commented out)
├── players/
│   ├── PokerPlayer.java             # Abstract base: hole cards, VPIP, combo index tables
│   ├── NLHPokerPlayer.java          # 2-card player: evaluates all 5-card combos from 2+5
│   └── PLOPokerPlayer.java          # 4-card player: exactly 2 hole + 3 community (60 combos)
├── playingcards/
│   ├── Card.java                    # Value + Suit, Comparable
│   ├── CardSuit.java                # Enum: SPADES/CLUBS/HEARTS/DIAMONDS
│   ├── CardValue.java               # Enum: TWO(1) through ACE(13) with friendlyName
│   ├── Deck.java                    # 52-card deck, shuffled on construction
│   ├── PokerHand.java               # 5-card hand: type classification, comparison, parsing
│   └── RankedHoleCards.java         # Starting hand rankings (top 39 hands, valueOf() is TODO)
├── tables/
│   ├── PokerTable.java              # Abstract: dealing, community cards, hand simulation loop
│   ├── NLHTable.java                # NLH qualification: must use both hole cards
│   ├── PLOTable.java                # PLO qualification: flop/turn restriction logic
│   └── PokerTableHistory.java       # Maps hand number to PlayedHandData
├── animation/
│   ├── PlayedHandData.java          # Record of one dealt hand (players, community, winner)
│   ├── HighHandPanel.java           # Swing UI panel (legacy animation)
│   ├── PokerRoomAnimation.java      # Swing animation runner (legacy)
│   └── PokerTablePanel.java         # Swing table display (legacy)
└── simulation_datas/
    ├── SimulationData.java          # Final results: hour data + table data + win counts
    ├── HourSimulationData.java      # One hour's winner: hand, isPlo
    ├── TableSimulationData.java     # One table's hourly high hands map
    └── SimulationStatisticsData.java # Empty placeholder class
```

## API Endpoints

### `POST /simulations/start`
Starts a simulation asynchronously. Returns `202 Accepted` with a `UUID`.

**Request body:**
```json
{
  "numNlhTables": 8,
  "numPloTables": 4,
  "numPlayersPerTable": 8,
  "numHandsPerHour": 25,
  "simulationDuration": 10000,
  "nlhMinimumQualifyingHand": "22233",
  "ploMinimumQualifyingHand": "22233",
  "noPloFlopRestriction": false,
  "notificationPhoneNumber": "+1234567890"
}
```

The minimum qualifying hand is a 5-char string of card values (e.g., `"AAAKK"`, `"7777J"`). Characters: A, K, Q, J, T, 9-2.

### `GET /simulations/{simulationID}/status`
Returns `"IN_PROGRESS"` or `"DONE"`.

### `GET /simulations/{simulationID}/hands/{handNum}`
Returns hand-by-hand replay data. **Must call with `handNum=0` first** to trigger snapshot generation, then can fetch any hand number. Returns `HandSnapshotApiModel` JSON with per-table card data, winning hand, high hand board state, and running NLH/PLO win statistics.

### `GET /greeting` and `GET /`
Unused test/placeholder endpoints.

## Core Simulation Flow

1. **`SimulationController.startSimulation()`** parses the request, creates a `HighHandSimulator`, calls `initializeSimulation()` which starts a background `Thread`.
2. **`HighHandSimulator.initSimulation()`** iterates over all tables, calling `table.runSimulation()`.
3. **`PokerTable.runSimulation()`** loops hour by hour. Each hour calls `playHourOfHands()` which plays N hands sequentially.
4. **`playOneHand()`**: Creates a new shuffled `Deck`, deals hole cards to players, deals community cards (with burn cards), finds the winning hand across all players, checks if it qualifies for high hand, and tracks the best qualifying hand for the hour.
5. **`determineSimulationWinners()`** compares each hour's best hand across all tables to determine NLH vs PLO winner per hour, then computes final percentages.

## Poker Hand Evaluation

**`PokerHand.java`** classifies 5-card hands into 9 types (HIGH_CARD through STRAIGHT_FLUSH) and compares same-type hands by relevant card ranks. Ace-low straights (A-2-3-4-5) are handled as a special case.

**NLH hand selection** (`NLHPokerPlayer.getBestHand`): Evaluates all combinations of {2 hole + 3 community, 1 hole + 4 community, 0 hole + 5 community} and picks the best.

**PLO hand selection** (`PLOPokerPlayer.getBestHand`): Per PLO rules, evaluates **exactly 2 hole + exactly 3 community** across all C(4,2) * C(5,3) = 60 combinations. Tracks whether the hand is "flopped" (all 3 community cards from the flop) or "turned" (all from flop + turn).

## High Hand Qualification Rules

- **NLH**: Hand >= NLH minimum qualifier AND uses exactly 3 community cards (i.e., uses both hole cards).
- **PLO**: Hand >= PLO minimum qualifier AND (no flop restriction OR hand is flopped) AND (no turn restriction OR hand is turned).

## Key Design Decisions & Known State

- **All in-memory**: No database. Simulations and their data live in a `HashMap<UUID, HighHandSimulator>` on the controller. Lost on restart, never cleaned up.
- **Sequential table simulation**: Tables run one after another in a single background thread (not parallelized).
- **Pre-flop filtering is disabled**: `shouldFilterPreflop` defaults to `false` and is not exposed in the API. The `getHoleCardRanking()` method returns hardcoded `0`, and `RankedHoleCards.valueOf()` returns `null`. The simulation assumes all players see the river.
- **VPIP randomization uses `System.currentTimeMillis()` seed**: Since `getRandomVpip()` creates a `new Random(System.currentTimeMillis())` on each player construction during rapid iteration, many players likely get the same seed.
- **Card display**: `Card.toString()` displays TEN as `"Ts"` (fixed from previous `"0s"` bug).
- **Commented-out code**: `MachineLearningHandler` (130 lines), `SimulationIterator` in `HighHandSimulator`, `debug()` output in `Utils`, and `api/WebConfig.java` are all commented out.
- **`SeatSnapshot.java`** and **`SimulationStatisticsData.java`** are unused.
- **`HighHand.highHandPeriod`** is stored but never used in qualification logic (hardcoded to 1 hour).

## How to Make Changes

### General Approach
- The Maven project root is `poker-high-hand-simulator-backend/` (not the git repo root).
- All source lives under `src/main/java/com/genevieve/pokersim/`.
- Tests at `src/test/` include a Spring context load test and `PokerHandTest.java` with comprehensive hand evaluation/comparison tests. There are standalone test files in the repo root `test/` directory that are not wired into Maven.
- No `application.properties` exists. To add config, create `src/main/resources/application.properties`.
- CORS is globally permissive via `WebConfig.java`. The `@CrossOrigin` annotation on the controller is redundant.

### Before Any PR
1. Verify the project still compiles: `./mvnw clean compile`
2. Run tests: `./mvnw test`
3. If changing the API contract, coordinate with the frontend repo.

### Areas Likely to Be Touched for Improvements

**Bugs / Correctness (fixed):**
- ~~`Card.toString()` displays TEN incorrectly as `"0"` instead of `"T"`.~~ Fixed.
- ~~`PokerHand.compare()` for STRAIGHT and STRAIGHT_FLUSH had reversed comparison direction and didn't handle ace-low wheel correctly.~~ Fixed: uses `getStraightHighRank()` helper with `Integer.compare()`.
- ~~`compareIndividualCards()` (flush comparison) used `CardValue.compareTo()` (ordinal-based) instead of `Card.compareTo()` (rank-based), reversing high/low.~~ Fixed.
- ~~`compareRemainingCards()` accepted unused `numCardsToCompare` parameter.~~ Fixed: parameter removed.
- ~~FULL_HOUSE and QUADS comparison used `CardValue.compareTo()` (ordinal-based).~~ Fixed: uses `Integer.compare()` on `.getRank()`.
- ~~`getValueToNumOccurrencesMap()` was duplicated as `getValueToOccurrencesMap()`.~~ Fixed: consolidated to single static method.

**Bugs / Correctness (remaining):**
- `NLHPokerPlayer.getBestHand()` includes "community only" as a candidate, which can't win a high hand since it wouldn't use both hole cards. Harmless but wasteful.

**Cleanup:**
- Remove commented-out code (`MachineLearningHandler`, `SimulationIterator`, `api/WebConfig`, `debug()` body).
- Remove unused classes (`SeatSnapshot`, `SimulationStatisticsData`, `SimulationStopRequest`).
- Remove unused endpoints (`/greeting`, `/`).
- Remove mutable setters on enum fields in `CardValue` and `CardSuit` (`.setRank()`, `.setFriendlyName()`) -- enum fields should be final.

**Robustness:**
- `simulationMap` on the controller is never cleaned up -- will leak memory.
- No input validation on the start request (negative tables, 0 players, etc.).
- `GET /` returns `null` which causes a 200 with empty body rather than a proper response.
- Exceptions from invalid simulation IDs throw `IllegalArgumentException` with no `@ExceptionHandler`, resulting in 500 errors instead of 400/404.

**Performance:**
- Tables are simulated sequentially. For large simulations (10k+ hours, 12+ tables), parallelizing table simulation would help significantly.
- ~~`getValueToNumOccurrencesMap()` was defined twice with slightly different names.~~ Fixed: consolidated.
- Hand evaluation allocates many short-lived `ArrayList` and `Card[]` objects per hand.

**Testing:**
- `PokerHandTest.java` covers card display, hand type classification, and comparison logic (54 tests). No unit tests for dealing or simulation logic yet.
