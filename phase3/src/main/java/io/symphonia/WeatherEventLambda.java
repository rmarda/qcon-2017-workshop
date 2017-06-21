package io.symphonia;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class WeatherEventLambda {

    private ObjectMapper objectMapper = new ObjectMapper();

    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request) throws IOException {
        WeatherEvent weatherEvent = objectMapper.readValue(request.body, WeatherEvent.class);
        return new ApiGatewayProxyResponse();
    }

    public static class WeatherEvent {
        public String location;
        public Double temperature;
    }

    public static class ApiGatewayProxyRequest {
        public String body;
    }

    public static class ApiGatewayProxyResponse {
        public Integer statusCode;
        public String body;

        public ApiGatewayProxyResponse() {
        }

        public ApiGatewayProxyResponse(Integer statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }
}
