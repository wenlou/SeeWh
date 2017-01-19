package com.example.lookweather.modules.adatper;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.xiecc.lookWeather.R;

/**
 * Created by sxj52 on 2016/7/30.
 */
public class CityViewHolder extends BaseViewHolder<String> {
    private TextView itemCity;
    private CardView cardView;
    public CityViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_city);
        itemCity=$(R.id.item_city);
        cardView=$(R.id.cardView);
    }

    @Override
    public void setData(String data) {
        super.setData(data);
        itemCity.setText(data);
    }


}
