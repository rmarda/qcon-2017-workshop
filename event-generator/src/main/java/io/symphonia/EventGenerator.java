package io.symphonia;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import io.symphonia.domain.WeatherEvent;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.stream.Collectors;

public class EventGenerator {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        AmazonCloudFormation cloudFormation = AmazonCloudFormationClientBuilder.defaultClient();
        DescribeStacksRequest describeStacksRequest =
                new DescribeStacksRequest().withStackName("oscon-2017-tutorial");
        DescribeStacksResult describeStacksResult = cloudFormation.describeStacks(describeStacksRequest);

        String apiUrl = describeStacksResult.getStacks().get(0).getOutputs().stream()
                .filter(output -> output.getOutputKey().equals("ApiUrl"))
                .collect(Collectors.toList()).get(0).getOutputValue() + "/events";

        System.out.println("API url: " + apiUrl);

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        Random random = new Random();

        CloseableHttpClient httpclient = HttpClients.createDefault();

        ObjectMapper jsonMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        CsvMapper csvMapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);
        InputStream locationsFile = EventGenerator.class.getResourceAsStream("/locations.csv");
        MappingIterator<String[]> it = csvMapper.readerFor(String[].class).readValues(locationsFile);

        while (it.hasNext()) {
            String[] row = it.next();
            String locationName = String.format("%s, %s", row[0], row[1]);
            String md5 = DatatypeConverter.printHexBinary(messageDigest.digest(locationName.getBytes()));

            WeatherEvent weatherEvent = new WeatherEvent(System.currentTimeMillis(), md5, random.nextDouble() * 100);
            weatherEvent.setCity(row[0]);
            weatherEvent.setState(row[1]);
            String json = jsonMapper.writeValueAsString(weatherEvent);

            // Build HTTP Post
            HttpPost post = new HttpPost(apiUrl);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            post.setEntity(new StringEntity(json, Charset.forName("UTF-8")));

            System.out.println("request: " + json);

            // Send it
            try (CloseableHttpResponse response = httpclient.execute(post)) {
                System.out.println(String.format("response: %s / %s",
                        response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity())));
            }
        }

        System.out.println("Exiting...");
        System.exit(0);
    }

}
