# Poker High Hand Simulator - Frontend

## First Steps

**Before writing any code, scan this repository and understand its structure.** Read the main entry point, routing, component tree, and any existing API call logic. Understand what framework is being used (React, Next.js, Vue, etc.), the styling approach, and the state management pattern. Only then proceed with changes.

## What This Project Is

This is the frontend UI for a poker **high hand (HH) promotion simulator**. A high hand promotion awards a cash prize to the player with the best poker hand during a set period (typically 1 hour). The simulator compares fairness between No Limit Hold'em (NLH, 2 hole cards) and Pot Limit Omaha (PLO, 4 hole cards) games.

The backend is a Spring Boot API deployed on AWS (EC2 behind API Gateway). The frontend should:
1. Let users configure and start a simulation
2. Show live progress while the simulation runs
3. Replay the simulation hand-by-hand, showing cards, high hand board, and NLH vs PLO win stats

---

## Backend API

### Base URL

The backend is exposed through **AWS API Gateway**. After the infrastructure is deployed via Terraform, the base URL is available as a Terraform output:

```bash
cd ../infrasturcture  # note the typo in the directory name
terraform output api_gateway_url
# e.g. https://abc123def.execute-api.us-east-1.amazonaws.com
```

For local development, the backend runs directly at `http://localhost:8080`.

CORS is enabled on both the API Gateway and the Spring Boot backend (all origins allowed).

### IMPORTANT: Frontend IP Restriction

The API Gateway is protected by AWS WAF. Only the frontend's public IP address is allowed to reach it. When deploying infrastructure, the frontend IP **must** be provided:

```bash
terraform apply -var="frontend_ip=YOUR.FRONTEND.IP.HERE"
```

If you are developing locally, tell the user they need to either:
- Set `frontend_ip` to their current public IP
- Or hit `http://localhost:8080` directly (bypasses API Gateway + WAF entirely)

---

### Endpoints

#### 1. Start Simulation

```
POST /simulations/start
Content-Type: application/json
```

