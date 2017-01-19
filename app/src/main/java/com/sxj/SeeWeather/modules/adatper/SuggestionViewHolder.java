package com.sxj.SeeWeather.modules.adatper;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.sxj.SeeWeather.R;
import com.sxj.SeeWeather.modules.domain.Weather;

/**
 * Created by sxj52 on 2016/7/30.
 */
public class SuggestionViewHolder extends BaseViewHolder<Weather> {
    private CardView cardView;
    private TextView clothBrief;
    private TextView clothTxt;
    private TextView sportBrief;
    private TextView sportTxt;
    private TextView travelBrief;
    private TextView travelTxt;
    private TextView fluBrief;
    private TextView fluTxt;
    public SuggestionViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_suggestion);
        cardView=$(R.id.cardView);
        clothBrief=$(R.id.cloth_brief);
        clothTxt=$(R.id.cloth_txt);
        sportBrief=$(R.id.sport_brief);
        sportTxt=$(R.id.sport_txt);
        travelTxt=$(R.id.travel_txt);
        travelBrief=$(R.id.travel_brief);
        fluBrief=$(R.id.flu_brief);
        fluTxt=$(R.id.flu_txt);
    }

    @Override
    public void setData(Weather data) {
        super.setData(data);
        clothBrief.setText("穿衣指数---" + data.suggestion.drsg.brf);
        clothTxt.setText(data.suggestion.drsg.txt);

        sportBrief.setText("运动指数---" +data.suggestion.sport.brf);
        sportTxt.setText(data.suggestion.sport.txt);

        travelBrief.setText("旅游指数---" + data.suggestion.trav.brf);
        travelTxt.setText(data.suggestion.trav.txt);

        fluBrief.setText("感冒指数---" + data.suggestion.flu.brf);
        fluTxt.setText(data.suggestion.flu.txt);
    }
}
