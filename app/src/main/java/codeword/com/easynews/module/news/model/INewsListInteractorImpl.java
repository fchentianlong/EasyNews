package codeword.com.easynews.module.news.model;

import java.util.List;
import java.util.Map;

import codeword.com.easynews.bean.NeteastNewsSummary;
import codeword.com.easynews.callback.RequestCallBack;
import codeword.com.easynews.http.Api;
import codeword.com.easynews.http.HostType;
import codeword.com.easynews.http.manager.RetrofitManager;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Administrator on 2016/3/21.
 */
public class INewsListInteractorImpl implements INewsListInteractor<List<NeteastNewsSummary>>{
    @Override
    public Subscription requestNewsList(RequestCallBack<List<NeteastNewsSummary>> callBack, String newsType, String newsId, int startId) {
        return RetrofitManager.builder(HostType.NETEASE_NEWS_VIDEO).getNewsListObservable(newsType, newsId, startId)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        callBack.beforeRequest();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())// 订阅之前操作在主线程
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callBack.requestError(throwable.toString());
                    }
                }).flatMap(new Func1<Map<String, List<NeteastNewsSummary>>, Observable<NeteastNewsSummary>>() {
                    @Override
                    public Observable<NeteastNewsSummary> call(Map<String, List<NeteastNewsSummary>> stringListMap) {
                        if (newsId.equals(Api.HOUSE_ID)) {
                            return Observable.from(stringListMap.get("北京"));
                        }
                        return Observable.from(stringListMap.get(newsId));
                    }
                }).toSortedList(new Func2<NeteastNewsSummary, NeteastNewsSummary, Integer>() {
                    @Override
                    public Integer call(NeteastNewsSummary neteastNewsSummary, NeteastNewsSummary neteastNewsSummary2) {
                        return neteastNewsSummary.ptime.compareTo(neteastNewsSummary2.ptime);
                    }
                }).subscribe(new Subscriber<List<NeteastNewsSummary>>() {
                    @Override
                    public void onCompleted() {
                        callBack.requestComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.requestError(e.getLocalizedMessage().toString());
                    }

                    @Override
                    public void onNext(List<NeteastNewsSummary> neteastNewsSummaries) {
                        callBack.requestSuccess(neteastNewsSummaries);
                    }
                });
    }
}
