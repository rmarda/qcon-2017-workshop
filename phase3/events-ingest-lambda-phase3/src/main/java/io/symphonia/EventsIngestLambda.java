package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.domain.WeatherEvent;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.symphonia.DynamoDbWeatherEventMapper.toPutItemRequest;

public class EventsIngestLambda {

    private final Logger LOG = LoggerFactory.getLogger(EventsIngestLambda.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private AmazonDynamoDB dynamoDbClient;
    private String locationsTable;

    public EventsIngestLambda() {
        this.dynamoDbClient = AmazonDynamoDBClientBuilder.defaultClient();
        this.locationsTable = System.getenv("LOCATIONS_TABLE");
    }

    public EventsIngestLambda(AmazonDynamoDB dynamoDbClient, String locationsTable) {
        this.dynamoDbClient = dynamoDbClient;
        this.locationsTable = locationsTable;
    }

    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request, Context context) throws Exception {
        LOG.info("Received request ID [{}]", context.getAwsRequestId());

        WeatherEvent weatherEvent = objectMapper.readValue(request.getBody(), WeatherEvent.class);
        PutItemRequest putItemRequest = toPutItemRequest(locationsTable, weatherEvent);

        // FIXME: What if this is throttled?
//        dynamoDbClient.putItem(putItemRequest);
        writeWithBackoff(putItemRequest);

        String responseBody = weatherEvent.getLocationId();

        return new ApiGatewayProxyResponse(200, responseBody);
    }

    private void writeWithBackoff(PutItemRequest putItemRequest) {
        writeWithBackoff(putItemRequest, 1000L, 5);
    }

    private void writeWithBackoff(PutItemRequest putItemRequest, long millis, int remainingTries) {
        try {
            LOG.info("Writing item to [{}]", putItemRequest.getTableName());
            dynamoDbClient.putItem(putItemRequest);
        } catch (ProvisionedThroughputExceededException e) {
            LOG.warn("Failed to write item to [{}], tries remaining [{}]", putItemRequest.getTableName(), remainingTries);
            if (remainingTries > 0) {
                writeWithBackoff(putItemRequest, (long) (millis * 1.5), remainingTries - 1);
            } else {
                LOG.warn("Giving up on writing item to [{}]", putItemRequest.getTableName());
            }
        }
    }

}
