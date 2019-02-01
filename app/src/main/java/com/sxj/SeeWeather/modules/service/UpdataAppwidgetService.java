package com.sxj.SeeWeather.modules.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.sxj.SeeWeather.R;
import com.sxj.SeeWeather.common.ACache;
import com.sxj.SeeWeather.common.PLog;
import com.sxj.SeeWeather.common.Util;
import com.sxj.SeeWeather.component.RetrofitSingleton;
import com.sxj.SeeWeather.modules.domain.Weather;
import com.sxj.SeeWeather.modules.receiver.WidgetProviderWeek;
import com.sxj.SeeWeather.modules.ui.MainActivity;
import com.sxj.SeeWeather.modules.ui.setting.Setting;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UpdataAppwidgetService extends Service {
    private AppWidgetManager awm;


    private final String TAG = AutoUpdateService.class.getSimpleName();
    private Setting mSetting;
    private ACache mAcache;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //PLog.i(TAG, "服务创建了");
        mSetting = Setting.getInstance();
        mAcache = ACache.get(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //PLog.i(TAG, "服务开始了");
        synchronized (this) {

            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            PLog.e(TAG, e.toString());
                        }

                        @Override
                        public void onNext(Long aLong) {
                            fetchDataByNetWork();
                        }
                    });

        }


        return START_REDELIVER_INTENT;
    }


    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void fetchDataByNetWork() {
        String cityName = mSetting.getString(Setting.CITY_NAME, "济南");
        if (cityName != null) {
            cityName = cityName.replace("市", "")
                    .replace("省", "")
                    .replace("自治区", "")
                    .replace("特别行政区", "")
                    .replace("地区", "")
                    .replace("盟", "");
        }
        RetrofitSingleton.getApiService(this)
                .mWeatherAPI(cityName, Setting.KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok"))
                .map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        mAcache.put("WeatherData", weather);
                        startUpdataService(weather);
                    }
                });
    }


    private void startUpdataService(Weather weather) {
        awm = AppWidgetManager.getInstance(this);

        // 激活主键--通讯
        ComponentName provider = new ComponentName(UpdataAppwidgetService.this, WidgetProviderWeek.class);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_week);
        //动作
        Intent intent = new Intent(UpdataAppwidgetService.this, MainActivity.class);
        //延迟意图
        PendingIntent pendingIntent = PendingIntent.getActivity(UpdataAppwidgetService.this, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_week_button, pendingIntent);
        views.setTextViewText(R.id.widget_week_week_1, weather.basic.city);

        try {
            views.setTextViewText(R.id.widget_week_week_2, "明天");
            views.setTextViewText(R.id.widget_week_week_3, Util.dayForWeek(weather.dailyForecast.get(2).date));
            //views.setTextViewText(R.id.widget_week_week_4, Util.dayForWeek(weather.dailyForecast.get(3).date));
            //views.setTextViewText(R.id.widget_week_week_5, Util.dayForWeek(weather.dailyForecast.get(4).date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        views.setTextViewText(R.id.widget_week_temp_1, weather.dailyForecast.get(0).tmp.min + "°/" + weather.dailyForecast.get(0).tmp.max + "°");
        views.setTextViewText(R.id.widget_week_temp_2, weather.dailyForecast.get(1).tmp.min + "°/" + weather.dailyForecast.get(1).tmp.max + "°");
        views.setTextViewText(R.id.widget_week_temp_3, weather.dailyForecast.get(2).tmp.min + "°/" + weather.dailyForecast.get(2).tmp.max + "°");
        //views.setTextViewText(R.id.widget_week_temp_4, weather.dailyForecast.get(3).tmp.min + "°/" + weather.dailyForecast.get(3).tmp.max + "°");
        //views.setTextViewText(R.id.widget_week_temp_5, weather.dailyForecast.get(4).tmp.min + "°/" + weather.dailyForecast.get(4).tmp.max + "°");
        views.setImageViewResource(R.id.widget_week_image_1, mSetting.getInt(weather.now.cond.txt, R.mipmap.none));
        views.setImageViewResource(R.id.widget_week_image_2, mSetting.getInt(weather.dailyForecast.get(1).cond.txtD, R.mipmap.none));
        views.setImageViewResource(R.id.widget_week_image_3, mSetting.getInt(weather.dailyForecast.get(2).cond.txtD, R.mipmap.none));
        //views.setImageViewResource(R.id.widget_week_image_4, mSetting.getInt(weather.dailyForecast.get(3).cond.txtD, R.mipmap.none));
        //views.setImageViewResource(R.id.widget_week_image_5, mSetting.getInt(weather.dailyForecast.get(4).cond.txtD, R.mipmap.none));
        awm.updateAppWidget(provider, views);
    }


}
