package codeword.com.easynews.module.video.model;

import com.socks.library.KLog;

import java.util.List;
import java.util.Map;

import codeword.com.easynews.bean.NeteastVideoSummary;
import codeword.com.easynews.callback.RequestCallBack;
import codeword.com.easynews.http.HostType;
import codeword.com.easynews.http.manager.RetrofitManager;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Administrator on 2016/3/8.
 */
public class IVideoListInteractorImpl implements IVideoListInteractor<List<NeteastVideoSummary>>{
    @Override
    public Subscription requestVideoList(String videoId, int startPage, RequestCallBack<List<NeteastVideoSummary>> callback) {
        return RetrofitManager.builder(HostType.NETEASE_NEWS_VIDEO).getVideoListObservable(videoId,startPage)
                .doOnSubscribe((Action0) () -> {
                    callback.beforeRequest();
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Map<String, List<NeteastVideoSummary>>, Observable<NeteastVideoSummary>>() {
                    @Override
                    public Observable<NeteastVideoSummary> call(Map<String, List<NeteastVideoSummary>> map) {
                        // 通过id取到list
                        return Observable.from(map.get(videoId));
                    }
                })
                .toSortedList(new Func2<NeteastVideoSummary, NeteastVideoSummary, Integer>() {
                    @Override
                    public Integer call(NeteastVideoSummary neteastVideoSummary, NeteastVideoSummary neteastVideoSummary2) {
                        // 时间排序
                        return neteastVideoSummary.ptime.compareTo(neteastVideoSummary2.ptime);
                    }
                })
                .subscribe(new Subscriber<List<NeteastVideoSummary>>() {
                    @Override
                    public void onCompleted() {
                        callback.requestComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e + "\n" + e.getLocalizedMessage());
                        callback.requestError(e + "\n" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<NeteastVideoSummary> data) {
                        callback.requestSuccess(data);
                    }
                });
    }
}
