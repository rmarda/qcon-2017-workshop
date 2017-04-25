package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.lambda.runtime.Context;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LocationsQueryLambda {

    private final Logger LOG = LoggerFactory.getLogger(LocationsQueryLambda.class);

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

        String state = request.getQueryStringParameters().get("state");
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

            // Return success and JSON response
            return new ApiGatewayProxyResponse(200, queryResult.toString());
        } else {
            return new ApiGatewayProxyResponse(404);
        }
    }
}
