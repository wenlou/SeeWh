package com.example.lookweather;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sxj52 on 2016/4/29.
 */
public interface APiInterface {
    /**
     * The constant HOST.
     */
    String HOST = "https://api.heweather.com/x3/";

    /**
     * M weather api observable.
     *
     * @param city the city
     * @param key  the key
     * @return the observable
     */
    @GET("weather")
    Observable<WeatherAPI> mWeatherAPI(@Query("city") String city, @Query("key") String key);

//    //而且在Retrofit 2.0中我们还可以在@Url里面定义完整的URL：这种情况下Base URL会被忽略。
//    @GET("http://api.fir.im/apps/latest/5630e5f1f2fc425c52000006") Observable<VersionAPI> mVersionAPI(
//            @Query("api_token") String api_token);
}
