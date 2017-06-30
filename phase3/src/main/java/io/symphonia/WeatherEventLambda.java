package io.symphonia;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class WeatherEventLambda {

    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request) throws IOException {

        // TODO A
        // 1. Deserialize the body of the incoming request into a WeatherEvent object.
        // 2. Remove the existing null return.
        // 3. Return a JSON response with a status code of 200, and a response body of the locationName.

        return null;
    }

    public static class WeatherEvent {
        public String locationName;
        public Double temperature;
    }

    public static class ApiGatewayProxyRequest {
        public String body;
    }


    public static class ApiGatewayProxyResponse {

        // TODO B
        // 1. Finish this POJO class that represents an API Gateway proxy response.
        //    Required fields are an integer 'statusCode', and a string 'body'.

    }
}
