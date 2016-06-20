package com.example.lookweather;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by sxj52 on 2016/4/29.
 */
public class BaseApplication extends Application {
    /**
     * The constant cacheDir.
     */
    public static String cacheDir = "";
    /**
     * The constant mAppContext.
     */
    public static Context mAppContext = null;


    @Override public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        // 初始化 retrofit
        RetrofitSingleton.init(getApplicationContext());
        //CrashHandler.init(new CrashHandler(getApplicationContext()));
        CrashReport.initCrashReport(getApplicationContext(), "900028220", false);

        //Thread.setDefaultUncaughtExceptionHandler(new MyUnCaughtExceptionHandler());

        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */

        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();

        }
        else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else {
            return false;
        }
    }
}
