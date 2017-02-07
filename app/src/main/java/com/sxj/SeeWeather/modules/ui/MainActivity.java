package com.sxj.SeeWeather.modules.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sxj.SeeWeather.R;
import com.sxj.SeeWeather.base.BaseActivity;
import com.sxj.SeeWeather.common.CircularAnimUtil;
import com.sxj.SeeWeather.common.PLog;
import com.sxj.SeeWeather.common.Util;
import com.sxj.SeeWeather.modules.domain.Weather;
import com.sxj.SeeWeather.modules.receiver.WidgetProviderWeek;
import com.sxj.SeeWeather.modules.service.AutoUpdateService;
import com.sxj.SeeWeather.modules.ui.about.AboutActivity;
import com.sxj.SeeWeather.modules.ui.setting.CityFrgment;
import com.sxj.SeeWeather.modules.ui.setting.Setting;
import com.sxj.SeeWeather.modules.ui.setting.SettingActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * The type Main activity.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener
    {
    private AppWidgetManager awm;

    private final String TAG = MainActivity.class.getSimpleName();

    private CollapsingToolbarLayout collapsingToolbarLayout;
    //@Bind(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    //private ImageView bannner;
    private LinearLayout noWIFILayout;
    private ImageView mErroImageView;
    private ImageView bannner;
    private RelativeLayout headerBackground;
        private TabLayout tabLayout;
        private ViewPager viewPager;
        private List<Fragment> fragments;
        private String[] titles = {"主界面","多城市"};


    private long exitTime = 0; ////记录第一次点击的时间




    //private boolean isLoaction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PLog.i(TAG,"onCreate");
        //ButterKnife.bind(this);
        initView();
        initDrawer();
        initIcon();
        startService(new Intent(this, AutoUpdateService.class));
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

    @Override
    protected void onStart() {
        initIcon();
        super.onStart();

    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == 2) {
                mSetting.putString(Setting.CITY_NAME, data.getStringExtra(Setting.CITY_NAME));
                //toolbar.setTitle(mSetting.getCityName());
                collapsingToolbarLayout.setTitle(mSetting.getCityName());
                PLog.e("7777",mSetting.getCityName());
            }
        }

        @Override
        protected void onRestart() {
            super.onRestart();
            initIcon();

        }

        /**
     * 初始化基础View
     */
    private void initView() {
        collapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        bannner= (ImageView) findViewById(R.id.banner);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mErroImageView= (ImageView) findViewById(R.id.iv_erro);
        fragments=new ArrayList<>() ;
        fragments.add(MainFragment.getInstance(titles[0]));
        fragments.add(CityFrgment.getInstance(titles[1]));
        viewPager= (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        });

        //TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tabLayout.setupWithViewPager(viewPager);
        toolbar.setTitle(mSetting.getCityName());
        setSupportActionBar(toolbar);

        //彩蛋-夜间模式
        Calendar calendar = Calendar.getInstance();

        //mSetting.putInt(Setting.HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        mSetting.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));

        setStatusBarColorForKitkat(R.color.colorSunrise);
        if (mSetting.getCurrentHour() < 6 || mSetting.getCurrentHour() > 18) {
             Glide.with(this).load(R.mipmap.sun_main).diskCacheStrategy(DiskCacheStrategy.ALL).into(bannner);
//             collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColorForKitkat(R.color.colorSunset);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    fab.setImageResource(R.drawable.ic_add_24dp);
                    fab.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, ChoiceCityActivity.class);
                            intent.putExtra(mSetting.MULTI_CHECK, true);
                            CircularAnimUtil.startActivity(MainActivity.this, intent, fab,
                                    R.color.colorPrimary);
                        }
                    });
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_24dp);
                    fab.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFabDialog();
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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
            drawer.addDrawerListener(toggle);
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
            mSetting.putInt("雨夹雪", R.mipmap.type_two_snowrain);
            mSetting.putInt("小雪", R.mipmap.type_two_snowrain);
            mSetting.putInt("中雪", R.mipmap.type_two_snowrain);
            mSetting.putInt("大雪雪", R.mipmap.type_two_snowrain);
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
            mSetting.putInt("小雪", R.mipmap.type_two_snowrain);
            mSetting.putInt("中雪", R.mipmap.type_two_snowrain);
            mSetting.putInt("大雪", R.mipmap.type_two_snowrain);

        }
    }


    private void showFabDialog() {
        new AlertDialog.Builder(MainActivity.this).setTitle("点赞")
                .setMessage("去项目地址给作者个Star，鼓励下作者୧(๑•̀⌄•́๑)૭✧")
                .setPositiveButton("好叻", (dialog, which) -> {
                    Uri uri = Uri.parse(getString(R.string.app_html));   //指定网址
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);           //指定Action
                    intent.setData(uri);                            //设置Uri
                    MainActivity.this.startActivity(intent);        //启动Activity
                })
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


    public void normalStyleNotification(Weather weather) {
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
    public void startUpdataService(Weather weather ) {
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



}
