package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import static io.symphonia.GeoJsonFeatureMapper.toFeatureCollection;

public class LocationsQueryLambda {

    private final Logger LOG = LoggerFactory.getLogger(LocationsQueryLambda.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private final AmazonDynamoDB dynamoDbClient;
    private final String locationsTable;

    public LocationsQueryLambda() {
        this.dynamoDbClient = AmazonDynamoDBClientBuilder.defaultClient();
        this.locationsTable = System.getenv("LOCATIONS_TABLE");
    }

    public LocationsQueryLambda(AmazonDynamoDB dynamoDbClient, String locationsTable) {
        this.dynamoDbClient = dynamoDbClient;
        this.locationsTable = locationsTable;
    }

    // Lambda handler function
    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request, Context context) throws Exception {
        LOG.info("Received request ID [{}]", context.getAwsRequestId());

        Map<String, String> parameters = request.getQueryStringParameters();
        int limit = parameters != null && parameters.containsKey("limit") ? Integer.parseInt(parameters.get("limit")) : 50;

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(locationsTable)
                .withLimit(limit);

        ScanResult scanResult = dynamoDbClient.scan(scanRequest);
        FeatureCollection featureCollection = toFeatureCollection(scanResult.getItems());
        String json = mapper.writeValueAsString(featureCollection);

        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse();
        response.setStatusCode(200);
        response.setBody(json);

        // FIXME: Add "Access-Control-Allow-Origin: *" header to response
        response.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin","*"));

        return response;
    }

}
