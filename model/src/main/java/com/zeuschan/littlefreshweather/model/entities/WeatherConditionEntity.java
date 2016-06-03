package com.zeuschan.littlefreshweather.model.entities;

/**
 * Created by chenxiong on 2016/5/31.
 */
public class WeatherConditionEntity {
    private int weatherCode;
    private String weatherDescription;
    private String weatherIconUrl;

    @Override
    public String toString() {
        return "WeatherConditionEntity{" +
                "weatherCode=" + weatherCode +
                ", weatherDescription='" + weatherDescription + '\'' +
                ", weatherIconUrl='" + weatherIconUrl + '\'' +
                '}';
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getWeatherIconUrl() {
        return weatherIconUrl;
    }

    public void setWeatherIconUrl(String weatherIconUrl) {
        this.weatherIconUrl = weatherIconUrl;
    }
}