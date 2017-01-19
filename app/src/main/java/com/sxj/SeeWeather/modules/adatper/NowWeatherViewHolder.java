package com.sxj.SeeWeather.modules.adatper;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.sxj.SeeWeather.R;
import com.sxj.SeeWeather.modules.domain.Weather;
import com.sxj.SeeWeather.modules.ui.setting.Setting;

/**
 * Created by sxj52 on 2016/7/30.
 */
public class NowWeatherViewHolder extends BaseViewHolder<Weather> {
    private CardView cardView;
    private ImageView weatherIcon;
    private TextView tempFlu;
    private TextView tempMax;
    private TextView tempMin;

    private TextView tempPm;
    private TextView tempQuality;
    private Setting mSetting=Setting.getInstance();
    public NowWeatherViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_temperature);
        cardView=$(R.id.cardView);
        weatherIcon=$(R.id.weather_icon);
        tempFlu=$(R.id.temp_flu);
        tempMax=$(R.id.temp_max);
        tempMin=$(R.id.temp_min);
        tempPm=$(R.id.temp_pm);
        tempQuality=$(R.id.temp_quality);
    }

    @Override
    public void setData(Weather data) {
        super.setData(data);
        tempFlu.setText(data.now.tmp + "℃");
        tempMax.setText("↑ " + data.dailyForecast.get(0).tmp.max + "°");
        tempMin.setText("↓ " + data.dailyForecast.get(0).tmp.min + "°");
        if (data.aqi != null) {
            tempPm.setText("PM25： " + data.aqi.city.pm25);
            tempQuality.setText("空气质量： " + data.aqi.city.qlty);
        }
        Glide.with(getContext())
                .load(mSetting.getInt(data.now.cond.txt, R.mipmap.none))
                .into(weatherIcon);
    }
}
