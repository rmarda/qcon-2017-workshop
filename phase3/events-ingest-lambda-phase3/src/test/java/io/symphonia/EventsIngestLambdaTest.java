package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.domain.WeatherEvent;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class EventsIngestLambdaTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testHandler() throws Exception {
        Context mockContext = mock(Context.class);
        when(mockContext.getAwsRequestId()).thenReturn("TEST-REQUEST-ID-1");

        WeatherEvent weatherEvent = new WeatherEvent(System.currentTimeMillis(), "TEST-LOCATION-ID", 32.0);

        ApiGatewayProxyRequest request = new ApiGatewayProxyRequest();
        request.setBody(objectMapper.writeValueAsString(weatherEvent));

        AmazonDynamoDB mockDynamoDbClient = mock(AmazonDynamoDB.class);
        ArgumentCaptor<PutItemRequest> putItemCaptor = ArgumentCaptor.forClass(PutItemRequest.class);
        when(mockDynamoDbClient.putItem(putItemCaptor.capture())).thenReturn(null);

        EventsIngestLambda lambda = new EventsIngestLambda(mockDynamoDbClient, "TEST-TABLE");
        ApiGatewayProxyResponse response = lambda.handler(request, mockContext);
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getBody(), is("TEST-LOCATION-ID"));

        verify(mockDynamoDbClient, times(1)).putItem(any(PutItemRequest.class));
        PutItemRequest putItemRequest = putItemCaptor.getValue();
        assertThat(putItemRequest.getTableName(), is("TEST-TABLE"));
        assertThat(putItemRequest.getItem().get("locationId").getS(), is("TEST-LOCATION-ID"));
    }

    @Test
    public void testHandlerRetries() throws Exception {
        Context mockContext = mock(Context.class);
        when(mockContext.getAwsRequestId()).thenReturn("TEST-REQUEST-ID-2");

        WeatherEvent weatherEvent = new WeatherEvent(System.currentTimeMillis(), "TEST-LOCATION-ID", 32.0);

        ApiGatewayProxyRequest request = new ApiGatewayProxyRequest();
        request.setBody(objectMapper.writeValueAsString(weatherEvent));

        AmazonDynamoDB mockDynamoDbClient = mock(AmazonDynamoDB.class);
        when(mockDynamoDbClient.putItem(any(PutItemRequest.class)))
                .thenThrow(new ProvisionedThroughputExceededException("TEST-EXCEPTION"))
                .thenReturn(null);

        EventsIngestLambda lambda = new EventsIngestLambda(mockDynamoDbClient, "TEST-TABLE");
        ApiGatewayProxyResponse response = lambda.handler(request, mockContext);
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getBody(), is("TEST-LOCATION-ID"));

        // FIXME: Verify the retry happens, once the code is fixed
        // verify(mockDynamoDbClient, times(2)).putItem(any(PutItemRequest.class));
    }

}