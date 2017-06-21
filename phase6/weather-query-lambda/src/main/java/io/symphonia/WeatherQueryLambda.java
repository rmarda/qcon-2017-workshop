package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.symphonia.GeoJsonFeatureMapper.toFeatureCollection;

public class WeatherQueryLambda {

    private ObjectMapper objectMapper = new ObjectMapper();

    private AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
    private String tableName = System.getenv("LOCATIONS_TABLE");

    private static String LIMIT = "limit";
    private static String DEFAULT_LIMIT = "50";

    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request) throws IOException {

        int limit = Integer.parseInt(getLimitParam(request));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(tableName)
                .withLimit(limit);

        ScanResult scanResult = amazonDynamoDB.scan(scanRequest);
        FeatureCollection featureCollection = toFeatureCollection(scanResult.getItems());
        String json = objectMapper.writeValueAsString(featureCollection);

        return new ApiGatewayProxyResponse(200, json, Collections.singletonMap("Access-Control-Allow-Origin", "*"));
    }

    private String getLimitParam(ApiGatewayProxyRequest request) {
        return Optional.ofNullable(request.queryStringParameters).orElse(new HashMap<>()).getOrDefault(LIMIT, DEFAULT_LIMIT);
    }

    public static class WeatherEvent {
        public String locationName;
        public Double temperature;
        public Integer timestamp;
        public Double longitude;
        public Double latitude;
    }

    public static class ApiGatewayProxyRequest {
        public Map<String, String> queryStringParameters;
    }

    public static class ApiGatewayProxyResponse {
        public Integer statusCode;
        public String body;
        public Map<String, String> headers;

        public ApiGatewayProxyResponse() {
        }

        public ApiGatewayProxyResponse(Integer statusCode, String body, Map<String, String> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }
    }
}
