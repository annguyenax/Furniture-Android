package model;

public class WeatherForecastModel {
    private String time;
    private String temperature;
    private String wind;
    private String icon;
    private String windSpeed;

    public WeatherForecastModel(String time, String temperature, String wind, String icon, String windSpeed) {
        this.time = time;
        this.temperature = temperature;
        this.wind = wind;
        this.icon = icon;
        this.windSpeed = windSpeed;
    }

    public WeatherForecastModel() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
