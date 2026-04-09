# PokerHighHandSimulator

![image info](./photos/royalflush.png)

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

### Statistics for Typical HH Promotions

For a typical HH promotion, where PLO players must *flop* the high hand but NLH players must not, and the simulation assumes all players make it to the river, it is approximately 2x easier to get a HH as a NLH player than as a PLO player.

| Run | Description                                  | Options                                       | Results         |
|-----|----------------------------------------------|-----------------------------------------------|---------------------|
| [1](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run1_output.txt)   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, 22233 qualifier     | `--highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 8`          |   NLH 63.42%, PLO 36.58%  |
| [2](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run2_output.txt)   | 500000 HHs, 8 players, Equal 8 NLH/PLO tables, 22233 qualifier      | `--simulationDuration 500000 --highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 8`       |    ABC    |
| [3](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run3_output.txt)   | 500000 HHs, 8 players, Equal 1 NLH/PLO tables, AAAKK qualifier      | `--simulationDuration 500000 --highHandMinimumQualifier AAAKK --numNlhTables 8 --numPloTables 8`       |    ABC    |
| [4](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run4_output.txt)   | 500000 HHs, 8 players, Equal 1 NLH/PLO table, 22233 qualifier      | `--simulationDuration 500000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1`      |  ABC | 
| [5](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run5_output.txt)   | 10000 HHs, 8 players, 8 NLH, 4 PLO tables, 22233 qualifier       | `--highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 4`       | NLH 77.02%, PLO 22.98% |
| [6](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/run6_output.txt)   | 10000 HHs, 8 players, Equal 8 NLH/PLO tables, *NO PLO Flop Restriction*, 22233 qualifier       | `--highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 8 --noPloFlopRestriction`       | NLH 15.39%, PLO 84.61% |

## Finding a Solution

### Hypotheses
#### 1) Remove PLO Flop Restriction & Find Equalizing Respective Minimum HH Qualifiers
Can we make the HH promotion equitable for both games if we remove the flop restriction for PLO (to isolate the exact odds of winning), and then come up with different minimum qualifying hands for both games in order to make the chances of winning equal?

| Run                                                                                                           | Description                                                                                                 | Options                                                                                                                               | Results   | Comments                                                 |
|---------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|-----------|----------------------------------------------------------|
| [7](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_output.txt)                | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, AAAAK qualifier PLO, 22233 qualifier NLH | `--highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier AAAAK --noPloFlopRestriction` |   NLH 80.91%, PLO 19.09%     | Unfair for PLO                                           | 
| [8](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA2_output.txt)               | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 2222A qualifier PLO, 22233 qualifier NLH | `--highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 2222A --noPloFlopRestriction` |   NLH 36.50%, PLO 63.50%     | Unfair for NLH                                           | 
| [9](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA3_output.txt)               | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 5555A qualifier PLO, 22233 qualifier NLH | `--highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 5555A --noPloFlopRestriction` |   NLH 43.69%, PLO 56.31%     | Almost fair                                              |
| [10](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA4_output.txt)              | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777Q qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 7777Q --noPloFlopRestriction` |   NLH 49.30%, PLO 50.69%     | Not quite fair                                           | 
| [11](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA5_output.txt)              | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777K qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 7777K --noPloFlopRestriction` |   NLH 48.59%, PLO 51.40%     | Not quite fair                                           | 
| [12](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA6_output.txt)              | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   NLH 47.73%, PLO 52.26%     | Not quite fair!                                          | 
| [13](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA7_output.txt)              | 100000 HHs, 8 players, Equal 1 NLH/PLO tables, NO PLO flop restriction, 7777A qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 1 --numPloTables 1 --ploHighHandMinimumQualifier 7777A --noPloFlopRestriction` |   NLH 49.30%, PLO 50.69%%    | Not quite fair                                           |
| [AA_final1](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final1_output.txt) | 100000 HHs, 8 players, Equal 2 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 2 --numPloTables 2 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   NLH 32.44%, PLO 67.47%     | Shows how minimum qualifier means nothing as tables increase |
| [AA_final2](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final2_output.txt) | 100000 HHs, 8 players, Equal 4 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 4 --numPloTables 4 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   NLH 20.21%, PLO 79.79%     | Shows how minimum qualifier means nothing as tables increase |
| [AA_final3](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final3_output.txt) | 100000 HHs, 8 players, Equal 8 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 8 --numPloTables 8 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   XYZ     | Shows how minimum qualifier means nothing as tables increase |
| [AA_final4](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runAA_final4_output.txt) | 100000 HHs, 8 players, Equal 16 NLH/PLO tables, NO PLO flop restriction, 7777J qualifier PLO, 22233 qualifier NLH | `--simulationDuration 100000 --highHandMinimumQualifier 22233 --numNlhTables 16 --numPloTables 16 --ploHighHandMinimumQualifier 7777J --noPloFlopRestriction` |   XYZ     | Shows how minimum qualifier means nothing as tables increase |

RESULT: This won't work because unfairness will increase proportionally to the number of tables active (i.e., the more tables => the more likely a bigger hand well beyond the qualifier will win)

#### 2) PLO Turn Restriction
Can we make the HH promotion equitable for both games if we update the flop restriction to be a turn restriction?

| Run       | Description                                                                                                 | Options                                                                                                                               | Results   | Comments          |
|-----------|-------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|-----------|----------------------|
| [B](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/runB_output.txt)         | 10000 HHs, 8 players, Equal 1 NLH/PLO tables, PLO turn restriction, 22233 qualifier NLH/PLO | `--numNlhTables 1 --numPloTables 1 --ploTurnRestriction` |   NLH 37.97%, PLO 61.90%     | PLO has an advantage even with the turn restriction. But maybe this turn restriction combined with PLO-specific minimum qualifier can produce a fairness equilibrium for all # players (certainly not but let's research the numbers anyways)       | 

  

#### 3) Continuously updated minimum qualifiers for PLO integrated with [PokerAtlas TableCaptain™](https://www.pokeratlas.com/info/table-captain)  *(in-progress)*
What if a system could be put in place where the HH minimum qualifier for PLO is automatically/continuously updated using data from TableCaptain about how many players of each type are currently playing.
To accomplish this, a top-layer function must be implemented which tests different minimum PLO qualifiers for the given # of players until the fair qualifier is found.
*Maybe*: We will keep a save file to be re-used on future runs of known player # configurations' fair qualifiers -- in order to optimize the performance of this feature (output duplicates automatically & pick proximity starting qualifiers to test). After some time, this function would have `O(1)` execution time for most all realistic player # combinations.
Issues: In practice, the HH minimum qualifier found to be fair would be updated at the beginning of each new HH period.
Players could exploit this if the # of players input to the program is taken at a static point in the period => it must be an average of the player #s for the period.

## Infrastructure (Terraform)

The `terraform/` directory deploys the backend to AWS (single account, single region).

### What Gets Created

| Resource | Purpose |
|----------|---------|
| **VPC** + public subnet, IGW, route table | Isolated network with internet access |
| **EC2** (t3.small) + Elastic IP | Builds and runs the Spring Boot backend on port 8080 |
| **DynamoDB** table (`poker-sim-dev-hands`) | Stores hand outputs, keyed by `simulation_id` + `hand_number` |
| **IAM** role + instance profile | Grants the EC2 instance write access to DynamoDB |
| **Security group** | Allows inbound 8080 (API) and 22 (SSH) |

```
terraform/
  bootstrap/main.tf         # One-time: S3 state bucket + DynamoDB lock table
  versions.tf                # Terraform >= 1.5, AWS provider ~> 5.0
  backend.tf                 # S3 backend (commented out until bootstrapped)
  providers.tf               # AWS provider with default tags
  variables.tf               # All input variables
  outputs.tf                 # EC2 IP, API URL, DynamoDB table name
  locals.tf                  # Shared naming prefix
  data.tf                    # AMI lookup, availability zones
  vpc.tf                     # VPC, subnet, internet gateway, routing
  security_groups.tf         # Inbound/outbound rules
  iam.tf                     # Role, DynamoDB policy, instance profile
  dynamodb.tf                # Hands table
  ec2.tf                     # Backend instance + elastic IP
  templates/user_data.sh     # EC2 startup: install Java 17, clone, build, run
  terraform.tfvars.example   # Example variable values
