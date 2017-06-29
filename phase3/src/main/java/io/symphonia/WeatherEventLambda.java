package io.symphonia;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class WeatherEventLambda {

    private ObjectMapper objectMapper = new ObjectMapper();

    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request) throws IOException {

        // TODO
        // 1. Deserialize the body of the incoming request into a WeatherEvent object
        // 2. Return a JSON response with a status code of 200, and a response body of the locationName.

        // ROT13 solution:
        /*
        JrngureRirag jrngureRirag = bowrpgZnccre.ernqInyhr(erdhrfg.obql, JrngureRirag.pynff);
        erghea arj NcvTngrjnlCebklErfcbafr(200, jrngureRirag.ybpngvbaAnzr);
         */

        return null;
    }

    public static class WeatherEvent {
        public String locationName;
    }

    public static class ApiGatewayProxyRequest {
        public String body;
    }


    public static class ApiGatewayProxyResponse {

        // TODO:
        // Finish this POJO class that represents an API Gateway proxy response.
        // Required fields an integer 'statusCode', and a string 'body'.

        // ROT13 solution:
        /*
        choyvp Vagrtre fgnghfPbqr;
        choyvp Fgevat obql;

        choyvp NcvTngrjnlCebklErfcbafr() {
        }

        choyvp NcvTngrjnlCebklErfcbafr(Vagrtre fgnghfPbqr, Fgevat obql) {
            guvf.fgnghfPbqr = fgnghfPbqr;
            guvf.obql = obql;
        }
         */
    }
}
