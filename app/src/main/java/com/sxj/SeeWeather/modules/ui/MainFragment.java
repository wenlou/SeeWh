package com.sxj.SeeWeather.modules.ui;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.sxj.SeeWeather.R;
import com.sxj.SeeWeather.base.BaseApplication;
import com.sxj.SeeWeather.common.PLog;
import com.sxj.SeeWeather.common.Util;
import com.sxj.SeeWeather.component.RetrofitSingleton;
import com.sxj.SeeWeather.modules.adatper.WeatherAdapter;
import com.sxj.SeeWeather.modules.domain.Weather;
import com.sxj.SeeWeather.modules.ui.setting.Setting;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sxj52 on 2016/7/31.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,AMapLocationListener {
    private LinearLayout noWIFILayout;
    private EasyRecyclerView mRecyclerView;
    //private Weather mWeatherData = new Weather();
    private WeatherAdapter mAdapter;
    private Observer<Weather> observer;
    private ArrayList<Weather> weatherList=new ArrayList<Weather>();
    private String title;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private MainActivity mActivity;
    private Handler handler = new Handler();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        initDataObserver();
    }
    public static MainFragment getInstance(String title){
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        //recclerview
        mRecyclerView = (EasyRecyclerView) view.findViewById(R.id.recycler_view);
        noWIFILayout= (LinearLayout) view.findViewById(R.id.no_network);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setRefreshListener(this);
        mRecyclerView.setHasTransientState(true);
        mRecyclerView.setRefreshListener(this);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());

    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchDataByNetWork(observer);
            }
        },1000);

    }
    /**
     * 初始化 observer (观察者)
     * 拿到数据后的操作
     */
    private void initDataObserver() {
        observer = new Observer<Weather>() {

            @Override
            public void onCompleted() {
                //mRecyclerView.setRefreshing(false);
                //mAdapter.addAll(weatherList);
                weatherList.clear();
            }

            @Override
            public void onError(Throwable e) {
                erroNetSnackbar(observer);
                //noWIFILayout.setVisibility(View.VISIBLE);
                mRecyclerView.setRefreshing(false);
                noWIFILayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(Weather weather) {
                //mProgressBar.setVisibility(View.GONE);
                //mErroImageView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                //collapsingToolbarLayout.setTitle(weather.basic.city);
                //mAdapter = new WeatherAdapter(MainActivity.this);
                mAdapter = new WeatherAdapter(getActivity(),weather);
                mRecyclerView.setAdapterWithProgress(mAdapter);
                weatherList.add(weather);
                mAdapter.add(weather);
                mAdapter.add(weather);
                mAdapter.add(weather);
                mAdapter.add(weather);
                mActivity.normalStyleNotification(weather);
                mActivity.startUpdataService(weather);
                noWIFILayout.setVisibility(View.GONE);
                mActivity.showSnackbar(getView(), "加载完毕，✺◟(∗❛ัᴗ❛ั∗)◞✺,");
            }
        };
//        fetchDataByCache(observer);
//        fetchDataByNetWork(observer);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Util.isNetworkConnected(mActivity)) {
            //CheckVersion.checkVersion(this, fab);
            // https://github.com/tbruyelle/RxPermissions
            RxPermissions.getInstance(mActivity).request(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(granted -> {
                        if (granted) {
                            location();
                        } else {
                            fetchDataByCache(observer);
                        }
                    });
            //fetchData();
        } else {
            fetchDataByCache(observer);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //为了实现 Intent 重启使图标生效

    }

    /**
     * 从本地获取
     */
    public void fetchDataByCache(final Observer<Weather> observer) {

        Weather weather = null;
        try {
            weather = (Weather) mActivity.aCache.getAsObject("WeatherData");
        } catch (Exception e) {
            //Log.e(TAG, e.toString());
        }

        if (weather != null) {
            //distinct()去除重复
            noWIFILayout.setVisibility(View.GONE);
            Observable.just(weather).distinct().subscribe(observer);
        } else {
            erroNetSnackbar(observer);
        }
    }

    private void erroNetSnackbar(final Observer<Weather> observer) {
        noWIFILayout.setVisibility(View.VISIBLE);

        //mRecyclerView.setVisibility(View.GONE);
        Snackbar.make(getView(), "网络不好,~( ´•︵•` )~", Snackbar.LENGTH_INDEFINITE).setAction("重试", v -> {
            fetchDataByNetWork(observer);
//            initDataObserver();
//            onRefresh();
        }).show();
    }



    /**
     * 从网络获取
     */
    public void fetchDataByNetWork(Observer<Weather> observer) {
        String cityName = mActivity.mSetting.getString(Setting.CITY_NAME, "济南");
        if (cityName != null) {
            cityName = cityName.replace("市", "")
                    .replace("省", "")
                    .replace("自治区", "")
                    .replace("特别行政区", "")
                    .replace("地区", "")
                    .replace("盟", "");
        }
        RetrofitSingleton.getApiService(getActivity())
                .mWeatherAPI(cityName, Setting.KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok"))
                .map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
                .doOnNext(weather -> {
                    mActivity.aCache.put("WeatherData", weather,
                            (mActivity.mSetting.getAutoUpdate() * Setting.ONE_HOUR));//默认一小时后缓存失效
                })
                .subscribe(observer);

    }


    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(BaseApplication.mAppContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔 单位毫秒
        //设置定位间隔 单位毫秒
        int tempTime = mActivity.mSetting.getAutoUpdate();
        if (tempTime == 0) {
            tempTime = 100;
        }
        mLocationOption.setInterval(tempTime * mActivity.mSetting.ONE_HOUR);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                mActivity.mSetting.setCityName(aMapLocation.getCity());
                PLog.i("889787", aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict() +
                        aMapLocation.getAdCode() + aMapLocation.getCityCode());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                PLog.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" +
                        aMapLocation.getErrorInfo());
                //Snackbar.make(fab, "定位失败,请尝试手动更新", Snackbar.LENGTH_LONG).show();
                mActivity.showSnackbar(getView(), "定位失败,加载默认城市", true);
            }
            fetchDataByNetWork(observer);
        }
    }

}
