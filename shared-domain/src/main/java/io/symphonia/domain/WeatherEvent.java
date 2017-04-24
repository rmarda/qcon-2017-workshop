package io.symphonia.domain;

public class WeatherEvent {

    /*
          EventBodyModel:
            type: "object"
            properties:
              timestamp:
                type: "integer"
              location_id:
                type: "string"
              location_name:
                type: "string"
              latitude:
                type: "number"
              longitude:
                type: "number"
              city:
                type: "string"
              state:
                type: "string"
              temperature:
                type: "number"
            required:
            - timestamp
            - location_id
            - temperature
     */

    private Long timestamp;
    private String locationId;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;
    private Double temperature;

    public WeatherEvent() {
    }

    public WeatherEvent(Long timestamp, String locationId, Double temperature) {
        this.timestamp = timestamp;
        this.locationId = locationId;
        this.temperature = temperature;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
