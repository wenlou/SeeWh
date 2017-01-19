package com.sxj.SeeWeather.modules.adatper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.sxj.SeeWeather.R;
import com.sxj.SeeWeather.common.Util;
import com.sxj.SeeWeather.modules.domain.Weather;
import com.sxj.SeeWeather.modules.ui.setting.Setting;

/**
 * Created by sxj52 on 2016/7/30.
 */
public class ForecastViewHolder extends BaseViewHolder<Weather> {
    Weather mWeatherData;
    private Setting mSetting=Setting.getInstance();;
    private LinearLayout forecastLinear;
    private TextView[] forecastDate ;
    private TextView[] forecastTemp;
    private TextView[] forecastTxt ;
    private ImageView[] forecastIcon ;
    public ForecastViewHolder(ViewGroup parent, Weather mWeatherData) {
        super(parent, R.layout.item_forecast);
        this.mWeatherData=mWeatherData;
        forecastDate = new TextView[mWeatherData.dailyForecast.size()];
        forecastTemp = new TextView[mWeatherData.dailyForecast.size()];
        forecastTxt = new TextView[mWeatherData.dailyForecast.size()];
        forecastIcon = new ImageView[mWeatherData.dailyForecast.size()];
        forecastLinear = (LinearLayout) itemView.findViewById(R.id.forecast_linear);
        for (int i = 0; i < mWeatherData.dailyForecast.size(); i++) {
            View view = View.inflate(getContext(), R.layout.item_forecast_line, null);
            forecastDate[i] = (TextView) view.findViewById(R.id.forecast_date);
            forecastTemp[i] = (TextView) view.findViewById(R.id.forecast_temp);
            forecastTxt[i] = (TextView) view.findViewById(R.id.forecast_txt);
            forecastIcon[i] = (ImageView) view.findViewById(R.id.forecast_icon);
            forecastLinear.addView(view);
        }
    }

    @Override
    public void setData(Weather data) {
        super.setData(data);
        //今日 明日
        forecastDate[0].setText("今日");
        forecastDate[1].setText("明日");
        for (int i = 0; i < mWeatherData.dailyForecast.size(); i++) {
            if (i > 1) {
                try {
                    forecastDate[i].setText(
                            Util.dayForWeek(mWeatherData.dailyForecast.get(i).date));
                } catch (Exception e) {
                    //PLog.e("66666", e.toString());
                }
            }
            // 图片
            Glide.with(getContext())
                    .load(mSetting.getInt(mWeatherData.dailyForecast.get(i).cond.txtD, R.mipmap.none))
                    .crossFade()
                    .into(forecastIcon[i]);

            View view = (View) forecastIcon[i].getParent();
            view.setOnClickListener(v -> {

            });

            forecastTemp[i].setText(
                    mWeatherData.dailyForecast.get(i).tmp.min + "° " +
                            mWeatherData.dailyForecast.get(i).tmp.max + "°");
            forecastTxt[i].setText(
                    mWeatherData.dailyForecast.get(i).cond.txtD + "。 最高" +
                            mWeatherData.dailyForecast.get(i).tmp.max + "℃。 " +
                            mWeatherData.dailyForecast.get(i).wind.sc + " " +
                            mWeatherData.dailyForecast.get(i).wind.dir + " " +
                            mWeatherData.dailyForecast.get(i).wind.spd + " km/h。 " +
                            "降水几率 " +
                            "" + mWeatherData.dailyForecast.get(i).pop + "%。");
        }
    }
}
