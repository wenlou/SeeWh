package com.xiecc.seeWeather.modules.adatper;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class CityAdapter extends RecyclerArrayAdapter<String>{

    public CityAdapter(Context context) {
        super(context);
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityViewHolder(parent);
    }



}
