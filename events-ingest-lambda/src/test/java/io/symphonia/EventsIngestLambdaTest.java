package io.symphonia;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.AmazonSNSException;
import com.amazonaws.services.sns.model.PublishResult;
import io.symphonia.events.ApiGatewayProxyRequest;
import io.symphonia.events.ApiGatewayProxyResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventsIngestLambdaTest {

    private String httpBody = "test-body";
    private String snsTopic = "test-topic";
    private String snsMessageId = "test-id";
    private String awsRequestId = "test-request-id";
    private PublishResult publishResult = new PublishResult().withMessageId(snsMessageId);

    private ApiGatewayProxyRequest httpRequest;
    private Context mockContext;

    @Before
    public void before() {
        httpRequest = new ApiGatewayProxyRequest();
        httpRequest.setBody(httpBody);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        httpRequest.setHeaders(headers);
        mockContext = mock(Context.class);
        when(mockContext.getAwsRequestId()).thenReturn(awsRequestId);
    }

    @Test
    public void testHandlerPublishesToSns() throws Exception {
        AmazonSNS mockSNSClient = mock(AmazonSNS.class);
        when(mockSNSClient.publish(eq(snsTopic), eq(httpBody))).thenReturn(publishResult);

        EventsIngestLambda lambda = new EventsIngestLambda(mockSNSClient, snsTopic);

        ApiGatewayProxyResponse httpResponse = lambda.handler(httpRequest, mockContext);
        assertThat(httpResponse.getStatusCode(), is(200));
        assertThat(httpResponse.getBody(), is(snsMessageId));
    }

    @Test(expected = AmazonSNSException.class)
    public void testHandlerThrowsException() throws Exception {
        AmazonSNS mockSNSClient = mock(AmazonSNS.class);
        when(mockSNSClient.publish(eq(snsTopic), eq(httpBody))).thenThrow(AmazonSNSException.class);

        EventsIngestLambda lambda = new EventsIngestLambda(mockSNSClient, snsTopic);

        lambda.handler(httpRequest, mockContext);
    }

    @Test
    public void testRejectsInvalidContentTypes() throws Exception {
        AmazonSNS mockSNSClient = mock(AmazonSNS.class);

        EventsIngestLambda lambda = new EventsIngestLambda(mockSNSClient, snsTopic);
        httpRequest.getHeaders().put("Content-type", "foo/bar");

        ApiGatewayProxyResponse httpResponse = lambda.handler(httpRequest, mockContext);
        assertThat(httpResponse.getStatusCode(), is(415));
        assertThat(httpResponse.getBody(), is("Request content-type [foo/bar] isn't [application/json]"));
    }

}