package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.domain.WeatherEvent;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationsPersistLambdaTest {

    ObjectMapper mapper = new ObjectMapper();
    private String locationsTable = "test-locations-table";

    @Test
    public void testHandlerWritesToDynamoDb() throws JsonProcessingException {
        AmazonDynamoDB mockDynamoDbClient = mock(AmazonDynamoDB.class);
        LocationsPersistLambda lambda = new LocationsPersistLambda(mockDynamoDbClient, locationsTable);
        ArgumentCaptor<PutItemRequest> putItemCaptor = ArgumentCaptor.forClass(PutItemRequest.class);

        Long timestamp = System.currentTimeMillis();
        SNSEvent snsEvent = buildSNSEvent(buildWeatherEventJson(timestamp, "test-location-id", 32.0));

        Context mockContext = mock(Context.class);
        when(mockContext.getAwsRequestId()).thenReturn("test-request-id");

        lambda.handler(snsEvent, mockContext);
        verify(mockDynamoDbClient).putItem(putItemCaptor.capture());
        PutItemRequest putItemRequest = putItemCaptor.getValue();
        assertThat(putItemRequest.getItem().get("lastUpdated").getN(), is(timestamp.toString()));
    }

    private String buildWeatherEventJson(Long timestamp, String locationId, Double temperature) {
        WeatherEvent weatherEvent = new WeatherEvent(timestamp, locationId, temperature);
        try {
            return mapper.writeValueAsString(weatherEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private SNSEvent buildSNSEvent(String message) {
        SNSEvent snsEvent = new SNSEvent();
        SNSEvent.SNSRecord snsRecord = new SNSEvent.SNSRecord();
        SNSEvent.SNS sns = new SNSEvent.SNS();
        sns.setMessage(message);
        snsRecord.setSns(sns);
        List<SNSEvent.SNSRecord> snsRecords = Collections.singletonList(snsRecord);
        snsEvent.setRecords(snsRecords);
        return snsEvent;
    }

}