package com.example.lookweather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxj52 on 2016/4/29.
 */
public class WeatherAPI {
    @SerializedName("HeWeather data service 3.0") @Expose
    public List<Weather> mHeWeatherDataService30s
            = new ArrayList<>();
}
