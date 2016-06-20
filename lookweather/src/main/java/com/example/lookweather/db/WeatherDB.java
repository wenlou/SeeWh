package com.example.lookweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lookweather.City;
import com.example.lookweather.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugo on 2015/9/30 0030.
 * 封装数据库操作
 */
public class WeatherDB {

    private Context context;

    /**
     * Instantiates a new Weather db.
     *
     * @param context the context
     */
    public WeatherDB(Context context) {
        this.context = context;
    }

    /**
     * Load provinces list.
     *
     * @param db the db
     * @return the list
     */
    public List<com.example.lookweather.Province> loadProvinces(SQLiteDatabase db) {

        List<Province> list = new ArrayList<>();

        Cursor cursor = db.query("T_Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            do {
                Province province = new Province();
                province.ProSort = cursor.getInt(cursor.getColumnIndex("ProSort"));
                province.ProName = cursor.getString(cursor.getColumnIndex("ProName"));
                list.add(province);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Load cities list.
     *
     * @param db    the db
     * @param ProID the pro id
     * @return the list
     */
    public List<com.example.lookweather.City> loadCities(SQLiteDatabase db, int ProID) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("T_City", null, "ProID = ?", new String[] { String.valueOf(ProID) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.CityName = cursor.getString(cursor.getColumnIndex("CityName"));
                city.ProID = ProID;
                city.CitySort = cursor.getInt(cursor.getColumnIndex("CitySort"));
                list.add(city);
                //city.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
                //city.setProID(ProID);
                //list.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return list;
    }
}