**Request body:**
```json
{
  "numNlhTables": 8,
  "numPloTables": 4,
  "numPlayersPerTable": 8,
  "numHandsPerHour": 25,
  "simulationDuration": 100,
  "nlhMinimumQualifyingHand": "22233",
  "ploMinimumQualifyingHand": "22233",
  "noPloFlopRestriction": false,
  "notificationPhoneNumber": "+1234567890"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `numNlhTables` | int | Number of NLH tables (2 hole cards per player) |
| `numPloTables` | int | Number of PLO tables (4 hole cards per player) |
| `numPlayersPerTable` | int | Players seated at each table |
| `numHandsPerHour` | int | Hands dealt per hour per table |
| `simulationDuration` | int | Total hours to simulate (total hands = duration * numHandsPerHour) |
| `nlhMinimumQualifyingHand` | string | 5-char minimum hand for NLH HH qualification (see below) |
| `ploMinimumQualifyingHand` | string | 5-char minimum hand for PLO HH qualification |
| `noPloFlopRestriction` | boolean | If `true`, PLO hands don't need to be "flopped" to qualify |
| `notificationPhoneNumber` | string | Optional phone number for SMS notification on completion |

**Minimum qualifying hand format:** A 5-character string of card values representing the minimum poker hand that qualifies for the high hand promotion. Characters: `A`, `K`, `Q`, `J`, `T`, `9`, `8`, `7`, `6`, `5`, `4`, `3`, `2`. Examples:
- `"AAAKK"` = full house, aces full of kings
- `"7777J"` = four sevens with a jack kicker
- `"22233"` = full house, twos full of threes (very low qualifier)

**Response:** `202 Accepted`
```json
"a1b2c3d4-e5f6-7890-abcd-ef1234567890"
```
The response body is the simulation UUID (as a JSON string). **Save this ID** -- every subsequent call needs it.

---

#### 2. Check Status

```
GET /simulations/{simulationID}/status
```

**Response:** `200 OK`
```json
"IN_PROGRESS"
```
or
```json
"DONE"
```

---

#### 3. Check Progress (hands completed count)

```
GET /simulations/{simulationID}/progress
```

**Response:** `200 OK`
```json
{
  "status": "IN_PROGRESS",
  "handsCompleted": 450
}
```

Use this to show a progress bar. Total expected hands = `simulationDuration * numHandsPerHour`.

---

#### 4. Get Hand Data (works DURING simulation)

```
GET /simulations/{simulationID}/hands/{handNum}
```

`handNum` is 0-indexed. You can request any hand that has already been simulated -- **you do NOT need to wait for the simulation to finish**. If the hand hasn't been simulated yet, you get an error.

**Response:** `200 OK`
```json
{
  "handNum": 0,
  "tableIdToSnapshot": {
    "550e8400-e29b-41d4-a716-446655440000": {
      "playerCards": [
        [
          {"value": "ACE", "suit": "SPADES", "strRepr": "As"},
          {"value": "KING", "suit": "HEARTS", "strRepr": "Kh"}
        ],
        [
          {"value": "TEN", "suit": "DIAMONDS", "strRepr": "Td"},
          {"value": "NINE", "suit": "CLUBS", "strRepr": "9c"}
        ]
      ],
      "communityCards": [
        {"value": "ACE", "suit": "HEARTS", "strRepr": "Ah"},
        {"value": "KING", "suit": "SPADES", "strRepr": "Ks"},
        {"value": "SEVEN", "suit": "DIAMONDS", "strRepr": "7d"},
        {"value": "TWO", "suit": "CLUBS", "strRepr": "2c"},
        {"value": "FIVE", "suit": "HEARTS", "strRepr": "5h"}
      ],
      "winningHand": [
        {"value": "ACE", "suit": "SPADES", "strRepr": "As"},
        {"value": "ACE", "suit": "HEARTS", "strRepr": "Ah"},
        {"value": "KING", "suit": "HEARTS", "strRepr": "Kh"},
        {"value": "KING", "suit": "SPADES", "strRepr": "Ks"},
        {"value": "SEVEN", "suit": "DIAMONDS", "strRepr": "7d"}
      ],
      "qualifiesForHighHand": true,
      "tableId": "550e8400-e29b-41d4-a716-446655440000",
      "plo": false
    }
  },
  "statsSnapshot": {
    "numHighHands": 3,
    "numPloWins": 1,
    "numHoldEmWins": 2
  },
  "highHandSnapshot": {
    "highHand": [
      {"value": "ACE", "suit": "SPADES", "strRepr": "As"},
      {"value": "ACE", "suit": "HEARTS", "strRepr": "Ah"},
      {"value": "KING", "suit": "HEARTS", "strRepr": "Kh"},
      {"value": "KING", "suit": "SPADES", "strRepr": "Ks"},
      {"value": "SEVEN", "suit": "DIAMONDS", "strRepr": "7d"}
    ],
    "tableID": "550e8400-e29b-41d4-a716-446655440000",
    "plo": false
  }
}
```

**Key details about the response:**

- `tableIdToSnapshot` is a **map keyed by UUID strings** (one entry per table). NLH tables have 2-card arrays in `playerCards`, PLO tables have 4-card arrays.
- `playerCards` is a list of hole card arrays (one per player at that table).
- `communityCards` is always 5 cards (flop + turn + river).
- `winningHand` is the best 5-card hand at that table (can be `null` if no players).
- `qualifiesForHighHand` indicates if this table's winning hand qualifies for the HH promotion.
- `plo` on a table snapshot indicates whether the table is PLO (`true`) or NLH (`false`).
- `highHandSnapshot` is the **current overall high hand across ALL tables** at this point in the simulation. It's `null`/empty if no qualifying hand exists yet. It resets every `numHandsPerHour` hands (i.e., every simulated hour).
- `statsSnapshot.numHighHands` counts completed HH hours. `numPloWins` and `numHoldEmWins` track how many hours each game type won.
- `highHandSnapshot.highHand` is `null` when no qualifying hand has appeared in the current hour.

**Card object format:**
```json
{
  "value": "ACE",       // enum name: ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
  "suit": "SPADES",     // enum name: SPADES, CLUBS, HEARTS, DIAMONDS
  "strRepr": "As"       // human-friendly: value char + suit char (A/K/Q/J/T/9-2 + s/c/h/d)
}
```

Use `strRepr` for display (e.g., "As" = Ace of Spades, "Td" = Ten of Diamonds). Use `value` and `suit` for logic like rendering card images.

---

#### Error Responses

All errors return a `500` with a JSON body containing a `message` field (Spring Boot default error format). Common errors:
- Simulation ID doesn't exist
- Hand number not yet available (simulation hasn't reached that hand)

---

## Recommended Frontend Flow

### Starting a Simulation

1. User fills out the configuration form.
2. `POST /simulations/start` -- save the returned simulation ID.
3. Immediately transition to the simulation view.

### During Simulation (live replay)

The backend now supports **live querying** -- you can fetch hand data while the simulation is still running:

1. Poll `GET /simulations/{id}/progress` every ~1-2 seconds to track how many hands are done. Use this for a progress indicator.
2. To replay hands: start fetching `GET /simulations/{id}/hands/0`, then `/hands/1`, `/hands/2`, etc.
3. You can step through hands at whatever pace the UI wants (animation speed).
4. If you request a hand that hasn't been simulated yet, you'll get an error -- just wait and retry.

### Hour Boundaries

Every `numHandsPerHour` hands, the high hand resets. For example, with `numHandsPerHour=25`:
- Hands 0-24 = Hour 1
- Hands 25-49 = Hour 2
- etc.

At each hour boundary, `statsSnapshot` is updated with who won (NLH or PLO), and `highHandSnapshot` resets to `null`. The UI should visually indicate hour transitions.

### After Simulation

Once `status` is `"DONE"`, all hands are available. The user can replay the full simulation or jump to any hand.

---

## Infrastructure Context

The backend repo lives at `../PokerHighHandSimulator` (sibling directory). Its Terraform is at `../PokerHighHandSimulator/infrasturcture/` (note the typo in the dir name -- it's checked in that way).

Key infrastructure:
- **API Gateway** (HTTP API v2): public HTTPS endpoint, proxies to EC2
- **AWS WAF**: restricts API Gateway to a single IP (the `frontend_ip` Terraform variable)
- **EC2** (t3.small): runs the Spring Boot JAR on port 8080
- **DynamoDB**: persists each hand snapshot during simulation
- **Region**: us-east-1 (default)

The API Gateway URL is the only URL the frontend should use. Never hardcode the EC2 IP -- it can change on instance replacement.

---

## Environment Variable Suggestions

Consider making the API base URL configurable via environment variable (e.g., `REACT_APP_API_URL` or `NEXT_PUBLIC_API_URL`) so it can be set to:
- `http://localhost:8080` for local dev
- The API Gateway URL for production
