package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class WeatherEventLambda {

    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
    private String tableName = System.getenv("LOCATIONS_TABLE");

    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request) throws IOException {
        WeatherEvent weatherEvent = objectMapper.readValue(request.body, WeatherEvent.class);

        Table table = dynamoDB.getTable(tableName);
        Item item = new Item()
                .withPrimaryKey("locationName", weatherEvent.locationName)
                .withDouble("temperature", weatherEvent.temperature)
                .withLong("timestamp", weatherEvent.timestamp)
                .withDouble("longitude", weatherEvent.longitude)
                .withDouble("latitude", weatherEvent.latitude);
        table.putItem(item);

        return new ApiGatewayProxyResponse(200, weatherEvent.locationName);
    }

    public static class WeatherEvent {
        public String locationName;
        public Double temperature;
        public Long timestamp;
        public Double longitude;
        public Double latitude;
    }

    public static class ApiGatewayProxyRequest {
        public String body;
    }

    public static class ApiGatewayProxyResponse {
        public Integer statusCode;
        public String body;

        public ApiGatewayProxyResponse() {
        }

        public ApiGatewayProxyResponse(Integer statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }
}
