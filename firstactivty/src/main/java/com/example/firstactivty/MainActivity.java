package com.example.firstactivty;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    @butterknife.Bind(R.id.city)
    EditText mCity;
    @butterknife.Bind(R.id.query)
    TextView mQuery;
    @butterknife.Bind(R.id.weather)
    TextView mWeather;
    private static final String WEATHRE_API_URL="http://php.weather.sina.com.cn/xml.php?city=%s&password=DJOYnieT8234jlsK&day=0";

    private Subscription mSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butterknife.ButterKnife.bind(this);
    }

      @butterknife.OnClick(R.id.query)
    public void onClick() {
          mWeather.setText("");
          String city=mCity.getText().toString();
          if(TextUtils.isEmpty(city)){
              Snackbar.make(mWeather,"城市不能为空",Snackbar.LENGTH_SHORT).show();
              return;
          }
          obseverAsNomul(city);
    }
    private void obseverAsNomul(String city){
        mSubscription= Observable.create(new Observable.OnSubscribe<Weather>(){
            @Override
            public void call(Subscriber<? super Weather> subscriber) {
                //1.如果已经取消订阅，则直接退出
                if(subscriber.isUnsubscribed()){
                    return;
                }
                try {
                    //2.开网络连接请求获取天气预报，返回结果是xml格式
                    String weatherxml=getWeather(city);
                    //3.解析xml格式，返回weather实例
                    Weather weather=parseWeather(weatherxml);
                    //4.发布事件通知订阅者
                    subscriber.onNext(weather);
                    //5.事件通知完成
                    subscriber.onCompleted();
                } catch (Exception e) {
                    //6.出现异常，通知订阅者
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.newThread()).//让Observable运行在新线程中
                observeOn(AndroidSchedulers.mainThread()). //让subscriber运行在主线程中
                subscribe(new Subscriber<Weather>() {
            @Override
            public void onCompleted() {
                //对应上面的第5点：subscriber.onCompleted();
                //这里写事件发布完成后的处理逻辑
            }

            @Override
            public void onError(Throwable e) {
                //对应上面的第6点：subscriber.onError(e);
                //这里写出现异常后的处理逻辑
                Snackbar.make(mWeather,"出了错",Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Weather weather) {
                //对应上面的第4点：subscriber.onNext(weather);
                //这里写获取到某一个事件通知后的处理逻辑
                if(weather!=null){
                    mWeather.setText(weather.toString());
                }
            }
        });
    }
    private void obseverAsMap(String city){
        mSubscription=Observable.create(new Observable.OnSubscribe<Weather>() {
            @Override
            public void call(Subscriber<? super Weather> subscriber) {
                if(mSubscription.isUnsubscribed()){
                    return;
                }
                try {
                    String Weatherxml=getWeather(city);
                    Weather weather=parseWeather(Weatherxml);
                    subscriber.onNext(weather);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                            Snackbar.make(mWeather,"",Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        if(weather!=null){
                            mWeather.setText(weather.toString());
                        }
                    }
                });
    }
    private void obseverAsLamada(String city){
        mSubscription=Observable.create(subscriber -> {
            if(mSubscription.isUnsubscribed()){
                return;
            }
            try {
                String weatherxml=getWeather(city);
                Weather weather=parseWeather(weatherxml);
                subscriber.onNext(weather);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }

        }).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(
                weather->{
                    if(weather != null)
                        mWeather.setText(weather.toString());
                },e->{
                    Snackbar.make(mWeather,"出了错",Snackbar.LENGTH_SHORT).show();
                }
        );


    }
    @Override
    protected void onDestroy() {
        //取消订阅
        if(mSubscription != null && !mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v == mWeather && event.getAction() == MotionEvent.ACTION_DOWN){
            //隐藏软键盘
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = getCurrentFocus();
            if(focusedView!=null && focusedView.getWindowToken()!=null){
                manager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }
        }
        return true;
    }
    private Weather parseWeather(String weatherxml){
        StringReader stringReader=new StringReader(weatherxml);
        Weather weather=null;
        XmlPullParser xmlPullParser= Xml.newPullParser();
        try {
            xmlPullParser.setInput(stringReader);
            int evenType=xmlPullParser.getEventType();
            while(evenType!=xmlPullParser.END_DOCUMENT){
                switch (evenType){
                    case XmlPullParser.START_DOCUMENT:
                        weather=new Weather();
                        break;
                    case XmlPullParser.START_TAG:
                        String nodeName = xmlPullParser.getName();
                        if("city".equals(nodeName)){
                            weather.city=xmlPullParser.nextText();
                        }else if("savedate_weather".equals(nodeName)){
                            weather.date = xmlPullParser.nextText();
                        } else if("temperature1".equals(nodeName)) {
                            weather.temperature = xmlPullParser.nextText();
                        } else if("temperature2".equals(nodeName)){
                            weather.temperature += "-" + xmlPullParser.nextText();
                        } else if("direction1".equals(nodeName)){
                            weather.direction = xmlPullParser.nextText();
                        } else if("power1".equals(nodeName)){
                            weather.power = xmlPullParser.nextText();
                        } else if("status1".equals(nodeName)){
                            weather.status = xmlPullParser.nextText();
                        }
                        break;
                }
                evenType=xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
        finally {
            stringReader.close();
        }
        return weather;
    }
    private String getWeather(String city)throws Exception{
        BufferedReader reader = null;
        HttpURLConnection connection=null;
        try {
            String urlString = String.format(WEATHRE_API_URL, URLEncoder.encode(city, "GBK"));
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            //连接
            connection.connect();
            //处理返回结果
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuffer buffer = new StringBuffer();
            String line="";
            while(!TextUtils.isEmpty(line = reader.readLine()))
                buffer.append(line);
            return buffer.toString();
        } finally {
            if(connection != null){
                connection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
