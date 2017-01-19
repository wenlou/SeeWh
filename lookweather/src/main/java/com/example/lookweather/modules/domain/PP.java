package com.example.lookweather.modules.domain;

import com.xiecc.lookWeather.modules.domain.City;
import com.xiecc.lookWeather.modules.domain.Province;

import java.util.List;

/**
 * Created by sxj52 on 2016/7/30.
 */
public class PP {
    private List<Result> results;
    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
     public static class Result{
         private com.xiecc.lookWeather.modules.domain.City mCity;
         private Province mProvince;

         public com.xiecc.lookWeather.modules.domain.City getCity() {
             return mCity;
         }

         public void setCity(City city) {
             mCity = city;
         }

         public Province getProvince() {
             return mProvince;
         }

         public void setProvince(Province province) {
             mProvince = province;
         }
     }
}
