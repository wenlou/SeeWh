package com.sxj.SeeWeather.modules.domain;

/**
 * Created by sxj52 on 2017/1/16.
 */

public class CityWeather {
    private String root;
    private String temp;
    private String cityName;
    private String text;

    public CityWeather(String root, String temp, String cityName, String text) {
        this.root = root;
        this.temp = temp;
        this.cityName = cityName;
        this.text = text;
    }

    public String getRoot() {
        return root;
    }

    public String getTemp() {
        return temp;
    }

    public String getCityName() {
        return cityName;
    }

    public String getText() {
        return text;
    }
}
