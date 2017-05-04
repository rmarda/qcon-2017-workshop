package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

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

        String state = request.getQueryStringParameters() != null ?
                request.getQueryStringParameters().get("state") : null;

        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse();
        response.setHeaders(new HashMap<String, String>() {{
            put("Access-Control-Allow-Origin", "*"); // Required for CORS
        }});

        if (state != null && !state.isEmpty()) {

            Condition condition = new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ)
                    .withAttributeValueList(new AttributeValue().withS(state));

            Map<String, Condition> keyConditions = new HashMap<>();
            keyConditions.put("state", condition);

            QueryRequest queryRequest = new QueryRequest()
                    .withTableName(locationsTable)
                    .withIndexName("stateIndex")
                    .withKeyConditions(keyConditions);

            QueryResult queryResult = dynamoDbClient.query(queryRequest);
            String json = toGeoJson(queryResult.getItems());

            if (queryResult.getCount() > 0) {
                // Return success and JSON response
                response.setStatusCode(200);
                response.setBody(json);
                return response;
            }
        } else {

            ScanRequest scanRequest = new ScanRequest()
                    .withTableName(locationsTable)
                    .withLimit(100);

            ScanResult scanResult = dynamoDbClient.scan(scanRequest);
            String json = toGeoJson(scanResult.getItems());

            response.setStatusCode(200);
            response.setBody(json);

            return response;
        }

        response.setStatusCode(404);
        return response;
    }

    private String toGeoJson(List<Map<String, AttributeValue>> items) throws JsonProcessingException {
        FeatureCollection featureCollection =
                items.stream()
                        .filter(this::hasLngLat)
                        .map(this::toFeature)
                        .collect(Collector.of(FeatureCollection::new, FeatureCollection::add,
                                (l, r) -> {
                                    l.addAll(r.getFeatures());
                                    return l;
                                }));

        return mapper.writeValueAsString(featureCollection);
    }

    private boolean hasLngLat(Map<String, AttributeValue> item) {
        return item.containsKey("longitude") && item.containsKey("latitude");
    }

    private Feature toFeature(Map<String, AttributeValue> item) {
        Double longitude = Double.parseDouble(item.get("longitude").getN());
        Double latitude = Double.parseDouble(item.get("latitude").getN());

        Feature feature = new Feature();
        feature.setGeometry(new Point(longitude, latitude));
        if (item.containsKey("temperature")) {
            Double temperature = Double.parseDouble(item.get("temperature").getN());
            feature.setProperty("temperature", temperature);
        }

        return feature;
    }

}
