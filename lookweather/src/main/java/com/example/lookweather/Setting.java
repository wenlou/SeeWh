package com.example.lookweather;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sxj52 on 2016/4/29.
 * 相关设置
 */
public class Setting {
    /**
     * The constant CHANGE_ICONS.
     */
    public static final String CHANGE_ICONS = "change_icons";//切换图标
    /**
     * The constant HOUR.
     */
    public static final String HOUR = "current_hour";//当前小时
    /**
     * The constant NOTIFICATION_MODEL.
     */
    public static final String NOTIFICATION_MODEL = "notification_model";
    /**
     * The constant CITY_NAME.
     */
    public static final String CITY_NAME = "城市";//选择城市
    /**
     * The constant AUTO_UPDATE.
     */
    public static final String AUTO_UPDATE = "change_update_time"; //自动更新时长

    /**
     * The constant KEY.
     */
    public static final String KEY = "e2faff6b359440c288e7fb675df53d22";// 和风天气 key

    /**
     * The constant ONE_HOUR.
     */
    public static int ONE_HOUR = 1000 * 60 * 60;

    private static Setting sInstance;

    private SharedPreferences mPrefs;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Setting getInstance() {
        if (sInstance == null) {
            sInstance = new Setting(BaseApplication.mAppContext);
        }
        return sInstance;
    }

    private Setting(Context context) {
        mPrefs = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        //mPrefs.edit().putInt(CHANGE_ICONS, 1).apply();
    }


    /**
     * Put int setting.
     *
     * @param key   the key
     * @param value the value
     * @return the setting
     */
    public Setting putInt(String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
        return this;
    }

    /**
     * Gets int.
     *
     * @param key      the key
     * @param defValue the def value
     * @return the int
     */
    public int getInt(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }

    /**
     * Put string setting.
     *
     * @param key   the key
     * @param value the value
     * @return the setting
     */
    public Setting putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
        return this;
    }

    /**
     * Gets icon type.
     *
     * @return the icon type
     */
    public int getIconType() {
        return mPrefs.getInt(CHANGE_ICONS, 0);
    }

    /**
     * Gets string.
     *
     * @param key      the key
     * @param defValue the def value
     * @return the string
     */
    public String getString(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }


    /**
     * Set current hour.
     *
     * @param h the h
     */
// 设置当前小时
    public void setCurrentHour(int h){
        mPrefs.edit().putInt(HOUR,h).apply();
    }

    /**
     * Get current hour int.
     *
     * @return the int
     */
    public int getCurrentHour(){
        return mPrefs.getInt(HOUR,0);
    }

//    // 图标种类相关
//    public void setIconType(int type) {
//        mPrefs.edit().putInt(CHANGE_ICONS, type).apply();
//    }
//
//    public int getIconType() {
//        return mPrefs.getInt(CHANGE_ICONS, 0);
//    }
//
    // 自动更新时间 hours
//    public void setAutoUpdate(int t) {
//        mPrefs.edit().putInt(AUTO_UPDATE, t).apply();
//    }
//
//    public int getAutoUpdate() {
//        return mPrefs.getInt(AUTO_UPDATE, 3);
//    }

    /**
     * Sets city name.
     *
     * @param name the name
     */
//当前城市
    public void setCityName(String name) {
        mPrefs.edit().putString(CITY_NAME, name).apply();
    }

    /**
     * Gets city name.
     *
     * @return the city name
     */
    public String getCityName() {return mPrefs.getString(CITY_NAME, "北京");}

    /**
     * Sets notification model.
     *
     * @param t the t
     */
//  通知栏模式 默认为常驻
    public void setNotificationModel(int t) {
        mPrefs.edit().putInt(NOTIFICATION_MODEL, t).apply();
    }

    /**
     * Gets notification model.
     *
     * @return the notification model
     */
    public int getNotificationModel() {
        return mPrefs.getInt(NOTIFICATION_MODEL, Notification.FLAG_AUTO_CANCEL);
    }
}
