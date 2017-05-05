package io.symphonia;

import com.amazonaws.services.lambda.runtime.Context;
import io.symphonia.domain.WeatherEvent;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventsIngestLambdaTest {

    @Test
    public void testHandler() throws Exception {
        Context mockContext = mock(Context.class);
        when(mockContext.getAwsRequestId()).thenReturn("TEST-REQUEST-ID");

        WeatherEvent weatherEvent = new WeatherEvent(System.currentTimeMillis(), "TEST-LOCATION-ID", 32.0);

        EventsIngestLambda lambda = new EventsIngestLambda();
        assertThat(lambda.handler(weatherEvent, mockContext), is(weatherEvent));
    }

}