package com.xiecc.seeWeather.component;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by sxj52 on 2016/5/27.
 */
public interface GankService {
    /**
     分类数据: http://gank.io/api/data/数据类型/请求个数/第几页
    数据类型： 福利 | Android | iOS | 休息视频 | 拓展资源 | 前端 | all
    请求个数： 数字，大于0
    第几页：数字，大于0
    例：
    http://gank.io/api/data/Android/10/1
    http://gank.io/api/data/福利/10/1
    http://gank.io/api/data/iOS/20/2
    http://gank.io/api/data/all/20/2
    */
   @GET("api/data/{type}/{count}/{page}")
   Observable<GanHuo> getGanHuo(
           @Path("type") String type,
           @Path("count") int count,
           @Path("page") int page
   );
}
