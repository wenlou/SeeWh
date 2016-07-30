package com.xiecc.seeWeather.modules.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.modules.adatper.CityAdapter;
import com.xiecc.seeWeather.modules.db.DBManager;
import com.xiecc.seeWeather.modules.db.WeatherDB;
import com.xiecc.seeWeather.modules.domain.City;
import com.xiecc.seeWeather.modules.domain.Province;
import com.xiecc.seeWeather.modules.ui.setting.Setting;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class ChoiceCityActivity extends BaseActivity
        {
    private static String TAG = ChoiceCityActivity.class.getSimpleName();
    private DBManager mDBManager;
    private WeatherDB mWeatherDB;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Province selectedProvince;
    private City selectedCity;
    private ArrayList<String> dataList = new ArrayList<>();
    private List<Province> provincesList;
    private List<City> cityList;
    private CityAdapter mAdapter;
     private EasyRecyclerView recyclerView;
    private Handler handler = new Handler();
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_city);

        //RxPermissions.getInstance(this).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
        //    .subscribe(permission ->{
        //       if (permission.granted){
        //
        //       }
        //    });
        mDBManager = new DBManager(this);
        mDBManager.openDatabase();
        mWeatherDB = new WeatherDB(this);
        initView();
        initRecyclerView();
        queryProvinces();

    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择城市");
        //加入后退按键
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ImageView bannner = (ImageView) findViewById(R.id.bannner);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setStatusBarColorForKitkat(R.color.colorSunrise);
        if (mSetting.getCurrentHour()< 6 || mSetting.getCurrentHour() > 18) {
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            Glide.with(this).load(R.mipmap.city_night).diskCacheStrategy(DiskCacheStrategy.ALL).into(bannner);
            setStatusBarColorForKitkat(R.color.colorSunset);
        }

    }


    private void initRecyclerView() {
        recyclerView = (EasyRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new CityAdapter(getApplicationContext());
        dealhAdapter(mAdapter);
        recyclerView.setRefreshing(false);



    }

    private void dealhAdapter(final RecyclerArrayAdapter<String> cityAdapter) {
        recyclerView.setAdapterWithProgress(cityAdapter);
        cityAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {

                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provincesList.get(position);
                    recyclerView.scrollTo(0, 0);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    Intent intent = new Intent();
                    String cityName = selectedCity.CityName;
                    //传送数据
                    intent.putExtra(Setting.CITY_NAME, cityName);
                    setResult(2, intent);
                    finish();
                }
            }
        });
    }

    /**
     * 查询全国所有的省，从数据库查询
     */
    private void queryProvinces() {
        collapsingToolbarLayout.setTitle("选择省份");
        Observer<Province> observer = new Observer<Province>() {
            @Override public void onCompleted() {
                currentLevel = LEVEL_PROVINCE;
                PLog.i(TAG,"省份");
                mAdapter.addAll(dataList);
            }


            @Override public void onError(Throwable e) {

            }


            @Override public void onNext(Province province) {

                dataList.add(province.ProName);


            }
        };

        Observable.defer(() -> {
            provincesList = mWeatherDB.loadProvinces(mDBManager.getDatabase());
            return Observable.from(provincesList);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);



    }


    /**
     * 查询选中省份的所有城市，从数据库查询
     */
    private void queryCities() {
        dataList.clear();
        collapsingToolbarLayout.setTitle(selectedProvince.ProName);
        PLog.i(TAG,"6666"+selectedProvince.ProSort);
        Observer<City> observer = new Observer<City>() {
            @Override public void onCompleted() {
                currentLevel = LEVEL_CITY;
                //定位到第一个item
                recyclerView.scrollToPosition(0);
                mAdapter.clear();

                mAdapter.addAll(dataList);
            }


            @Override public void onError(Throwable e) {

                PLog.i(TAG,e.toString());
            }


            @Override public void onNext(City city) {

                dataList.add(city.CityName);
            }
        };


        Observable.defer(() -> {
            cityList = mWeatherDB.loadCities(mDBManager.getDatabase(), selectedProvince.ProSort);
            PLog.i(TAG,""+selectedProvince.ProSort);
            return Observable.from(cityList);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentLevel == LEVEL_PROVINCE) {
                finish();
            }
            else {
                queryProvinces();
            }
        }
        return false;
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        mDBManager.closeDatabase();
    }


}
