package io.symphonia;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishResult;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsIngestLambda {

    private static String REQUIRED_CONTENT_TYPE = "application/json";

    private final Logger LOG = LoggerFactory.getLogger(EventsIngestLambda.class);

    private final String snsTopic;
    private final AmazonSNS snsClient;

    public EventsIngestLambda() {
        this.snsClient = AmazonSNSClientBuilder.defaultClient();
        this.snsTopic = System.getenv("EVENTS_TOPIC");
    }

    public EventsIngestLambda(AmazonSNS snsClient, String snsTopic) {
        this.snsClient = snsClient;
        this.snsTopic = snsTopic;
    }

    // Lambda handler function
    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request, Context context) throws Exception {
        LOG.info("Received request ID [{}]", context.getAwsRequestId());

        // Short-circuit if we're not passed 'application/json' as the Content-type, otherwise API Gateway
        // didn't validate against the "WeatherEvent" model that we specified in the SAM/Swagger configuration.
        String contentType = request.getHeaders() != null ? request.getHeaders().get("Content-type") : null;
        if (!REQUIRED_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            String message = String.format("Request content-type [%s] isn't [%s]", contentType, REQUIRED_CONTENT_TYPE);
            LOG.warn(message);
            return new ApiGatewayProxyResponse(415, message);
        }

        // Put HTTP body on SNS Topic
        PublishResult publishResult = snsClient.publish(snsTopic, request.getBody());
        LOG.info("Published message ID [{}] on topic [{}]", publishResult.getMessageId(), snsTopic);

        // Return success and JSON response
        return new ApiGatewayProxyResponse(200, publishResult.getMessageId());
    }

}
