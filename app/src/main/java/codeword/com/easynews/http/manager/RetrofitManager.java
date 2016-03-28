package codeword.com.easynews.http.manager;

import android.support.annotation.NonNull;

import com.socks.library.KLog;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import codeword.com.easynews.app.App;
import codeword.com.easynews.bean.NeteastNewsDetail;
import codeword.com.easynews.bean.NeteastNewsSummary;
import codeword.com.easynews.bean.NeteastVideoSummary;
import codeword.com.easynews.bean.SinaPhotoDetail;
import codeword.com.easynews.bean.SinaPhotoList;
import codeword.com.easynews.http.Api;
import codeword.com.easynews.http.HostType;
import codeword.com.easynews.http.service.ApiService;
import codeword.com.easynews.utils.NetUtil;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/8.
 */
public class RetrofitManager {
    //设缓存有效期为两天
    protected static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    protected static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    protected static final String CACHE_CONTROL_NETWORK = "max-age=0";

    private final ApiService mApiService;
    private static OkHttpClient mOkHttpClient;


    /**
     * 创建实例
     *
     * @param hostType host类型
     * @return 实例
     */
    public static RetrofitManager builder(HostType hostType){
        return new RetrofitManager(hostType);
    }

    /**
     *
     * @param hostType
     */
    private RetrofitManager(HostType hostType){
        //初始化httpclient
        initOkHttpClient();

        Retrofit retrofit=new Retrofit.Builder().baseUrl(getHost(hostType)).client(mOkHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        mApiService=retrofit.create(ApiService.class);
    }

    private void initOkHttpClient(){
        if (mOkHttpClient==null){
            synchronized (RetrofitManager.class){
                if (mOkHttpClient==null){
                    KLog.e("初始化mOkHttpClient");
                    // 因为BaseUrl不同所以这里Retrofit不为静态，但是OkHttpClient配置是一样的,静态创建一次即可
                    File cacheFile=new File(App.getContext().getCacheDir(),"HttpCache");//缓存地址
                    Cache cache=new Cache(cacheFile,1024 * 1024 * 100);// 指定缓存大小100Mb
                    // 云端响应头拦截器，用来配置缓存策略
                    Interceptor interceptor=chain->{
                        Request request=chain.request();
                        if (!NetUtil.isConnected(App.getContext())){
                            request=request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                            KLog.e("no network");
                        }
                        Response originalResponse=chain.proceed(request);
                        if (NetUtil.isConnected(App.getContext())){
                            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                            String cacheControl=request.cacheControl().toString();
                            return originalResponse.newBuilder().header("Cache-Control",cacheControl)
                                    .removeHeader("Pragma").build();
                        }else {
                            return originalResponse.newBuilder().header("Cache-Control",
                                    "public, only-if-cached," + CACHE_STALE_SEC)
                                    .removeHeader("Pragma").build();
                        }
                    };

                    mOkHttpClient=new OkHttpClient.Builder().cache(cache)
                            .addNetworkInterceptor(interceptor)
                            .addInterceptor(interceptor)
                            .connectTimeout(30, TimeUnit.SECONDS).build();
                }
            }
        }
    }


    /**
     * 网易新闻列表 例子：http://c.m.163.com/nc/article/headline/T1348647909107/0-20.html
     * <p/>
     * 对API调用了observeOn(MainThread)之后，线程会跑在主线程上，包括onComplete也是，
     * unsubscribe也在主线程，然后如果这时候调用call.cancel会导致NetworkOnMainThreadException
     * 加一句unsubscribeOn(io)
     *
     * @param type      新闻类别：headline为头条,list为其他
     * @param id        新闻类别id
     * @param startPage 开始的页码
     * @return 被观察对象
     */
    public Observable<Map<String,List<NeteastNewsSummary>>> getNewsListObservable(String type,String id,int startPage){
        return mApiService.getNewsList(getCacheControl(),type,id,startPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 网易新闻详情：例子：http://c.m.163.com/nc/article/BG6CGA9M00264N2N/full.html
     *
     * @param id 新闻详情的id
     * @return 被观察对象
     */

    public Observable<Map<String,NeteastNewsDetail>> getNewsDetailObservable(String id){
        return mApiService.getNewsDetail(getCacheControl(),id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }



    /**
     * 新浪图片新闻列表 例子：http://api.sina.cn/sinago/list.json?channel=hdpic_pretty&adid=4ad30dabe134695c3b7c3a65977d7e72&wm=b207&from=6042095012&chwm=12050_0001&oldchwm=12050_0001&imei=867064013906290&uid=802909da86d9f5fc&p=1
     *
     * @param page 页码
     * @return 被观察对象
     */

    public Observable<SinaPhotoList> getSinaPhotoListObservable(String photoTypeId, int page) {
        KLog.e("新浪图片新闻列表: " + photoTypeId + ";" + page);
        return mApiService.getSinaPhotoList(getCacheControl(), photoTypeId,
                "4ad30dabe134695c3b7c3a65977d7e72", "b207", "6042095012", "12050_0001",
                "12050_0001", "867064013906290", "802909da86d9f5fc", page)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }


    /**
     * 新浪图片详情 例子：http://api.sina.cn/sinago/article.json?postt=hdpic_hdpic_toutiao_4&wm=b207&from=6042095012&chwm=12050_0001&oldchwm=12050_0001&imei=867064013906290&uid=802909da86d9f5fc&id=20550-66955-hdpic
     *
     * @param id 图片的id
     * @return 被观察者
     */
    public Observable<SinaPhotoDetail> getSinaPhotoDetailObservable(String id) {
        return mApiService.getSinaPhotoDetail(getCacheControl(), Api.SINA_PHOTO_DETAIL_ID, "b207",
                "6042095012", "12050_0001", "12050_0001", "867064013906290", "802909da86d9f5fc", id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }


    /**
     * 网易视频列表 例子：http://c.m.163.com/nc/video/list/V9LG4B3A0/n/0-10.html
     *
     * @param videoId        视频类别id
     * @param startPage 开始的页码
     * @return 被观察者
     */

     public Observable<Map<String,List<NeteastVideoSummary>>> getVideoListObservable(String videoId,int startPage){
         return mApiService.getVideoList(getCacheControl(),videoId,startPage)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .unsubscribeOn(Schedulers.io());
     }



    /**
     * 根据网络状况获取缓存的策略
     *
     * @return
     */
    @NonNull
    private String getCacheControl() {
        return NetUtil.isConnected(App.getContext()) ? CACHE_CONTROL_NETWORK : CACHE_CONTROL_CACHE;
    }

    /**
     * 获取对应的host
     *
     * @param hostType host类型
     * @return host
     */
    private String getHost(HostType hostType) {
        switch (hostType) {
            case NETEASE_NEWS_VIDEO:
                return Api.NETEAST_HOST;
            case SINANEWS_PHOTO:
                return Api.SINA_PHOTO_HOST;
            case WEATHER_INFO:
                return Api.WEATHER_HOST;
        }
        return "";
    }
}
