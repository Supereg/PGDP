package temperature;

import java.util.Date;

public class Temperature {

    private final Date date;
    private final double averageTemperature;
    private final double averageTemperatureUncertainty;
    private final String city;
    private final String country;
    private final double latitude;
    private final double longitude;

    public Temperature(Date date, double averageTemperature, double averageTemperatureUncertainty, String city, String country, double latitude, double longitude) {
        this.date = date;
        this.averageTemperature = averageTemperature;
        this.averageTemperatureUncertainty = averageTemperatureUncertainty;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public double getAverageTemperatureUncertainty() {
        return averageTemperatureUncertainty;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}