```

### Prerequisites

- [Terraform CLI](https://developer.hashicorp.com/terraform/install) >= 1.5.0
- [AWS CLI](https://aws.amazon.com/cli/) configured with credentials (`aws configure`)
- An AWS account with permissions to create VPC, EC2, DynamoDB, IAM, and S3 resources

### Step 1: Bootstrap Remote State

Creates an S3 bucket (versioned, encrypted, private) and a DynamoDB table for state locking. Run once.

```bash
cd terraform/bootstrap
terraform init
terraform apply
```

Note the output values `state_bucket_name` and `lock_table_name`.

### Step 2: Enable Remote Backend

Edit `terraform/backend.tf` -- uncomment the `backend "s3"` block and verify the bucket name and region match your bootstrap outputs:

```hcl
terraform {
  backend "s3" {
    bucket         = "poker-sim-terraform-state"
    key            = "terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "poker-sim-terraform-locks"
    encrypt        = true
  }
}
```

### Step 3: Deploy

```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars   # Edit with your values
terraform init                                  # Initialize backend + providers
terraform plan                                  # Preview changes
terraform apply                                 # Deploy
```

After `apply` completes, Terraform outputs the API URL:
```
api_base_url = "http://<elastic-ip>:8080"
```

Point your frontend at this URL. The EC2 instance automatically clones the repo, builds the jar, and starts the backend as a systemd service.

### SSH Access (Optional)

To enable SSH, create an EC2 key pair in the AWS console and set `key_pair_name` in your `terraform.tfvars`. Restrict `ssh_cidr` to your IP (e.g. `"1.2.3.4/32"`).

```bash
ssh -i ~/.ssh/your-key.pem ec2-user@<elastic-ip>
sudo journalctl -u poker-sim -f    # Tail application logs
```

### Day-to-Day Workflow

```bash
cd terraform
terraform plan      # See what would change
terraform apply     # Apply changes
terraform destroy   # Tear down all resources (use with caution)
```

---

## Program Implementation Details

### Program Usage Instructions
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

### Testing 
See [PokerHighHandSimulatorTests.java](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/test/PokerHighHandSimulatorTests.java) and [PokerHighHandSimulatorTests_results.txt](https://github.com/gepstein23/PokerHighHandSimulator/blob/master/results/PokerHighHandSimulatorTests_results.txt)

### Simulation Animation *(in-progress)*

Per request, I'm in the process of generating an animation to go alongside any simulation.
This will be beneficial for manual verification of gameplay mechanics. A preliminary 
example is shown here: 

[![PokerHighHandSimulator Animation Preview [v3 - no UI]](https://img.youtube.com/vi/3wAh1azKxZk/0.jpg)](https://www.youtube.com/watch?v=3wAh1azKxZk)
