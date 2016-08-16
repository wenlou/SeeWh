package com.xiecc.seeWeather.modules.adatper;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.xiecc.seeWeather.modules.domain.Weather;
import com.xiecc.seeWeather.modules.ui.setting.Setting;

/**
 * Created by hugo on 2016/1/31 0031.
 */
public class WeatherAdapter extends RecyclerArrayAdapter<Weather> {
    private Weather mWeatherData;
    private Setting mSetting;
    private final int TYPE_ONE = 0;
    private final int TYPE_TWO = 1;
    private final int TYPE_THREE = 2;
    private final int TYPE_FORE = 3;
    private boolean animateItems = false;
    private int lastAnimatedPosition = -1;
    private static final int ANIMATED_ITEMS_COUNT = 4;

    public WeatherAdapter(Context context, Weather weatherData) {
        super(context);
        this.mWeatherData = weatherData;

        mSetting = Setting.getInstance();

    }
    public WeatherAdapter(Context context) {
        super(context);

        mSetting = Setting.getInstance();

    }


    @Override
    public int getCount() {
        return 4;
    }



    @Override
    public int getViewType(int position) {
        if (position == TYPE_ONE) {
            return TYPE_ONE;
        }
        if (position == TYPE_TWO) {
            return TYPE_TWO;
        }
        if (position == TYPE_THREE) {
            return TYPE_THREE;
        }
        if (position == TYPE_FORE) {
            return TYPE_FORE;
        }
        return super.getViewType(position);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_ONE){
            return new NowWeatherViewHolder(parent);
        }
      if(viewType==TYPE_TWO){
          return  new HoursWeatherViewHolder(parent,mWeatherData);
      }
        if(viewType==TYPE_THREE){
            return new SuggestionViewHolder(parent);
        }
        if(viewType==TYPE_FORE){
            return  new ForecastViewHolder(parent,mWeatherData);
        }
        return null;
    }
}
