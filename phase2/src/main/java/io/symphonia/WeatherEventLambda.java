package io.symphonia;

public class WeatherEventLambda {

    public WeatherEvent handler(WeatherEvent weatherEvent) {
        System.out.println("weatherEvent.locationName = " + weatherEvent.locationName);
        System.out.println("weatherEvent.temperature = " + weatherEvent.temperature);
        return weatherEvent;
    }

    public static class WeatherEvent {
        public String locationName;
        public Double temperature;
    }
}
