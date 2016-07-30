package com.xiecc.seeWeather.modules.ui;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.Util;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.adatper.WeatherAdapter;
import com.xiecc.seeWeather.modules.domain.Weather;
import com.xiecc.seeWeather.modules.listener.HidingScrollListener;
import com.xiecc.seeWeather.modules.receiver.WidgetProviderWeek;
import com.xiecc.seeWeather.modules.service.AutoUpdateService;
import com.xiecc.seeWeather.modules.ui.about.AboutActivity;
import com.xiecc.seeWeather.modules.ui.setting.Setting;
import com.xiecc.seeWeather.modules.ui.setting.SettingActivity;

import java.util.ArrayList;
import java.util.Calendar;

import me.drakeet.materialdialog.MaterialDialog;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * The type Main activity.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
    AMapLocationListener ,SwipeRefreshLayout.OnRefreshListener{
    private AppWidgetManager awm;

   private MaterialDialog materialDialog;

    private final String TAG = MainActivity.class.getSimpleName();

    private CollapsingToolbarLayout collapsingToolbarLayout;
    //@Bind(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private ImageView bannner;
    private LinearLayout noWIFILayout;
    private ImageView mErroImageView;
    private RelativeLayout headerBackground;

    private EasyRecyclerView mRecyclerView;
    //private Weather mWeatherData = new Weather();
    private WeatherAdapter mAdapter;
    private Observer<Weather> observer;
    private long exitTime = 0; ////记录第一次点击的时间
    private ArrayList<Weather> weatherList=new ArrayList<Weather>();

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    //private boolean isLoaction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PLog.i(TAG,"onCreate");
        //ButterKnife.bind(this);
        initView();
        initDrawer();

        initDataObserver();
        startService(new Intent(this, AutoUpdateService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //为了实现 Intent 重启使图标生效
        initIcon();
        if (Util.isNetworkConnected(this)) {
            //CheckVersion.checkVersion(this, fab);
            // https://github.com/tbruyelle/RxPermissions
            RxPermissions.getInstance(this).request(Manifest.permission.ACCESS_COARSE_LOCATION)
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
    protected void onPause() {
        super.onPause();
        PLog.i(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PLog.i(TAG,"onStop");
    }

    /**
     * 初始化基础View
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mErroImageView= (ImageView) findViewById(R.id.iv_erro);
        setSupportActionBar(toolbar);
        bannner = (ImageView) findViewById(R.id.bannner);
        //标题
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(" ");
        }

        //彩蛋-夜间模式
        Calendar calendar = Calendar.getInstance();

        //mSetting.putInt(Setting.HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        mSetting.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));

        setStatusBarColorForKitkat(R.color.colorSunrise);
        if (mSetting.getCurrentHour() < 6 || mSetting.getCurrentHour() > 18) {
            Glide.with(this).load(R.mipmap.sunset).diskCacheStrategy(DiskCacheStrategy.ALL).into(bannner);
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColorForKitkat(R.color.colorSunset);
        }

        //fab
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> showFabDialog());
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            final int fabBottomMargin = lp.bottomMargin;

            //recclerview
            mRecyclerView = (EasyRecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerView.setRefreshListener(this);
            mRecyclerView.setHasTransientState(true);
            //mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setOnScrollListener(new HidingScrollListener() {
                @Override
                public void onHide() {
                    fab.animate()
                        .translationY(fab.getHeight() + fabBottomMargin)
                        .setInterpolator(new AccelerateInterpolator(2))
                        .start();
                }

                @Override
                public void onShow() {
                    fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                }
            });

        }

        //mAdapter = new WeatherAdapter(MainActivity.this, mWeatherData);
        //mRecyclerView.setAdapter(mAdapter);

    }

    private void dealWithAdapter(final RecyclerArrayAdapter<Weather> adapter) {
        mRecyclerView.setAdapterWithProgress(adapter);
        //mAdapter.setOnItemClickListener(new);
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        //https://segmentfault.com/a/1190000004151222
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
            headerBackground = (RelativeLayout) headerLayout.findViewById(R.id.header_background);
            if (mSetting.getInt(Setting.HOUR, 0) < 6 || mSetting.getInt(Setting.HOUR, 0) > 18) {
                //headerBackground.setBackground(this.getResources().getDrawable(R.mipmap.header_back_night)); 过时
                headerBackground.setBackground(ContextCompat.getDrawable(this, R.mipmap.head2));
            }
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }
    }

    /**
     * 初始化Icon
     */
    private void initIcon() {
        if (mSetting.getIconType() == 0) {
            mSetting.putInt("未知", R.mipmap.none);
            mSetting.putInt("晴", R.mipmap.type_one_sunny);
            mSetting.putInt("阴", R.mipmap.type_one_cloudy);
            mSetting.putInt("多云", R.mipmap.weather_cloud_day);
            mSetting.putInt("少云", R.mipmap.type_one_cloudy);
            mSetting.putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            mSetting.putInt("小雨", R.mipmap.type_one_light_rain);
            mSetting.putInt("中雨", R.mipmap.type_one_light_rain);
            mSetting.putInt("大雨", R.mipmap.type_one_heavy_rain);
            mSetting.putInt("阵雨", R.mipmap.type_one_thunderstorm);
            mSetting.putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            mSetting.putInt("霾", R.mipmap.weather_haze);
            mSetting.putInt("雾", R.mipmap.weather_fog);
        } else {
            mSetting.putInt("未知", R.mipmap.none);
            mSetting.putInt("晴", R.mipmap.type_two_sunny);
            mSetting.putInt("阴", R.mipmap.type_two_cloudy);
            mSetting.putInt("多云", R.mipmap.type_two_cloudy);
            mSetting.putInt("少云", R.mipmap.type_two_cloudy);
            mSetting.putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            mSetting.putInt("小雨", R.mipmap.type_two_light_rain);
            mSetting.putInt("中雨", R.mipmap.type_two_rain);
            mSetting.putInt("大雨", R.mipmap.type_two_rain);
            mSetting.putInt("阵雨", R.mipmap.type_two_rain);
            mSetting.putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            mSetting.putInt("霾", R.mipmap.type_two_haze);
            mSetting.putInt("雾", R.mipmap.type_two_fog);
            mSetting.putInt("雨夹雪", R.mipmap.type_two_snowrain);
        }
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
                mErroImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(Weather weather) {
                //mProgressBar.setVisibility(View.GONE);
                mErroImageView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                collapsingToolbarLayout.setTitle(weather.basic.city);
                //mAdapter = new WeatherAdapter(MainActivity.this);
                mAdapter = new WeatherAdapter(MainActivity.this,weather);
                dealWithAdapter(mAdapter);
                weatherList.add(weather);
                mAdapter.add(weather);
                mAdapter.add(weather);
                mAdapter.add(weather);
                mAdapter.add(weather);
                PLog.e("7777",weather.suggestion.comf.toString());
                //mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(int position) {
                        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View dialogLayout = inflater.inflate(R.layout.weather_dialog, (ViewGroup) MainActivity.this.findViewById(
                                R.id.weather_dialog_root));
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setView(dialogLayout);
                        final AlertDialog alertDialog = builder.create();

                        RelativeLayout root = (RelativeLayout) dialogLayout.findViewById(R.id.weather_dialog_root);
                        switch (Util.getWeatherType(Integer.parseInt(weather.now.cond.code))) {
                            case "晴":
                                root.setBackgroundResource(R.mipmap.dialog_bg_sunny);
                                break;
                            case "阴":
                                root.setBackgroundResource(R.mipmap.dialog_bg_cloudy);
                                break;
                            case "雨":
                                root.setBackgroundResource(R.mipmap.dialog_bg_rainy);
                                break;
                            default:
                                break;
                    }


                        TextView city = (TextView) dialogLayout.findViewById(R.id.dialog_city);
                        city.setText(weather.basic.city);
                        TextView temp = (TextView) dialogLayout.findViewById(R.id.dialog_temp);
                        temp.setText(String.format("%s°", weather.now.tmp));
                        ImageView icon = (ImageView) dialogLayout.findViewById(R.id.dialog_icon);

                        Glide.with(MainActivity.this)
                                .load(mSetting.getInt(weather.now.cond.txt, R.mipmap.none))
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        icon.setImageBitmap(resource);
                                        icon.setColorFilter(Color.WHITE);
                                    }
                                });

                        alertDialog.show();
                    }
                } );
                normalStyleNotification(weather);
                startUpdataService(weather);
                showSnackbar(fab, "加载完毕，✺◟(∗❛ัᴗ❛ั∗)◞✺,");
            }
        };
        //fetchDataByCache(observer);
        //fetchDataByNetWork(observer);
    }

    /**
     * 从本地获取
     */
    public void fetchDataByCache(final Observer<Weather> observer) {

        Weather weather = null;
        try {
            weather = (Weather) aCache.getAsObject("WeatherData");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (weather != null) {
            //distinct()去除重复
            Observable.just(weather).distinct().subscribe(observer);
        } else {
            erroNetSnackbar(observer);
        }
    }

    private void erroNetSnackbar(final Observer<Weather> observer) {
        mErroImageView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        Snackbar.make(fab, "网络不好,~( ´•︵•` )~", Snackbar.LENGTH_INDEFINITE).setAction("重试", v -> {
            fetchDataByNetWork(observer);
        }).show();
    }

    /**
     * 从网络获取
     */
    public void fetchDataByNetWork(Observer<Weather> observer) {
        String cityName = mSetting.getString(Setting.CITY_NAME, "济南");
        if (cityName != null) {
            cityName = cityName.replace("市", "")
                .replace("省", "")
                .replace("自治区", "")
                .replace("特别行政区", "")
                .replace("地区", "")
                .replace("盟", "");
        }
        RetrofitSingleton.getApiService(this)
            .mWeatherAPI(cityName, Setting.KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok"))
            .map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
            .doOnNext(weather -> {
                aCache.put("WeatherData", weather,
                    (mSetting.getAutoUpdate() * Setting.ONE_HOUR));//默认一小时后缓存失效
            })
            .subscribe(observer);

    }

    private void showFabDialog() {
        materialDialog=new MaterialDialog(this);
        materialDialog.setTitle("喜欢").setMessage("这只是个喜欢按钮，并没有什么卵用୧(๑•̀⌄•́๑)૭✧")
                .setPositiveButton("退下", new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                materialDialog.dismiss();

                            }})
                .show();

    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_set:
                Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentSetting);
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_city:
                startActivityForResult(new Intent(this, ChoiceCityActivity.class), 1);
                //Intent intentCity = new Intent(MainActivity.this, ChoiceCityActivity.class);
                //intentCity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivityForResult(intentCity, 1);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //Snackbar.make(fab, "再按一次退出程序", Snackbar.LENGTH_SHORT).show();
                showSnackbar(fab, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
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
        mLocationOption.setInterval(mSetting.getAutoUpdate() * Setting.ONE_HOUR);
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
                mSetting.setCityName(aMapLocation.getCity());
                PLog.i(TAG, aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict() +
                    aMapLocation.getAdCode() + aMapLocation.getCityCode());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                PLog.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" +
                   aMapLocation.getErrorInfo());
                //Snackbar.make(fab, "定位失败,请尝试手动更新", Snackbar.LENGTH_LONG).show();
                showSnackbar(fab, "定位失败,加载默认城市", true);
            }
            fetchDataByNetWork(observer);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode标示请求的标示   resultCode表示有数据
        if (requestCode == 1 && resultCode == 2) {
           mRecyclerView.setRefreshing(true);
            mSetting.putString(Setting.CITY_NAME, data.getStringExtra(Setting.CITY_NAME));
            fetchDataByNetWork(observer);
        }
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
            PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        Notification notification = builder.setContentIntent(pendingIntent)
            .setContentTitle(weather.basic.city)
            .setContentText(weather.now.cond.txt + " 当前温度: " + weather.now.tmp + "℃")
            // 这里部分 ROM 无法成功
            .setSmallIcon(mSetting.getInt(weather.now.cond.txt, R.mipmap.none))
            .build();
        notification.flags = mSetting.getNotificationModel();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
    private void startUpdataService(Weather weather ) {
        awm= AppWidgetManager.getInstance(this);
                // 激活主键--通讯
                ComponentName provider=new ComponentName(MainActivity.this, WidgetProviderWeek.class);
                RemoteViews views=new RemoteViews(getPackageName(), R.layout.widget_week);
                //动作
                Intent intent=new Intent(MainActivity.this,MainActivity.class);
                //延迟意图
                PendingIntent pendingIntent=PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                views.setOnClickPendingIntent(R.id.widget_week_button, pendingIntent);
                views.setTextViewText(R.id.widget_week_week_1,weather.basic.city);
                try {
                    views.setTextViewText(R.id.widget_week_week_2, "明天");
                    views.setTextViewText(R.id.widget_week_week_3,Util.dayForWeek(weather.dailyForecast.get(2).date));
                    views.setTextViewText(R.id.widget_week_week_4,Util.dayForWeek(weather.dailyForecast.get(3).date));
                    views.setTextViewText(R.id.widget_week_week_5,Util.dayForWeek(weather.dailyForecast.get(4).date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                views.setTextViewText(R.id.widget_week_temp_1,weather.dailyForecast.get(0).tmp.min+"°/"+weather.dailyForecast.get(0).tmp.max+ "°");
                views.setTextViewText(R.id.widget_week_temp_2,weather.dailyForecast.get(1).tmp.min+"°/"+weather.dailyForecast.get(1).tmp.max+ "°");
                views.setTextViewText(R.id.widget_week_temp_3,weather.dailyForecast.get(2).tmp.min+"°/"+weather.dailyForecast.get(2).tmp.max+ "°");
                views.setTextViewText(R.id.widget_week_temp_4,weather.dailyForecast.get(3).tmp.min+"°/"+weather.dailyForecast.get(3).tmp.max+ "°");
                views.setTextViewText(R.id.widget_week_temp_5,weather.dailyForecast.get(4).tmp.min+"°/"+weather.dailyForecast.get(4).tmp.max+ "°");
                views.setImageViewResource(R.id.widget_week_image_1,mSetting.getInt(weather.now.cond.txt, R.mipmap.none));
                views.setImageViewResource(R.id.widget_week_image_2,mSetting.getInt(weather.dailyForecast.get(1).cond.txtD, R.mipmap.none));
                views.setImageViewResource(R.id.widget_week_image_3,mSetting.getInt(weather.dailyForecast.get(2).cond.txtD, R.mipmap.none));
                views.setImageViewResource(R.id.widget_week_image_4,mSetting.getInt(weather.dailyForecast.get(3).cond.txtD, R.mipmap.none));
                views.setImageViewResource(R.id.widget_week_image_5,mSetting.getInt(weather.dailyForecast.get(4).cond.txtD, R.mipmap.none));
                awm.updateAppWidget(provider, views);


    }


    @Override
    public void onRefresh() {
        mRecyclerView.postDelayed(() -> fetchDataByNetWork(observer), 1000);
    }
}
