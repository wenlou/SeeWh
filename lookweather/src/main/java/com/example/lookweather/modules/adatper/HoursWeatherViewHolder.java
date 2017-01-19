package com.example.lookweather.modules.adatper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.xiecc.lookWeather.R;
import com.xiecc.lookWeather.modules.domain.Weather;

/**
 * Created by sxj52 on 2016/7/30.
 */
public class HoursWeatherViewHolder extends BaseViewHolder<Weather> {
    Weather mWeatherData;
    private LinearLayout itemHourInfoLinearlayout;
    private TextView[] mClock;
    private TextView[] mTemp ;
    private TextView[] mHumidity ;
    private TextView[] mWind ;

    public HoursWeatherViewHolder(ViewGroup parent,Weather mWeatherData) {
        super(parent, R.layout.item_hour_info);
        this.mWeatherData=mWeatherData;
         mClock = new TextView[mWeatherData.hourlyForecast.size()];
        mTemp = new TextView[mWeatherData.hourlyForecast.size()];
       mHumidity = new TextView[mWeatherData.hourlyForecast.size()];
        mWind = new TextView[mWeatherData.hourlyForecast.size()];
        itemHourInfoLinearlayout=$(R.id.item_hour_info_linearlayout);
        for (int i = 0; i < mWeatherData.hourlyForecast.size(); i++) {
            View view = View.inflate(getContext(), R.layout.item_hour_info_line, null);
            mClock[i] = (TextView) view.findViewById(R.id.one_clock);
            mTemp[i] = (TextView) view.findViewById(R.id.one_temp);
            mHumidity[i] = (TextView) view.findViewById(R.id.one_humidity);
            mWind[i] = (TextView) view.findViewById(R.id.one_wind);
            itemHourInfoLinearlayout.addView(view);
        }
    }

    @Override
    public void setData(Weather data) {
        super.setData(data);
        for (int i = 0; i < data.hourlyForecast.size(); i++) {
            //s.subString(s.length-3,s.length);
            //第一个参数是开始截取的位置，第二个是结束位置。
            String mDate = data.hourlyForecast.get(i).date;
            mClock[i].setText(
                    mDate.substring(mDate.length() - 5, mDate.length()));
            mTemp[i].setText(data.hourlyForecast.get(i).tmp + "°");
            mHumidity[i].setText(
                    data.hourlyForecast.get(i).hum + "%");
            mWind[i].setText(
                    data.hourlyForecast.get(i).wind.spd + "Km");
        }
    }
}
