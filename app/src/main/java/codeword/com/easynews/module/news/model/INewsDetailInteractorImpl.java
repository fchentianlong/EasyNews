package codeword.com.easynews.module.news.model;

import java.util.Map;

import codeword.com.easynews.bean.NeteastNewsDetail;
import codeword.com.easynews.bean.NeteastNewsSummary;
import codeword.com.easynews.callback.RequestCallBack;
import codeword.com.easynews.http.HostType;
import codeword.com.easynews.http.manager.RetrofitManager;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/25.
 */
public class INewsDetailInteractorImpl implements INewsDetailInteractor<NeteastNewsDetail>{
    @Override
    public Subscription requestNewsDetail(RequestCallBack<NeteastNewsDetail> callBack, String id) {
        return RetrofitManager.builder(HostType.NETEASE_NEWS_VIDEO).getNewsDetailObservable(id)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        callBack.beforeRequest();
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<Map<String, NeteastNewsDetail>, NeteastNewsDetail>() {
                    @Override
                    public NeteastNewsDetail call(Map<String, NeteastNewsDetail> stringNeteastNewsDetailMap) {
                        return stringNeteastNewsDetailMap.get(id);
                    }
                })
                .subscribe(new Subscriber<NeteastNewsDetail>() {
                    @Override
                    public void onCompleted() {
                        callBack.requestComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.requestError(e.getLocalizedMessage().toString());
                    }

                    @Override
                    public void onNext(NeteastNewsDetail neteastNewsDetail) {
                        callBack.requestSuccess(neteastNewsDetail);
                    }
                });
    }
}
