package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LocationsQueryLambdaTest {

    @Test
    public void testScan() throws Exception {
        // Indianapolis,IN,39.790942,-86.147685
        Map<String, AttributeValue> item1 = new HashMap<>();
        item1.put("longitude", new AttributeValue().withN("-86.147685"));
        item1.put("latitude", new AttributeValue().withN("39.790942"));
        // Des Moines,IA,41.590939,-93.620866
        Map<String, AttributeValue> item2 = new HashMap<>();
        item2.put("longitude", new AttributeValue().withN("-93.620866"));
        item2.put("latitude", new AttributeValue().withN("41.590939"));
        // No longitude or latitude
        Map<String, AttributeValue> item3 = new HashMap<>();

        ScanResult scanResult = new ScanResult().withItems(item1, item2, item3);

        AmazonDynamoDB mockDynamoDbClient = Mockito.mock(AmazonDynamoDB.class);
        when(mockDynamoDbClient.scan(any(ScanRequest.class))).thenReturn(scanResult);

        Context mockContext = Mockito.mock(Context.class);
        when(mockContext.getAwsRequestId()).thenReturn("TEST_REQUEST_ID");

        LocationsQueryLambda lambda = new LocationsQueryLambda(mockDynamoDbClient, "TEST-LOCATIONS-TABLE");
        ApiGatewayProxyResponse response = lambda.handler(new ApiGatewayProxyRequest(), mockContext);

        assertThat(response.getStatusCode(), is(200));

        // Deserialize the response body into a GeoJson FeatureCollection
        FeatureCollection featureCollection = new ObjectMapper().readValue(response.getBody(), FeatureCollection.class);
        assertThat(featureCollection.getFeatures().size(), is(2));
        assertThat(featureCollection.getFeatures().get(0).getGeometry(), is(new Point(-86.147685, 39.790942)));
        assertThat(featureCollection.getFeatures().get(1).getGeometry(), is(new Point(-93.620866, 41.590939)));
    }

    @Test
    public void testScanLimit() throws Exception {
        ApiGatewayProxyRequest request = new ApiGatewayProxyRequest();
        request.setQueryStringParameters(Collections.singletonMap("limit", "42"));

        ScanResult scanResult = new ScanResult().withItems(new HashMap<>());

        AmazonDynamoDB mockDynamoDbClient = Mockito.mock(AmazonDynamoDB.class);
        ArgumentCaptor<ScanRequest> scanRequestCaptor = ArgumentCaptor.forClass(ScanRequest.class);
        when(mockDynamoDbClient.scan(scanRequestCaptor.capture())).thenReturn(scanResult);

        Context mockContext = Mockito.mock(Context.class);
        when(mockContext.getAwsRequestId()).thenReturn("TEST_REQUEST_ID");

        LocationsQueryLambda lambda = new LocationsQueryLambda(mockDynamoDbClient, "TEST-LOCATIONS-TABLE");
        lambda.handler(request, mockContext);

        ScanRequest scanRequest = scanRequestCaptor.getValue();
        assertThat(scanRequest.getLimit(), is(42));
    }

}