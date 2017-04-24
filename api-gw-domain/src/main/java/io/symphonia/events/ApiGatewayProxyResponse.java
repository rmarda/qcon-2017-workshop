
package io.symphonia.events;

import java.util.Map;

public class ApiGatewayProxyResponse {

    private Integer statusCode;
    private Map<String, String> headers;
    private String body;

    public ApiGatewayProxyResponse() {
    }

    public ApiGatewayProxyResponse(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public ApiGatewayProxyResponse(Integer statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public ApiGatewayProxyResponse(Integer statusCode, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
