package com.xiecc.seeWeather.modules.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by sxj52 on 2016/7/31.
 */
public class ChoiceCityDao {
    ChoiceCityDBOpenHelper mHelper;
    public ChoiceCityDao(Context context){
            mHelper=new ChoiceCityDBOpenHelper(context);
    }
    public void add(int id,String cityname){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cityname", cityname);
        values.put("_id",id);
        db.insert("choicecity", null, values);
        db.close();
    }
    public void delete(String cityname){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete("choicecity", "cityname=?", new String[] { cityname });
        db.close();
    }
    public ArrayList<String> queryMode() {
        ArrayList<String> result=new ArrayList<String>();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select cityname from choicecity",null);
        while (cursor.moveToNext()){
            String cityname=cursor.getString(0);
            result.add(cityname);
        }
        db.close();
        return result;
    }
}
