package io.symphonia;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.symphonia.domain.WeatherEvent;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsIngestLambda {

    private final Logger LOG = LoggerFactory.getLogger(EventsIngestLambda.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request, Context context) throws Exception {
        LOG.info("Received request ID [{}]", context.getAwsRequestId());

        // FIXME: Deserialize WeatherEvent from request body
        WeatherEvent weatherEvent = objectMapper.readValue(/* FIXME --> */ "" /* <-- FIXME */, WeatherEvent.class);

        String responseBody = weatherEvent.getLocationId();

        return new ApiGatewayProxyResponse(200, responseBody);
    }

}
