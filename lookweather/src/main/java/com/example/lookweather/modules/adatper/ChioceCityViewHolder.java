package com.example.lookweather.modules.adatper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.xiecc.lookWeather.R;
import com.xiecc.lookWeather.common.PLog;
import com.xiecc.lookWeather.modules.domain.Weather;
import com.xiecc.lookWeather.modules.ui.setting.Setting;

/**
 * Created by sxj52 on 2016/8/1.
 */
public class ChioceCityViewHolder extends BaseViewHolder<Weather> {
    private CardView root;
    private TextView dialog_city;
    private ImageView dialog_icon;
    private TextView dialog_temp;
    Context mContext=getContext();
    Setting mSetting=Setting.getInstance();


    public ChioceCityViewHolder(ViewGroup parent) {
        super(parent,R.layout.item_multi_city);
        root=$(R.id.cardView1);
        dialog_city=$(R.id.dialog_city);
        dialog_icon=$(R.id.dialog_icon);
        dialog_temp=$(R.id.dialog_temp);
    }

    @Override
    public void setData(Weather data) {
        super.setData(data);
        int code = Integer.valueOf(data.now.cond.code);
        PLog.e("090008",""+code);
        if (code == 100) {

            root.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.dialog_bg_sunny));
        } else if (code >= 300 && code < 408) {
            root.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.dialog_bg_rainy));
        } else {
            root.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.dialog_bg_cloudy));
        }
        dialog_city.setText(data.basic.city);
        dialog_temp.setText(String.format("%s°", data.now.tmp));
        Glide.with(getContext())
                .load(mSetting.getInt(data.now.cond.txt, R.mipmap.none))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        dialog_icon.setImageBitmap(resource);
                        dialog_icon.setColorFilter(Color.WHITE);
                    }
                });

    }
}
