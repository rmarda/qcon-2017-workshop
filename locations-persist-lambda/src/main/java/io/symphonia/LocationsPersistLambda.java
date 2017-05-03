package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.symphonia.domain.WeatherEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocationsPersistLambda {

    private final Logger LOG = LoggerFactory.getLogger(LocationsPersistLambda.class);

    private String locationsTable;
    private AmazonDynamoDB dynamoDbClient;

    private ObjectMapper mapper = new ObjectMapper();

    public LocationsPersistLambda() {
        this.locationsTable = System.getenv("LOCATIONS_TABLE");
        this.dynamoDbClient = AmazonDynamoDBAsyncClientBuilder.defaultClient();
    }

    public LocationsPersistLambda(AmazonDynamoDB dynamoDbClient, String locationsTable) {
        this.locationsTable = locationsTable;
        this.dynamoDbClient = dynamoDbClient;
    }

    public void handler(SNSEvent snsEvent, Context context) {
        LOG.info("Received request ID [{}]", context.getAwsRequestId());

        List<WriteRequest> writeRequests = snsEvent.getRecords().stream()
                .map(this::deserialize)
                .collect(Collectors.groupingBy(
                        WeatherEvent::getLocationId,
                        Collectors.reducing((e1, e2) -> e1.getTimestamp() > e2.getTimestamp() ? e1 : e2)))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::transform)
                .collect(Collectors.toList());

        LOG.info("Processed [{}] records into [{}] location updates", snsEvent.getRecords().size(), writeRequests.size());

        // Batch write requests to "requests" table
        Lists.partition(writeRequests, 25)
                .forEach(batch -> writeWithBackoff(locationsTable, batch, 1000L, 5));

    }

    private void writeWithBackoff(String dynamoDbTable, List<WriteRequest> batch, long millis, int remainingTries) {
        try {
            LOG.info("Writing [{}] items to [{}]", batch.size(), dynamoDbTable);

            BatchWriteItemRequest batchWriteItemRequest = new BatchWriteItemRequest();
            batchWriteItemRequest.addRequestItemsEntry(dynamoDbTable, batch);
            BatchWriteItemResult batchWriteItemResult = dynamoDbClient.batchWriteItem(batchWriteItemRequest);

            List<WriteRequest> unprocessedWriteRequests =
                    batchWriteItemResult.getUnprocessedItems().getOrDefault(dynamoDbTable, new ArrayList<>());

            if (!unprocessedWriteRequests.isEmpty()) {
                LOG.warn("Failed to write [{}] items to [{}]",
                        unprocessedWriteRequests.size(), dynamoDbTable);
                if (remainingTries > 0) {
                    writeWithBackoff(dynamoDbTable, unprocessedWriteRequests, (long) (millis * 1.5), remainingTries - 1);
                } else {
                    LOG.warn("Giving up on writing [{}] items to [{}]", unprocessedWriteRequests.size(), dynamoDbTable);
                }
            }
        } catch (Exception e) {
            LOG.error("Batch = [{}]", batch, e);
        }
    }

    private WeatherEvent deserialize(SNSEvent.SNSRecord snsRecord) {
        try {
            return mapper.readValue(snsRecord.getSNS().getMessage(), WeatherEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private WriteRequest transform(WeatherEvent weatherEvent) {
        PutRequest putRequest = new PutRequest();
        putRequest.addItemEntry("locationId", new AttributeValue(weatherEvent.getLocationId()));
        if (weatherEvent.getLocationName() != null) {
            putRequest.addItemEntry("locationName", new AttributeValue(weatherEvent.getLocationName()));
        }
        if (weatherEvent.getLatitude() != null) {
            putRequest.addItemEntry("latitude", new AttributeValue().withN(weatherEvent.getLatitude().toString()));
        }
        if (weatherEvent.getLongitude() != null) {
            putRequest.addItemEntry("longitude", new AttributeValue().withN(weatherEvent.getLongitude().toString()));
        }
        if (weatherEvent.getCity() != null) {
            putRequest.addItemEntry("city", new AttributeValue(weatherEvent.getCity()));
        }
        if (weatherEvent.getState() != null) {
            putRequest.addItemEntry("state", new AttributeValue(weatherEvent.getState()));
        }
        if (weatherEvent.getTemperature() != null) {
            putRequest.addItemEntry("temperature", new AttributeValue().withN(weatherEvent.getTemperature().toString()));
        }
        if (weatherEvent.getTimestamp() != null) {
            putRequest.addItemEntry("lastUpdated", new AttributeValue().withN(weatherEvent.getTimestamp().toString()));
        }
        return new WriteRequest(putRequest);
    }

}

