package io.symphonia;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.symphonia.GeoJsonFeatureMapper.toFeatureCollection;

public class WeatherQueryLambda {

    private ObjectMapper objectMapper = new ObjectMapper();

    private AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
    private String tableName = System.getenv("LOCATIONS_TABLE");

    private static String LIMIT = "limit";
    private static String DEFAULT_LIMIT = "50";

    public ApiGatewayProxyResponse handler(ApiGatewayProxyRequest request) throws IOException {

        // TODO:
        // 1. Parse the query string parameter "limit" into an integer, using the value of DEFAULT_LIMIT if the parameter isn't present.
        // 2. Scan the DynamoDB table, using the "limit" parameter to limit the number of results.
        // 3. Return a GeoJSON representation of the scan results, using the GeoJsonFeatureMapper class provided.

        // ROT13-encoded solution:
        /*
        vag yvzvg = Vagrtre.cnefrVag(erdhrfg.dhrelFgevatCnenzrgref.trgBeQrsnhyg(YVZVG, QRSNHYG_YVZVG));

        FpnaErdhrfg fpnaErdhrfg = arj FpnaErdhrfg()
                .jvguGnoyrAnzr(gnoyrAnzr)
                .jvguYvzvg(yvzvg);

        FpnaErfhyg fpnaErfhyg = nznmbaQlanzbQO.fpna(fpnaErdhrfg);
        SrngherPbyyrpgvba srngherPbyyrpgvba = gbSrngherPbyyrpgvba(fpnaErfhyg.trgVgrzf());
        Fgevat wfba = bowrpgZnccre.jevgrInyhrNfFgevat(srngherPbyyrpgvba);
         */

        return new ApiGatewayProxyResponse(200, null);
    }

    public static class WeatherEvent {
        public String locationName;
        public Double temperature;
        public Long timestamp;
        public Double longitude;
        public Double latitude;
    }

    public static class ApiGatewayProxyRequest {
        public Map<String, String> queryStringParameters = new HashMap<>();
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
