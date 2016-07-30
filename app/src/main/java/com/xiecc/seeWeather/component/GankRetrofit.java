package com.xiecc.seeWeather.component;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sxj52 on 2016/5/27.
 */
public class GankRetrofit {
    private static GankService apiService = null;

    //private static Retrofit retrofit = null;

    private static OkHttpClient okHttpClient = null;

    private static final String TAG = GankRetrofit.class.getSimpleName();

    public static Context context;
    //地址
    private static final String GANHUO_API = "http://gank.io/";
    private static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        if(retrofit==null){
            synchronized (GankRetrofit.class){
                if(retrofit==null){
                   retrofit=new Retrofit.Builder().
                           baseUrl(GANHUO_API).
                           addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                           .addConverterFactory(GsonConverterFactory.create())
                           .build();
                }
            }
        }
        return retrofit;

    }
//    /**
//     * 初始化
//     */
//
//    public static void init(final Context context) {
//
//        Executor executor = Executors.newCachedThreadPool();
//
//        Gson gson = new GsonBuilder().create();
//        // https://drakeet.me/retrofit-2-0-okhttp-3-0-config
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//
//                .retryOnConnectionFailure(true)
//                .connectTimeout(15, TimeUnit.SECONDS)
//                .build();
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(GANHUO_API)
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .callbackExecutor(executor)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
//
//        apiService = retrofit.create(GankService.class);
//    }
//
//    public static GankService getApiService(Context context) {
//        if (apiService != null) return apiService;
//        init(context);
//        return getApiService(context);
//    }
//
//    public static Retrofit getRetrofit(Context context) {
//        if (retrofit != null) return retrofit;
//        init(context);
//        return getRetrofit(context);
//    }
//
//    public static OkHttpClient getOkHttpClient(Context context) {
//        if (okHttpClient != null) return okHttpClient;
//        init(context);
//        return getOkHttpClient(context);
//    }
//
//    public static void disposeFailureInfo(Throwable t, Context context, View view) {
//        if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
//                t.toString().contains("UnknownHostException")) {
//            Snackbar.make(view, "网络不好,~( ´•︵•` )~", Snackbar.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
//        }
//        Log.w(TAG, t.toString());
//    }
}
