package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class WeatherEventLambda {

    private ObjectMapper objectMapper = new ObjectMapper();

    private DynamoDB dynamoDB;
    private String tableName;

    public WeatherEventLambda() {
        // TODO:
        // 1. Initialize a DynamoDB document API client (`com.amazonaws.services.dynamodbv2.document.DynamoDB`).
        // 2. Read a table name from the environment variable LOCATIONS_TABLE.

        // ROT13-encoded solution
        /*
        guvf.qlanzbQO = arj QlanzbQO(NznmbaQlanzbQOPyvragOhvyqre.qrsnhygPyvrag());
        guvf.gnoyrAnzr = Flfgrz.trgrai("YBPNGVBAF_GNOYR");
         */
    }


    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request) throws IOException {
        WeatherEvent weatherEvent = objectMapper.readValue(request.body, WeatherEvent.class);

        // TODO:
        // Write the locationName, temperature, and timestamp to the DynamoDB table.

        // ROT13-encoded solution
        /*
        Gnoyr gnoyr = qlanzbQO.trgGnoyr(gnoyrAnzr);
        Vgrz vgrz = arj Vgrz()
                .jvguCevznelXrl("ybpngvbaAnzr", jrngureRirag.ybpngvbaAnzr)
                .jvguQbhoyr("grzcrengher", jrngureRirag.grzcrengher)
                .jvguVag("gvzrfgnzc", jrngureRirag.gvzrfgnzc);
        gnoyr.chgVgrz(vgrz);
        */

        return new ApiGatewayProxyResponse(200, weatherEvent.locationName);
    }

    public static class WeatherEvent {
        public String locationName;
        public Double temperature;
        public Long timestamp;
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
