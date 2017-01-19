package com.example.lookweather.modules.ui.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.xiecc.lookWeather.R;
import com.xiecc.lookWeather.common.PLog;
import com.xiecc.lookWeather.component.RetrofitSingleton;
import com.xiecc.lookWeather.modules.adatper.ChoiceCityAdpter;
import com.xiecc.lookWeather.modules.db.ChoiceCityDao;
import com.xiecc.lookWeather.modules.domain.Weather;
import com.xiecc.lookWeather.modules.ui.setting.Setting;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sxj52 on 2016/8/1.
 */
public class CityFrgment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private LinearLayout noWIFILayout;
    private EasyRecyclerView mRecyclerView;
    //private Weather mWeatherData = new Weather();
    private ChoiceCityAdpter mAdapter;
    private Observer<Weather> observer;
    private ArrayList<Weather> weatherList=new ArrayList<Weather>();
    private String title;
    private Handler handler = new Handler();
    private ChoiceCityDao dao;
    ArrayList<String>WeatherList=new ArrayList<String>();
    private ImageView iv_erro;
    private TextView iv_text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        dao=new ChoiceCityDao(getContext());
        queryProvinces();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        queryProvinces();
        onRefresh();

    }
    public static CityFrgment getInstance(String title){
        CityFrgment cityFrgment = new CityFrgment();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        cityFrgment.setArguments(bundle);
        return cityFrgment;
    }

    private void initView(View view) {
        mRecyclerView = (EasyRecyclerView) view.findViewById(R.id.recycler_view);
        iv_erro= (ImageView) view.findViewById(R.id.iv_erro);
        iv_text= (TextView) view.findViewById(R.id.iv_text);
        noWIFILayout= (LinearLayout) view.findViewById(R.id.no_network);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setRefreshListener(this);
        mRecyclerView.setHasTransientState(true);
        mRecyclerView.setRefreshListener(this);
        mAdapter = new ChoiceCityAdpter(getActivity());
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());

      queryProvinces();
    }

    @Override
    public void onResume() {
        super.onResume();
       queryProvinces();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!WeatherList.isEmpty()){
                    noWIFILayout.setVisibility(View.GONE);
                    queryProvinces();
                    //multiLoad();
                    }
                else{
                    iv_erro.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.empty));
                    iv_text.setText("亲，还没有添加城市哦.. ( ＞ω＜)");
                    noWIFILayout.setVisibility(View.VISIBLE);

                }
            }
        },1000);

    }
    private void queryProvinces() {

        Observer<String> observer= new Observer<String>() {
                String mString=null;
            @Override
            public void onCompleted() {
                PLog.e("9769776",WeatherList.size()+"");
                //mAdapter.clear();
                weatherList.clear();
                mAdapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
                    @Override
                    public boolean onItemClick(int position) {
                        new AlertDialog.Builder(getActivity()).setMessage("是否删除该城市?")
                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dao.delete(mString);
                                        Snackbar.make(getView(), "已经将" + mString + "删掉了 Ծ‸ Ծ", Snackbar.LENGTH_LONG).show();
                                        onRefresh();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                                .show();
                        return true;
                    }
                });

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                mString=s;
              getData(s);
            }
        };

        Observable.defer(() -> {
            WeatherList = dao.queryMode();
            return Observable.from(WeatherList);
        }).distinct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);



    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //multiLoad();
        //initDataObserver();
        queryProvinces();
    }
    private void getData(String cityName) {
        PLog.e("97697765",cityName);
        if (cityName != null) {
            cityName = cityName.replace("市", "")
                    .replace("省", "")
                    .replace("自治区", "")
                    .replace("特别行政区", "")
                    .replace("地区", "")
                    .replace("盟", "");
        }
        final String finalCityName = cityName;
        RetrofitSingleton.getApiService(getActivity())
                .mWeatherAPI(cityName, Setting.KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok"))
                .map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                        mAdapter.clear();
                        mAdapter.addAll(weatherList);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        mRecyclerView.setAdapterWithProgress(mAdapter);
                        weatherList.add(weather);
                        //mAdapter.add(weather);
//                String p=finalCityName;
//                PLog.e("97697762", finalCityName);
                        PLog.e("97697768",weather.basic.city);
                    }
                });
    }

}
