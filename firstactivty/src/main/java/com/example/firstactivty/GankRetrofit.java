package com.example.firstactivty;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sxj52 on 2016/5/27.
 */
public class GankRetrofit {
    //地址
    private static final String GANHUO_API = "http://gank.io/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if(retrofit==null){
            synchronized (GankRetrofit.class){
                    if(retrofit==null){
                        retrofit = new Retrofit.Builder()
                                .baseUrl(GANHUO_API)
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .build();
                    }
            }
        }
        return retrofit;
    }
}
