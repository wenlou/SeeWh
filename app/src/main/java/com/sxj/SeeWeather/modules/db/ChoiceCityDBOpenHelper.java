package com.sxj.SeeWeather.modules.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sxj52 on 2016/7/31.
 */
public class ChoiceCityDBOpenHelper extends SQLiteOpenHelper{
    public ChoiceCityDBOpenHelper(Context context) {
        super(context, "choicecity.db", null, 1);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table choicecity(_id integer primary key,cityname varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
