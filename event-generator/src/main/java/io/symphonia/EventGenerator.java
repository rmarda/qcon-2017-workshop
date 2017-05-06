package io.symphonia;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.paumard.streams.StreamsUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class EventGenerator implements Supplier<WeatherEvent> {

    private final MessageDigest messageDigest;
    private final Random random;
    private final String[] location;

    public EventGenerator(String[] location) {
        this.random = new Random();
        this.location = location;
        try {
            this.messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WeatherEvent get() {
        String locationName = String.format("%s, %s", location[0], location[1]);
        String md5 = DatatypeConverter.printHexBinary(messageDigest.digest(locationName.getBytes()));

        // Generate temperature based on latitude
        Double latitude = Double.parseDouble(location[2]);
        Double temperature = ((1 - (Math.abs(latitude) / 90.0)) * 140) + (random.nextDouble() * 10) - 5;

        WeatherEvent weatherEvent = new WeatherEvent(System.currentTimeMillis(), md5, temperature);
        weatherEvent.setCity(location[0]);
        weatherEvent.setState(location[1]);
        weatherEvent.setLatitude(latitude);
        weatherEvent.setLongitude(Double.parseDouble(location[3]));
        weatherEvent.setLocationName(locationName);

        return weatherEvent;
    }

    public static void main(String[] argv) throws IOException, NoSuchAlgorithmException {

        Args args = new Args();

        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);

        String apiBaseUrl = "";
        if (!args.test) {
            ApiUrlFinder finder = new ApiUrlFinder();
            apiBaseUrl = finder.find(args.stackName);
            if (apiBaseUrl == null) {
                System.err.println("Couldn't find API url for stack: " + args.stackName);
                return;
            }
        }

        String apiUrl = apiBaseUrl + "/events";
        System.out.println("API url: " + apiUrl);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        CsvMapper csvMapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);
        InputStream locationsFile = EventGenerator.class.getResourceAsStream("/locations.csv");
        MappingIterator<String[]> it = csvMapper.readerFor(String[].class).readValues(locationsFile);
        Stream<EventGenerator> eventGeneratorStream =
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED), false)
                        .map(EventGenerator::new);

        ObjectMapper jsonMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        StreamsUtils.cycle(eventGeneratorStream)
                .limit(args.limit)
                .map(EventGenerator::get)
                .map(event -> {
                    if (args.invalid) {
                        event.setLocationId(null);
                    }
                    return event;
                })
                .map(event -> {
                    try {
                        return jsonMapper.writeValueAsString(event);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(json -> {
                    if (!args.test) {
                        send(httpclient, apiUrl, json);
                    } else {
                        System.out.println("TEST request: " + json);
                    }
                });

        System.out.println("Exiting...");
        System.exit(0);
    }

    private static void send(CloseableHttpClient httpClient, String apiUrl, String json) {

        // Build HTTP Post
        HttpPost post = new HttpPost(apiUrl);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        post.setEntity(new StringEntity(json, Charset.forName("UTF-8")));

        System.out.println("request: " + json);

        // Send it
        try (CloseableHttpResponse response = httpClient.execute(post)) {
            System.out.println(String.format("response: %s / %s",
                    response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
