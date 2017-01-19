package com.sxj.SeeWeather.modules.domain;

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
         private City mCity;
         private Province mProvince;

         public City getCity() {
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
