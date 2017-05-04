package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.domain.WeatherEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.symphonia.DynamoDbWeatherEventMapper.toPutItemRequest;

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

    public void handler(SNSEvent snsEvent, Context context) throws IOException {
        LOG.info("Received request ID [{}]", context.getAwsRequestId());
        // NB: SNS -> Lambda is only ever one message at a time.
        WeatherEvent weatherEvent = deserialize(snsEvent.getRecords().get(0));
        writeWithBackoff(locationsTable, toPutItemRequest(weatherEvent), 1000L, 5);
    }

    private void writeWithBackoff(String dynamoDbTable, PutItemRequest putItemRequest, long millis, int remainingTries) {
        try {
            LOG.info("Writing item to [{}]", dynamoDbTable);
            dynamoDbClient.putItem(putItemRequest);
        } catch (ProvisionedThroughputExceededException e) {
            LOG.warn("Failed to write item to [{}], tries remaining [{}]", dynamoDbTable, remainingTries);
            if (remainingTries > 0) {
                writeWithBackoff(dynamoDbTable, putItemRequest, (long) (millis * 1.5), remainingTries - 1);
            } else {
                LOG.warn("Giving up on writing item to [{}]", dynamoDbTable);
            }
        }
    }

    private WeatherEvent deserialize(SNSEvent.SNSRecord snsRecord) throws IOException {
        return mapper.readValue(snsRecord.getSNS().getMessage(), WeatherEvent.class);
    }

}
