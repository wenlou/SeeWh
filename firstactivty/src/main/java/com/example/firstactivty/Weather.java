package com.example.firstactivty;


/* 天气情况类
        */
public class Weather{

    /**
     * 城市
     */
    String city;
    /**
     * 日期
     */
    String date;
    /**
     * 温度
     */
    String temperature;
    /**
     * 风向
     */
    String direction;
    /**
     * 风力
     */
    String power;
    /**
     * 天气状况
     */
    String status;

    public static Weather objectFromData(String str) {

        return new com.google.gson.Gson().fromJson(str, Weather.class);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("城市:" + city + "\r\n");
        builder.append("日期:" + date + "\r\n");
        builder.append("天气状况:" + status + "\r\n");
        builder.append("温度:" + temperature + "\r\n");
        builder.append("风向:" + direction + "\r\n");
        builder.append("风力:" + power + "\r\n");
        return builder.toString();
    }
}
