package io.symphonia;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.domain.WeatherEvent;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventsIngestLambdaTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testHandler() throws Exception {
        Context mockContext = mock(Context.class);
        when(mockContext.getAwsRequestId()).thenReturn("TEST-REQUEST-ID");

        WeatherEvent weatherEvent = new WeatherEvent(System.currentTimeMillis(), "TEST-LOCATION-ID", 32.0);
        ApiGatewayProxyRequest request = new ApiGatewayProxyRequest();
        request.setBody(objectMapper.writeValueAsString(weatherEvent));

        EventsIngestLambda lambda = new EventsIngestLambda();
        ApiGatewayProxyResponse response = lambda.handler(request, mockContext);
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getBody(), is("TEST-LOCATION-ID"));
    }

}