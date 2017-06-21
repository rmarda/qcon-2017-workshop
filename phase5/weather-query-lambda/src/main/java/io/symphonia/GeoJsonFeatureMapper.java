package io.symphonia;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

public class GeoJsonFeatureMapper {

    private static String LONGITUDE = "longitude";
    private static String LATITUDE = "latitude";
    private static String TEMPERATURE = "temperature";

    public static FeatureCollection toFeatureCollection(List<Map<String, AttributeValue>> items) throws JsonProcessingException {
        return items.stream()
                .filter(GeoJsonFeatureMapper::hasLngLat)
                .map(GeoJsonFeatureMapper::toFeature)
                .collect(Collector.of(FeatureCollection::new, FeatureCollection::add,
                        (l, r) -> {
                            l.addAll(r.getFeatures());
                            return l;
                        }));
    }

    private static boolean hasLngLat(Map<String, AttributeValue> item) {
        return item.containsKey(LONGITUDE) && item.containsKey(LATITUDE);
    }

    private static Feature toFeature(Map<String, AttributeValue> item) {
        Double longitude = Double.parseDouble(item.get(LONGITUDE).getN());
        Double latitude = Double.parseDouble(item.get(LATITUDE).getN());

        Feature feature = new Feature();
        feature.setGeometry(new Point(longitude, latitude));
        if (item.containsKey(TEMPERATURE)) {
            Double temperature = Double.parseDouble(item.get(TEMPERATURE).getN());
            feature.setProperty(TEMPERATURE, temperature);
        }

        return feature;
    }
}
