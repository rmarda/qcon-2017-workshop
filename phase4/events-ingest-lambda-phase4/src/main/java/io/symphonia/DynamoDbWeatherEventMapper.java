package io.symphonia;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import io.symphonia.domain.WeatherEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DynamoDbWeatherEventMapper {

    private static Map<String, Function<WeatherEvent, Optional<AttributeValue>>> MAPPING =
            new HashMap<String, Function<WeatherEvent, Optional<AttributeValue>>>() {{
                put("locationId", e -> attrS(e.getLocationId()));
                put("locationName", e -> attrS(e.getLocationName()));
                put("latitude", e -> attrN(e.getLatitude()));
                put("longitude", e -> attrN(e.getLongitude()));
                put("city", e -> attrS(e.getCity()));
                put("state", e -> attrS(e.getState()));
                put("temperature", e -> attrN(e.getTemperature()));
                put("lastUpdated", e -> attrN(e.getTimestamp()));
            }};

    public static PutItemRequest toPutItemRequest(String table, WeatherEvent weatherEvent) {
        return MAPPING.entrySet()
                .stream()
                .collect(() -> new PutItemRequest().withTableName(table),
                        (r, e) -> e.getValue().apply(weatherEvent).ifPresent(v -> r.addItemEntry(e.getKey(), v)),
                        (a, b) -> a.getItem().putAll(b.getItem()));
    }

    private static Optional<AttributeValue> attrS(String data) {
        return data == null || data.isEmpty() ? Optional.empty() : Optional.of(new AttributeValue(data));
    }

    private static Optional<AttributeValue> attrN(Number data) {
        return data == null ? Optional.empty() : Optional.of(new AttributeValue().withN(data.toString()));
    }

}
