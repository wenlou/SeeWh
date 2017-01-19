package com.example.lookweather.modules.adatper;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.xiecc.lookWeather.modules.domain.Weather;
import com.xiecc.lookWeather.modules.ui.setting.Setting;

/**
 * Created by sxj52 on 2016/8/1.
 */
public class ChoiceCityAdpter extends RecyclerArrayAdapter<Weather> {
    private Setting mSetting;
    private Context mContext;
    public ChoiceCityAdpter(Context context) {
        super(context);
       // this.mWeatherData = weatherData;
        mContext=context;
        mSetting = Setting.getInstance();
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChioceCityViewHolder(parent);
    }

}
