package io.symphonia.domain;

public class WeatherEvent {

    private Long timestamp;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private Double temperature;

    public WeatherEvent() {
    }

    public WeatherEvent(Long timestamp, String locationName, Double temperature) {
        this.timestamp = timestamp;
        this.locationName = locationName;
        this.temperature = temperature;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
