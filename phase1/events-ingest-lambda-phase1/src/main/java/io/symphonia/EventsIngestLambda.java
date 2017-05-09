package io.symphonia;

import com.amazonaws.services.lambda.runtime.Context;
import io.symphonia.domain.WeatherEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsIngestLambda {

    private final Logger LOG = LoggerFactory.getLogger(EventsIngestLambda.class);

    // FIXME: Write a handler method that accepts a WeatherEvent object, and returns it
    public WeatherEvent handler(WeatherEvent weatherEvent, Context context) throws Exception {
        LOG.info("Received request ID [{}]", context.getAwsRequestId());
        return weatherEvent;
    }

}
