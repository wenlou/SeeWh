# 看看天气

---- 
### 简介
就看天气——是一款遵循**Material Design**风格的只看天气的APP。无流氓权限，无自启，xxx，用最少的权限做最优的体验。
- 卡片展现（当前天气情况，未来几小时天气情况，生活建议，一周七天概况）
- 补全城市（第一版本因为自己偷懒所以城市有缺陷对不起各位）
- 自动定位
- 缓存数据，减少网络请求，保证离线查看
- 内置两套图标（设置里更改）
- 彩蛋（自动夜间状态）


---- 

权限说明

```
	<!--用于进行网络定位-->
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	<!--用于访问GPS定位-->
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
	<!--获取运营商信息，用于支持提供运营商信息相关的接口-->
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	<!--这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
	<!--用于访问网络，网络定位需要上网-->
	<uses-permission android:name="android.permission.INTERNET"/>
	<!--用于读取手机当前的状态-->
	<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
	<!--写入扩展存储，向扩展卡写入数据，用于写入缓存定位数据-->
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```





---- 

### 项目
#### 公开 API

天气数据来源于：和风天气

城市信息来源于：CSDN

地理定位服务： 高德地图

#### 开源技术
1. [Rxjava][2]
2. [RxAndroid][3]
3. [Retrofit][4]
4. [GLide][5]
5. [ASimpleCache][6]

#### 简单介绍代码

##### 网络
就看天气的网络部分的支持是用`RxJava+RxAndroid+Retrofit+Gson`再加上`ACache`缓存
```
   /**
	 * <p/>
	 * 首先从本地缓存获取数据
	 * if 有
	 * 更新UI
	 * else
	 * 直接进行网络请求，更新UI并保存在本地
	 */
	private void fetchData() {
	    observer = new Observer<Weather>() {
	                    //节约篇幅，已省略
	                    ...
	    };
	
	    fetchDataByCache(observer);
	}
	
	
	/**
	 * 从本地获取
	 */
	private void fetchDataByCache(Observer<Weather> observer) {
	    Weather weather = null;
	    try {
	        weather = (Weather) aCache.getAsObject("WeatherData");
	    } catch (Exception e) {
	        Log.e(TAG, e.toString());
	    }
	    if (weather != null) {
	    //distinct去重
	        Observable.just(weather).distinct().subscribe(observer);
	    } else {
	        fetchDataByNetWork(observer);
	    }
	}
	
	
	/**
	 * 从网络获取
	 */
	private void fetchDataByNetWork(Observer<Weather> observer) {
	    String cityName = mSetting.getString(Setting.CITY_NAME, "重庆");
	    RetrofitSingleton.getApiService(this)
	                     .mWeatherAPI(cityName, key)
	                     .subscribeOn(Schedulers.io())
	                     .observeOn(AndroidSchedulers.mainThread())
	                    ////节约篇幅，已省略
	                    ...
	}
```
##### RecycerVIew展示
就像洪洋说的一样
> 整体上看RecyclerView架构，提供了一种插拔式的体验，高度的解耦，异常的灵活，通过设置它提供的不同LayoutManager，ItemDecoration , ItemAnimator实现令人瞠目的效果。

该项目中用到RecyclerView中级的用法是根据itemType展示不同的布局，这就是主页核心的代码了。
```
@Override public int getItemViewType(int position) {
	    if (position == TYPE_ONE) {
	    //标识
	        ...
	    }
	    return super.getItemViewType(position);
	}

@Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	    if (viewType == TYPE_ONE) {
	    //绑定
	        ...
	        }
	    }
   }

@Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

	    if (holder instanceof NowWeatherViewHolder) {
	    //更新布局
	    ....
	    }
}

```



---- 


