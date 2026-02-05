package com.genevieve.pokersim.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genevieve.pokersim.api.snapshots.HandSnapShot;
import com.genevieve.pokersim.api.snapshots.StatsSnapshot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Write-through DynamoDB repository.
 * Extends InMemorySimulationRepository so reads use the fast in-memory cache
 * while writes are also persisted to DynamoDB for durability.
 *
 * DynamoDB key schema (matches existing table):
 *   simulation_id (S, partition) + hand_number (N, sort)
 *
 * Special hand_number values:
 *   -1 = simulation status record
 *   -2 = final stats record
 *    0+ = hand snapshots
 */
public class DynamoDBSimulationRepository extends InMemorySimulationRepository {

    private final DynamoDbClient client;
    private final String tableName;
    private final ObjectMapper objectMapper;

    private static final long TTL_HOURS = 72;

    public DynamoDBSimulationRepository(String tableName, String awsRegion) {
        this.tableName = tableName;
        this.objectMapper = new ObjectMapper();
        this.client = DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }

    private static String ttlEpochSeconds() {
        return String.valueOf(Instant.now().plus(TTL_HOURS, ChronoUnit.HOURS).getEpochSecond());
    }

    @Override
    public void saveHandSnapshot(UUID simulationId, int handNum, HandSnapShot snapshot) {
        // Write to in-memory cache first
        super.saveHandSnapshot(simulationId, handNum, snapshot);

        // Persist to DynamoDB
        try {
            String snapshotJson = objectMapper.writeValueAsString(snapshot.transform());

            Map<String, AttributeValue> item = new HashMap<>();
            item.put("simulation_id", AttributeValue.fromS(simulationId.toString()));
            item.put("hand_number", AttributeValue.fromN(String.valueOf(handNum)));
            item.put("record_type", AttributeValue.fromS("HAND"));
            item.put("snapshot_json", AttributeValue.fromS(snapshotJson));
            item.put("ttl", AttributeValue.fromN(ttlEpochSeconds()));

            client.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build());
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize hand snapshot for DynamoDB: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to write hand snapshot to DynamoDB: " + e.getMessage());
        }
    }

    @Override
    public void saveSimulationStatus(UUID simulationId, String status) {
        // Write to in-memory cache
        super.saveSimulationStatus(simulationId, status);

        // Persist to DynamoDB
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("simulation_id", AttributeValue.fromS(simulationId.toString()));
            item.put("hand_number", AttributeValue.fromN("-1"));
            item.put("record_type", AttributeValue.fromS("STATUS"));
            item.put("status", AttributeValue.fromS(status));
            item.put("ttl", AttributeValue.fromN(ttlEpochSeconds()));

            client.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to write simulation status to DynamoDB: " + e.getMessage());
        }
    }

    @Override
    public void saveFinalStats(UUID simulationId, StatsSnapshot stats) {
        // Write to in-memory cache
        super.saveFinalStats(simulationId, stats);

        // Persist to DynamoDB
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("simulation_id", AttributeValue.fromS(simulationId.toString()));
            item.put("hand_number", AttributeValue.fromN("-2"));
            item.put("record_type", AttributeValue.fromS("STATS"));
            item.put("num_high_hands", AttributeValue.fromN(String.valueOf(stats.getNumHighHands())));
            item.put("num_plo_wins", AttributeValue.fromN(String.valueOf(stats.getNumPloWins())));
            item.put("num_nlh_wins", AttributeValue.fromN(String.valueOf(stats.getNumHoldEmWins())));
            item.put("ttl", AttributeValue.fromN(ttlEpochSeconds()));

            client.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to write final stats to DynamoDB: " + e.getMessage());
        }
    }
}
